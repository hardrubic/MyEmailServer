package server.smtp;

public class SMTPException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMsg;

	public SMTPException(String errorMsg) {
		super();
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
