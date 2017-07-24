package bank.account.exception;

public class NegativeAmountException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NegativeAmountException(String reason, double amt) {
		super(reason + ": " + amt);
	}
	
}
