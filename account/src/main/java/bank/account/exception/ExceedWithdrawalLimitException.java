package bank.account.exception;

public class ExceedWithdrawalLimitException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExceedWithdrawalLimitException(String reason, double amt) {
		super(reason + ": " + amt);
	}
	
}
