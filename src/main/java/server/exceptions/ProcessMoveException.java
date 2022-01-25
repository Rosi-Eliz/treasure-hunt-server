package server.exceptions;

public class ProcessMoveException extends GenericException {
    public ProcessMoveException(String msg) {
        super(ProcessMoveException.class.getTypeName(), msg);
    }
}