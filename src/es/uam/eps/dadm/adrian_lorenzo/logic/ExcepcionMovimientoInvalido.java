package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * 
 * @author Adrián Lorenzo
 */
public class ExcepcionMovimientoInvalido extends ExcepcionJuego{

	
    private final static String mensaje = "Movimiento invalido";

    public ExcepcionMovimientoInvalido() {
            super(mensaje);
    }

    public ExcepcionMovimientoInvalido(Exception causa) {
        super(mensaje, causa);
    }
	private static final long serialVersionUID = 1L;

}
