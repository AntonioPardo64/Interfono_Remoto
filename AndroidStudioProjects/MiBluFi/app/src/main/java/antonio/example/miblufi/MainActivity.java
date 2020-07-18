/***********************************************************************
 * FICHERO/FILE:     MainActivity.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-05
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file MainActivity.java
 * @brief Activity inicial con metodos para iniciar el cliente bluetooth
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bAdap;

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

        bAdap = BluetoothAdapter.getDefaultAdapter();
        int i = 2;

        /***********************************************************************
         *  DESCRIPCION/DESCRIPTION:
         */
        /** @b Description:  Metodo para retrasar 4 segundos las respuesta a la llamada
         * del dispositivo local bluetooth
         *                                                              @if CERO
         *----------------------------------------------------------------------
         * PARAMETROS                                                   @endif
         *                                                              @if CERO
         * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
         *  @return : void
         ***********************************************************************/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bAdap == null) {                              // Si el dispositivo tiene bluetooth
                    // Nos movemos al activity cerrar
                    cerrarAPP();
                } else if(bAdap.isEnabled()) {                   // Si el despositivo tiene activado el bluetooth
                    // Nos movemos al activity cliente
                    iniciarCliente();
                } else {
                    // Nos movemos al activity bletooth         // Si tiene el dispositivo bluetooth desactivado
                    confBluetooth();
                }
            }
        }, 4000 );

    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Metodo para iniciar activity  "Cerrar", con el fin
     * de cerrar la aplicación.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void cerrarAPP(){
        Intent close = new Intent(this, Cerrar.class);
        startActivity(close);
        finish();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Metodo para iniciar activity "Cliente", con el fin
     * de iniciar el cliente bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void iniciarCliente(){
        Intent iniCli = new Intent(this, Cliente.class);
        startActivity(iniCli);
        finish();
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Metodo para iniciar activity "Bluetooth" con el fin
     * de configurar el dispositivo fisico bluetooth.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void confBluetooth(){
        Intent confBlu = new Intent(this, Bluetooth.class);
        startActivity(confBlu);
        finish();
    }
}
