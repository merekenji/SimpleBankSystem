package bank.account.utilities;

public class IDGenerator {
	private static int accountID = 9999;
	private static int transactionID = 19999;
	
	private IDGenerator() {
		//to prevent creation of IDGenerator class
	}
	
	public static int generateAccountID() {
		return ++accountID;
	}
	
	public static int generateTransactionID() {
		return ++transactionID;
	}
}
