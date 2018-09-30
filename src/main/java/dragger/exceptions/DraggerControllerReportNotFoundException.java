package dragger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class DraggerControllerReportNotFoundException extends DraggerControllerException {
	private static final long serialVersionUID = -1823822967496666164L;

	public DraggerControllerReportNotFoundException(String message) {
		super(message);
	}

	public DraggerControllerReportNotFoundException(String message, Throwable cause) {
		super(message);
	}
}
