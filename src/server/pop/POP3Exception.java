package server.pop;

public class POP3Exception extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMsg;

	public POP3Exception(String errorMsg) {
		super();
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
