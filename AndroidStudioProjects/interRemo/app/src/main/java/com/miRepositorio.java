/***********************************************************************
 * FICHERO/FILE:     miRepositorio.java
 * AUTOR/AUTHOR:     Antonio Pardo
 * FECHA_CREACION/CREATION_DATE:             2020-03-29
 *
 *    Copyright(c)   Antonio Pardo
 *-----------------------------------------------------------------------
 * DESCRIPCION_FICHERO/FILE_DESCRIPTION:*/
/**@file miRepositorio.java
 * @brief Variables y constantes compartidas entre las activity, servicio,
 * eventos y tareas
 *
 * */
/***********************************************************************/

package com;

/***********************************************************************
 * LITERALES, MACROS TIPOS DE DATOS Y FUNCIONES IMPORTADAS
 * IMPORTED LITERALS, MACROS, DATA TYPES AND FUNCTIONS
 ***********************************************************************/
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class miRepositorio {

    /***********************************************************************
     * DECLARACION DE TIPOS DE DATOS, CONSTANTES Y VARIABLES INTERNOS
     * DECLARATION OF INTERNAL DATA TYPES, CONSTANTS AND VARS
     ***********************************************************************/
    public static boolean latido = false;

    public static boolean colgar = false;
    public static boolean puerta = false;
    public static boolean hablar = false;
    public final static byte PUERTA = 0x1;
    public static boolean coger = false;
    public final static byte GOGER = 0x2;
    public static boolean timbre = false;
    public static boolean llamada = false;
    public final static byte TIMBRE = 0x4;
    public static boolean comunicacion = false;
    public static boolean aviso = false;
    public static boolean servicio = false;
    public static boolean pulso = false;
    public static boolean cerrar = false;

    public static DatagramSocket misocket;
    public static InetAddress address;
    public static int miport;
    public static final int SAMPLE_RATE = 8000; // Hertz
    public static final int SAMPLE_INTERVAL = 230; // Milliseconds
    public static final int SAMPLE_SIZE = 2; // Bytes
    public static final int BUF_SIZE = (SAMPLE_RATE * SAMPLE_INTERVAL * SAMPLE_SIZE)/1000;
    //public final int BUF_SIZE = 8192;
    public static AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2);
    public static AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);

    public static String miError;
}
