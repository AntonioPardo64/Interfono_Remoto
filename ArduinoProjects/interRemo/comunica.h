/***********************************************************************
 * FICHERO/FILE:     blue.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file blue.h
 * @brief Interfaz de la tarea y metodos de manejo de la comunicación UDP.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef COMUNICA_H
#define COMUNICA_H

#define PORT    50000  //numero de puerto
#define MAXLINE 1024   // tamaño maximo de trama
#define T_ABORT 10000  // time-out tras descolgar
#define TAM_RESP 4000  // tamaño maximo mensaje respuesta

#define PIN_TMBR 34    // pin del esp32 conectado al timbre
#define PIN_DCOL 33    // pin del esp32 conectado a descolgar
#define PIN_PUER 32    // pin del esp32 conectado a la puerta

#define PUER	0x1	  // byte de puerta
#define DCOL	0x2	  // byte de descolgar
#define TMBR	0x4	  // byte de timbre

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la tarea de que maneja la comunicación 
 *  UDP via wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void comunica(void *pvParameters);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para inicializar 
 *  la comunicación UDP.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void init_Comunica();

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para parar la comunicación UDP.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void stop_Comunica();

#endif  // COMUNICA_H
