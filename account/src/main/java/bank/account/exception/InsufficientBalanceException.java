package bank.account.exception;

public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException(String reason, double balance) {
		super(reason + "; " + balance);
	}
	
}
