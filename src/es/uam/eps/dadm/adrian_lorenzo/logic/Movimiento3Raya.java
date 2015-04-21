package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * 
 * @author Adrián Lorenzo
 */
public class Movimiento3Raya extends Movimiento{

    private int fila;
    private int columna;

    public Movimiento3Raya(int fila,int columna) {

            super();
            this.fila = fila;
            this.columna = columna;
    }

    public int getFila() {
            return fila;
    }

    public int getColumna() {
            return columna;
    }

    @Override
    public String toString() {

            return fila + "@" +columna;
    }

    @Override
	public boolean equals(Object o) {

		if (!(o instanceof Movimiento3Raya))
			 return false;

		 else
			 return this.toString().equals(o.toString());

	}

}
