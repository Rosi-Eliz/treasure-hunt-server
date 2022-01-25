package server.exceptions;

public class TransferHalfMapException extends GenericException {
    public TransferHalfMapException(String msg) {
        super(TransferHalfMapException.class.getTypeName(), msg);
    }
}
