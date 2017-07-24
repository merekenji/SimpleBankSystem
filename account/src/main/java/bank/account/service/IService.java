package bank.account.service;

import java.util.Date;

import bank.account.beans.Account;
import bank.account.exception.ExceedWithdrawalLimitException;
import bank.account.exception.InsufficientBalanceException;
import bank.account.exception.NegativeAmountException;
import bank.account.exception.InvalidAccountException;

public interface IService {
	
	Account createAccount(double balance) throws InsufficientBalanceException;
	Account deposit(int accID, double amt) throws InvalidAccountException, NegativeAmountException;
	Account withdraw(int accID, double amt) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException;
	Account transfer(int senderID, int receiverID, double amt) throws InvalidAccountException, NegativeAmountException;
	Account showBalance(int accID) throws InvalidAccountException;
	Account showLastTenTransactions(int accID) throws InvalidAccountException;
	Account showTransactionsInRange(int accID, Date startDate, Date endDate) throws InvalidAccountException;
	
}
