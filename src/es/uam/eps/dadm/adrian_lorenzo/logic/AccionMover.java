/*
 * AccionMover.java
 *
 * Created on March 2, 2005, 4:41 PM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;


/**
 * La accion mas basica de toda partida: mover de un sitio a otro. 
 *
 * @author mfreire
 */
public class AccionMover implements Accion {
    
    private Jugador j;
    private Movimiento m;
        
    /** Creates a new instance of AccionMover */
    public AccionMover(Jugador j, Movimiento m) {
        this.j = j;
        this.m = m;
    }
    
    /**
     * Lleva a cabo la accion sobre una partida
     */
    public void ejecuta(Partida p)  throws ExcepcionJuego {
        Tablero t = p.getTablero();
        int turno = t.getTurno();

        t.mueve(m);
        
        
        if (t.getEstado() != Tablero.EN_CURSO) {
            String notaFinal = "Fin de partida - ";
			if (t.getEstado() == Tablero.TABLAS)
				notaFinal += "todo queda en tablas";
			else
				notaFinal += "gana " + p.getJugador(t.getTurno()).getNombre();
         
            p.notificaCambio(notaFinal, this);
        }
        else
        	p.notificaCambio(j.getNombre() + " juega " + m.toString(), this);
        if (turno != p.getTablero().getTurno()) {
            p.notificaTurno("Te toca", this);
        }
        	
    }
    
    /**
     * Devuelve 'true' si esta accion requiere ser confirmada antes de ejecutarse
     */
    public boolean requiereConfirmacion() {
        return false;
    }
    
    /**
     * Devuelve el originante de esta accion (util para mostrarlo al notificar
     * sus efectos).
     */
    public Object getOrigen() {
        return j;
    }
    
    /**
     * Devuelve una descripcion textual de la accion
     */
     public String toString() {
         return "movimiento: " + m;
     }  
}
