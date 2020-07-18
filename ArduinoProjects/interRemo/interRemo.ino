/***********************************************************************
 * FICHERO/FILE:     interRemo.ino
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file interRemo.ino
 * @brief modulo de inicio del sistema que gestiona las tareas que manejan 
 * el funcionamiento de "telefonillo remoto".
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include <rom/rtc.h>
#include "codec_rx.h"
#include "codec_tx.h"
#include "comunica.h"
#include "confi.h"
#include "eeprom.h"
#include "I2S.h"

RTC_NOINIT_ATTR int configact;
RESET_REASON reason;

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función de inicialización del sitema.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void setup() {
  
  // put your setup code here, to run once:
  reason = rtc_get_reset_reason(0);
  Serial.begin(115200);
  iniEEPROM();
  i2sInit();

  //Serial.print("RESET RAZON=>");
  //Serial.println(reason);
  if(reason != 12)
    configact = 0;

  xTaskCreate(codec_rx,"codec_rx",4000,NULL, 3,NULL);
  xTaskCreate(codec_tx,"codec_tx",4000,NULL, 3,NULL);
  xTaskCreate(comunica,"comunica",4000,NULL, 2,NULL);
  xTaskCreate(confi,"confi",8000,NULL, 1,NULL);
  //Serial.println("TASK lanzadas");
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Bucle por defecto del sistema.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void loop() {
  // put your main code here, to run repeatedly:
  taskYIELD();

}
