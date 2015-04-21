/*
 * EventoPartida.java
 *
 * Created on March 2, 2005, 11:46 AM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * Un evento usado para notificar a los suscriptores a una partida de todo
 * lo que sucede en la misma.
 *
 * @author mfreire
 *
 *
 *
 */
public class Evento {
    
    // constantes de tipos de evento
    
    /** para cambios genericos al estado (ej.: alguien mueve, pero no te toca) */
    public static final int EVENTO_CAMBIO = 1;    
    /** para notificar que te toca mover */
    public static final int EVENTO_TURNO = 2;    
    /** para pedir una confirmacion */
    public static final int EVENTO_CONFIRMA = 3;
    /** para notificar errores en peticiones (ej.: alguien no confirma tu accion) */
    public static final int EVENTO_ERROR = 4;    
    
    // variables de instancia
    
    /** tipo de evento */
    private int tipo;
    /** descripcion del evento */
    private String descripcion;
    /** partida actual (de la cual se pueden consultar tablero, jugadores, ...) */
    private Partida partida;
    /** la accion que ocasiono este evento (puede ser null) */
    private Accion accion;
    
    /** Creates a new instance of EventoPartida */
    public Evento(int tipo, String descripcion, 
            Partida partida, Accion accionCausa) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.partida = partida;
        this.accion = accionCausa;        
    }

    public int getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Partida getPartida() {
        return partida;
    }
    
    public Accion getCausa() {
        return accion;
    }
}
