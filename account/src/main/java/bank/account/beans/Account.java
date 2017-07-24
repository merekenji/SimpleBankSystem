package bank.account.beans;

import java.util.ArrayList;
import java.util.List;

import bank.account.utilities.IDGenerator;

public class Account {
	
	private int accountID;
	private double balance;
	private List<Transaction> transactions;
	
	public Account(double balance) {
		accountID = IDGenerator.generateAccountID();
		this.balance = balance;
		transactions = new ArrayList<Transaction>();
	}
	
	public Account(int accountID) {
		this.accountID = accountID;
	}
	
	public int getAccountID() {
		return accountID;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
}
