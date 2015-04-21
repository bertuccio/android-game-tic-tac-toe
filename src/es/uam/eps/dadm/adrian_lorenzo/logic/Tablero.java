/*
 * Tablero.java
 *
 * Created on February 14, 2004, 9:13 PM
 *
 * (C) 2003-2004 Escuela Polit??cnica Superior, Universidad Aut??noma de Madrid
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

import java.util.*;

/**
 * Representa un tablero de un juego multijugador sencillo
 */
public abstract class Tablero {           
       
    /** partida en curso */
    public static final int EN_CURSO = 1;
    
    /** partida finalizada; el jugador que haya ganado esta en 'turno' */
    public static final int FINALIZADA = 2;
    
    /** partida ha acabado en tablas */
    public static final int TABLAS = 3;
    
    /** por defecto, solo para 2 jugadores (las subclases pueden cambiarlo) */
    protected int numJugadores = 2;
    
    /** representa el jugador al que le toca mover (numeros del 0 a numJugadores) */
    protected int turno;
    
    /** devuelve el estado de la partida */
    protected int estado;

    /** Numero de jugadas hechas en la partida */
	protected int numJugadas = 0;
         
    /** Ultimo movimiento realizado */
	protected Movimiento ultimoMovimiento;
         
    /**
     * Devuelve el numero del jugador al que le toca mover
     * @return el turno del jugador
     */
    public int getTurno() {
        return turno;
    }          

    /**
     * Devuelve el estado de la partida
     */
    public int getEstado() {
        return estado;
    }                
    
    /**
     * Devuelve el numero de jugadores
     */
    public int getNumJugadores() {
        return numJugadores;
    }    

    /**
     * 
     * @return numero de jugadas
     */
	public int getNumJugadas() {
		return numJugadas;
	}

    /**
     * 
     * @return ultimoMovimiento
     */
	public Movimiento getUltimoMovimiento() {
		return ultimoMovimiento;
	}

	/**
     * Cambia el turno
     */
    public void cambiaTurno() {
        turno = (turno + 1) % numJugadores;
        numJugadas++;
    }
    
    /** 
     * Ejecuta un movimiento en el tablero
     * @param m movimiento a llevar a cabo
     */
    public abstract void mueve(Movimiento m) throws ExcepcionJuego;
            
    /**
     * Devuelve 'true' si el movimiento especificado es valido en este tablero
     * @return true si el movimiento es valido
     */
    public abstract boolean esValido(Movimiento m);

    /**
     * Genera todos los movimientos v??lidos para el jugador actual en este tablero
     * @return un ArrayList que contiene los movimientos validos
     */    
    public abstract ArrayList<Movimiento> movimientosValidos();

    /** 
     * Crea una cadena que contiene una representacion interna del tablero. 
     * Esta representacion se debe poder usar para inicializar un tablero a un
     * estado dado.
     * @return la cadena pedida. 
     */
    public abstract String tableroToString();
    
    /** 
     * Crea una cadena que contiene una representacion interna del tablero. 
     * Esta representacion se debe poder usar para inicializar un tablero a un
     * estado dado.
     * @return la cadena pedida. 
     */
    public abstract void stringToTablero(String cadena) throws ExcepcionJuego;
    
    /** 
     * Devuelve una representacion algo mas amigable que la encontrada en 
     * toString.
     * @return la cadena pedida. 
     */
    public abstract String toString();
    
    /**
     * Pone el tablero en su posicion inicial
     * @return si se ha podido resetear el tablero. 
     */
    public boolean reset() {
    	numJugadas = 0;
    	return true;
    }
}
