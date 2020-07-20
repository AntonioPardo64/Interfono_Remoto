/***********************************************************************
 * FICHERO/FILE:     fifo.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file fifo.h
 * @brief Interfaz de los metodos de los bufer de datos
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef FIFO_H
#define FIFO_H

#include <FreeRTOS.h>

#define MAXOCUPA  4000  // Maximo de ocupacion fifo.
#define TAMFIFO   8192  // Tamaño fifo.
#define MASCFIFO  0x1FFF // Mascara de tamaño fifo.

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
typedef struct{
  int pin;                        // apuntador insercion.
  int pout;                       // apuntador extraccion
  SemaphoreHandle_t xSemaphore;   //Semaforo mutex para zona critica
  unsigned char ultimo;           // ultimo dato extraido.
  unsigned char buf[TAMFIFO];     // Buffer.
  } FIFO_t;                       // FIFO.

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para iniciar un semaforo mutex.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void iniFIFO(FIFO_t *cola);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para vaciar la cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void flushFIFO(FIFO_t *cola);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para insertar un BYTE en una 
 *  cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *  \param dato: Dato que se desea insertar.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void insert(FIFO_t *cola, unsigned char dato);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para extraer un BYTE de una 
 *  cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : El BYTE extraido de la cola.
 ***********************************************************************/
extern unsigned char extraer(FIFO_t *cola);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para sacar la diferencia de 
 *  posición entre el primer elemento libre y el ultimo ocupado.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Logintud del espacio ocupado
 ***********************************************************************/
extern int ocupacion(FIFO_t *cola);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para insertar un bloque 
 *  de BYTES en una cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *  \param datos: Un bufer de datos donde se encuentra el bloque de BYTES.
 *  \param tam: Tamaño del bufer de datos.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void insert_Bl(FIFO_t *cola, unsigned char *datos, int tam);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para extraer un bloque 
 *  de BYTES de una cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *  \param datos: Un bufer de datos donde se meterá el bloque de BYTES.
 *  \param tam: Tamaño del bloque de BYTES que se desea sacar.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Dimensión final del bufer de datos donde se ha metido 
 *  el bloque de BYTES.
 ***********************************************************************/
extern int extraer_Bl(FIFO_t *cola, unsigned char *datos, int tam);

#endif  // FIFO_H
