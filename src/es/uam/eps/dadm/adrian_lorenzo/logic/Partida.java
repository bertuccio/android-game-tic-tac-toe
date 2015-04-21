/*
 * Partida.java
 *
 * Created on March 2, 2005, 11:46 AM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

import java.util.*;


/**
 * Una Partida permite a varios Jugadores jugar sobre un Tablero del juego
 * que sea.
 * Los jugadores actuan sobre la partida mediante acciones (patron 'comando'), y
 * son notificados de los cambios que sufre la partida suscribiendose a eventos
 * de tipo 'Evento' (patron 'publicacion-suscripcion').
 * Las acciones pueden ser 'confirmadas', en cuyo caso solamente se llevara a 
 * cabo si todos los jugadores llaman al metodo confirmaAccion y contestan 
 * afirmativamente. 
 * Una partida no puede tener mas de una accion en proceso de confirmacion.
 *
 * @author mfreire
 */
public class Partida {
    
    /** todos los jugadores que estan jugando en este momento */
    private ArrayList<Jugador> jugadores;
    
    /** tablero sobre el que estan jugando */
    private Tablero tablero;
    
    /** accion que esta en proceso de confirmacion; puede ser null */
    private Accion porConfirmar = null; 
    
    /** jugadores que han dado su consentimiento a la accion en confirmacion */
    private boolean[] confirmaciones;
    
    /** todos los 'PartidaListener' no-jugadores registrados */
    private ArrayList<PartidaListener> observadores;        
    
    /** 
     * Crea una nueva partida, notificando a los jugadores de su estado, y 
     * avisando al jugador actual de que le toca mover 
     */
    public Partida(Tablero tablero, ArrayList<Jugador> jugadores) {
        observadores = new ArrayList<PartidaListener>();
        this.tablero = tablero;
        this.jugadores = jugadores;
    }
    
    /**
     * Comienza una partida
     */
    public void comienzaPartida(Tablero tablero, ArrayList<Jugador> jugadores) {
        this.tablero = tablero;
        this.jugadores = jugadores;
        comienzaPartida();
    }
        
    /**
     * Comienza una partida
     */
    public void comienzaPartida() {
        
        // comprueba argumentos
        if (jugadores.size() != tablero.getNumJugadores()) {
            throw new IllegalArgumentException(
                    "Faltan o sobran jugadores en este tablero");
        }
        if (tablero.estado != Tablero.EN_CURSO) {
            throw new IllegalArgumentException(
                    "Este tablero ya tiene una posicion final: no se puede empezar");
        }

        // envia a todos los jugadores el estado actual
        notificaCambio("La partida va a comenzar", null);
        
        // envia, al jugador al que le toca mover, un evento de tipo turno
        notificaTurno("Te toca empezar", null);
    
     
    }

    // Para registrar y dar de baja observadores
    
    /**
     * Da de alta un observador (parecido a un jugador, pero no juega)
     */
    public void addObservador(PartidaListener observador) {
        if ( ! observadores.contains(observador)) {
            observadores.add(observador);        
        }
    }
    
    /**
     * Da de alta un observador (parecido a un jugador, pero no juega)
     */
    public void removeObservador(PartidaListener observador) {
        observadores.remove(observador);        
    }        
    
    // Metodos de utilidad para notificar cosas a los jugadores y observadores
    
    /**
     * Notifica un cambio de estado a todos los jugadores y observadores
     */
    public void notificaCambio(String descripcion, Accion accion) {
        Evento e = new Evento(Evento.EVENTO_CAMBIO, 
                descripcion, this, accion);
        
        for (Iterator<Jugador> it=jugadores.iterator(); it.hasNext(); ) {
            Jugador j = (Jugador)it.next();
                j.onCambioEnPartida(e);
        }
        
        for (Iterator<PartidaListener> it=observadores.iterator(); it.hasNext(); ) {
            PartidaListener s = (PartidaListener)it.next();
                s.onCambioEnPartida(e);
        }        
    }
    
    /**
     * Notifica el cambio de turno al jugador al que le toca mover
     */
    public void notificaTurno(String descripcion, Accion accion) {
        Jugador j = (Jugador)jugadores.get(tablero.getTurno());
        Evento e = new Evento(Evento.EVENTO_TURNO,
                descripcion, this, accion);
        j.onCambioEnPartida(e);
    }
    
    /**
     * Notifica un error en una accion a un jugador (el jugador debe constar
     * como originario de la accion)
     */
    public void notificaError(String descripcion, Accion accion) {        
        Evento e = new Evento(Evento.EVENTO_ERROR,
                descripcion, this, accion);
        ((Jugador)accion.getOrigen()).onCambioEnPartida(e);
    }
    
    // Metodos de acceso a variables de instancia
    
    /**
     * Devuelve el tablero actual (solo para consultas - los jugadores no
     * deben usarlo para mover directamente).
     */
    public Tablero getTablero() {
        return tablero;
    }
    
    /**
     * Devuelve el jugador i-esimo
     */
    public Jugador getJugador(int i) {
        return (Jugador)jugadores.get(i);
    }
    
    // Metodos generales
    
    /**
     * Procesa una accion de un jugador
     */
    public void realizaAccion(Accion a)  throws ExcepcionJuego {
        Evento evento;
        
        if (a.requiereConfirmacion()) {
            if (porConfirmar != null) {
                // solo puede haber una accion pendiente de confirmacion en un momento dado...
                notificaError("Ya hay un evento en fase de confirmacion", a);
                return;
            }
            porConfirmar = a;
            confirmaciones = new boolean[jugadores.size()];
            
            // si el que la propone es un jugador, se asume que esta de acuerdo
            int jugadorPropone = jugadores.indexOf(a.getOrigen());
            if (jugadorPropone != -1) {
                confirmaciones[jugadorPropone] = true;
            }
            
            // solicita confirmacion a todos los restantes
            evento = new Evento(Evento.EVENTO_CONFIRMA,
                    "Confirmacion requerida para accion", this, a);
            for (Iterator<Jugador> it=jugadores.iterator(); it.hasNext(); ) {
                Jugador j = (Jugador)it.next();
                if (jugadorPropone != -1 && j != jugadores.get(jugadorPropone)) {
                    j.onCambioEnPartida(evento);
                }
            }         
        }
        else {
            a.ejecuta(this);            
        }        
    }  
    
    /**
     * Confirma la accion actual para un jugador dado; si el voto es negativo,
     * la accion queda totalmente cancelada. Devuelve 'true' si, contando este
     * voto, la accion ha sido aprobada por todos los jugadores.
     */
    public void confirmaAccion(Jugador j, Accion a, boolean voto)  throws ExcepcionJuego {        
        int posJugador = jugadores.indexOf(j);        
        if (a == porConfirmar && posJugador != -1) {
            if (voto == true) {
                // confirmada: lo apunta, y mira a ver si ya estan todos
                confirmaciones[posJugador] = true;
                boolean ok = true;
                for (int i=0; i<confirmaciones.length; i++) {
                    if (confirmaciones[i] != true) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    // luz verde: todos de acuerdo
                    porConfirmar.ejecuta(this);
                    porConfirmar = null;
                    confirmaciones = null;
                }
            }
            else {
                // cancelada: notifica al que la propuso (si era un jugador)
                if (jugadores.contains(porConfirmar.getOrigen())) {
                    notificaError("El jugador "+j.getNombre()+" ha votado en contra",
                            porConfirmar);
                }
                
                // cancela la accion
                porConfirmar = null;
                confirmaciones = null;
            }
        }
    }           


	public void mueve(Jugador jugador, Movimiento mov)  throws ExcepcionJuego {
        realizaAccion(new AccionMover(jugador, mov));                                                
	}

	/**
     * Punto de entrada (en modo texto):
     * Acepta argumentos de la forma 
     *  <nombreJuego> <jugador1> ... <jugadorN>
     * Los nombres de los jugadores y de los tableros deben estar descritos
     * en un fichero de configuracion apropiado.
     */
    public static void main(String[] args) throws Exception {
        
        // comprueba argumentos (minimo 3)
        /*if (args.length < 3) {
            System.err.println(
                    "Faltan argumentos. Uso:\n" +
                    "java " + Partida.class.getName() + 
                        "<nombreJuego> <jugador1> ... <jugadorN>\n");
            return;
        }
        
        try {
            // lee el fichero de propiedades
            Properties props = new Properties();
            ClassLoader cl = Partida.class.getClassLoader();
            props.load(cl.getResourceAsStream(nombreFicheroPropiedades));

            // instancia tablero
            String nombreClase = (String)props.get(args[0]);
            Tablero t = (Tablero)Partida.class.forName(nombreClase).newInstance();

            // instancia jugadores
            ArrayList aj = new ArrayList();
            for (int i=1; i<args.length; i++) {
                nombreClase = (String)props.get(args[i]);
                aj.add(Partida.class.forName(nombreClase).newInstance());
            }

            // lanza la partida
            Partida p = new Partida(t, aj);
        }
        catch (Exception e) {
            System.err.println("Error detectado - partida finalizada");
            System.err.println("El error es:");
            e.printStackTrace();
        }*/
    }
}
