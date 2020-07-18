/***********************************************************************
 * FICHERO/FILE:     codec_rx.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file codec_rx.h
 * @brief Interfaz del manejo del escritura del codec
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef CODEC_RX_H
#define CODEC_RX_H

#include "I2S.h"
/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de función para escribir en el codec audio 
 *  datos a partir de un bufer.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void codec_rx(void *pvParameters);

#endif  // CODEC_RX_H
