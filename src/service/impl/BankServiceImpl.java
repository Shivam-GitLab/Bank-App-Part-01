package service.impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository = new AccountRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();

    @Override
    public String openAccount(String name, String email, String accountType) {
        String customerId = UUID.randomUUID().toString();

        // CREATE Customer
        Customer customer = new Customer(email,customerId,name);
        customerRepository.save(customer);

        String accountNumber = generateAccountNumber();
        // CREATE ACCOUNT OBJECT
        Account account = new Account(accountNumber, customerId, (double) 0, accountType);
        // SAVE
        accountRepository.save(account);
        return accountNumber;
    }

    private String generateAccountNumber() {
        int size = accountRepository.findAll().size() + 1;
       /* String accountNumber = String.format("AC%06d", size);
        return accountNumber;*/
        return String.format("AC%06d", size);
    }

    @Override
    public List<Account> listAccount() {
        return accountRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposit(String accountNumber, Double amount, String note) {
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found : " + accountNumber));
        if (amount > 0) {
            account.setBalance(account.getBalance() + amount);
            System.out.println("Deposit Done !");
        } else {
            throw new RuntimeException("Invalid amount");
        }
        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                amount,
                UUID.randomUUID().toString(),
                note,
                LocalDateTime.now(),
                Type.DEPOSIT
        );
        transactionRepository.add(transaction);
    }

    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found : " + accountNumber));
        if (account.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient Balance");
        if (amount > 0) {
            account.setBalance(account.getBalance() - amount);
            System.out.println("Withdraw Done !");
        } else {
            throw new RuntimeException("Invalid amount");
        }


        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                amount,
                UUID.randomUUID().toString(),
                note,
                LocalDateTime.now(),
                Type.WITHDRAW
        );
        transactionRepository.add(transaction);
    }

    @Override
    public void transfer(String fromAccount, String toAccount, Double amount, String note) {
        if (fromAccount.equals(toAccount)) {
            throw new RuntimeException("Cannot transfer to your own account");
        }
        Account fromAcc = accountRepository.findByNumber(fromAccount)
                .orElseThrow(() -> new RuntimeException("Account not found : " + fromAccount));
        Account toAcc = accountRepository.findByNumber(toAccount)
                .orElseThrow(() -> new RuntimeException("Account not found : " + toAccount));
        if (fromAcc.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient Balance");

        fromAcc.setBalance(fromAcc.getBalance() - amount);
        toAcc.setBalance(toAcc.getBalance() + amount);

        Transaction fromTransaction = new Transaction(
                fromAcc.getAccountNumber(),
                amount,
                UUID.randomUUID().toString(),
                note,
                LocalDateTime.now(),
                Type.TRANSFER_OUT
        );
        transactionRepository.add(fromTransaction);

/*        transactionRepository.add(new Transaction(
                fromAcc.getAccountNumber(),
                amount,
                UUID.randomUUID().toString(),
                note,
                LocalDateTime.now(),
                Type.WITHDRAW
        ));*/
        Transaction toTransaction = new Transaction(
                fromAcc.getAccountNumber(),
                amount,
                UUID.randomUUID().toString(),
                note,
                LocalDateTime.now(),
                Type.TRANSFER_IN
        );
        transactionRepository.add(toTransaction);
    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionRepository.
                findByAccount(account)
                .stream()
                .sorted(Comparator.comparing(Transaction::getTimeStamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountsByCustomerName(String name) {
     String query = (name == null) ? "" : name.toLowerCase();
     /* List<Account> result = new ArrayList<>();
        for (Customer customer : customerRepository.findAll()){
            if (customer.getName().toLowerCase().contains(query)){
                result.addAll(accountRepository.findByCustomerId(customer.getId()));
            }
        }
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;*/

        return customerRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(query))
                .flatMap(c -> accountRepository.findByCustomerId(c.getId()).stream())
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

}
