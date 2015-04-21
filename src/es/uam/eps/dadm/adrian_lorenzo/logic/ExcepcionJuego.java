/*
 * ExcepcionJuego.java
 *
 * Created on February 11, 2005, 3:33 PM
 */

package es.uam.eps.dadm.adrian_lorenzo.logic;

/**
 * Base de las excepciones para juegos
 */
public class ExcepcionJuego extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor con descripcion
     * @param descripcion un mensaje con lo que ha ocurrido
     */
    public ExcepcionJuego(String descripcion) {
        super(descripcion);
    }
        
    /**
     * Constructor con descripcion y una causa anidada
     * @param descripcion un mensaje con lo que ha ocurrido
     * @param causa una excepcion que causo a esta
     */
    public ExcepcionJuego(String descripcion, Exception causa) {
        super(descripcion, causa);
    }
}
