package server.exceptions;


public class GameCreationException extends GenericException {
    public GameCreationException(String msg) {
        super(GameCreationException.class.getName(), msg);
    }

}
