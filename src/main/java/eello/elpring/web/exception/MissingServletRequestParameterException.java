package eello.elpring.web.exception;

public class MissingServletRequestParameterException extends MissingRequestValueException{

    public MissingServletRequestParameterException(String message) {
        super(message);
    }
}
