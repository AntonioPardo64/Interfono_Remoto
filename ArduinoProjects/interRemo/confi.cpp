/***********************************************************************
 * FICHERO/FILE:     confi.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file confi.cpp
 * @brief Metodos y tarea de la configuración de la conexión wiFi a traves 
 * del bluetooth.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "miWiFi.h"
#include "blue.h"
#include "confi.h"
#include "comunica.h"
#include "eeprom.h"

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
enum {ONIDLE, WCOM, ONCOM, ONTEST} estado;
unsigned long t_abortar;
unsigned int s_LED;
unsigned int miBOTON;
unsigned char trama [TRAMA];
unsigned long t_testConnect;
char nombreRED [TAM_NOM];
char claveRED [TAM_PASS];
BluetoothSerial SerialBT;
bool wiFi = false;
extern int configact;
/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Metodo para manejar el led señalizador según el 
 *  dato de entrada.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param valor: Indice que indica el valor de la señal.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void contLED(int valor){ 
//  Serial.print("El valor del led es: ");
//  Serial.println(valor);                   
	switch(valor){
		case 0:
			s_LED = 0;
			digitalWrite(4, HIGH); 
			break;
			
		case 1:
			s_LED = 1;
			digitalWrite(4, LOW);
			break;
			
		case 2:
			s_LED ^= 1;
			if(s_LED)
				digitalWrite(4, LOW);
			else
				digitalWrite(4, HIGH);
			break;
	}
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para inicializar la conexión wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void initWifi(){ 
  boolean ret = false;
  //------------------
//  wiFi = false;
//  return;
  //------------------
  if(WiFi.status() != WL_CONNECTED){
    readSaveSSID(nombreRED);
    readSavePASS(claveRED);
    if((nombreRED[0] != 0) && (claveRED[0] != 0)){     
      ret = conectWifi(T_WIFI, nombreRED, claveRED);
      if(ret)
      { 
        //Serial.print("IP Address: ");
        //Serial.println(WiFi.localIP());  
        init_Comunica();
      }
    }
  }
  else
    ret = true;
    
  wiFi = ret; 
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para para parar y descolectar el wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param valor: Indice que indica el valor de la señal.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void stopWifi(){

//  Serial.print("WIFIST_P=>");
//  Serial.println(WiFi.status());

  if(WiFi.status() == WL_CONNECTED){
    disconnectWifi();
  }
  wiFi=false;
  vTaskDelay(200);
//  Serial.print("WIFIST=>");
//  Serial.println(WiFi.status());
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para compruebar si hay una configuración 
 *  valida de wiFi guardada y en caso contrario el estado del bóton.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Un uno en caso de exito y un cero en caso de fracaso.
 ***********************************************************************/
int fonidle(){
  if(xTaskGetTickCount() > t_testConnect){
    t_testConnect = xTaskGetTickCount() + pdMS_TO_TICKS(T_NEXT);
    if(wiFi && (WiFi.status() != WL_CONNECTED)){
        esp_restart();
    }
  }
  
	if(digitalRead(PIN_BOTON) != miBOTON){
		miBOTON = digitalRead(PIN_BOTON);
		vTaskDelay(20);
		return 1;
	}
	return 0; 
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para capturar tramas bluetooth, 
 *  buscando un cliente con que enlazarse.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Un uno en caso de exito, un dos en caso de fracaso 
 *  y un cero si es ninguno de los casos anteriores.
 ***********************************************************************/
int fwcom(){
	int ret = false;
  
  ret= readBLE(&SerialBT,trama, TRAMA, 1000); 

  contLED(2);
	if(ret){

		writeBLE(&SerialBT,trama, ret);
		return 1;
	}
	else if(digitalRead(PIN_BOTON) != miBOTON){
    miBOTON = digitalRead(PIN_BOTON);
    return 2;
  }
  else
		return 0;
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para capturar una trama bluetooth cuando ya 
 *  está enlazado con la app móvil y la trata.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Un cero si captura una trama de latido, un uno si es de 
 *  configuración y un dos si no captura una trama.
 ***********************************************************************/
int foncom(){
	int ret;
	int i;
	int d;
	boolean finNom = false;

	ret = readBLE(&SerialBT,trama, TRAMA, 5000);
	if(ret){
		if(trama[0] == LATIDO){
			writeBLE(&SerialBT, trama, ret);
			return 0;
		}
		else if(trama[0] == MWIFI){
			for(i = 0; i < trama[1]; i++){
				if((trama[i+2] == '#') && (!finNom)){
					finNom = true;
					nombreRED[i] = 0;
					d = i+1;
				} 
				else if(!finNom){
					nombreRED[i] = trama[i+2];
				}
				else{
					claveRED[i-d] = trama[i+2];
				}
			}
			claveRED[i-d] = 0;
			return 1;
		} 
	}
	return 2;   
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Metodo para intentar establecer una conexión wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Un cero en caso de exito, un uno en caso de fracaso
 ***********************************************************************/
int fontest(){
	bool ret; 
	unsigned char bufResuOK [20];
	unsigned char bufResuNo [2] = {3,0};
  uint32_t localip;

	/*		// Trazas . . .
  Serial.print("Nombre=>");
  Serial.print(nombreRED);
  Serial.println("|");
  Serial.print("Clave=>");
  Serial.print(claveRED);
  Serial.println("|");
  */ 
	if((ret = conectWifi(T_WIFI, nombreRED, claveRED))){   // unsigned long t_abortarWiFi, char* ssid, char* password 	
		saveWifi(nombreRED, claveRED); 
 //   Serial.println("Salvado configura..");
    localip = WiFi.localIP();
		sprintf((char *)(&bufResuOK[2]),"%d.%d.%d.%d",localip & 0xff,(localip>>8)&0xff,(localip>>16)&0xff,(localip>>24)& 0xff);
		bufResuOK[0] = 2;
		bufResuOK[1] = strlen((char *)(&bufResuOK[2]));
		// Comprobar los valores
		//Serial.print("Montada respuesta=>");
    //Serial.println((char *)(&bufResuOK[2]));
		stopWifi();
    vTaskDelay(600);
    writeBLE(&SerialBT, (unsigned char *)bufResuOK, bufResuOK[1]+2);
    vTaskDelay(600);
		endBLE(&SerialBT);
		wiFi = 0; 
		return 0;
	} else{
		stopWifi();
		vTaskDelay(600);
		writeBLE(&SerialBT, bufResuNo, 2);
		//Serial.println("Enviada Res KO");
		vTaskDelay(600);
		return 0;
	}
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para configurar las señales de entrada 
 *  y salida del boton y el led.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void iniConfi() { 
	pinMode(15,INPUT_PULLUP);
	pinMode(4,OUTPUT);
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para manejar la maquina de estados encargada 
 *  de la configuración wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void confi(void *pvParameters) {
	iniConfi();

	static int anterior = 9;
  int ret;
  
	miBOTON = digitalRead(PIN_BOTON);
  if(configact == 0)
	  initWifi();
  else
  {
    configact = 0;
    initBLE(&SerialBT);
    estado = WCOM;
    t_abortar = xTaskGetTickCount() + pdMS_TO_TICKS(T_BLUE);
    contLED(1);
  }
  t_testConnect = xTaskGetTickCount() + pdMS_TO_TICKS(T_NEXT);
	
	while(1){
		if(anterior != estado){
			anterior = estado;
//			Serial.println(estado);
		}

		switch(estado){
			case ONIDLE:
				contLED(0);
				if(fonidle()){
					configact = 1;
					contLED(1);
					esp_restart();
					estado = WCOM;
					t_abortar = xTaskGetTickCount() + pdMS_TO_TICKS(T_BLUE);
					contLED(1);
				}  
				break;
				  
			case WCOM:
				ret = fwcom();
				if(ret == 1){
					contLED(1);
					estado = ONCOM;
				}
				if((ret == 2) || (xTaskGetTickCount() > t_abortar))
				{
					esp_restart();
					estado = ONIDLE;
				}   
				break;

			case ONCOM:
				ret = foncom();
				if(ret == 0)
					estado = ONCOM;
				else if(ret == 1){
					estado = ONTEST;
				}
				else{
					esp_restart();
					estado = ONIDLE;
				}      
				break;

			case ONTEST:
				if(fontest() == 0)
				{
					esp_restart();
					estado = ONIDLE;
				}
				else
					estado = ONCOM;
				break;

				default:
					break;
		}
    vTaskDelay(20);
	} 
}
