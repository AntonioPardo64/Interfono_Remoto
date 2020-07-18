/***********************************************************************
 * FICHERO/FILE:     blue.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file blue.cpp
 * @brief Metodos relacionados con el manejo del bluetooth
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "blue.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

/***********************************************************************
  *  DESCRIPCION/DESCRIPTION:
  */
 /** @b Description:  Inicializa el dispositivo bluetooth
  *                                                              @if CERO
  *----------------------------------------------------------------------
  * PARAMETROS                                                   @endif
  *  @param SerialBle   : Contexto del servicio.
  *                                                              @if CERO
  * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
  *  @return : void
  ***********************************************************************/
void initBLE(BluetoothSerial *SerialBLE) {
  bool ret;
  ret = SerialBLE->begin("ESP32test"); // Bluetooth device name
}

/***********************************************************************
  *  DESCRIPCION/DESCRIPTION:
  */
 /** @b Description:  Metodo para cerrar el socket de comunicaciones bluetooth
  *                                                              @if CERO
  *----------------------------------------------------------------------
  * PARAMETROS                                                   @endif
  *  @param SerialBle   : 
  *                                                              @if CERO
  * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
  *  @return : void
  ***********************************************************************/
void endBLE(BluetoothSerial *SerialBLE) { SerialBLE->end(); }

/***********************************************************************
  *  DESCRIPCION/DESCRIPTION:
  */
 /** @b Description:  Función para capturar los datos leidos via bluetooth 
  *  en un bufer
  *                                                              @if CERO
  *----------------------------------------------------------------------
  * PARAMETROS                                                   @endif
  *  @param SerialBle   : 
  *  @param buf   		:  Puntero al bufer de datos.
  *  @param maxlen   	:  Longitud del bufer de datos.
  *                                                              @if CERO
  * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
  *  @return maxlen: Longitud del bus de datos o cero si hay algín error 
  *  o ningún dato.
  ***********************************************************************/
int readBLE(BluetoothSerial *SerialBLE, unsigned char *buf, int maxlen,
            int maxtim) {
  unsigned long timeout =
      xTaskGetTickCount() + maxtim; // variable de control para poner un tiempo limite
  int i;
  unsigned char c, cksum = MAGIC;
  while (1) // Bucle que busca el inicio de la trama
  {
    if (SerialBLE->available()) { // Si el serial tiene algun caracter
      c = SerialBLE->read();      // Leer el primer byte del serial
      if (c == MAGIC) { // Condición para salir del bloque al encontrar el
                        // MAGIC, la trama empieza
        break;
      }
    }
    if (xTaskGetTickCount() > timeout) { // comprueba que no se haya agotado el timeout
      return 0;
    }
    taskYIELD();
  }
  for (i = 0; i <= maxlen; i++) // bucle para leer la trama
  { // el limite será temporalmente maxlen, hasta que se actualice al leer el
    // campo de la trama <longitud mensaje>
    while (!SerialBLE->available()) {
      if (xTaskGetTickCount() > timeout) {
        return 0;
      }
      taskYIELD();
    }
    c = SerialBLE->read();

    buf[i] = c;
    cksum += c; // el cksum es el resultado negativo de la suma de todos los
                // valores de la trama
    if ((i == 1) && (c < maxlen)) { // se actualiza de forma precisa el limite,
                                    // con el tamaño real del mensaje
      maxlen = c + 2;
    }
  }

  if (!cksum) {
    return maxlen;
  }
  return 0;
}

/***********************************************************************
  *  DESCRIPCION/DESCRIPTION:
  */
 /** @b Description:  Función para transmitir información via bluetooth
  *                                                              @if CERO
  *----------------------------------------------------------------------
  * PARAMETROS                                                   @endif
  *  @param SerialBle   : 
  *  @param buf   		:  Puntero al bufer de datos.
  *  @param maxlen   	:  Longitud del bufer de datos. 
  *                                                              @if CERO
  * VALORES_DE_RETORNO/RETURN_VALUES:                            @endif
  *  @return : void
  ***********************************************************************/
void writeBLE(BluetoothSerial *SerialBLE, unsigned char *buf, int len) {
  unsigned char cksum = MAGIC;

  SerialBLE->write(MAGIC); // envía el magic de la trama
  for (int i = 0; i < len; i++) {

    SerialBLE->write(buf[i]); // envía el mensaje de la trama
    cksum += buf[i];          // calcula el cksum, falta negarlo
  }
  SerialBLE->write((-cksum) & 0xff); // envía el cksum
}
