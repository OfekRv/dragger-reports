package dragger.exceptions;

public class DraggerException extends Exception {
	private static final long serialVersionUID = 1567149688147983150L;

	public DraggerException(String message) {
		super(message);
	}

	public DraggerException(String message, Throwable cause) {
		super(message);
	}
}
