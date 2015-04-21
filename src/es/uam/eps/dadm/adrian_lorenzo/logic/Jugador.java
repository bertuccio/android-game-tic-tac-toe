/*
 * Jugador.java
 *
 * Created on March 2, 2005, 11:46 AM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * Un jugador es un subscriptor que, ademas, interacciona con la partida 
 * (tambien se podrian incorporar "mirones", que no cambiarian nada).
 *
 * @author mfreire
 */
public interface Jugador extends PartidaListener {
    
    /**
     * Devuelve el nombre del jugador
     */
    String getNombre();
    
    /**
     * Devuelve 'true' si este jugador sabe jugar al juego indicado
     */
    boolean puedeJugar(Tablero tablero);        
}
