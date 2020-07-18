/***********************************************************************
 * FICHERO/FILE:     ServicioComunicacion.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file ServicioComunicacion.java
 * @brief Servicio con metodos para gestionar y mantener la
 * comunicacion/conexion con el servidor
 *
 * */
/***********************************************************************/

package com;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
//import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
//import android.util.Log;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import static android.os.Process.THREAD_PRIORITY_AUDIO;
//import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
//import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static android.os.Process.setThreadPriority;

public class ServicioComunicacion extends Service {

    static MiTareaAsincronaAudioEnviar miTaskS;
    static MiTareaAsincronaAudioRecibir miTaskR;
    static MiTareaAsincronaLatido miTaskL;
    static MediaPlayer miAlarm;
    //static WifiManager.WifiLock wifiLock = null;

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Inicializador del complemento audio, la alarma.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void onCreate(){
        super.onCreate();
        miAlarm = MediaPlayer.create(this,R.raw.alarma_01);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Solicitud de inicio del servicio
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param intent   : Contexto del servicio.
     *  @param flags   : Información adicional de la solicitud.
     *  @param startId   : Identificador para el inicio de la solicitud.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return START_STICKY: constante de retorno nativa.
     *  En el caso de que el proceso del servicio es matado una vez inicializado,
     *  mantendrá el estado incicializado sin el contexto del servicio(intent).
     *  Posteriormente el sistema intentara recrear el servicio.
     ***********************************************************************/
    public int onStartCommand(Intent intent, int flags, int startId){
  //      if(wifiLock == null) {
  //          wifiLock = ((WifiManager)
  //                  getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
  //      }
  //      wifiLock.acquire();
        latido();
        return START_STICKY;
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo invocado cuando se destruye el servicio
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void onDestroy(){
        super.onDestroy();
        //Log.i("SERVICIO", "SERV DESTRUCCION...");
        if(miAlarm != null) {
            if (miAlarm.isPlaying())
                miAlarm.stop();
            CustomMessageEvent event = new CustomMessageEvent();
            event.setCustomMessage("Timbre apagando");
            EventBus.getDefault().post(event);
            miAlarm.release();
            miAlarm = null;
        }
      //  if((wifiLock != null) && wifiLock.isHeld())
      //      wifiLock.release();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para iniciar las tareas ENVIAR y RECIBIR
     * audio en el "thread pool".
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public static void startCall(){
        miRepositorio.colgar = false;
        //Log.i("Colgar_serv_startCall","Al iniciar comunicacion, colgar = false| " + miRepositorio.colgar);

        miTaskS = new MiTareaAsincronaAudioEnviar();
        miTaskR = new MiTareaAsincronaAudioRecibir();
        //Log.i("LOG_TAG", "LANZA AUDIOCALL");
        // AsyncTasks executed one one thread in Honeycomb+ unless executed in thread pool manually
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            miTaskS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            miTaskR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            miTaskS.execute();
            miTaskR.execute();
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que controla el flujo de RECIBO de
     * mensajes de audio durante una llamada.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public static class MiTareaAsincronaAudioRecibir extends AsyncTask<Void, Boolean, Integer> {

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Thread de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : Identificador de causa de salida del thread
         ***********************************************************************/
        @Override
        protected Integer doInBackground(Void... params) {
            setThreadPriority(THREAD_PRIORITY_AUDIO);
            int BUF_SIZE = miRepositorio.BUF_SIZE;

            // Create an instance of the AudioRecord class
            //Log.i("LOG_TAG_RECIBIR", "Recibir, Inicio del thread asincrono Audio recibir");

            byte[] bufrx = new byte[BUF_SIZE];
            byte[] bufdcrx = new byte[BUF_SIZE * 2];
            cmg711 codec = new cmg711();
            miRepositorio.track.play();

            if((miAlarm != null) && miAlarm.isPlaying()){
                miAlarm.pause();
                CustomMessageEvent event = new CustomMessageEvent();
                event.setCustomMessage("Timbre pausado");
                EventBus.getDefault().post(event);
                //Log.i("LOG_TAG_RECIBIR", "Recibir, Pausar Alarma");
            }
            // Create a socket and start recording
            //Log.i("LOG_TAG_RECIBIR", "Recibir, Packet destination: " + miRepositorio.address.toString());
            try {
                miRepositorio.misocket.setSoTimeout(1000);
            } catch (SocketException e) {
                //Log.e("FIN_THREAD_ENVIAR", "Enviar, setSoTimeout, SocketToutException: " + e.toString());
            }
            //Log.i("Colgar_serv_AudioRe","Mientras no este colgado recibir datagramas| " + miRepositorio.colgar);
            while (!miRepositorio.colgar) {
                try {
                    // Capture audio from the mic and transmit it
                    DatagramPacket packetrx = new DatagramPacket(bufrx, BUF_SIZE);
                    //Log.i("LOG_TAG_RECIBIR", "Recibir, Recibido Packete");
                    miRepositorio.misocket.receive(packetrx);
                    //Cojo los datos y lo muestro
                    if(packetrx.getLength() > 0)
                    {
                        //Log.i("LOG_TAG_RECIBIR", "Recibir, Packete mayor de 0");
                        if((bufrx[1] & miRepositorio.PUERTA)  != 0)
                        {
                            //Log.i("LOG_TAG_RECIBIR", "Recibir, puerta activada");
                            miRepositorio.puerta = false;
                        }
                        if((bufrx[1] & miRepositorio.TIMBRE)  != 0)
                        {
                            //Log.i("LOG_TAG_RECIBIR", "Recibir, timbre activada");
                            miRepositorio.timbre = true;
                            //Log.i("Timbre_serv_AudioRe","Si llega byte timbre, timbre =true | " + miRepositorio.timbre);
                        }
                    }
                    if(packetrx.getLength() > 2) {
                        //Log.i("LOG_TAG_RECIBIR", "Recibir, Packete mayor de 2");
                        if(miRepositorio.hablar){
                            for(int i = 0; i < ((packetrx.getLength() - 2)*2) ; i++) {
                                bufdcrx[i] = 0;
                            }
                        } else{
                            codec.decode(packetrx.getData(), 2, packetrx.getLength() - 2, bufdcrx);
                        }
                        miRepositorio.track.write(bufdcrx, 0, (packetrx.getLength() - 2) * 2);
                    }

                } catch (SocketTimeoutException e) {
                    //Log.e("FIN_THREAD_RECIBIR", "Recibir, SocketToutException: " + e.toString());
                } catch (SocketException e) {

                    //Log.e("FIN_THREAD_RECIBIR", "Recibir, SocketException: " + e.toString());
                    miRepositorio.miError = "Audio function SocketException: " + e.toString();
                    return 0;
                } catch (UnknownHostException e) {

                    //Log.e("FIN_THREAD_RECIBIR", "Recibir, UnknownHostException: " + e.toString());
                    miRepositorio.miError = "Audio function UnknownHostException: " + e.toString();
                    return 0;
                } catch (IOException e) {
                    //Log.e("FIN_THREAD_RECIBIR", "Recibir, IOException: " + e.toString());
                    miRepositorio.miError = "Audio function IOException: " + e.toString();
                    return 0;
                }
            }
            //Log.i("Colgar_serv_AudioRe","Recibido clogar, fin audio| " + miRepositorio.colgar);
            //Log.i("FIN_COMUNICA_RECIBIR", "Recibir, Final de recibimiento de mensajes");
            return 2;
        }

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Tratamiento de las causas de salida de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *  @param result   : Identificador de causa de salida.
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        @Override
        protected void onPostExecute(Integer result) {
            miRepositorio.track.stop();
            miRepositorio.coger = false;
            miRepositorio.comunicacion = false;
            miRepositorio.colgar = true;
            //Log.i("Colgar_serv_AudioRe","onPostExecute colgar =true| " + miRepositorio.colgar);

            switch (result){
                case 0:             //Error critico
                    break;

                case 1:             //Error leve
                    //Log.i("FIN_THREAD_RECIBIR", "Recibir, FALLO TIMEOUT, iniciando latido");
                    latido();
                    break;

                case 2:             // Todo correcto
                    //Log.i("FIN_THREAD_RECIBIR", "Recibir, Exito, comunicación realizada, iniciando latido");
                    latido();
                    break;
            }
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que controla el flujo de ENVIO de
     * mensajes de audio durante una llamada.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public static class MiTareaAsincronaAudioEnviar extends AsyncTask<Void, Void, Integer> {

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Thread de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : Identificador de causa de salida del thread
         ***********************************************************************/
        @Override
        protected Integer doInBackground(Void... params) {
            setThreadPriority(THREAD_PRIORITY_AUDIO);
            int BUF_SIZE = miRepositorio.BUF_SIZE;

            if(miAlarm.isPlaying()){
                miAlarm.pause();
                CustomMessageEvent event = new CustomMessageEvent();
                event.setCustomMessage("Timbre iniciando");
                EventBus.getDefault().post(event);
                //Log.i("LOG_TAG_ENVIAR", "Enviar, Pausar Alarma");
            }

            // Create an instance of the AudioRecord class
            //Log.i("LOG_ENVIAR", "Enviar, Inicio del thread asincrono Audio enviar");
            int bytes_read;
            int secuencia = 0;
            //int bytes_sent = 0;
            byte[] buftx = new byte[BUF_SIZE*2];
            byte[] bufctx = new byte[BUF_SIZE ];

            cmg711 codec = new cmg711();

            // Create a socket and start recording
            //Log.i("LOG_ENVIAR", "Enviar, Packet destination: " + miRepositorio.address.toString());
            miRepositorio.audioRecorder.startRecording();
            try {
                miRepositorio.misocket.setSoTimeout(1000);
            } catch (SocketException e) {
                //Log.e("FIN_THREAD_ENVIAR", "Enviar, setSoTimeout, SocketToutException: " + e.toString());
            }

            //Log.i("LOG_ENVIAR", "Enviar, Packet destination: " + miRepositorio.address.toString());

            //Log.i("LOG_ENVIAR", "Enviar, Se inicia el envio de mensajes");
            //Log.i("Colgar_serv_AudioSe","Mientras no este colgado enviar datagramas| " + miRepositorio.colgar);
            while (!miRepositorio.colgar) {
                if(miRepositorio.latido)
                    break;
                try {
                    // Capture audio from the mic and transmit it
                    bytes_read = miRepositorio.audioRecorder.read(buftx, 0, 2600);
                    bufctx[0] = (byte) (secuencia & 0xff);
                    bufctx[1] = 0x2;

                    if (miRepositorio.puerta) {
                        bufctx[1] |= miRepositorio.PUERTA;
                    }

                    //Log.i("Timbre_serv_AudioSe","Si timbre, byte buffer.timbre = true | " + miRepositorio.timbre);
                    if (miRepositorio.timbre) {
                    //    Log.i("Timbre_serv_AudioSe","Si timbre, byte buffer.timbre = true | " + miRepositorio.timbre);
                        bufctx[1] |= miRepositorio.TIMBRE;
                    }
                    codec.encode(buftx, 0, bytes_read, bufctx, 2);
                    if(!miRepositorio.hablar)
                        bytes_read = 0;
                    DatagramPacket packettx = new DatagramPacket(bufctx, bytes_read / 2 + 2, miRepositorio.address, miRepositorio.miport);
                    if(miRepositorio.latido)
                        break;
                    miRepositorio.misocket.send(packettx);

                    //Log.i("LOG_TAG_ENVIAR", "Enviar, bytes sent: " + bytes_read + " " + secuencia);
                    secuencia++;
                    secuencia &= 0xff;

                } catch (SocketTimeoutException e) {
                    //Log.e("FIN_THREAD_ENVIAR", "Enviar, SocketToutException: " + e.toString());
                } catch (SocketException e) {
                    //Log.e("FIN_THREAD_ENVIAR", "Enviar, SocketException: " + e.toString());
                    return 0;
                } catch (UnknownHostException e) {
                    //Log.e("FIN_THREAD_ENVIAR", "Enviar, UnknownHostException: " + e.toString());
                    miRepositorio.miError = "Audio function UnknownHostException: " + e.toString();
                    return 0;
                } catch (IOException e) {
                    //Log.e("FIN_THREAD_ENVIAR", "Enviar, IOException: " + e.toString());
                    miRepositorio.miError = "Audio function IOException: " + e.toString();
                    return 0;
                }
            }
            //Log.i("Colgar_serv_AudioSe","colgado, fin envio | " + miRepositorio.colgar);
            //Log.i("FIN_COMUNICACION_ENVIAR", "Enviar, Fin del envio de mensajes");
            return 2;
        }

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Tratamiento de las causas de salida de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *  @param result   : Identificador de causa de salida.
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        @Override
        protected void onPostExecute(Integer result) {
            miRepositorio.audioRecorder.stop();
            switch (result){
                case 0:             //Error critico
                    //Log.i("FIN_THREAD_ENVIAR", "FALLO_ENVIAR_critico , Fallo");
                    miRepositorio.colgar = true;
                    //Log.i("Colgar_serv_AudioSe","Caso 0, error critico colgar = true| " + miRepositorio.colgar);
                    break;

                case 1:             //Error leve
                    miRepositorio.colgar = true;
                    //Log.i("Colgar_serv_AudioSe","Caso 1, error leve colgar = true| " + miRepositorio.colgar);
                    //Log.i("FIN_THREAD_ENVIAR", "FALLO_ENVIAR_leve ,  TIMEOUT");
                    break;

                case 2:             // Todo correcto
                    //Log.i("FIN_THREAD_ENVIAR", "Exito enviar, comunicación realizada");
                    break;
            }
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para inicializar la tarea asincrona de latido.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public static void latido(){
        if((!miRepositorio.latido) && (!miRepositorio.coger)) {
            miRepositorio.colgar = false;
            //Log.i("Colgar_serv_latido","inicio latido, colgar= false| " + miRepositorio.colgar);
            miTaskL = new MiTareaAsincronaLatido();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                miTaskL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else
                miTaskL.execute();
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que controla el flujo de mensajes
     * fuera de una llamada.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public static class MiTareaAsincronaLatido extends AsyncTask<Void, Boolean, Integer> {

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Thread de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : Identificador de causa de salida del thread
         ***********************************************************************/
        @Override
        protected Integer  doInBackground(Void... params) {

            setThreadPriority(THREAD_PRIORITY_AUDIO);
            int BUF_SIZE = miRepositorio.BUF_SIZE ;
            miRepositorio.latido = true;
            // Create an instance of the AudioRecord class
            //Log.i("LOG_TAG", "Inicio del thread asincrono Latido");
            long tiempo;
            int timeOut = 10000;
            int bytes_read = 0;
            int secuencia = 0;
            byte[] bufrx = new byte[BUF_SIZE * 2];
            byte[] bufctx = new byte[BUF_SIZE * 2];
            //Button alarm = (Button) findViewById(R.id.btn_coger);
            int cont = 0;

            //Log.i("LOG_TAG", "Packet destination: " + miRepositorio.address.toString());
            try {
                miRepositorio.misocket.setSoTimeout(1000);
            } catch (SocketException e) {
                //Log.e("FIN_THREAD_LATIDO", "Latido, setSoTimeout, SocketToutException: " + e.toString());
            }

            while (true) {
                if(!miRepositorio.latido)
                    return 1;
                try {
                    //Log.i("LATIDO", "Inicio latido");
                    //Suspender el thread 10 segundos

                    bufctx[0] = (byte) (secuencia & 0xff);
                    bufctx[1] = 0x0;

                    //Log.i("LATIDO", "Latido, el timbre esta a: " + miRepositorio.timbre);
                    //Log.i("LATIDO", "Latido, el coger esta a: " + miRepositorio.coger);
                    //Log.i("Timbre_serv_Tarea_Lat","Si timbre, byte buffer.Timbre = true | " + miRepositorio.timbre);
                    if (miRepositorio.timbre) {
                        //Log.i("Timbre_serv_Tarea_Lat","Si timbre, byte buffer.Timbre = true | " + miRepositorio.timbre);
                        bufctx[1] |= miRepositorio.TIMBRE;
                    }
                    //Preparo la respuesta
                    DatagramPacket packettx = new DatagramPacket(bufctx, bytes_read / 2 + 2, miRepositorio.address, miRepositorio.miport);
                    miRepositorio.misocket.send(packettx);
                    //Log.i("LATIDO", "latido enviado");
                    DatagramPacket packetrx = new DatagramPacket(bufrx, BUF_SIZE * 2);
                    do {
                        miRepositorio.misocket.receive(packetrx);
                    } while(packetrx.getLength() > 2);
                    //Log.i("LATIDO", String.format(String.format("latido recibido=>" + bufrx[0] + "=" +bufrx[1])));

                    //Cojo los datos y lo muestro
                    if ((bufrx[1] & miRepositorio.TIMBRE) != 0) {
                        miRepositorio.timbre = true;
                        //Log.i("Timbre_serv_Tarea_Lat","Si byte bufferRe.timbre  = true, Timbre = true | " + miRepositorio.timbre);
                        //Log.i("LATIDO", "Latido, timbre publishprogress");
                        publishProgress(true);
                    } else {
                        //Log.i("Timbre_serv_Tarea_Lat","final bucle, si timbre | " + miRepositorio.timbre);
                        if (miRepositorio.timbre && (cont == 0)) {
                            cont ++;
                            //Log.i("Timbre_serv_Tarea_Lat","final bucle, inicio cont timbre = true | " + miRepositorio.timbre);
                        }
                        if(cont>0)
                            cont++;
                        if(cont>2){
                            cont = 0;
                            //Log.i("Timbre_serv_Tarea_Lat","final bucle, si timbre | " + miRepositorio.timbre);
                            miRepositorio.timbre = false;
                            publishProgress(true);
                        }
                    }
                    miRepositorio.pulso = true;

                    //Log.i("LATIDO", "Latido, final bucle EL timbre esta a: " + miRepositorio.timbre);
                    //Log.i("LATIDO", "Latido, final bucle bytes sent: " + bytes_read + " " + secuencia);

                } catch (SocketTimeoutException e) {
                    //Log.e("FIN_THREAD LATIDO", "SocketToutException: " + e.toString());
                    miRepositorio.pulso = false;
                } catch (SocketException e) {
                    //Log.e("FIN_THREAD", "SocketException: " + e.toString());
                    miRepositorio.miError = "Latido function SocketException: " + e.toString();
                    miRepositorio.pulso = false;
                    return 1;
                } catch (UnknownHostException e) {
                    //Log.e("FIN_THREAD", "UnknownHostException: " + e.toString());
                    miRepositorio.miError = "Latido function UnknownHostException: " + e.toString();
                    miRepositorio.pulso = false;
                    return 1;
                } catch (IOException e) {
                    //Log.e("FIN_THREAD", "IOException: " + e.toString());
                    miRepositorio.miError = "Latido function IOException: " + e.toString();
                    miRepositorio.pulso = false;
                    return 1;
                }

                try {
                    tiempo = System.currentTimeMillis() + timeOut;
                    while (System.currentTimeMillis() < tiempo) {
                        Thread.sleep(20);
                        if (miRepositorio.coger) {
                            //Log.i("LATIDO_fin", "latido, Llamada cogida, salida 2");
                            return 2;
                        }
                        //Log.i("Colgar_serv_Tarea_lat","final cada bucle, pregunta por colgar | " + miRepositorio.colgar);
                        if (miRepositorio.colgar) {
                            //Log.i("Colgar_serv_Tarea_lat","final cada bucle, pregunta por colgar | " + miRepositorio.colgar);
                            //Log.i("LATIDO", "latido, Llamada colgada, continua");
                            miRepositorio.colgar = false;
                            //Log.i("Colgar_serv_Tarea_lat","final cada bucle, cambia colgar = false | " + miRepositorio.colgar);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    //Log.e("FIN_THREAD", "InterruptedException: " + e.toString());
                    miRepositorio.miError = "Latido function InterruptedException: " + e.toString();
                    miRepositorio.pulso = false;
                    return 1;
                }
            }
        }

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Evento de actualización del estado timbre/alarma como
         *  disparar el evento "timbre".
         * En el contexto del activity
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *  @param values   : variable no utilizada, obligada por el metodo.
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        @Override
        protected void onProgressUpdate(Boolean... values) {         // publishProgress()
            if (values[0]){
                //Log.i("LOG_LLAMADA_LATIDO", "Latido, Timbre llamada en curso ");
                //Log.i("LOG_LLAMADA_LATIDO", "Coger: " +  miRepositorio.coger);
                if(miRepositorio.timbre && !miRepositorio.coger) {
                    if ((miAlarm != null) && !miAlarm.isPlaying()) {
                        miAlarm.seekTo(0);
                        miAlarm.start();
                        //Log.i("LOG_Timbre_LATIDO", "Latido, Timbre iniciado " + miRepositorio.timbre);
                    }
                }
                else{
                    miAlarm.pause();
                }
                CustomMessageEvent event = new CustomMessageEvent();
                event.setCustomMessage("Timbre iniciando");
                EventBus.getDefault().post(event);
            }
        }

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Tratamiento de las causas de salida de la tarea asincrona.
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *  @param result   : Identificador de causa de salida.
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        @Override
        protected void onPostExecute(Integer result) {

            miRepositorio.latido = false;
            miRepositorio.timbre = false;
            miRepositorio.colgar = false;
            miRepositorio.pulso = false;
            //Log.i("Timbre_serv_Tarea_Lat","onPostEexecute, timbre= false | " + miRepositorio.timbre);
            if((miAlarm != null) && miAlarm.isPlaying()) {
                miAlarm.pause();

                CustomMessageEvent event = new CustomMessageEvent();
                event.setCustomMessage("Timbre pausado");
                EventBus.getDefault().post(event);
            }
            //Log.i("latido","salida: " + result);
            switch (result){
                case 0:
                    miRepositorio.coger = false;
                    //Log.i("FIN_THREAD_LATIDO", "Latido ya activado");
                    break;

                case 1:
                    miRepositorio.coger = false;
                    break;

                case 2:
                    //Log.i("FIN_THREAD_fin_LATIDO ", "Fin latido e Iniciando conversación");
                    //Log.i("COGER final bucle ","Inicio proceso "+ miRepositorio.coger);
                    startCall();
                    break;
            }
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para enviar el canal de comunicación al servicio.
     * El servicio no lo necesita, por lo tanto devuelve null
     * Metodo de implementación obligatoria.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param intent   : Contexto del servicio.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return null
     ***********************************************************************/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
