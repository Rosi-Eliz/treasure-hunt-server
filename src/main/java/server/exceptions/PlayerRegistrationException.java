package server.exceptions;

public class PlayerRegistrationException extends GenericException {

    public PlayerRegistrationException(String msg) {
        super(PlayerRegistrationException.class.getTypeName(), msg);
    }

}
