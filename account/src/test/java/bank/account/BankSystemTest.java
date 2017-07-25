package bank.account;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import bank.account.beans.Account;
import bank.account.beans.Customer;
import bank.account.exception.*;
import bank.account.service.*;

public class BankSystemTest {
	Logger logger = Logger.getLogger("ExceptionMessages");

	@Test
	public void createAccountSuccessfully() {
		IService service = new Service();
		try {
			assertEquals(10004, service.createAccount(new Customer("Kenji"), 500.0).getAccountID(), 0);
		} catch(InsufficientBalanceException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test (expected = InsufficientBalanceException.class)
	public void createAccountWithInsufficientBalance() throws InsufficientBalanceException {
		IService service = new Service();
		assertEquals(10001, service.createAccount(new Customer("Kenji"), 50.0).getAccountID());
	}
	
	@Test
	public void depositMoneySuccessfully() {
		IService service = new Service();
		try {
			Account acc = service.createAccount(new Customer("Kenji"), 500.0);
			assertEquals(700.0, service.deposit(acc.getAccountID(), 200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException | NegativeAmountException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void depositMoneyIntoNonExistantAccount() throws InvalidAccountException, InsufficientBalanceException {
		IService service = new Service();
		try {
			assertEquals(700.0, service.deposit(100, 200.0).getBalance(), 0);
		} catch(NegativeAmountException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test (expected = NegativeAmountException.class)
	public void depositNegativeAmountOfMoney() throws NegativeAmountException {
		IService service = new Service();
		try {
			Account acc = service.createAccount(new Customer("Kenji"), 500.0);
			assertEquals(300.0, service.deposit(acc.getAccountID(), -200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test
	public void withdrawMoneySuccessfully() {
		IService service = new Service();
		try {
			Account acc = service.createAccount(new Customer("Kenji"), 500.0);
			assertEquals(300.0, service.withdraw(acc.getAccountID(), 200.0).getBalance(), 0);
		} catch(InvalidAccountException | InsufficientBalanceException | NegativeAmountException | ExceedWithdrawalLimitException | ParseException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test (expected = InvalidAccountException.class)
	public void withdrawMoneyFromNonExistantAccount() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException, ExceedWithdrawalLimitException, ParseException {
		IService service = new Service();
		try {
			assertEquals(300.0, service.withdraw(100, 200.0).getBalance(), 0);
		} catch(InsufficientBalanceException | NegativeAmountException | ExceedWithdrawalLimitException | ParseException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test (expected = ExceedWithdrawalLimitException.class)
	public void withdrawMoneyThatExceedWithdrawalLimit() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException, ParseException {
		IService service = new Service();
		try {
			Account acc = service.createAccount(new Customer("Kenji"), 2000.0);
			acc = service.withdraw(acc.getAccountID(), 800.0);
			assertEquals(900.0, service.withdraw(acc.getAccountID(), 300.0));
		} catch(InsufficientBalanceException | InvalidAccountException | NegativeAmountException | ParseException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
	
	@Test
	public void transferMoneySuccessfully() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException {
		IService service = new Service();
		Account sender = service.createAccount(new Customer("Kenji"), 1000.0);
		Account receiver = service.createAccount(new Customer("Jerry"), 500.0);
		assertEquals(700.0, service.transfer(sender.getAccountID(), receiver.getAccountID(), 300.0).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void transferMoneyFromNonExistantAccount() throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		IService service = new Service();
		Account receiver = service.createAccount(new Customer("Kenji"), 500.0);
		assertEquals(700.0, service.transfer(100, receiver.getAccountID(), 300.0).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void transferMoneyToNonExistantAccount() throws InvalidAccountException, NegativeAmountException, InsufficientBalanceException {
		IService service = new Service();
		Account sender = service.createAccount(new Customer("Kenji"), 1000.0);
		assertEquals(700.0, service.transfer(sender.getAccountID(), 100, 300.0).getBalance(), 0);
	}
	
	@Test (expected = NegativeAmountException.class)
	public void transferNegativeAmountOfMoney() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException {
		IService service = new Service();
		Account sender = service.createAccount(new Customer("Kenji"), 1000.0);
		Account receiver = service.createAccount(new Customer("Jerry"), 500.0);
		assertEquals(700.0, service.transfer(sender.getAccountID(), receiver.getAccountID(), -200.0).getBalance(), 0);
	}
	
	@Test
	public void showBalance() throws InsufficientBalanceException, InvalidAccountException {
		IService service = new Service();
		Account acc = service.createAccount(new Customer("Kenji"), 500.0);
		assertEquals(500.0, service.showBalance(acc.getAccountID()).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showBalanceOfNonExistantAccount() throws InsufficientBalanceException, InvalidAccountException {
		IService service = new Service();
		assertEquals(500.0, service.showBalance(100).getBalance(), 0);
	}
	
	@Test
	public void showLast10Transactions() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException, ParseException {
		IService service = new Service();
		Account acc = service.createAccount(new Customer("Kenji"), 1000.0);
		acc = service.deposit(acc.getAccountID(), 500.0);
		acc = service.withdraw(acc.getAccountID(), 200.0);
		acc = service.deposit(acc.getAccountID(), 300.0);
		acc = service.withdraw(acc.getAccountID(), 150.0);
		acc = service.deposit(acc.getAccountID(), 180.0);
		acc = service.deposit(acc.getAccountID(), 175.0);
		acc = service.deposit(acc.getAccountID(), 195.0);
		acc = service.withdraw(acc.getAccountID(), 180.0);
		acc = service.withdraw(acc.getAccountID(), 200.0);
		acc = service.deposit(acc.getAccountID(), 500.0);
		assertEquals(10, service.showLastTenTransactions(acc.getAccountID()).getTransactions().size());
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showLast10TransactionsFromNonExistantAccount() throws InvalidAccountException {
		IService service = new Service();
		assertEquals(10, service.showLastTenTransactions(100).getTransactions().size());
	}
	
	@Test
	public void showTransactionsWithinDateRange() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException, IncorrectDateRangeException, ParseException {
		IService service = new Service();
		Account acc = service.createAccount(new Customer("Kenji"), 1000.0);
		acc = service.deposit(acc.getAccountID(), 500.0);
		acc = service.withdraw(acc.getAccountID(), 200.0);
		acc = service.deposit(acc.getAccountID(), 300.0);
		acc = service.withdraw(acc.getAccountID(), 150.0);
		acc = service.deposit(acc.getAccountID(), 180.0);
		acc = service.deposit(acc.getAccountID(), 175.0);
		acc = service.deposit(acc.getAccountID(), 195.0);
		acc = service.withdraw(acc.getAccountID(), 180.0);
		acc = service.withdraw(acc.getAccountID(), 200.0);
		acc = service.deposit(acc.getAccountID(), 500.0);
		assertEquals(11, service.showTransactionsInRange(acc.getAccountID(), "20170601", "20170625").getTransactions().size());
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showTransactionsWithinDateRangeOfNonExistentAccount() throws InvalidAccountException, IncorrectDateRangeException {
		IService service = new Service();
		assertEquals(11, service.showTransactionsInRange(100, "20170601", "20170625").getTransactions().size());
	}
	
	@Test (expected = IncorrectDateRangeException.class)
	public void showTransactionsWithinWrongDateRange() throws InvalidAccountException, IncorrectDateRangeException, InsufficientBalanceException {
		IService service = new Service();
		Account acc = service.createAccount(new Customer("Kenji"), 1000.0);
		assertEquals(11, service.showTransactionsInRange(acc.getAccountID(), "20170630", "20170601").getTransactions().size());
	}
	
}
