package newparser;

public class AndroidRecorderException extends Exception {
    Exception exception;
    AndroidRecorderException(Exception exception){
       this.exception = exception;
    }
}
