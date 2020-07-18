/***********************************************************************
 * FICHERO/FILE:     blue.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file blue.h
 * @brief Interfaz del manejo del bluetooth
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef BLUE_H
#define BLUE_H

#include "BluetoothSerial.h"
#define MAGIC 0xA6

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de función para inicializar el dispositivo 
 *  bluetooth.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  @param SerialBle   : 
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/
extern void initBLE(BluetoothSerial *SerialBLE);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de función para finalizar el dispositivo 
 *  bluetooth.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  @param SerialBle   : 
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/ 
extern void endBLE(BluetoothSerial *SerialBLE);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de función para capturar mensajes bluetooth
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  @param SerialBle : 
 *  @param buf   		:  Puntero al bufer de datos.
 *  @param maxlen   	:  Longitud del bufer de datos.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/ 
extern int readBLE(BluetoothSerial *SerialBLE, unsigned char *buf,int maxlen,int maxtim);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de función para escribir mensajes bluetooth.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  @param SerialBle   : 
 *  @param buf   		:  Puntero al bufer de datos.
 *  @param maxlen   	:  Longitud del bufer de datos. 
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/
extern void writeBLE(BluetoothSerial *SerialBLE, unsigned char *buf, int len);

#endif //BLUE_H 
