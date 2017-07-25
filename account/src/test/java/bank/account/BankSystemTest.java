package bank.account;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import bank.account.beans.Account;
import bank.account.beans.Customer;
import bank.account.exception.*;
import bank.account.service.*;

public class BankSystemTest {
	
	private static final String KENJI = "Kenji";
	private Logger logger = Logger.getLogger("ExceptionMessages");
	private IService service;

	@Before
	public void init() {
		service = new Service();
	}
	
	private void initTransactions(Account acc) throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException, ExceedWithdrawalLimitException, ParseException {
		service.deposit(acc.getAccountID(), 500.0);
		service.withdraw(acc.getAccountID(), 200.0);
		service.deposit(acc.getAccountID(), 300.0);
		service.withdraw(acc.getAccountID(), 150.0);
		service.deposit(acc.getAccountID(), 180.0);
		service.deposit(acc.getAccountID(), 175.0);
		service.deposit(acc.getAccountID(), 195.0);
		service.withdraw(acc.getAccountID(), 180.0);
		service.withdraw(acc.getAccountID(), 200.0);
		service.deposit(acc.getAccountID(), 500.0);
	}
	
	@Test
	public void createAccountSuccessfully() {
		try {
			assertEquals(10004, service.createAccount(new Customer(KENJI), 500.0).getAccountID(), 0);
		} catch(InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);;
		}
	}
	
	@Test (expected = InsufficientBalanceException.class)
	public void createAccountWithInsufficientBalance() throws InsufficientBalanceException {
		assertEquals(10001, service.createAccount(new Customer(KENJI), 50.0).getAccountID());
	}
	
	@Test
	public void depositMoneySuccessfully() {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 500.0);
			assertEquals(700.0, service.deposit(acc.getAccountID(), 200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException | NegativeAmountException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void depositMoneyIntoNonExistantAccount() throws InvalidAccountException {
		try {
			assertEquals(700.0, service.deposit(100, 200.0).getBalance(), 0);
		} catch(NegativeAmountException | InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = NegativeAmountException.class)
	public void depositNegativeAmountOfMoney() throws NegativeAmountException {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 500.0);
			assertEquals(300.0, service.deposit(acc.getAccountID(), -200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test
	public void withdrawMoneySuccessfully() {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 500.0);
			assertEquals(300.0, service.withdraw(acc.getAccountID(), 200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException | NegativeAmountException | ExceedWithdrawalLimitException | ParseException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void withdrawMoneyFromNonExistantAccount() throws InvalidAccountException {
		try {
			assertEquals(300.0, service.withdraw(100, 200.0).getBalance(), 0);
		} catch(InsufficientBalanceException | NegativeAmountException | ExceedWithdrawalLimitException | ParseException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = ExceedWithdrawalLimitException.class)
	public void withdrawMoneyThatExceedWithdrawalLimit() throws ExceedWithdrawalLimitException {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 2000.0);
			acc = service.withdraw(acc.getAccountID(), 800.0);
			assertEquals(900.0, service.withdraw(acc.getAccountID(), 300.0));
		} catch(InsufficientBalanceException | InvalidAccountException | NegativeAmountException | ParseException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test
	public void transferMoneySuccessfully() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException {
		try {
			Account sender = service.createAccount(new Customer(KENJI), 1000.0);
			Account receiver = service.createAccount(new Customer("Jerry"), 500.0);
			assertEquals(700.0, service.transfer(sender.getAccountID(), receiver.getAccountID(), 300.0).getBalance(), 0);
		} catch(InsufficientBalanceException | InvalidAccountException | NegativeAmountException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void transferMoneyFromNonExistantAccount() throws InvalidAccountException {
		try {
			Account receiver = service.createAccount(new Customer(KENJI), 500.0);
			assertEquals(700.0, service.transfer(100, receiver.getAccountID(), 300.0).getBalance(), 0);
		} catch(NegativeAmountException | InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void transferMoneyToNonExistantAccount() throws InvalidAccountException {
		try {
			Account sender = service.createAccount(new Customer(KENJI), 1000.0);
			assertEquals(700.0, service.transfer(sender.getAccountID(), 100, 300.0).getBalance(), 0);
		} catch(NegativeAmountException | InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = NegativeAmountException.class)
	public void transferNegativeAmountOfMoney() throws NegativeAmountException {
		try {
			Account sender = service.createAccount(new Customer(KENJI), 1000.0);
			Account receiver = service.createAccount(new Customer("Jerry"), 500.0);
			assertEquals(700.0, service.transfer(sender.getAccountID(), receiver.getAccountID(), -200.0).getBalance(), 0);
		} catch(InsufficientBalanceException | InvalidAccountException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test
	public void showBalance() throws InsufficientBalanceException, InvalidAccountException {
		Account acc = service.createAccount(new Customer(KENJI), 500.0);
		assertEquals(500.0, service.showBalance(acc.getAccountID()).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showBalanceOfNonExistantAccount() throws InsufficientBalanceException, InvalidAccountException {
		assertEquals(500.0, service.showBalance(100).getBalance(), 0);
	}
	
	@Test
	public void showLast10Transactions() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException, ParseException {
		Account acc = service.createAccount(new Customer(KENJI), 1000.0);
		initTransactions(acc);
		assertEquals(10, service.showLastTenTransactions(acc.getAccountID()).getTransactions().size());
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showLast10TransactionsFromNonExistantAccount() throws InvalidAccountException {
		assertEquals(10, service.showLastTenTransactions(100).getTransactions().size());
	}
	
	@Test
	public void showTransactionsWithinDateRange() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException, IncorrectDateRangeException, ParseException {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 1000.0);
			initTransactions(acc);
			assertEquals(11, service.showTransactionsInRange(acc.getAccountID(), "20170601", "20170625").getTransactions().size());
		} catch(InsufficientBalanceException | InvalidAccountException | NegativeAmountException | ExceedWithdrawalLimitException | IncorrectDateRangeException | ParseException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showTransactionsWithinDateRangeOfNonExistentAccount() throws InvalidAccountException {
		try {
			assertEquals(11, service.showTransactionsInRange(100, "20170601", "20170625").getTransactions().size());
		} catch(IncorrectDateRangeException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
	@Test (expected = IncorrectDateRangeException.class)
	public void showTransactionsWithinWrongDateRange() throws IncorrectDateRangeException {
		try {
			Account acc = service.createAccount(new Customer(KENJI), 1000.0);
			assertEquals(11, service.showTransactionsInRange(acc.getAccountID(), "20170630", "20170601").getTransactions().size());
		} catch(InvalidAccountException | InsufficientBalanceException e) {
			logger.log(Level.FINEST, e.getMessage(), e);
		}
	}
	
}
