/***********************************************************************
 * FICHERO/FILE:     CustomMessageEvent.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file CustomMessageEvent.java
 * @brief Clase del objeto CustomMessageEvent para manejar el mensaje
 * del evento publish-subscribe
 *
 * */
/***********************************************************************/
package com;

class CustomMessageEvent {
    private String customMessage;

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para obtener el mensaje almacenado.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : mensaje guardado.
     ***********************************************************************/
    public String getCustomMessage() {
        return customMessage;
    }

    /***********************************************************************
     *  DESCRIPCION/DESCRIPTION:
     */
    /** @b Description:  Metodo para guardar un mensaje.
     *                                                              @if CERO
     *----------------------------------------------------------------------
     * PARAMETROS                                                   @endif
     *                                                              @if CERO
     * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
     *  @return : void
     ***********************************************************************/
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
