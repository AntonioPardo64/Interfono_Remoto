/***********************************************************************
 * FICHERO/FILE:     conectado.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file Bluetooth.java
 * @brief Activity con la configuración del dispositivo hardware del bluetooth
 *
 * */
/***********************************************************************/

package antonio.example.miblufi;
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
//import static android.os.SystemClock.sleep;

public class Bluetooth extends AppCompatActivity {
    private BluetoothAdapter blue;

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
        setContentView(R.layout.activity_bluetooth);
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para iniciar activity "Cerrar",
     * con el fín de cerrar la app.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void negar(View view){
        Intent negBlu = new Intent(this, Cerrar.class);
        startActivity(negBlu);

    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description: Evento de botón para intentar activar dispositivo bluetooth.
     * Dependiendo del exito o fracaso iniciara el activity "Cliente" o "Cerrar".
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *  @param view   : Vista de los comoponentes del interfaz grafico.
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void activar(View view){
        blue = BluetoothAdapter.getDefaultAdapter();
        if(!blue.isEnabled())
        {
            try {
                /*Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);*/
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);

                Intent cliIni = new Intent(this, Cliente.class);
                startActivity(cliIni);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Intent err = new Intent(this, Cerrar.class);
                startActivity(err);
                finish();
            }
        }
        else{
            Intent err = new Intent(this, Cerrar.class);
            startActivity(err);
            finish();
        }
    }
}
