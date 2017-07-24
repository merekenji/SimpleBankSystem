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
	
	private static final String INSUFFICIENT_BALANCE = "Error - Insufficient Bank Balance";
	private static final String INVALID_ACCOUNT = "Error - Invalid Account";
	private static final String WITHDRAWAL_LIMIT = "Error - Exceed Withdrawal Limit";
	private static final String NEGATIVE_AMOUNT = "Error - Amount Is Negative";
			
	private List<Account> accounts;
	
	public AccountRepo() {
		accounts = new ArrayList<>();
	}

	public Account addAccount(double balance) throws InsufficientBalanceException {
		if(balance < 100.0) {
			throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, balance);
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
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return account;
	}

	public Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException {
		checkNegativeAmount(amt);
		
		Account account = findOne(accID);
		if(account != null) {
			if(account.getBalance() - amt < 100) {
				throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, account.getBalance() - amt);
			} else {
				checkWithdrawalLimit(account, amt);
				account.setBalance(account.getBalance() - amt);
				account.addTransaction(new Transaction("Withdrawal", amt, account.getBalance(), new Date()));
			}
		} else {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return account;
	}

	public Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException, NegativeAmountException {
		checkNegativeAmount(amt);
		
		Account sender = findOne(senderID);
		Account receiver = findOne(receiverID);
		if(sender == null || receiver == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		
		return null;
	}

	public Account showBalance(int accID) throws InvalidAccountException {
		Account acc = findOne(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return acc;
	}

	public Account showLastTenTransactions(int accID) throws InvalidAccountException {
		Account acc = findOne(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		if(acc.getTransactions().size() <= 10) {
			return new Account(acc.getTransactions());
		} else {
			List<Transaction> transactions = acc.getTransactions().subList(acc.getTransactions().size()-10, acc.getTransactions().size()-1);
			return new Account(transactions);
		}
	}

	public Account showTransactionsInRange(int accID, Date startDate, Date endDate) throws InvalidAccountException {
		Account acc = findOne(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return null;
	}
	
	private void checkNegativeAmount(double amt) throws NegativeAmountException {
		if(amt <= 0.0) {
			throw new NegativeAmountException(NEGATIVE_AMOUNT, amt);
		}
	}
	
	private void checkWithdrawalLimit(Account acc, double amt) throws ExceedWithdrawalLimitException {
		double total = amt;
		Date date = new Date();
		for(Transaction transaction : acc.getTransactions()) {
			if("Withdrawal".equals(transaction.getDescription()) && date.equals(transaction.getDate())) {
				total += transaction.getAmount();
			}
		}
		if(total > 1000) {
			throw new ExceedWithdrawalLimitException(WITHDRAWAL_LIMIT, total);
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
