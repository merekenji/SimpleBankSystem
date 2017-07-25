package bank.account.repositories;

import java.util.ArrayList;
import java.util.List;

import bank.account.beans.Account;

public class AccountRepo implements IAccountRepo {
			
	private List<Account> accounts;
	
	public AccountRepo() {
		accounts = new ArrayList<>();
	}

	public boolean save(Account acc) {
		accounts.add(acc);
		return true;
	}
	
	public Account find(int accID) {
		for(Account a : accounts) {
			if(a.getAccountID() == accID) {
				return a;
			}
		}
		return null;
	}

}
