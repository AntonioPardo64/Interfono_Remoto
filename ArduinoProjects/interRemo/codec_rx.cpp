/***********************************************************************
 * FICHERO/FILE:     codec_rx.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file codec_rx.cpp
 * @brief Manejo de la escritura del codec
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "cmg711_13.h"
#include "codec_rx.h"
#include "fifo.h"
#include <EEPROM.h>
/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
uint32_t bufrx[16];
extern FIFO_t fifo_rx; 
extern int dcol;
short rvalmin;
short rvalmax;
int nmuestras;
int primero = 0;
/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para desencolar datos de un bufer y los 
 *  escribe en el codec.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void codec_rx(void *pvParameters){
	unsigned char ret;
	size_t i2s_bytes_read = 0;
	//Serial.println("semaforo RX");
	iniFIFO(&fifo_rx);
  short valtmp;
  
	
	while(1){
		i2s_read(I2S_NUM_0, bufrx,32,&i2s_bytes_read, 1000);
 
		for(int i = 0; i< i2s_bytes_read/4; i++)
		{
		
			valtmp = bufrx[i] >> 16;
			/*			// Trazas...
			nmuestras++;
			if(nmuestras > 8000)
			{
				nmuestras = 0;
				rvalmin = 0;
				rvalmax = 0;
			}
			if(rvalmin > valtmp)
				rvalmin = valtmp;
			if(rvalmax < valtmp)
				rvalmax = valtmp;
			*/
			ret = comprime(bufrx[i] >> 16);
			if(dcol)
					insert(&fifo_rx, ret);
		}
		taskYIELD();
	}
}
