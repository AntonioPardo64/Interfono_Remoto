/***********************************************************************
 * FICHERO/FILE:     cmg711.cpp
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-02-03
 *
 *    Copyright(c)   Antonio Pardo 
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**\file cmg711.cpp
 * \brief Funciones de compresion y decompresion A-Law
 * 
 * */
/***********************************************************************/

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS               
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS                   
 ***********************************************************************/
#include "cmg711_13.h"

/***********************************************************************
 * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS       
 * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS               
 ***********************************************************************/
/** tabla de descompresion de 13 bits extendidos con signo hasta 16 */
static short tab_des_leya[] = {
	-688 ,-656 ,-752 ,-720 ,-560 ,-528 ,-624 ,-592 ,
	-944 ,-912 ,-1008,-976 ,-816 ,-784 ,-880 ,-848 ,
	-344 ,-328 ,-376 ,-360 ,-280 ,-264 ,-312 ,-296 ,
	-472 ,-456 ,-504 ,-488 ,-408 ,-392 ,-440 ,-424 ,
	-2752,-2624,-3008,-2880,-2240,-2112,-2496,-2368,
	-3776,-3648,-4032,-3904,-3264,-3136,-3520,-3392,
	-1376,-1312,-1504,-1440,-1120,-1056,-1248,-1184,
	-1888,-1824,-2016,-1952,-1632,-1568,-1760,-1696,
	-43  ,-41  ,-47  ,-45  ,-35  ,-33  ,-39  ,-37  ,
	-59  ,-57  ,-63  ,-61  ,-51  ,-49  ,-55  ,-53  ,
	-11  ,-9   ,-15  ,-13  ,-3   ,-1   ,-7   ,-5   ,
	-27  ,-25  ,-31  ,-29  ,-19  ,-17  ,-23  ,-21  ,
	-172 ,-164 ,-188 ,-180 ,-140 ,-132 ,-156 ,-148 ,
	-236 ,-228 ,-252 ,-244 ,-204 ,-196 ,-220 ,-212 ,
	-86  ,-82  ,-94  ,-90  ,-70  ,-66  ,-78  ,-74  ,
	-118 ,-114 ,-126 ,-122 ,-102 ,-98  ,-110 ,-106 ,
	 688 , 656 , 752 , 720 , 560 , 528 , 624 , 592 ,
	 944 , 912 , 1008, 976 , 816 , 784 , 880 , 848 ,
	 344 , 328 , 376 , 360 , 280 , 264 , 312 , 296 ,
	 472 , 456 , 504 , 488 , 408 , 392 , 440 , 424 ,
	 2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368,
	 3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392,
	 1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184,
	 1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696,
	 43  , 41  , 47  , 45  , 35  , 33  , 39  , 37  ,
	 59  , 57  , 63  , 61  , 51  , 49  , 55  , 53  ,
	 11  , 9   , 15  , 13  , 3   , 1   , 7   , 5   ,
	 27  , 25  , 31  , 29  , 19  , 17  , 23  , 21  ,
	 172 , 164 , 188 , 180 , 140 , 132 , 156 , 148 ,
	 236 , 228 , 252 , 244 , 204 , 196 , 220 , 212 ,
	 86  , 82  , 94  , 90  , 70  , 66  , 78  , 74  ,
	 118 , 114 , 126 , 122 , 102 , 98  , 110 , 106 
	};

static int valmax = 4079;

static unsigned char tabexponente[] =
{ 1, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4,
  5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
  6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
  7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
  7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
  7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
  7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
  
 /***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Comprime una muestra de 13 bit a A-Law.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param sample   : Muestra a comprimir.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/
unsigned char comprime( short muestra )
{
   int signo = 0x80;
   int exponente;
   int mantisa;
   int valor;

	// determinacion de signo y valor absoluto
	if(muestra & 0x1000)	// el signo en el bit 12
	{
		signo = 0;
		muestra = (-muestra) & 0xfff;
	}
	else
		muestra &= 0xfff;

	// saturacion
   if ( muestra > valmax )
   {
      muestra = valmax;
   }
   if ( muestra >= 0x20 )	// segmento 0 alto o superior 
   {
      exponente = (int) tabexponente[( muestra >> 5 ) & 0x7F];
      mantisa = ( muestra >> exponente ) & 0x0F;
      valor = ( exponente << 4 ) | mantisa;
   }
   else  // segmento 0 inferior
   {
      valor = muestra >> 1;
   }
   valor ^= ( signo ^ 0x55 );
   return (unsigned char) valor;
}

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  descomprime una muestra de A-Law. a 13 bit lineal.
 * 	complementa con signo hasta los 16 bits
 *	                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param src   : Muestra a descomprimir.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra descomprimida
 ***********************************************************************/
short descomprime( unsigned char src)
{
	short res = tab_des_leya[src];
	return res;
}
