/***********************************************************************
 * FICHERO/FILE:     codec_tx.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file codec_tx.h
 * @brief Interfaz del manejo del escritura del codec
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "cmg711_13.h"
#include "codec_tx.h"
#include "fifo.h"
#include <string.h>
#include <EEPROM.h>

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/  
uint32_t buftx[8];
extern FIFO_t fifo_tx; 
extern int dcol;
//int primero = 0;

short tvalmin;
short tvalmax;
int nmuestx;

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Tarea para leer datos del codec y los encola en un bufer.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros (En este caso, 
 *  no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void codec_tx(void *pvParameters){
	size_t i2s_bytes_write = 0;
	unsigned char ret;
	int len;
	unsigned int *pbuf;
 
//  Serial.println("semaforo TX");
  iniFIFO(&fifo_tx);
  short valtmp;
  
	while(1)
	{
	// transfiere al codec en bloque de 8 muestras.
		if(dcol)
		{
			for(int i = 0; i< 8; i++)
			{			
				ret = extraer(&fifo_tx);

				valtmp = descomprime(ret);

				buftx[i] = valtmp & 0xffff;
				/*				// Trazas ...
				nmuestx++;
				if(nmuestx > 8000)
				{
					nmuestx = 0;
					tvalmin = 0;
					tvalmax = 0;
				}
				if(tvalmin > valtmp)
					tvalmin = valtmp;
				if(tvalmax < valtmp)
					tvalmax = valtmp;
				*/ 
			}
		}
		else
			memset(buftx,0,sizeof(buftx));
		len = 8;
		pbuf = buftx;
		while(len)
		{
			i2s_write(I2S_NUM_0, pbuf,len*4, &i2s_bytes_write, portMAX_DELAY);
			len -= i2s_bytes_write/4;
			pbuf += i2s_bytes_write/4;
		}

		taskYIELD();
	}
}
