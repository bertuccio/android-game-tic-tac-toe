
package es.uam.eps.dadm.adrian_lorenzo.logic;


public class JugadorOnline implements Jugador {
    
    private String nombre;
    

    
    /** Creates a new instance of JugadorAleatorio */
    public JugadorOnline(String nombre) {
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
//                System.out.println(nombre + ": Turno: "+evento.getDescripcion());
                
//                evento.getPartida().getTablero().cambiaTurno();
//                evento.getPartida().notificaTurno("te toca", null);
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
