/***********************************************************************
 * FICHERO/FILE:     fifo.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file fifo.cpp
 * @brief Interfaz de los metodos de los bufer de datos
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include <EEPROM.h>

#include "fifo.h"
#include <string.h>

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para iniciar un semaforo mutex.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
//inicializa el semaforo mutex
void iniFIFO(FIFO_t *cola){
  cola->xSemaphore = xSemaphoreCreateMutex();
  /*		// Trazas ...
  if(cola->xSemaphore == NULL)
    Serial.println("falla el semaforo");
   */
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para insertar un BYTE en una cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *  \param dato: Dato que se desea insertar.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
// Inserta un BYTE en un cola FIFO pasada como parametro
// Actualiza el puntero al primer elemento libre, pin
// Si la cola esta llena quita el valor mas antiguo y actualiza el puntero al ultimo elemento ocupado, pout
void insert(FIFO_t *cola, unsigned char dato){
  int len;

  xSemaphoreTake( cola->xSemaphore, portMAX_DELAY);
  len = (cola->pin - cola->pout) & MASCFIFO;
  cola->buf[cola->pin] = dato; 
  cola->pin = (cola->pin +1) & MASCFIFO;
  if(len >= MAXOCUPA)                     // Si el buffer esta lleno.
    cola->pout = (cola->pout +1) & MASCFIFO; // El dato nuevo lo insertas y el mas antiguo lo desechas.
  xSemaphoreGive( cola->xSemaphore ); 
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para extraer un BYTE de una cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : El BYTE extraido de la cola.
 ***********************************************************************/
// Extrae un BYTE de la cola FIFO pasada como parametro
// Mueve el puntero al ultimo elemento de la cola, pout
// Actualiza un indicador con el ultimo valor extraido, ultimo
// Si la cola esta vacia, no hace nada y devuelve el ultimo valor extraido, ultimo
unsigned char extraer(FIFO_t *cola){
  unsigned char dato;
  int len;

  xSemaphoreTake( cola->xSemaphore, portMAX_DELAY); 
  len = (cola->pin - cola->pout) & MASCFIFO;
  if(len){
    dato = cola->buf[cola->pout]; 
    
    cola->pout = (cola->pout +1) & MASCFIFO;
    cola->ultimo = dato;
    xSemaphoreGive( cola->xSemaphore ); 
    return dato;
  }
  else{
    xSemaphoreGive( cola->xSemaphore ); 
    return (cola->ultimo);
  }
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para sacar la diferencia de posición entre 
 *  el primer elemento libre y el ultimo ocupado.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Logintud del espacio ocupado
 ***********************************************************************/
// Devuelve la longitud del espacio ocupado de la FIFO pasada como parametro
// La diferencia entre el primer el elemento libre y el ultimo ocupado
int ocupacion(FIFO_t *cola){
  int len;
  xSemaphoreTake( cola->xSemaphore, portMAX_DELAY); 
  len = (cola->pin - cola->pout) & MASCFIFO;  
  xSemaphoreGive( cola->xSemaphore ); 
  return len;
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para insertar un bloque de BYTES en una cola FIFO.
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
// Inserta un bloque de BYTES en un cola FIFO
/* 
 * Como utiliza memcopy, que copia un bloque de memoria de forma lineal, se tiene que dividir en dos trozos.
 * El primer trozo va desde el primer elemento libre, pin, hasta el final del buffer 
 * El segundo trozo va desde el inicio del buffer hasta el ultimo elemento ocupado, pout
 */
// Actualiza el puntero al primer elemento libre, pin
// Si la cola esta llena quita los valores mas antiguos y actualiza el puntero al ultimo elemento ocupado, pout
void insert_Bl(FIFO_t *cola, unsigned char *datos, int tam){
  int len;
  int len1;
  int resta;
  
  xSemaphoreTake( cola->xSemaphore,portMAX_DELAY);
  resta = TAMFIFO - cola->pin;
  len = (cola->pin - cola->pout) & MASCFIFO; 
  if(tam > resta){
    len1 = resta;
  }else{
    len1 = tam;
  } 
  memcpy(&cola->buf[cola->pin],datos,len1);
  datos += len1;
  if(tam > len1)
  {
    len1 = tam - len1;
    memcpy(&cola->buf[0],datos,len1);
  }
  cola->pin = (cola->pin +tam) & MASCFIFO;
  if((len+tam) >= MAXOCUPA)                         // Si el buffer esta lleno.
    cola->pout = (cola->pout + len + tam -MAXOCUPA) & MASCFIFO; // los datos nuevos los insertas y los mas antiguos los desechas.
  xSemaphoreGive( cola->xSemaphore ); 
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para extraer un bloque de BYTES de una cola FIFO.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param cola: Un apuntador a una cola FIFO.
 *  \param datos: Un bufer de datos donde se meterá el bloque de BYTES.
 *  \param tam: Tamaño del bloque de BYTES que se desea sacar.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Dimensión final del bufer de datos donde se ha metido el bloque de BYTES.
 ***********************************************************************/
int extraer_Bl(FIFO_t *cola, unsigned char *datos, int tam){
  unsigned char dato;
  int len;
  int len1;
  int resta;
  int tamMax;
  
  xSemaphoreTake( cola->xSemaphore, portMAX_DELAY);
  len = (cola->pin - cola->pout) & MASCFIFO;
  if(len){
    tamMax = (tam < len) ? tam : len;
    resta = TAMFIFO - cola->pout;
  
    if(tamMax > resta)
      len1 = resta;
    else
      len1 = tamMax;
    memcpy(datos,&cola->buf[cola->pout],len1);
    datos += len1;
    if(tamMax > len1)
    {
      len1 = tamMax - len1;
      memcpy(datos,&cola->buf[0],len1);
    }
    cola->pout = (cola->pout +tamMax) & MASCFIFO;
    cola->ultimo = datos[len1-1];
  } else{
    tamMax = 0;
  }  
  xSemaphoreGive( cola->xSemaphore ); 
  return tamMax;
}
