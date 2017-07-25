package bank.account.beans;

import java.util.ArrayList;
import java.util.List;

import bank.account.utilities.IDGenerator;

public class Account {
	
	private int accountID;
	private double balance;
	private List<Transaction> transactions;
	private Customer customer;
	
	public Account(Customer customer, double balance) {
		accountID = IDGenerator.generateAccountID();
		this.customer = customer;
		this.balance = balance;
		transactions = new ArrayList<>();
	}
	public Account(List<Transaction> transactions) {
		this.transactions = transactions;
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
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Customer getCustomer() {
		return customer;
	}
	
}