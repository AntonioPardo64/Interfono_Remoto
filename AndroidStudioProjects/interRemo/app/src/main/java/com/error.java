/***********************************************************************
 * FICHERO/FILE:     error.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file error.java
 * @brief Activity final por error critico
 *
 * */
/***********************************************************************/

package com;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class error extends AppCompatActivity {

    private TextView mensaje;

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
        setContentView(R.layout.activity_error);

        mensaje = (TextView) findViewById(R.id.txt_error);
        mensaje.setText(miRepositorio.miError);
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
        System.exit(0);
    }
}
