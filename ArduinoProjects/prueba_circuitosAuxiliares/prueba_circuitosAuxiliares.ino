
#define PIN_BOTON 15
#define PIN_TMBR 34    	// pin del esp32 conectado al timbre
#define PIN_DCOL 33    	// pin del esp32 conectado a descolgar
#define PIN_PUER 32   	// pin del esp32 conectado a la puerta 
#define PIN_LED 4    	// pin del esp32 conectado al led


void iniPin(){
	pinMode(PIN_TMBR,INPUT);	// timbre
	pinMode(PIN_BOTON,INPUT); 	// botón
	pinMode(PIN_DCOL,OUTPUT);	// descolgar 
	pinMode(PIN_PUER,OUTPUT);	// puerta
	pinMode(PIN_LED,OUTPUT);	// LED
}

void setup() {
  Serial.begin(115200);
	iniPin();
  Serial.println("Iniciado..");
}


void loop() {
  
	if(!digitalRead(PIN_BOTON))
	{
		if(!digitalRead(PIN_TMBR)) {
			digitalWrite(PIN_DCOL,HIGH);
			digitalWrite(PIN_PUER,LOW);
			digitalWrite(PIN_LED,LOW);
		} else {
			digitalWrite(PIN_DCOL,LOW);
			digitalWrite(PIN_PUER,HIGH);
			digitalWrite(PIN_LED,HIGH);
		}
	} else {
		digitalWrite(PIN_DCOL,LOW);
		digitalWrite(PIN_PUER,LOW);
		digitalWrite(PIN_LED,LOW);
	}
}
