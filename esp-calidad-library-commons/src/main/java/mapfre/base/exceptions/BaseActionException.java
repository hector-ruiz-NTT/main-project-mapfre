
package mapfre.base.exceptions;

public class BaseActionException extends Exception {

    public BaseActionException(String message) {
        super(message);
    }

    public BaseActionException(String message, Throwable t) {
        super(message, t);
    }
}

