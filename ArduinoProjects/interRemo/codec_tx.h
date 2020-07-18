/***********************************************************************
 * FICHERO/FILE:     codec_tx.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file codec_tx.h
 * @brief Interfaz del manejo de la lectura del codec
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef CODEC_TX_H
#define CODEC_TX_H
#include "I2S.h"

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la tarea que lee datos del codec 
 *  y los encola en un bufer.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void codec_tx(void *pvParameters);

#endif  // CODEC_TX_H
