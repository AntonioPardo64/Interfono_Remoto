/***********************************************************************
 * FICHERO/FILE:     Cliente.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-05
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file Cliente.java
 * @brief Activity con la gestión del enlazado cliente-servidor bluetooth
 *
 * */
/***********************************************************************/
package antonio.example.miblufi;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class Cliente extends AppCompatActivity {

 // private BluetoothSocket miSocket;
    private final String TAG = new String("CLIENTE");
    private Comunicaciones comm;
    private BluetoothAdapter blue;
    private BluetoothDevice device;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
        setContentView(R.layout.activity_cliente);

        comm = new Comunicaciones();
        blue = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (blue.isDiscovering())
            blue.cancelDiscovery();
        blue.startDiscovery();

    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Objeto para iniciar el apareado cliente-servidor bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *   @param context   : Contexto del activity.
     *   @param intent   : Objeto de mensajeria del activity.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice mdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceNombre = mdevice.getName();
                String deviceMAC = mdevice.getAddress();
                if(deviceNombre != null) {
                    if (deviceNombre.compareTo("ESP32test") == 0) {
                        blue.cancelDiscovery();
                        device = mdevice;
                        if (comm.InitCom(device) == true)
                            iniciarConectadoe();
                    }
                }
            }  else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }

        }
    };


    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Metodo para iniciar activity "Conectado", con el fin
     * de iniciar el la comunicación con el servidor bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void iniciarConectadoe(){
        Intent iniCon = new Intent(this, Conectado.class);
        startActivity(iniCon);
        finish();
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
        unregisterReceiver(receiver);
    }

}
