package dragger.exceptions;

public class DraggerConnectionException extends DraggerException {
	private static final long serialVersionUID = 1910988997967894109L;

	public DraggerConnectionException(String message) {
		super(message);
	}

	public DraggerConnectionException(String message, Throwable cause) {
		super(message);
	}
}
