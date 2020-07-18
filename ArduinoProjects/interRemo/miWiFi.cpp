/***********************************************************************
 * FICHERO/FILE:     miWiFi.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file miWiFi.cpp
 * @brief Metodos para manejar el wifi.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "miWiFi.h"


/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para desconectar el wifi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void disconnectWifi(){
  WiFi.mode(WIFI_OFF);
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para conectar el wifi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param t_abortarWiFi: Tiempo limite para efectuar la conexión. 
 *  \param ssid: Bufer de datos con el nombre de la red wifi.
 *  \param password: Bufer de datos con la clave de la red wifi.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Valor booleano que notifica el exito o fracaso de 
 *  la conexión con la red wifi.
 ***********************************************************************/
boolean conectWifi(unsigned long t_abortarWiFi, char* ssid, char* password){
  
	WiFi.mode(WIFI_STA);
	WiFi.begin(ssid, password);
	unsigned long t_tmp = xTaskGetTickCount() + t_abortarWiFi;

	while (WiFi.status() != WL_CONNECTED) {
		vTaskDelay(5);
		if(xTaskGetTickCount() > t_tmp){
			return false;
		}
	}
	return true;  
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para parar y cerrar la comunicación udp.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param udp: Socket de comunicación udp.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void stopUDP(AsyncUDP *udp){
  if(udp->connected())
    udp->close();
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para iniciar la comunicación udp.
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
void initUDP(AsyncUDP *udp, int port, AuPacketHandlerFunction cb){
	udp->listen(port);  // Asigna puerto de escucha.
	udp->onPacket(cb);  // Asigna funcion de escucha (Callback).
}
