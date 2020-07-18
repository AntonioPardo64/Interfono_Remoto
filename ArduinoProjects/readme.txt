Para compilar el proyectos es necesario instalar las librerías de ESPRESSIF.

INSTALACIÓN LIBRERIAS DE ESPRESSIF (ESP32) EN ARDUINO
=====================================================
1.- 	En el menú de preferencias de ARDUINO, introducir en el apartado "Gestor de 
	URLs Adicionales de Tarjeta" la siguiente dirrección:
		https://dl.espressif.com/dl/package_esp32_index.json

2.- 	En la pestaña de herramientas seleccionar la placa ESP32 y seleccionar 
	la opción de gestor de tarjetas.

3.- 	En la ventana desplegable buscar ESP32 e instalar.

Una vez instaladas las librerías hay que seleccionar las siguientes opciones
en el IDE de ARDUINO, en la sección de herramientas:

	-Placa: "ESP32 Dev Module"
	-Cpu Frequency : 80 Mhz.
	-Flash Frequency : 80 Mhz.
	-Flash Size : 4 MB.
	-Partition Scheme : "Huge APP (3MB No OTA/1MB SPIFFS)"
	
	
