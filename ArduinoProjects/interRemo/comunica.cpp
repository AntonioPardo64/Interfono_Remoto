/***********************************************************************
 * FICHERO/FILE:     comunica.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file comunica.cpp
 * @brief Metodos y tarea para manejar la comunicación con tramas UDP 
 * con la APP móvil.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "miWiFi.h"
#include "fifo.h"
#include "comunica.h"

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
extern short rvalmin;
extern short rvalmax;
extern int nmuestras;
extern short tvalmin;
extern short tvalmax;

AsyncUDP udp;
FIFO_t fifo_rx;
FIFO_t fifo_tx;

extern bool wiFi; 

int tmbr = 0;
int puer = 0;
int dcol = 0;

int puer_r = 0;
int latido = 0;
unsigned long t_latido;

IPAddress p_IP;
int p_Port;

unsigned short secuenciat = 0;
unsigned short secuenciar = 0;
unsigned char resp[TAM_RESP];
unsigned char resp_r[TAM_RESP];

unsigned long t_timer;
unsigned long tpuerta;
bool comun = false;   // comunicacion

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para configurar las señales fisicas que 
 *  interaztuan en la comunicación.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void iniPin(){
	pinMode(PIN_TMBR,INPUT); //timbre
	pinMode(PIN_DCOL,OUTPUT);// descolgar 
	pinMode(PIN_PUER,OUTPUT);// puerta
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para manejar las señales fisicas durante 
 *  la comunicación.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void circuitListen(){
  if(!digitalRead(PIN_TMBR))
  {
		tmbr = 1;
    Serial.println("TTBR");
  }
	// Actualizar señales
	if(dcol)
	{
		digitalWrite(PIN_DCOL,HIGH); // pin descolgar
	}
	else
	{
		digitalWrite(PIN_DCOL,LOW);
		comun = false;

	}
		
	if(puer)
  {
  //  Serial.println("PUER");
		digitalWrite(PIN_PUER,HIGH);
    if(tpuerta < xTaskGetTickCount())
    {
      digitalWrite(PIN_PUER,LOW);
      Serial.println("PUER");
      puer = 0;
    }
  }
	
	if(comun && (t_timer < xTaskGetTickCount())){
		//Serial.print("TOUT: ");
		//Serial.println(xTaskGetTickCount());
		digitalWrite(PIN_TMBR,LOW);
		digitalWrite(PIN_PUER,LOW); 
		digitalWrite(PIN_DCOL,LOW);

		tmbr = 0;
		puer = 0;
		dcol = 0;

		comun = false;
	}
  if(latido && (t_latido < xTaskGetTickCount())){
		latido = 0;
		//Serial.print("LATIDO_OFF: ");
		//Serial.println(xTaskGetTickCount());
  }
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para tratar y procesar un mensaje capturado, 
 *  posteriormente, monta una respuesta.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param packet: Trama UDP capturada (mensaje) vía wiFi.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
// Callback 
void udpListen(AsyncUDPPacket packet){
  /*			// Trazas ...
  static int media = 0;
  static int maxima = 0;
  static int minima = 99999;
  static int muestras = 0;
  static int errores = 0;
  */
  static int secant = 0;
  static unsigned long tanterior;
  static unsigned long tactual;

  tactual = xTaskGetTickCount();
   /*			// Trazas ...
  if(muestras == 0)
  {
    media = 0;
    maxima = 0;
    minima = 99999;
    errores = 0;
    tanterior = tactual;
    muestras = 1;
  }
  else if(muestras >= 101)
  {
    Serial.print("MED: ");
    Serial.print(media/100);
    Serial.print(" MAX: ");
    Serial.print(maxima);
    Serial.print(" MIN: ");
    Serial.print(minima);
    Serial.print(" ERR: ");
    Serial.print(errores);
    Serial.print(" RVALMIN: ");
    Serial.print(rvalmin);
    Serial.print(" RVALMAX: ");
    Serial.print(rvalmax);
    Serial.print(" TVALMIN: ");
    Serial.print(tvalmin);
    Serial.print(" TVALMAX: ");
    Serial.print(tvalmax);
    Serial.print(" NMUES: ");
    Serial.println(nmuestras);
    muestras = 0;
  }
  else
  {
    int dif = tactual - tanterior;
    if(dif > 1000)
      dif = 1000;
    if(dif < minima)
      minima = dif;
    if(dif > maxima)
      maxima = dif;
    media += dif;
    tanterior = tactual;
    muestras++;
  }
  */
	int len = packet.length();
	unsigned char *data = packet.data();
  p_Port = packet.remotePort();  
  p_IP = packet.remoteIP();
  //Serial.println("PK");
	//Actualizar variables de control con datos buffer
	secuenciar = data[0];
//  if(secuenciar != secant)
//    errores++;
  secant = (secuenciar + 1) & 0xff;
  t_timer = tactual +  pdMS_TO_TICKS(T_ABORT);  //xTaskGetTickCount() + pdMS_TO_TICKS(T_ABORT);
	if(data[1] & PUER)				// El cliente marca puerta
  {
		puer = 1;
		tpuerta = xTaskGetTickCount() + pdMS_TO_TICKS(1000);
		puer_r = 1;
  }
  else
    puer_r = 0;
	if(tmbr && (data[1] & TMBR))	// El cliente ya se ha enterado
		tmbr = 0;
	if(data[1] & DCOL){				// El cliente marca descolgado
		dcol = 1;
		comun = true;
	}
	else
		dcol = 0;
	
	// Manipulación de las dos colas FIFO
	if(dcol){
		insert_Bl(&fifo_tx, &data[2], len-2);
	}
	else{
    t_latido = tactual + pdMS_TO_TICKS(25000);
    /*			// Trazas ...
    if(latido == 0)
    {
      latido = 1;
      Serial.print("LATIDO_ON: ");
      Serial.println(tactual);
    }
    */ 
  	// Formar mensaje de respuesta
  	resp[0] = secuenciat & 0xff;
    secuenciat++;
  	resp[1] = 0;
  	
  	if(data[1] & PUER)		
  		resp[1] |= PUER;							
  	if(tmbr)
  		resp[1] |= TMBR;
  	if(dcol)		
  		resp[1] |= DCOL;
  				
  	// Enviar respuesta
  	if(wiFi)	
  		udp.writeTo(resp,2,packet.remoteIP(),packet.remotePort());
	/*			// Trazas ...
    Serial.print("LENV: ");
    Serial.print(resp[0]);
    Serial.print("=");
    Serial.println(resp[1]);
    */ 
	}
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para inicializar la comunicación UDP.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void init_Comunica(){
	initUDP (&udp, PORT, udpListen);
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para parar la comunicación UDP.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void stop_Comunica(){
  stopUDP(&udp); 
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para manerja la comunicación UDP via wiFi.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param pvParameters: Un apuntador a los parametros 
 *  (En este caso, no utilizado)
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void comunica(void *pvParameters){
  int len;
  int i;
	iniPin();
	
	while(1){

    if(dcol){
      // Formar mensaje de respuesta
      resp_r[0] = secuenciat & 0xff;
      secuenciat++;
      resp_r[1] = 0;
      
      if(puer_r )    
        resp_r[1] |= PUER;              
      if(tmbr)
        resp_r[1] |= TMBR;
      if(dcol)    
        resp_r[1] |= DCOL;
          
      if(dcol){
        len = ocupacion(&fifo_rx);
        if (len > 2400)
        {
          len = 2400;
        }
        extraer_Bl(&fifo_rx, &resp_r[2], len);
      }
      else
        len = 0;
      
      // Enviar respuesta
      if(wiFi && p_Port)  
        udp.writeTo(resp_r,len+2,p_IP,p_Port);
    }
    // Delay de bucle
    for(i=0;i<10;i++)
    {
		  vTaskDelay(pdMS_TO_TICKS(16));
      circuitListen();
    }
	}
}
