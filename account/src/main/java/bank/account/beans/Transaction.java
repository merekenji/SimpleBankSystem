package bank.account.beans;

import java.util.Date;

import bank.account.utilities.IDGenerator;

public class Transaction {
	
	private int transactionID;
	private String description;
	private double amount;
	private double balance;
	private Date date;
		
	public Transaction(String description, double amount, double balance, Date date) {
		transactionID = IDGenerator.generateTransactionID();
		this.description = description;
		this.amount = amount;
		this.balance = balance;
		this.date = date;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public String getDescription() {
		return description;
	}
	public double getAmount() {
		return amount;
	}
	public double getBalance() {
		return balance;
	}
	public Date getDate() {
		return date;
	}
	
}
