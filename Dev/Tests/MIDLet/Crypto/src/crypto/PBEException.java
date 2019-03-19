package crypto;

/**
 * 11.03.2010   Created.
 *
 * @author Yerzhan Tulepov.
 */
final class PBEException extends Exception {

    /**
     * Creates a new instance of <code>PBEException</code> without detail message.
     */
    PBEException() {
    }


    /**
     * Constructs an instance of <code>PBEException</code> with the specified detail message.
     * @param msg the detail message.
     */
    PBEException(String msg) {
        super(msg);
    }
}
