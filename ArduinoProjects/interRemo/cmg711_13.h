/***********************************************************************
 * FICHERO/FILE:     cmg711.h
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-02-03
 *
 *    Copyright(c)   Antonio Pardo 
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**\file cmg711.h
 * \brief Funciones de compresion y decompresion A-Law
 * 
 * */
/***********************************************************************/
#ifndef CMG711_13_H
#define CMG711_13_H

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  Comprime una muestra de 13 bit a A-Law.
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param src   : Muestra a comprimir.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra comprimida
 ***********************************************************************/
extern unsigned char comprime( short src);

/***********************************************************************
 *  DESCRIPCION/DESCRIPTION:                                             
 */
/** \b Description:  descomprime una muestra de A-Law. a 13 bit lineal.
 * 	complementa con signo hasta los 16 bits
 *                                                              \if CERO
 *----------------------------------------------------------------------
 * PARAMETROS                                                   \endif
 *  \param src   : Muestra a descomprimir.
 *                                                              \if CERO
 * VALORES_DE_RETORNO/RETURN_VALUES:                            \endif
 *  \return : Muestra descomprimida
 ***********************************************************************/
extern short descomprime( unsigned char src);


#endif  // CMG711_13_H
