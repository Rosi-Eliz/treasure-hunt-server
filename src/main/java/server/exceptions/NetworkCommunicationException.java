package server.exceptions;

public class NetworkCommunicationException extends Exception{

    public NetworkCommunicationException(String msg) {
        super(msg);
    }

    public NetworkCommunicationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}