package bank.account.service;

import java.util.Date;

import bank.account.beans.Account;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.InsufficientBalanceException;
import bank.account.exception.NegativeAmountException;
import bank.account.exception.InvalidAccountException;
import bank.account.repositories.AccountRepo;
import bank.account.repositories.IAccountRepo;

public class Service implements IService {
	private IAccountRepo accRepo;
	
	public Service() {
		accRepo = new AccountRepo();
	}

	public Account createAccount(double balance) throws InsufficientBalanceException {
		return accRepo.addAccount(balance);
	}

	public Account deposit(int accID, double amt) throws InvalidAccountException, NegativeAmountException {
		return accRepo.deposit(accID, amt);
	}

	public Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException {
		return accRepo.withdraw(accID, amt);
	}

	public Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		return accRepo.transfer(senderID, receiverID, amt);
	}

	public Account showBalance(int accID) throws InvalidAccountException {
		return accRepo.showBalance(accID);
	}

	public Account showLastTenTransactions(int accID) throws InvalidAccountException {
		return accRepo.showLastTenTransactions(accID);
	}

	public Account showTransactionsInRange(int accID, Date startDate, Date endDate) throws InvalidAccountException {
		return accRepo.showTransactionsInRange(accID, startDate, endDate);
	}

}
