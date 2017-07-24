package bank.account.repositories;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bank.account.beans.Account;
import bank.account.beans.Transaction;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.IncorrectDateRangeException;
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

	public Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException, ParseException {
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

	public Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		checkNegativeAmount(amt);
		
		Account sender = findOne(senderID);
		Account receiver = findOne(receiverID);
		if(sender == null || receiver == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		if(sender.getBalance() - amt < 100) {
			throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, sender.getBalance() - amt);
		}
		sender.setBalance(sender.getBalance() - amt);
		sender.addTransaction(new Transaction("Transferred to " + receiver.getAccountID(), amt, sender.getBalance(), new Date()));
		receiver.setBalance(receiver.getBalance() + amt);
		receiver.addTransaction(new Transaction("Transferred from " + sender.getAccountID(), amt, receiver.getBalance(), new Date()));
		return sender;
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
			List<Transaction> transactions = acc.getTransactions().subList(acc.getTransactions().size()-11, acc.getTransactions().size()-1);
			return new Account(transactions);
		}
	}

	public Account showTransactionsInRange(int accID, Date startDate, Date endDate) throws InvalidAccountException, IncorrectDateRangeException {
		Account acc = findOne(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		if(endDate.before(startDate)) {
			throw new IncorrectDateRangeException("");
		}
		List<Transaction> transactions = new ArrayList<>();
		for(Transaction transaction : acc.getTransactions()) {
			if(startDate.compareTo(transaction.getDate())* transaction.getDate().compareTo(endDate) >= 0) {
				transactions.add(transaction);
			}
		}
		return new Account(transactions);
	}
	
	private void checkNegativeAmount(double amt) throws NegativeAmountException {
		if(amt <= 0.0) {
			throw new NegativeAmountException(NEGATIVE_AMOUNT, amt);
		}
	}
	
	private void checkWithdrawalLimit(Account acc, double amt) throws ExceedWithdrawalLimitException, ParseException {
		double total = amt;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(date);
		String startDateString = dateString + " 00:00:00.0";
		String endDateString = dateString + " 23:59:59.0";
		Date startDate = sdf.parse(startDateString);
		Date endDate = sdf.parse(endDateString);
		
		for(Transaction transaction : acc.getTransactions()) {
			if("Withdrawal".equals(transaction.getDescription()) && transaction.getDate().equals(date)) {
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
