package es.uam.eps.dadm.adrian_lorenzo.logic;

import java.util.Scanner;

public class JugadorHumano implements Jugador {

    private String nombre;
    private static int numHumanos = 0;
	
	
	public JugadorHumano(String string) {
		
		nombre = string;
		numHumanos++;
	}

	@Override
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
			    
			    boolean finTurno;
			    			    
			    do{
			    	try {
			    		
				    	System.out.println("Nuevo movimiento:");
						Scanner in = new Scanner(System.in); 
						System.out.print("Introduce fila:  ");
						int fila = in.nextInt();
						System.out.print("Introduce columna:  ");
						int columna = in.nextInt();
					
				    	evento.getPartida().realizaAccion(new AccionMover(
				    			this, new Movimiento3Raya(fila,columna)));
				    	break;
				    }
				    catch(Exception e) {
				    	
				    	System.out.println(" >"+e.getMessage());
				    }
				    
			    }while(true);
			    break;
		}
	}  

	@Override
	public String getNombre() {
		
		return nombre;
	}

	@Override
	public boolean puedeJugar(Tablero tablero) {
		
		return true;
	}

}
