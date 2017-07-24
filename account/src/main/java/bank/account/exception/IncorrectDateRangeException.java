package bank.account.exception;

public class IncorrectDateRangeException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncorrectDateRangeException(String reason) {
		super(reason);
	}
	
}
