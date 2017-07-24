package bank.account;

import static org.junit.Assert.*;
import org.junit.Test;

import bank.account.beans.Account;
import bank.account.exception.*;
import bank.account.service.*;

public class BankSystemTest {

	@Test
	public void createAccountSuccessfully() throws InsufficientBalanceException {
		IService service = new Service();
		assertEquals(10002, service.createAccount(500.0).getAccountID());
	}
	
	@Test (expected = InsufficientBalanceException.class)
	public void createAccountWithInsufficientBalance() throws InsufficientBalanceException {
		IService service = new Service();
		assertEquals(10001, service.createAccount(50.0).getAccountID());
	}
	
	@Test
	public void depositMoneySuccessfully() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException {
		IService service = new Service();
		Account acc = service.createAccount(500.0);
		assertEquals(700.0, service.deposit(acc.getAccountID(), 200.0).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void depositMoneyIntoNonExistantAccount() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException {
		IService service = new Service();
		assertEquals(700.0, service.deposit(100, 200.0).getBalance(), 0);
	}
	
	@Test (expected = NegativeAmountException.class)
	public void depositNegativeAmountOfMoney() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException {
		IService service = new Service();
		Account acc = service.createAccount(500.0);
		assertEquals(300.0, service.deposit(acc.getAccountID(), -200.0).getBalance(), 0);
	}
	
	@Test
	public void withdrawMoneySuccessfully() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException, ExceedWithdrawalLimitException {
		IService service = new Service();
		Account acc = service.createAccount(500.0);
		assertEquals(300.0, service.withdraw(acc.getAccountID(), 200.0).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void withdrawMoneyFromNonExistantAccount() throws InvalidAccountException, InsufficientBalanceException, NegativeAmountException, ExceedWithdrawalLimitException {
		IService service = new Service();
		assertEquals(300.0, service.withdraw(100, 200.0).getBalance(), 0);
	}
	
	@Test (expected = ExceedWithdrawalLimitException.class)
	public void withdrawMoneyThatExceedWithdrawalLimit() throws InsufficientBalanceException, InvalidAccountException, NegativeAmountException, ExceedWithdrawalLimitException {
		IService service = new Service();
		Account acc = service.createAccount(2000.0);
		acc = service.withdraw(acc.getAccountID(), 800.0);
		assertEquals(900.0, service.withdraw(acc.getAccountID(), 300.0));
	}
	
	@Test
	public void showBalance() throws InsufficientBalanceException, InvalidAccountException {
		IService service = new Service();
		Account acc = service.createAccount(500.0);
		assertEquals(500.0, service.showBalance(acc.getAccountID()).getBalance(), 0);
	}
	
	@Test (expected = InvalidAccountException.class)
	public void showBalanceOfNonExistantAccount() throws InsufficientBalanceException, InvalidAccountException {
		IService service = new Service();
		assertEquals(500.0, service.showBalance(100).getBalance(), 0);
	}
}
