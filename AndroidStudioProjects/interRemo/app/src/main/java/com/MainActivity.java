/***********************************************************************
 * FICHERO/FILE:     MainActivity.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file MainActivity.java
 * @brief Activity inicial con metodos de conexión inicial con el servidor
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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import android.util.Log;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView mensaje;
    private String ip;
    private int puerto;
    private EditText ADDREMO;
    private EditText miPORT;
    //static final String ADDREMO = "192.168.1.31";
    //static final String LOG_TAG = "UDPchat";
    DatagramSocket socket;

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
        setContentView(R.layout.activity_main);
        //Log.i("Inicio", "Activity INICIAL..");
        mensaje = findViewById(R.id.txt_waitIni);
        ADDREMO = findViewById(R.id.text_Input_IP);
        miPORT = findViewById(R.id.text_Input_PORT);
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
    public void onPause(){
        super.onPause();

        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor miEditor = datos.edit();
        ip = ADDREMO.getText().toString();
        if(ip.isEmpty())
            ip = "Introduzca una direccion ip";
        if(miPORT.getText().toString().isEmpty())
            puerto = 50000;
        else
            puerto = Integer.parseInt(miPORT.getText().toString());
        miEditor.putString("IP", ip);
        miEditor.putInt("MIPORT", puerto);
        miEditor.apply();
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
    public void onResume(){
        super.onResume();
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        ip = datos.getString("IP","Introduzca una direccion ip");
        ADDREMO.setText(ip);
        puerto = datos.getInt("MIPORT",50000);
        miPORT.setText(String.valueOf(puerto));
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Valida la correcta entrada de datos IP y puerto
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : Si los datos son vaildos o no
     ***********************************************************************/

    private boolean validate(){
        boolean result = true;
        ip = ADDREMO.getText().toString();
        if((ip.isEmpty()) || (ip.equals("Introduzca una direccion ip" )))
          result = false;
        if((miPORT.getText().toString().isEmpty()))
            result = false;
        else
            puerto = Integer.parseInt(miPORT.getText().toString());
        return result;
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para iniciar la conexión con el servidor.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/

    public void Inicio(View view){
        mensaje.setVisibility(View.VISIBLE);
        mensaje.setText("Iniciando ...");
        try {
            socket = new DatagramSocket();
            miRepositorio.misocket = socket;
            //Log.i("Inicio","creado correctamente miaudio");
            //variable.address = InetAddress.getByName(ADDREMO);
            if(validate()) {
                miRepositorio.address = InetAddress.getByName(ip);
                miRepositorio.miport = puerto;
                //Log.i("Inicio", "calculando el adrress");

                miRepositorio.servicio = true;
                Intent miCom = new Intent(this, conectado.class);
                startActivity(miCom);
                finish();
            }
            else {
                mensaje.setText("ERROR, iontroduce una dirrección ip");
            }

        } catch (Exception e) {
            //Log.i(LOG_TAG, "Host Exception: " + e.toString());
            miRepositorio.miError = "Inicio Host Exception: " + e.toString();
            mensaje.setText("ERROR de conexión, intentelo de nuevo");
        }
    }
}
