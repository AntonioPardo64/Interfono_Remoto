#include <driver/i2s.h>

#define PIN_BOTON 15
#define PIN_TMBR 34      // pin del esp32 conectado al timbre
#define PIN_DCOL 33     // pin del esp32 conectado a descolgar
#define PIN_PUER 32     // pin del esp32 conectado a la puerta 
#define PIN_LED 4     // pin del esp32 conectado al led
// I2S
#define I2SCLK 26
#define I2SWS   25
#define I2SIN  17
#define I2SOUT 22


uint32_t bufrx[16];
size_t i2s_bytes_read = 0;
size_t i2s_bytes_write = 0;
short valtmp;
int len;
volatile int retardo;
short smayor = 0;
unsigned int *pbuf;
unsigned long tiempo;


void iniPin(){
  /*
  pinMode(PIN_TMBR,INPUT);  // timbre
  pinMode(PIN_BOTON,INPUT);   // botón
  pinMode(PIN_PUER,OUTPUT); // puerta
  pinMode(PIN_LED,OUTPUT);  // LED
*/
  pinMode(PIN_DCOL,OUTPUT); // descolgar 

}

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
//  WRITE_PERI_REG(I2S_CONF_REG(0),0x30010);

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



void setup() {
  Serial.begin(115200);
  iniPin();
  i2sInit();
  digitalWrite(PIN_DCOL,HIGH);
  Serial.println("Iniciado..");
  tiempo = millis() + 1000;
}


void loop() {

  i2s_read(I2S_NUM_0, bufrx,32,&i2s_bytes_read, 1000);

  for(int i = 0; i< i2s_bytes_read/4; i++)
  { 
    bufrx[i]= (bufrx[i] >> 16) & 0xffff;
    valtmp = bufrx[i];
    if(abs(valtmp) > smayor)
      smayor = abs(valtmp);
  }
  if(millis() > tiempo){
    Serial.print("Mayor =");
    Serial.println(smayor);
    smayor=0;
    tiempo = millis() + 1000;
  }
  
  len = 8;
  pbuf = bufrx;
  while(len)
  {
    i2s_write(I2S_NUM_0, pbuf,len*4, &i2s_bytes_write, portMAX_DELAY);
    len -= i2s_bytes_write/4;
    pbuf += i2s_bytes_write/4;
  } 
}
