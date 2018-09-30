package dragger.exceptions;

public class DraggerControllerReportNotFoundException extends DraggerControllerException {
	private static final long serialVersionUID = -1823822967496666164L;

	public DraggerControllerReportNotFoundException(String message) {
		super(message);
	}

	public DraggerControllerReportNotFoundException(String message, Throwable cause) {
		super(message);
	}
}
