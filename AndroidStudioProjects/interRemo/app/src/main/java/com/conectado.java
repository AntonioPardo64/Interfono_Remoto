/***********************************************************************
 * FICHERO/FILE:     conectado.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file conectado.java
 * @brief Activity con la gestión del interfaz grafico durante la conexión.
 *
 * */
/***********************************************************************/

package com;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class conectado extends AppCompatActivity {

    private static ImageView milatrun;
    private int color; // 0 verde, 1 blanco, 2 rojo
    private static MiTareaAsincronaPulso miPulso;
    private static boolean initPulso;
    private Sensor sensor;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    // botones
    private Button btnCoger;
    private Button btnColgar;
    private Button btnAbrir;
    private Button btnCerrar;
    private Button btnHablar;

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Inicializador del activity.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param savedInstanceState   : Estado de datos persisitente.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_conectado);
        Intent iniCon = new Intent(this, ServicioComunicacion.class);
        startService(iniCon);
        EventBus.getDefault().register(this);
        color = 0;
        initPulso = false;

        btnCoger = findViewById(R.id.btn_coger);
        btnColgar = findViewById(R.id.btn_colgar);
        btnAbrir = findViewById(R.id.btn_abrir);
        btnCerrar = findViewById(R.id.btn_cerrar);
        btnHablar = findViewById(R.id.btn_hablar);
        milatrun = findViewById(R.id.latrun);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null)
        {
            sensorEventListener = new SensorEventListener() {
                /***********************************************************************
                 *  DESCRIPCION/DESCRIPTION:
                 */
                /** @b Description: Metodo de sensorEventListener activado por sensor de
                 *  proximidad.
                 *                                                              @if CERO
                 *----------------------------------------------------------------------
                 * PARAMETROS                                                   @endif
                 *  @param event   : Evento provocado por el sensor.
                 *                                                              @if CERO
                 * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
                 *  @return : void
                 ***********************************************************************/
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(event.values[0]<sensor.getMaximumRange()){
                        // Esta dentro del alcance del sensor de proximidad
                        btnCoger.setEnabled(false);
                        btnColgar.setEnabled(false);
                        btnAbrir.setEnabled(false);
                        btnCerrar.setEnabled(false);
                    } else{
                        btnCoger.setEnabled(true);
                        btnColgar.setEnabled(true);
                        btnAbrir.setEnabled(true);
                        btnCerrar.setEnabled(true);
                    }
                }

                /***********************************************************************
                 *  DESCRIPCION/DESCRIPTION:
                 */
                /** @b Description:  Metodo de cambio de precision (no usado).
                 *                                                              @if CERO
                 *----------------------------------------------------------------------
                 * PARAMETROS                                                   @endif
                 *  @param sensor   : Sensor de proximidad.
                 *  @param accuracy : Precisión al que se cambia el sensor.
                 *                                                              @if CERO
                 * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
                 *  @return : void
                 ***********************************************************************/
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Si la precisión del sensor cambia, no se utiliza
                }
            };
            btnHablar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Log.i("HABLAR", "dentro del ontouch");
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        //Log.i("HABLAR", "detectado presion de boton");
                        miRepositorio.hablar = true;
                        btnHablar.setBackgroundColor(0xffff0000);
                    } else  if (event.getAction() == MotionEvent.ACTION_UP){
                        //Log.i("HABLAR", "detectado soltar boton");
                        miRepositorio.hablar = false;
                        btnHablar.setBackgroundColor(0xFFD6D7D7);
                    }
                    return false;
                }
            });
        }

        start();
        //Log.i("conectado","iniciando activity conectado");
        controlPulso();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Registro al evento de escucha al sensor de proximidad.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void start(){
        sensorManager.registerListener(sensorEventListener,sensor,200);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Desregistro al evento de escucha al sensor de proximidad.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void stop(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo invocado cauando la interación activa con
     * el activity se pausa.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }
    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo invocado cuando la activity esta activa y
     *  preparada para iniciar la interacion con el usuario
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Override
    protected void onResume() {
        start();
        super.onResume();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Guarda datos en una estructura de datos persistente.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param estado   : Estructura de datos persisitente.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void onSaveInstanceState(Bundle estado){
        estado.putBoolean("servicio", miRepositorio.servicio);
        estado.putBoolean("colgar", miRepositorio.colgar);
        estado.putBoolean("puerta", miRepositorio.puerta);
        estado.putBoolean("comunicacion", miRepositorio.comunicacion);
        estado.putBoolean("aviso", miRepositorio.aviso);
        estado.putBoolean("timbre", miRepositorio.timbre);
        estado.putInt("color",color);
        estado.putBoolean("pulso",miRepositorio.pulso);
        estado.putBoolean("initPulso", initPulso);
        super.onSaveInstanceState(estado);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Recupera datos y estados a partir de una estructura
     * de datos persistente.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param estado   : Estructura de datos persisitente.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void onRestoreInstanceState(Bundle estado){
        super.onRestoreInstanceState(estado);
        miRepositorio.servicio  =  estado.getBoolean("servicio");
        miRepositorio.colgar = estado.getBoolean("colgar");
        miRepositorio.puerta = estado.getBoolean("puerta");
        miRepositorio.comunicacion = estado.getBoolean("comunicacion");
        miRepositorio.aviso = estado.getBoolean("aviso");
        miRepositorio.timbre = estado.getBoolean("timbre");
        color = estado.getInt("color");
        miRepositorio.pulso = estado.getBoolean("pulso");
        initPulso = estado.getBoolean("initPulso");

        //Log.i("Timbre_con_onRestore","Al restaurar valores, cambia color boton timbre segun valor timbre | " + miRepositorio.timbre);
        if(miRepositorio.timbre)
            btnCoger.setBackgroundColor(0xffff0000);
        else
            btnCoger.setBackgroundColor(0xFFD6D7D7);

        colorPulso(color);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Subscribe a un evento de tipo mensaje.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param event   : Identificador del evento subscrito.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Subscribe
    public  void onEvent(CustomMessageEvent event){
        //Log.i("Timbre_con_evento","cambia color boton timbre segun valor timbre | " + miRepositorio.timbre);
        if(miRepositorio.timbre)
                btnCoger.setBackgroundColor(0xffff0000);
            else
                btnCoger.setBackgroundColor(0xFFD6D7D7);
        //Log.i("Colgar_con_evento","Pregunta si colgar = true, cambiar color boton a color original| " + miRepositorio.colgar);
        if(miRepositorio.colgar) {
                btnCoger.setBackgroundColor(0xFFD6D7D7);
            }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para cerrar la conexión.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void cerrar(View view){
        miRepositorio.colgar = true;
        //Log.i("Colgar_con_cerrar","clic boton cerrar, colgar cambia a true| "+ miRepositorio.colgar);
        miRepositorio.latido = false;
        miRepositorio.coger = false;
        miRepositorio.cerrar = true;
        Intent finCon = new Intent(this, ServicioComunicacion.class);
        stopService(finCon);
        miRepositorio.misocket.disconnect();
        miRepositorio.misocket.close();

        System.exit(0);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Cambia la animación del icono del boton que refleja
     * el pulso del latido
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param color   : Identificador del color al que se desea cambiar el botón
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    private static void colorPulso(int color){
        switch (color){
            case 0:
                milatrun.setImageResource(R.drawable.latido_0);
                break;
            case 1:
                milatrun.setImageResource(R.drawable.latido_1);
                break;
            case 2:
                milatrun.setImageResource(R.drawable.latido_2);
                break;
            case 3:
                milatrun.setImageResource(R.drawable.latido_3);
                break;
            case 4:
                milatrun.setImageResource(R.drawable.latido_4);
                break;
            case 5:
                milatrun.setImageResource(R.drawable.latido_5);
                break;
            case 6:
                milatrun.setImageResource(R.drawable.latido_6);
                break;
            case 7:
                milatrun.setImageResource(R.drawable.latido_7);
                break;
            default:
                break;
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para cancelar llamada entrante
     * activa o no.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void Colgar(View view){
        //Log.i("LOG_TAG", "Ending call!");
        miRepositorio.colgar = true;
        //Log.i("Colgar_con_colgar","Pulsar boton colgar, colagar cambia a true| " + miRepositorio.colgar);
        btnCoger.setBackgroundColor(0xFFD6D7D7);
        //Log.i("Timbre_con_colgar","pregunta si hay timbre o coger | " + miRepositorio.timbre);
        if(miRepositorio.timbre || miRepositorio.coger) {
            //miRepositorio.colgar = true;
            //miRepositorio.timbre = false;
            miRepositorio.coger = false;
            if (ServicioComunicacion.miAlarm.isPlaying())
            {
                ServicioComunicacion.miAlarm.pause();
            }
            //miBTNtimbre.setBackgroundColor(0xFFD6D7D7);
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para abrir puerta durante llamada activa.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void Abrir(View view){
        //Log.i("LOG_TAG", "Open door!");
        if(miRepositorio.coger && !miRepositorio.puerta)
            miRepositorio.puerta = true;
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para aceptar llamada entrante.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void Coger(View view){
        //Log.i("COGER","Inicio proceso "+ miRepositorio.coger);
        milatrun.setImageResource(R.drawable.ic_cblanco);
        //Log.i("Timbre_con_coger","pregunta si timbre y no coger | " + miRepositorio.timbre);
        if(miRepositorio.timbre && !miRepositorio.coger){
            miRepositorio.coger = true;
            miRepositorio.timbre = false;
            //Log.i("Timbre_con_coger","timbre a false | " + miRepositorio.timbre);
            btnCoger.setBackgroundColor(0xFFD6D7D7);
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Metodo que inicia la tarea asincrona del pulso.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public static void controlPulso(){
        if(!initPulso){
            initPulso = true;
            miPulso = new MiTareaAsincronaPulso();
            miPulso.execute();
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que controla el flujo de mensajes
     * durante el latido.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public static class MiTareaAsincronaPulso extends AsyncTask<Void, Integer, Integer> {

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
            int color = 4;
            int fallo = 0;
            while (!miRepositorio.cerrar) {
                switch (color){
                    case 0:
                        if(miRepositorio.pulso) {
                            color = 1;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 1:
                        if(miRepositorio.pulso) {
                            color = 2;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 2:
                        if(miRepositorio.pulso) {
                            color = 3;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 3:
                        if(miRepositorio.pulso) {
                            color = 4;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 4:
                        if(miRepositorio.pulso) {
                            color = 5;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 5:
                        if(miRepositorio.pulso) {
                            color = 6;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 6:
                        if(miRepositorio.pulso) {
                            color = 7;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    case 7:
                        if(miRepositorio.pulso) {
                            color = 0;
                            fallo = 0;
                        }
                        else
                            fallo++;
                        break;
                    default:
                        color = 4;
                        fallo = 0;
                        break;
                }
                if (fallo > 2)
                    color = 4;

                publishProgress(color);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return 1;
                }
            }
            return 0;
        }

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description: Evento actualización del color del botón.
         * En el contexto del activity
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *  @param values   : Identificador del color del botón.
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        @Override
        protected void onProgressUpdate(Integer... values) {         // publishProgress()
            if(!miRepositorio.coger)
                colorPulso(values[0]);
            else
                colorPulso(1);
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

            //Log.i("Pulso","salida: " + result);
            switch (result){
                case 0:
                    //Log.i("FIN_Pulso", "Aplicacion cerrada");
                    break;
                case 1:
                    //Log.i("FIN_Pulso", "Error al crear el thread.sleep");
                    initPulso = false;
                    controlPulso();
                    break;
            }
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo invocado cuando se destruye el activitiy
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Desregistra el bus de datos al evento
        EventBus.getDefault().unregister(this);
    }
}
