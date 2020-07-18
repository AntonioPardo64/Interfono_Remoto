/***********************************************************************
 * FICHERO/FILE:     miWiFi.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file miWiFi.h
 * @brief Interfaces de los metodos para manejar el wifi.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#ifndef MIWIFI_H
#define MIWIFI_H

#include <esp_wifi.h>
#include "WiFi.h"
#include "AsyncUDP.h"

#if CONFIG_FREERTOS_UNICORE
#define ARDUINO_RUNNING_CORE 0
#else
#define ARDUINO_RUNNING_CORE 1
#endif

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para desconectar el wifi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void disconnectWifi();

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para conectar el wifi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param t_abortarWiFi: Tiempo limite para efectuar la conexión. 
 *  \param ssid: Bufer de datos con el nombre de la red wifi.
 *  \param password: Bufer de datos con la clave de la red wifi.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Valor booleano que notifica el exito o fracaso de la 
 *  conexión con la red wifi.
 ***********************************************************************/
extern boolean conectWifi(unsigned long t_abortarWiFi, char* ssid, char* password);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para parar y cerrar 
 *  la comunicación udp.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param udp: Socket de comunicación udp.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void initUDP(AsyncUDP *udp, int port, AuPacketHandlerFunction cb);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Interfaz de la función para iniciar la 
 *  comunicación udp.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param udp: Socket de comunicación udp que se desa inicializar.
 *  \param port: Puerto al que se desea enlazar para la comunicación.
 *  \param cb: Handler para los paquetes udp.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
extern void stopUDP(AsyncUDP *udp);

#endif  // MIWIFI_H
