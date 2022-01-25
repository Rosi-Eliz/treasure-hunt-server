package server.exceptions;

public class CheckGameStatusException extends GenericException{
    public CheckGameStatusException(String msg) {
        super(CheckGameStatusException.class.getTypeName(), msg);
    }
}
