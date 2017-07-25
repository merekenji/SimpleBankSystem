package bank.account.repositories;

import bank.account.beans.Account;

public interface IAccountRepo {

	boolean save(Account acc);
	Account find(int accID);
	
}
