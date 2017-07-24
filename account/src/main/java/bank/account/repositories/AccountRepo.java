package bank.account.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bank.account.beans.Account;
import bank.account.beans.Transaction;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.InsufficientBalanceException;
import bank.account.exception.NegativeAmountException;
import bank.account.exception.InvalidAccountException;

public class AccountRepo implements IAccountRepo {
	
	private List<Account> accounts;
	
	public AccountRepo() {
		accounts = new ArrayList<Account>();
	}

	public Account addAccount(double balance) throws InsufficientBalanceException {
		if(balance < 100.0) {
			throw new InsufficientBalanceException("Error - Insufficient bank balance", balance);
		}
		
		Account acc = new Account(balance);
		acc.addTransaction(new Transaction("Deposit", balance, balance, new Date()));
		accounts.add(acc);
		return acc;
	}

	public Account deposit(int accID, double amt) throws InvalidAccountException, NegativeAmountException {
		checkNegativeAmount(amt);
		
		Account account = findOne(accID);
		if(account != null) {
			account.setBalance(account.getBalance() + amt);
			account.addTransaction(new Transaction("Deposit", amt, account.getBalance(), new Date()));
		} else {
			throw new InvalidAccountException("Error - Invalid Account ID");
		}
		return account;
	}

	public Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException {
		checkNegativeAmount(amt);
		
		Account account = findOne(accID);
		if(account != null) {
			if(account.getBalance() - amt < 100) {
				throw new InsufficientBalanceException("Error - Insufficient Bank Balance", account.getBalance() - amt);
			} else {
				checkWithdrawalLimit(account, amt);
				account.setBalance(account.getBalance() - amt);
				account.addTransaction(new Transaction("Withdrawal", amt, account.getBalance(), new Date()));
			}
		} else {
			throw new InvalidAccountException("Error - Invalid Account ID");
		}
		return account;
	}

	public Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException {
		return null;
	}

	public Account showBalance(int accID) throws InvalidAccountException {
		Account acc = findOne(accID);
		if(acc == null) {
			throw new InvalidAccountException("Error - Invalid Account ID");
		}
		return acc;
	}

	public Account showLastTenTransactions(Account acc) throws InvalidAccountException {
		return null;
	}

	public Account showTransactionsInRange(Account acc, Date startDate, Date endDate) throws InvalidAccountException {
		return null;
	}
	
	private void checkNegativeAmount(double amt) throws NegativeAmountException {
		if(amt <= 0.0) {
			throw new NegativeAmountException("Error - Amount is negative", amt);
		}
	}
	
	private void checkWithdrawalLimit(Account acc, double amt) throws ExceedWithdrawalLimitException {
		double total = amt;
		Date date = new Date();
		for(Transaction transaction : acc.getTransactions()) {
			if(transaction.getDescription().equals("Withdrawal") && date.equals(transaction.getDate())) {
				total += transaction.getAmount();
			}
		}
		if(total > 1000) {
			throw new ExceedWithdrawalLimitException("Error - Withdrawal limit exceeded", total);
		}
	}
	
	private Account findOne(int accID) {
		for(Account a : accounts) {
			if(a.getAccountID() == accID) {
				return a;
			}
		}
		return null;
	}

}
