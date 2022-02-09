package newparser;

public class ImplementationNotFoundException extends Exception {
    String message;
    public ImplementationNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
