package bank.account.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bank.account.beans.Account;
import bank.account.beans.Customer;
import bank.account.beans.Transaction;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.IncorrectDateRangeException;
import bank.account.exception.InsufficientBalanceException;
import bank.account.exception.NegativeAmountException;
import bank.account.exception.InvalidAccountException;
import bank.account.repositories.AccountRepo;
import bank.account.repositories.IAccountRepo;

public class Service implements IService {
	private IAccountRepo accRepo;
	
	private static final String INSUFFICIENT_BALANCE = "Error - Insufficient Bank Balance";
	private static final String INVALID_ACCOUNT = "Error - Invalid Account";
	private static final String WITHDRAWAL_LIMIT = "Error - Exceed Withdrawal Limit";
	private static final String NEGATIVE_AMOUNT = "Error - Amount Is Negative";
	private static final String INCORRECT_DATERANGE = "Error - Incorrect Date Range";
	
	public Service() {
		accRepo = new AccountRepo();
	}

	public Account createAccount(Customer customer, double balance) throws InsufficientBalanceException {
		if(balance < 100.0) {
			throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, balance);
		}
		Account acc = new Account(customer, balance);
		acc.addTransaction(new Transaction("Deposit", balance, balance, new Date()));
		boolean success = accRepo.save(acc);
		if(success) {
			return acc;
		} else {
			return null;
		}
	}

	public Account deposit(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		checkNegativeAmount(amt);
		if(amt < 100.0) {
			throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, amt);
		}
		Account acc = accRepo.find(accID);
		if(acc != null) {
			acc.setBalance(acc.getBalance() + amt);
			acc.addTransaction(new Transaction("Deposit", amt, acc.getBalance(), new Date()));
		} else {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return acc;
	}

	public Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException, ParseException {
		checkNegativeAmount(amt);
		if(amt < 100.0) {
			throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, amt);
		}
		Account acc = accRepo.find(accID);
		if(acc != null) {
			if(acc.getBalance() - amt < 100) {
				throw new InsufficientBalanceException(INSUFFICIENT_BALANCE, acc.getBalance() - amt);
			} else {
				checkWithdrawalLimit(acc, amt);
				acc.setBalance(acc.getBalance() - amt);
				acc.addTransaction(new Transaction("Withdrawal", amt, acc.getBalance(), new Date()));
			}
		} else {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return acc;
	}

	public Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		checkNegativeAmount(amt);
		Account sender = accRepo.find(senderID);
		Account receiver = accRepo.find(receiverID);
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
		Account acc = accRepo.find(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		return acc;
	}

	public Account showLastTenTransactions(int accID) throws InvalidAccountException {
		Account acc = accRepo.find(accID);
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

	public Account showTransactionsInRange(int accID, String startDate, String endDate) throws InvalidAccountException, IncorrectDateRangeException {
		Account acc = accRepo.find(accID);
		if(acc == null) {
			throw new InvalidAccountException(INVALID_ACCOUNT);
		}
		Calendar cal = Calendar.getInstance();
		String startYear = startDate.substring(0, 4);
		String startMonth = startDate.substring(4, 6);
		String startDay = startDate.substring(6, 8);
		cal.set(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date sDate = cal.getTime();
		String endYear = endDate.substring(0, 4);
		String endMonth = endDate.substring(4, 6);
		String endDay = endDate.substring(6, 8);
		cal.set(Integer.parseInt(endYear), Integer.parseInt(endMonth), Integer.parseInt(endDay));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date eDate = cal.getTime();
		
		if(eDate.compareTo(sDate) < 0 || sDate.compareTo(new Date()) > 0) {
			throw new IncorrectDateRangeException(INCORRECT_DATERANGE);
		}
		List<Transaction> transactions = new ArrayList<>();
		for(Transaction transaction : acc.getTransactions()) {
			if(transaction.getDate().compareTo(sDate) > 0 && transaction.getDate().compareTo(eDate) < 0) {
				transactions.add(transaction);
			}
		}
		return new Account(transactions);
	}
	
	private void checkWithdrawalLimit(Account acc, double amt) throws ExceedWithdrawalLimitException, ParseException {
		double total = amt;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date startDate = cal.getTime();
		
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date endDate = cal.getTime();
		
		for(Transaction transaction : acc.getTransactions()) {
			if("Withdrawal".equals(transaction.getDescription()) && transaction.getDate().compareTo(startDate) > 0 && transaction.getDate().compareTo(endDate) < 0) {
				total += transaction.getAmount();
			}
		}
		if(total > 1000) {
			throw new ExceedWithdrawalLimitException(WITHDRAWAL_LIMIT, total);
		}
	}
	
	private void checkNegativeAmount(double amt) throws NegativeAmountException {
		if(amt <= 0.0) {
			throw new NegativeAmountException(NEGATIVE_AMOUNT, amt);
		}
	}

}
