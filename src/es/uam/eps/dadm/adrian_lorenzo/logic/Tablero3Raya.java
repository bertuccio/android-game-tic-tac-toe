package es.uam.eps.dadm.adrian_lorenzo.logic;

import java.util.ArrayList;

/**
 * 
 * @author Adrián Lorenzo
 */
public class Tablero3Raya extends Tablero{

    public static final int MAX_SIZE = 3;
    private static final char VACIO = '-';
    public static final char FICHA_X = 'X';
    public static final char FICHA_O = 'O';
    private char[][] tablero = new char[MAX_SIZE][MAX_SIZE];
    private static final String DELIMS = "@";
    private static final int NUM_ATTR = 6;

    public Tablero3Raya() {

        super();
        for(int i=0; i<MAX_SIZE; i++){
            for(int j=0; j<MAX_SIZE; j++){
                tablero[i][j] = VACIO;
            }   
        }
        estado = EN_CURSO;
    }
    /**
     * 
     * Comprueba si se ha ganado a las tres en raya. Por fila,
     * por columna y por diagonales.
     * 
     * @param fila
     * @param columna
     * @param ficha
     * @return 
     */
    private boolean compruebaGanar(int fila, int columna, int ficha){


    	//check end conditions

    	//check col
    	for(int i = 0; i < MAX_SIZE; i++){
    		if(tablero[fila][i] != ficha)
    			break;
    		if(i == MAX_SIZE-1){
    			return true;
    		}
    	}

    	//check row
    	for(int i = 0; i < MAX_SIZE; i++){
    		if(tablero[i][columna] != ficha)
    			break;
    		if(i == MAX_SIZE-1){
    			return true;
    		}
    	}

    	//check diag
    	if(fila == columna){
    		//we're on a diagonal
    		for(int i = 0; i < MAX_SIZE; i++){
    			if(tablero[i][i] != ficha)
    				break;
    			if(i == MAX_SIZE-1){
    				return true;
    			}
    		}
    	}

        //check anti diag
    	for(int i = 0 ;i<MAX_SIZE; i++){
    		if(tablero[i][(MAX_SIZE-1)-i] != ficha)
    			break;
    		if(i == MAX_SIZE-1){
    			return true;
    		}
    	}
    	
    	return false;
    }
    /**
     * 
     * Comprueba si se ha finalizado en tablas (se han realizado todos los movimientos posibles,
     * que son el número de celdas del tablero).
     * 
     * @param numJugadas
     * @return 
     */
    private boolean compruebaTablas(int numJugadas){
    	
    	//check draw
    	return (numJugadas == (Math.pow(MAX_SIZE,2) - 1));
    }
    /**
     * 
     */
    @Override
    public void mueve(Movimiento m) throws ExcepcionJuego {

        if(esValido(m)){

            int fila = ((Movimiento3Raya) m).getFila();
            int columna = ((Movimiento3Raya) m).getColumna();


            if(getTurno() == 0)
                tablero[fila][columna] = FICHA_O;  
            else
               tablero[fila][columna] = FICHA_X;    

            ultimoMovimiento = m;
            
            

            if(compruebaGanar(fila,columna,tablero[fila][columna])){
                estado = FINALIZADA;
                return;
            }
            else if(compruebaTablas(this.numJugadas)){
                estado = TABLAS;
                return;
            }
            else
                this.cambiaTurno();
        }
        else{
            throw new ExcepcionMovimientoInvalido();
        }
        
    }
    /**
     * 
     * @param m
     * @return 
     */
    @Override
    public boolean esValido(Movimiento m) {
        
        if (!( m instanceof Movimiento3Raya))  
            return false;
 
        int fila = ((Movimiento3Raya) m).getFila();
        int columna = ((Movimiento3Raya) m).getColumna();
        
        if(fila >= MAX_SIZE || fila < 0 || columna >= MAX_SIZE || 
        		columna < 0)
        	return false;

        return (tablero[fila][columna] == VACIO);

    }
    /**
     * 
     * @return 
     */
    @Override
    public ArrayList<Movimiento> movimientosValidos() {

        ArrayList<Movimiento> movimientosValidos = new ArrayList<Movimiento>();

        for(int i=0; i<MAX_SIZE; i++){
            for(int j=0; j<MAX_SIZE; j++){
                if(tablero[i][j] == VACIO){
                    movimientosValidos.add(new Movimiento3Raya(i,j));
                }
            }   
        }

        return movimientosValidos;
    }
    /**
     * 
     * @return 
     */
    @Override
    public String tableroToString() {

        String string = turno +DELIMS + estado + DELIMS + 
        		numJugadas + DELIMS;
        if(ultimoMovimiento == null)
        	string += -1 +"DELIMS" + -1 + DELIMS;
        else
        	string += ultimoMovimiento.toString() + DELIMS;

        for(int i=0; i<MAX_SIZE; i++){

                for(int j=0; j<MAX_SIZE; j++)
                        string += tablero[i][j]; 

        }
        return string;
    }
    /**
     * 
     * @param cadena
     * @throws ExcepcionJuego 
     */
    @Override
    public void stringToTablero(String cadena) throws ExcepcionJuego {

        if(cadena == null || cadena.isEmpty())
			throw new ExcepcionJuego("Imposible cargar el tablero");

    
		Integer turno = null;
		Integer estado =  null;
		Integer numJugadas = null;
		Integer fila = null;
		Integer columna = null;
		
		String[] tokens = cadena.split(DELIMS);
		
		if(tokens.length != NUM_ATTR)
			throw new ExcepcionJuego("Imposible cargar el tablero");

		
		turno = Integer.parseInt(tokens[0]);
		estado = Integer.parseInt(tokens[1]);                  
		numJugadas = Integer.parseInt(tokens[2]);                 
		fila = Integer.parseInt(tokens[3]);                 
		columna = Integer.parseInt(tokens[4]);              	
		
		if(tokens[5].length() != Math.pow(MAX_SIZE,2)){
			throw new ExcepcionJuego("Imposible cargar la partida");
		}
						
		int z = 0;
		
		for(int j = 0; j<MAX_SIZE; j++){
			for(int k = 0; k<MAX_SIZE; k++){

				tablero[j][k] = tokens[5].charAt(z);
				z++;
			}
		}

		if( turno == null || estado== null || numJugadas== null
			|| fila== null ||columna== null){
			
			throw new ExcepcionJuego("Imposible cargar el tablero");
		}
		
		this.turno = turno;
		this.estado =  estado;
		this.numJugadas = numJugadas;
		
		if(fila == -1 && columna == -1)
			this.ultimoMovimiento = null;
		else
			this.ultimoMovimiento = new Movimiento3Raya(fila,columna);
  
    }
    
    /**
     * Obtiene la celda correspondiente a las coordenadas introducidas
     * como parámetro.
     * 
     * @param x
     * @param y
     * @return
     */
    public char getCelda(int x, int y){
    	return tablero[y][x];
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {

        String out = "";

        out += "__|  0 | 1 | 2 " +"\n";

        for(int i=0; i<MAX_SIZE; i++){

            out += i+ " | ";
            for(int j=0; j<MAX_SIZE; j++){
                out +="[" + String.valueOf(tablero[i][j]) + "] " ;
            }

            out += "\n";
        }

        return out;
    }
}
