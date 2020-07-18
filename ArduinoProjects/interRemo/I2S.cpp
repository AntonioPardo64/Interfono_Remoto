/***********************************************************************
 * FICHERO/FILE:     I2S.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-04-06
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file I2S.cpp
 * @brief Metodo para inicializar y congigurar el protocolo de comunicación i2s.
 *
 * */
/***********************************************************************/
/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "I2S.h"

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
volatile int retardo;

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Función para inicializar y configurar la comunicación i2s.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : void
 ***********************************************************************/
void i2sInit()
{
   unsigned int value = 0;
   
   i2s_config_t i2s_config = {
    .mode = (i2s_mode_t)(I2S_MODE_MASTER | I2S_MODE_TX | I2S_MODE_RX),
    .sample_rate =  8000,              // The format of the signal using ADC_BUILT_IN
    .bits_per_sample = I2S_BITS_PER_SAMPLE_16BIT, // is fixed at 12bit, stereo, MSB
    .channel_format = I2S_CHANNEL_FMT_ALL_LEFT,
    .communication_format = I2S_COMM_FORMAT_PCM,
    .intr_alloc_flags = ESP_INTR_FLAG_LEVEL1,
    .dma_buf_count = 4,
    .dma_buf_len = 16,
    .use_apll = false,
    .tx_desc_auto_clear = true,
    .fixed_mclk = 0
   };

	static const i2s_pin_config_t pin_config = {
	 .bck_io_num = I2SCLK,
	 .ws_io_num = I2SWS,
	 .data_out_num = I2SOUT,
	 .data_in_num = I2SIN
	};
   
   i2s_driver_install(I2S_NUM_0, &i2s_config, 0, NULL);
 
   i2s_set_pin(I2S_NUM_0, &pin_config);

	// !!!!Para invertir el reloj ya que el micro genera las senales en el flanco equivocado
	// con respecto a lo que espera el CODEC.
	value = READ_PERI_REG(GPIO_FUNC26_OUT_SEL_CFG_REG);
	WRITE_PERI_REG(GPIO_FUNC26_OUT_SEL_CFG_REG,value | 0x200); // invierte BCLK
  i2s_stop(I2S_NUM_0);
  for(retardo=0;retardo<1000000;retardo++);
	i2s_start(I2S_NUM_0);
	// Serial.print("I2s Iniciado: ");
	// Serial.println(value);
}
