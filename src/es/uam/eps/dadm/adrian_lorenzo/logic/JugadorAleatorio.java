/*
 * JugadorAleatorio.java
 *
 * Created on March 2, 2005, 11:48 AM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 *
 * @author mfreire
 */
public class JugadorAleatorio implements Jugador {
    
    private String nombre;
    private static int numAleatorios = 0;
    
    /** Constructor por defecto */
    public JugadorAleatorio() {
        this("Aleatorio "+ (++numAleatorios));
    }
    
    /** Creates a new instance of JugadorAleatorio */
    public JugadorAleatorio(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Recibe una notificacion de un cambio en la partida
     */
    public void onCambioEnPartida(Evento evento) {
        switch (evento.getTipo()) {
            case Evento.EVENTO_CAMBIO:
                System.out.println(nombre + ": Cambio: "
                        + evento.getDescripcion());
                System.out.println(nombre + ": Tablero es:\n" 
                        + evento.getPartida().getTablero());
                break;
                
            case Evento.EVENTO_CONFIRMA:
                System.out.println(nombre + ": Confirmacion: "
                        + evento.getDescripcion());
                
                // este jugador confirma al azar
                try {
                    evento.getPartida().confirmaAccion(
                            this, evento.getCausa(), (Math.random() > .5));
                }
                catch(Exception e) {
                	
                }
                break;
                
            case Evento.EVENTO_TURNO:
                System.out.println(nombre + ": Turno: "+evento.getDescripcion());
                
                // jugar al azar, que gran idea
                Tablero t = evento.getPartida().getTablero();
                int r = (int)(Math.random() * t.movimientosValidos().size());
                try {
                	evento.getPartida().realizaAccion(new AccionMover(
                			this, (Movimiento)t.movimientosValidos().get(r)));
                }
                catch(Exception e) {
                	System.out.println("WTF "+e.getMessage());
                }
                break;
        }
    }    
    
    /**
     * Devuelve el nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Este jugador come *todos* los juegos
     */
    public boolean puedeJugar(Tablero tablero)  {
        return true;
    }    
}
