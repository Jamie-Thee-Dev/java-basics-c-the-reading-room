/**
 * Thrown when the library refuses a request, such as lending an item that is already
 * out. It is a checked exception so callers have to decide what to do about it.
 */
public class LendingException extends Exception {
    private static final long serialVersionUID = 1L;

    public LendingException(String message) {
        super(message);
    }
}
