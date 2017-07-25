package bank.account.beans;

public class Customer {
	
	private String custName;
	
	public Customer(String name) {
		custName = name;
	}
	public void setCustName(String name) {
		custName = name;
	}
	public String getCustName() {
		return custName;
	}
	
}
