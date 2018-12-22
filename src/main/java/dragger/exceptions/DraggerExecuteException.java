package dragger.exceptions;

public class DraggerExecuteException extends DraggerException {
	private static final long serialVersionUID = 1910988997967894109L;

	public DraggerExecuteException(String message) {
		super(message);
	}

	public DraggerExecuteException(String message, Throwable cause) {
		super(message);
	}
}
