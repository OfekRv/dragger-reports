package dragger.exceptions;

public class DraggerControllerException extends DraggerException {
	private static final long serialVersionUID = -1823822967496666164L;

	public DraggerControllerException(String message) {
		super(message);
	}

	public DraggerControllerException(String message, Throwable cause) {
		super(message);
	}
}
