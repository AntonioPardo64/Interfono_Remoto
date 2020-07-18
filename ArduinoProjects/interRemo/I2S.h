/***********************************************************************
 * FICHERO/FILE:     I2S.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file I2S.h
 * @brief Interfaz para inicializar y congigurar el protocolo de comunicación i2s.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef I2S_H
#define I2S_H

#include <FreeRTOS.h>
#include <driver/i2s.h>

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
#define I2SCLK 26
#define I2SWS 	25
#define I2SIN  17
#define I2SOUT 22 

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la finción para inicalizar y configurar 
 *  la comunicación i2s.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void i2sInit();


#endif //I2S_H 
