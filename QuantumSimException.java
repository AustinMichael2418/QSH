/*
 * This class simply enables a custom error by extending RuntimeException
 */

public class QuantumSimException extends RuntimeException {
    public QuantumSimException(String error) {
        // The constructor simply calls the superclass constructor with the
        // one-argument constructor.
        super(error);
    }
}
