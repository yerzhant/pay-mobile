package crypto;

/**
 * 11.03.2010   Created.
 *
 * @author Yerzhan Tulepov
 */
final class SHA256Exception extends Exception {

    /**
     * Creates a new instance of <code>SHA256Exception</code> without detail message.
     */
    SHA256Exception() {
    }


    /**
     * Constructs an instance of <code>SHA256Exception</code> with the specified detail message.
     * @param msg the detail message.
     */
    SHA256Exception(String msg) {
        super(msg);
    }
}
