package app;

import service.BankService;
import service.impl.BankServiceImpl;

import java.util.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        BankService bankService = new BankServiceImpl();
        System.out.println("Welcome To Console Bank");
        while (running) {
            System.out.println("""
                        1) Open Account
                        2) Deposit
                        3) Withdraw
                        4) Transfer
                        5) Account Statement
                        6) List Accounts
                        7) Search Accounts by Customer Name
                        0) Exit
                    """);
            System.out.print("CHOOSE : ");
            String choice = scanner.nextLine().trim();
            System.out.println("CHOICE : " + choice);

            switch (choice) {
                case "1" -> openAccount(scanner, bankService);
                case "2" -> deposit(scanner, bankService);
                case "3" -> withdraw(scanner, bankService);
                case "4" -> transfer(scanner, bankService);
                case "5" -> statement(scanner);
                case "6" -> listAccounts(scanner, bankService);
                case "7" -> searchAccounts(scanner);
                case "0" -> running = false;
            }
        }
    }

    private static void openAccount(Scanner scanner, BankService bankService) {
        System.out.println("Customer name: ");
        String name = scanner.nextLine().trim();
        System.out.println("Customer email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Account Type (SAVINGS/CURRENT): ");
        String type = scanner.nextLine().trim();
        System.out.println("Initial deposit (optional, blank for 0): ");
        String amountStr = scanner.nextLine().trim();
        if (amountStr.isBlank()) {
            amountStr = "0";
        }
        Double initialAmount = Double.valueOf(amountStr);
        String accountNumber = bankService.openAccount(name, email, type);
        bankService.deposit(accountNumber, initialAmount, "Initial Deposit");
        System.out.println("Account opened: " + accountNumber);
    }

    private static void deposit(Scanner scanner, BankService bankService) {
        System.out.println("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.println("Amount: ");
        Double amount = Double.valueOf(scanner.nextLine().trim());
        bankService.deposit(accountNumber, amount, "Deposit");
        System.out.println("Deposited");
    }

    private static void withdraw(Scanner scanner, BankService bankService) {
        System.out.println("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.println("Amount: ");
        Double amount = Double.valueOf(scanner.nextLine().trim());
        bankService.withdraw(accountNumber, amount, "Withdrawal");
        System.out.println("Withdrawn");
    }

    private static void transfer(Scanner scanner, BankService bankService) {
        System.out.println("From Account: ");
        String fromAccount = scanner.nextLine().trim();
        System.out.println("To Account: ");
        String toAccount = scanner.nextLine().trim();
        System.out.println("Amount: ");
        Double amount = Double.valueOf(scanner.nextLine().trim());
        bankService.transfer(fromAccount, toAccount, amount, "Transfer");
    }

    private static void statement(Scanner sc) {
    }

    private static void listAccounts(Scanner sc, BankService bankService) {
        bankService.listAccount().forEach(account -> {
            System.out.println(account.getAccountNumber() + " | " + account.getAccountType() + " | " + account.getBalance());
        });
    }

    private static void searchAccounts(Scanner sc) {
    }


}