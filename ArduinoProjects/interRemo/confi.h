/***********************************************************************
 * FICHERO/FILE:     confi.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file confi.h
 * @brief Interfaz de la tarea para la configuración del wiFi del dispositivo.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef CONFI_H
#define CONFI_H

#define MAGIC 0xA6		// Codigo de inicio trama bluetooth
#define LATIDO 1			// Codigo tipo de trama bluetooth con latido 
#define MWIFI 2			// Codigo tipo de trama bluetooth con datos wifi 

#define TRAMA 100			// Tamaño maximo trama bluetooth
#define TAM_NOM 256		// Tamaño maximo de nombre red
#define TAM_PASS 256		// Tamaño maximo de clave red

#define PIN_BOTON 15    // Pin esp32 conectado al boton
#define T_WIFI 5000   //60000		// Timeout para conectar wifi
#define T_BLUE 45000		// Timeout para enlazar bluetooth
#define T_NEXT 5000     // Timeout comprobación wifi

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la tarea para configurar el wiFi 
 *  del dispositivo.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void confi(void *pvParameters);

#endif  // CONFI_H
