package repository;

import domain.Transaction;

import java.util.*;

public class TransactionRepository {
    private final Map<String, List<Transaction>> transactionByAccount = new HashMap<>();

    public void add(Transaction transaction){
        List<Transaction> list = transactionByAccount.computeIfAbsent(
                transaction.getAccountNumber(),
                k -> new ArrayList<>());
        list.add(transaction);
    }

    public List<Transaction> findByAccount(String account) {
        return new ArrayList<>(transactionByAccount.getOrDefault(account, Collections.emptyList()));
    }
}
