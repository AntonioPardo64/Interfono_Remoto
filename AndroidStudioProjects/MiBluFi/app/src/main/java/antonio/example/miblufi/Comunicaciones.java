/***********************************************************************
 * FICHERO/FILE:     Comunicaciones.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-05
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file Comunicaciones.java
 * @brief Clase con metodos de gestión de la comunicación Bluetooth
 *
 * */
/***********************************************************************/

package antonio.example.miblufi;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
//import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.UUID;

public class Comunicaciones {
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String TAG = new String("COMM");
    private static BluetoothSocket miSocket = null;
    private static InputStream mInStream;
    private static OutputStream miOutStream;
    public static boolean onLatido = false;
    public static boolean onWifi = false;
    public static String ipWifi;
    public static boolean confWifi = false;
    public static boolean errWifi = false;

    private final int MAGIC = 0xA6;

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para inicializar el socket de comunicación bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param device   : Referencia al dispositivo bluetoth.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public boolean InitCom(BluetoothDevice device){
        if(miSocket == null){
            try {
                miSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                try{
                    miSocket.connect();
                    mInStream = miSocket.getInputStream();
                    miOutStream = miSocket.getOutputStream();
                } catch (Exception e) {
                    //Log.i(TAG, "Socket EXCEP 1." + e);
                    miSocket.close();
                    miSocket = null;
                    return false;
                }
            } catch (Exception e) {
                //Log.i(TAG, "Socket EXCEP 2.");
                miSocket = null;
                return false;
            }
            //Log.i(TAG, "Socket OK.");
            return true;
        }
        else {
            //Log.i(TAG, "Socket ya abierto");
            return true;
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para enviar un bus de datos por comunicación bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param bytes   : Bus de datos que se desea enviar.
     *  @param len   : Longitud del bus de datos.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public boolean btwrite(byte[] bytes,int len) {
        int cksum = MAGIC;

        for(int i=0;i<len;i++)
            cksum += bytes[i];
        cksum = -cksum;
        cksum &= 0xff;
        try {
            miOutStream.write(MAGIC);
            miOutStream.write(bytes,0,len);
            miOutStream.write(cksum);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para capturar los datos leidos en la comunicación bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param buf   : Bus de datos donde guardar los datos leidos.
     *  @param maxlen   : Longitud del bus de datos.
     *  @param timeOut   : Timeut para el tiempo de espera enla lectura de datos.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : Longitud de datos leidos o cero si ha habido un error o ningún dato.
     ***********************************************************************/
    public int btread(byte[] buf,int maxlen,int timeOut) {
        long tiempo;

        tiempo = System.currentTimeMillis() + timeOut;
        int cksum = MAGIC;
        int c;
        int i;


        //Log.i(TAG, "Entra Read: " + maxlen);
        // Busca principio de trama
        while(true){
            try {
                if(mInStream.available() > 0) {
                    c = mInStream.read();
                    if(c == MAGIC)
                        break;
                }
                if(System.currentTimeMillis() > tiempo)
                    return 0;
                Thread.sleep(1);
            }catch (IOException | InterruptedException e) {
                return 0;
            }
        }
        //Log.i(TAG, "Read Smensa");
        for(i=0;i<=maxlen;i++) {
            try {
                while(mInStream.available() == 0) {
                    if (System.currentTimeMillis() > tiempo)
                        return 0;
                }
                c = mInStream.read();
                cksum += c;
                buf[i] = (byte)(c & 0xff);
                if((i==1) && (c < maxlen))
                    maxlen = c +2;


            }catch (IOException e) {
                return 0;
            }
        }
        if((cksum & 0xff) == 0)
            return maxlen;
        //Log.i(TAG, "Read Falla CKSUM");
        return 0;
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Método para cerrar el socket de comunicación bluetoth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void CierraCom(){
        try {
            if(miSocket != null) {
                miSocket.close();
                miSocket = null;
            }
        } catch (IOException e) {
            //Log.e("COMUNICA", "Could not close the client socket", e);
        }
    }
}
