/***********************************************************************
 * FICHERO/FILE:     eeprom.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file eeprom.cpp
 * @brief Interfaz de la tarea y metodos de manejo de la eeprom.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "eeprom.h"

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para iniciar la eeprom.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void iniEEPROM(){
	EEPROM.begin(512);
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para salvar los datos de una red wiFi en la eeprom.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param ssid: Nombre de la red wiFi.
 *  \param pass: Clave de la red wiFi.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void saveWifi( char ssid[], char pass[]) {

	int ini = strlen(ssid);
	EEPROM.write(0, ini);
	int val = EEPROM.read(0);
	//Serial.print("Lenght of the ssid: ");
	//Serial.println(val);
	for(int i = 0; i<ini; i++){
		EEPROM.write(i+1, ssid[i]);
	}

	ini = strlen(pass);
	EEPROM.write(256, ini);
	val = EEPROM.read(256);
	//Serial.print("Lenght of the pass: ");
	//Serial.println(val);
	for(int j = 0; j<ini; j++){
		EEPROM.write(j+257, pass[j]);
	}

	EEPROM.commit();
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para leer la clave de la red wiFi salvada en la eeprom.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param ssid: bufer de datos donde introducir la clave salvada.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void readSaveSSID(char *ssid){
	int i = 0,j;
	ssid[0] = 0;
	i = EEPROM.read(0);
	if((i == 0) || (i == 255))
		return;
	for(j = 0; j< i; j++){
		ssid[j] = EEPROM.read(j+1);
	}
	ssid[j] = 0;
	//Serial.print("Nombre de la red: ");
	//Serial.println(ssid);
	return;
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para leer el nombre de la red wiFi salvada en la eeprom.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pass: bufer de datos donde introducir el nombre de la red salvada.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void readSavePASS(char *pass){
	int i = 0,j;
   pass[0] = 0;
	i = EEPROM.read(256);
	if((i == 0) || (i == 255))
		return;
	for(j = 0; j< i; j++){
		pass[j] = EEPROM.read(j+257);
	}
	pass[j] = 0;
	//Serial.print("Clave de la red: ");
	//Serial.println(pass);
	return;
}
