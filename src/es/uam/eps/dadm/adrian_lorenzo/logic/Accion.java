/*
 * AccionPartida.java
 *
 * Created on March 2, 2005, 11:47 AM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * Una accion que puede ser llevada a cabo sobre una partida.
 * Las acciones tambien incluyen un 'originante', que puede ser un jugador u
 * otra accion o la partida o lo que sea.
 *
 * @author mfreire
 */
public interface Accion {
    
    /**
     * Lleva a cabo la accion sobre una partida
     */
    void ejecuta(Partida p) throws ExcepcionJuego;
    
    /**
     * Devuelve 'true' si esta accion requiere ser confirmada antes de ejecutarse
     */
    boolean requiereConfirmacion();
    
    /**
     * Devuelve el originante de esta accion (util para mostrarlo al notificar
     * sus efectos).
     */
    Object getOrigen();
    
    /**
     * Devuelve una descripcion textual de la accion
     */
     String toString();
}
