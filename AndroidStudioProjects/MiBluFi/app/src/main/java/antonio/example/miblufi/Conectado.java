/***********************************************************************
 * FICHERO/FILE:     Conectado.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-05
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file Conectado.java
 * @brief Activity con las opciones para configurar el wifi del "Telefonillo remoto" por bluetooth
 *
 * */
/***********************************************************************/
package antonio.example.miblufi;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import java.nio.charset.StandardCharsets;


public class Conectado extends AppCompatActivity {
    private Comunicaciones comm;
    private final String TAG = new String("CONECTADO");
    private Conectado.MiTareaAsincronaLatido miTaskL;
    private MiTareaAsincronaWifi miTaskW;
    public static String parwifi;
    private EditText nombreRed;
    private EditText claveRed;

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Inicializador del activity.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param savedInstanceState   : Estructura de datos persisitente.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectado);
        nombreRed = (EditText) findViewById(R.id.txt_conectado_redName);
        claveRed = (EditText) findViewById(R.id.txt_conectado_redPass);
        comm = new Comunicaciones();
        latido();
        new Handler().post(new Runnable() {
            @Override
            public void run() {

            }
        });
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
    public void latido(){
        miTaskL = new Conectado.MiTareaAsincronaLatido();
        Comunicaciones.onLatido = true;
        miTaskL.execute();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que controla el flujo de mensajes bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public class MiTareaAsincronaLatido extends AsyncTask<Void, Void, Boolean> {

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
        protected Boolean doInBackground(Void... params) {
            Comunicaciones comunica = new Comunicaciones();
            byte[] mLatido = {0x1,0x0};
            byte[] rLatido = new byte[100];
            int ret;

            while (!comunica.onWifi){
                //Log.i(TAG, "latido por enviar");
                if(comunica.btwrite(mLatido,2))
                {
                    if((ret = comunica.btread(rLatido,100,100)) == 0)
                    {
                        //Log.i(TAG, "No lectura");
                        return false;
                    }
                    //else
                        //Log.i(TAG, "lectura OK: " + ret);
                }
                else {
                    //Log.i(TAG, "Falla TX");
                    return false;
                }
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e) {
                    //Log.i(TAG, "Falla SLEEP");
                    return false;
                }
            }
            return true;
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
        protected void onPostExecute(Boolean result) {
            Comunicaciones.onLatido = false;
            //Log.i(TAG, "salida: " + result);
            if(!result) {
                //Log.i(TAG, "salida: ABORTA");
                comm.CierraCom();
                Intent iniCli = new Intent(getApplicationContext(), Cliente.class);
                startActivity(iniCli);
                finish();
            }
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para inicializar la tarea configuración wifi.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void onWifi(View view){
        String valorNombre = nombreRed.getText().toString();
        String valorClave = claveRed.getText().toString();
        String separador = "#";
        parwifi =valorNombre + separador + valorClave;
        Comunicaciones.onWifi = true;
        miTaskW = new MiTareaAsincronaWifi();
        miTaskW.execute(this);
    }


    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Tarea asincrona que comprueba la configuración wifi..
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *
     ***********************************************************************/
    public class MiTareaAsincronaWifi extends AsyncTask<Conectado, Void, Integer> {

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
        protected Integer doInBackground(Conectado... params) {
            Comunicaciones comunica = new Comunicaciones();
            int ret;
            byte[] rwifi = new byte[100];
            int len = Conectado.parwifi.length();
            byte[] msg = new byte[len+2];
            msg[0] = (byte)0x2;
            msg[1] = (byte)len;
            for(int i=0; i<len; i++) {
                msg[i+2] = (byte)parwifi.charAt(i);
            }
            //Log.i(TAG, "Wifi wait ");
            while (Comunicaciones.onLatido);  // Espera finalice latido.
            //Log.i(TAG, "Wifi write ");
            if(comunica.btwrite(msg,len+2)) {
                //Log.i(TAG, "Wifi REC ");
                if((ret = comunica.btread(rwifi,100,30000)) == 0)
                {
                   // Log.i(TAG, "wifi No lectura");
                    return 0;
                }
                else {
                    for(int i=ret;i<17;i++)
                    {
                        rwifi[i] = ' ';
                     //   Log.i(TAG,"" + rwifi[i+2]);
                    }
                    Comunicaciones.ipWifi = new String(rwifi); //StandardCharsets.UTF_8);
                    Comunicaciones.ipWifi = (Comunicaciones.ipWifi).substring(2,17);
                    //Log.i(TAG, "wifi lectura OK: " + Comunicaciones.ipWifi);
                    return (int)rwifi[0];
                }
            }
           // Log.i(TAG, "Wifi falla write ");
            return 0;
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
            Comunicaciones.onWifi = false;
            Intent iniCerrar;
            switch (result){
                case 0:
                    //Log.i(TAG, "Salida NO CONEX ");
                    comm.CierraCom();

                    Intent Finaliza = new Intent(getApplicationContext(), Cerrar.class);
                    startActivity(Finaliza);
                    finish();
                    break;
                case 2:
                //    Log.i(TAG, "Salida Exito ");
                    comm.CierraCom();
                    Comunicaciones.confWifi = true;
                    Comunicaciones.errWifi = false;
                    iniCerrar= new Intent(getApplicationContext(), Cerrar.class);
                    startActivity(iniCerrar);
                    finish();
                    break;
                case 3:
                //    Log.i(TAG, "Salida Fallo ");

                    Comunicaciones.confWifi = false;
                    //latido();
                    Comunicaciones.errWifi = true;
                    iniCerrar = new Intent(getApplicationContext(), Cerrar.class);
                    startActivity(iniCerrar);
                    finish();
                    break;
            }
        }
    }
}

