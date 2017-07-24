package bank.account.repositories;

import java.util.Date;

import bank.account.beans.Account;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.InsufficientBalanceException;
import bank.account.exception.NegativeAmountException;
import bank.account.exception.InvalidAccountException;

public interface IAccountRepo {

	Account addAccount(double balance) throws InsufficientBalanceException;
	Account deposit(int accID, double amt) throws InvalidAccountException, NegativeAmountException;
	Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException;
	Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException;
	Account showBalance(int accID) throws InvalidAccountException;
	Account showLastTenTransactions(Account acc) throws InvalidAccountException;
	Account showTransactionsInRange(Account acc, Date startDate, Date endDate) throws InvalidAccountException;
	
}
