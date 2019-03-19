package crypto;

/**
 * 11.03.2010   Created.
 *
 * @author Yerzhan Tulepov
 */
final class HMACException extends Exception {

    /**
     * Creates a new instance of <code>HMACException</code> without detail message.
     */
    HMACException() {
    }


    /**
     * Constructs an instance of <code>HMACException</code> with the specified detail message.
     * @param msg the detail message.
     */
    HMACException(String msg) {
        super(msg);
    }
}
