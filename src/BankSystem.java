import java.util.HashMap;
import java.util.Scanner;

public class BankSystem {
    static HashMap<String, BankAccount> accounts = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String option;

        do {
            System.out.println("\n--- Bank Management System ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            option = sc.nextLine();

            switch (option) {
                case "1":
                    createAccount();
                    break;
                case "2":
                    depositMoney();
                    break;
                case "3":
                    withdrawMoney();
                    break;
                case "4":
                    checkBalance();
                    break;
                case "5":
                    System.out.println("Thank you for using the Bank System!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }

        } while (!option.equals("5"));
    }

    static void createAccount() {
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine();
        if (accounts.containsKey(accNum)) {
            System.out.println("Account already exists.");
            return;
        }

        System.out.print("Enter Account Holder Name: ");
        String name = sc.nextLine();
        BankAccount account = new BankAccount(accNum, name);
        accounts.put(accNum, account);
        System.out.println("Account created successfully!");
    }

    static void depositMoney() {
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine();
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            System.out.print("Enter amount to deposit: ");
            double amount = Double.parseDouble(sc.nextLine());
            account.deposit(amount);
        } else {
            System.out.println("Account not found.");
        }
    }

    static void withdrawMoney() {
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine();
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            System.out.print("Enter amount to withdraw: ");
            double amount = Double.parseDouble(sc.nextLine());
            account.withdraw(amount);
        } else {
            System.out.println("Account not found.");
        }
    }

    static void checkBalance() {
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine();
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            account.displayBalance();
        } else {
            System.out.println("Account not found.");
        }
    }
}

