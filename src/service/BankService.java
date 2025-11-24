package service;

import domain.Account;
import domain.Transaction;

import java.util.List;

public interface BankService {
    String openAccount(String name, String email, String accountType);
    List<Account> listAccount();

    void deposit(String accountNumber, Double amount, String note);

    void withdraw(String accountNumber, Double amount, String note);

    void transfer(String fromAccount, String toAccount, Double amount, String note);

    List<Transaction> getStatement(String account);

    List<Account> searchAccountsByCustomerName(String name);
}
