/*
 * Movimiento.java
 *
 * Created on February 14, 2004, 9:09 PM
 *
 * (C) 2003-2004 Escuela Polit??cnica Superior, Universidad Aut??noma de Madrid
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * Esta clase representa un movimiento o jugada en algun juego multijugador.
 */
abstract public class Movimiento {    
    
    /** 
     * genera una cadena que describe este movimiento
     * @return una cadena de la forma "E2-E4", o en caso de coronacion, "E7-E8=r"
     */    
    abstract public String toString();
        
    /** compara esta jugada con otra, a fin de comprobar si son iguales
     * @param o otro Movimiento
     * @return el valor de la comparacion (true o false)
     */
    abstract public boolean equals(Object o);
}
