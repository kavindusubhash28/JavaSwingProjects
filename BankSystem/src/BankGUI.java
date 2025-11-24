import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class BankGUI {
    private JFrame frame;
    private JTextField accNumField, nameField, amountField;
    private HashMap<String, BankAccount> accounts = new HashMap<>();

    public BankGUI() {
        frame = new JFrame("Bank Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(8, 1, 10, 10));

        accNumField = new JTextField();
        nameField = new JTextField();
        amountField = new JTextField();

        JButton createBtn = new JButton("Create Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBtn = new JButton("Check Balance");

        frame.add(new JLabel("Account Number:"));
        frame.add(accNumField);

        frame.add(new JLabel("Account Holder Name:"));
        frame.add(nameField);

        frame.add(new JLabel("Amount:"));
        frame.add(amountField);

        frame.add(createBtn);
        frame.add(depositBtn);
        frame.add(withdrawBtn);
        frame.add(checkBtn);

        // Button actions
        createBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        checkBtn.addActionListener(e -> checkBalance());

        frame.setVisible(true);
    }

    private void createAccount() {
        String accNum = accNumField.getText();
        String name = nameField.getText();

        if (accounts.containsKey(accNum)) {
            JOptionPane.showMessageDialog(frame, "Account already exists.");
        } else {
            accounts.put(accNum, new BankAccount(accNum, name));
            JOptionPane.showMessageDialog(frame, "Account created.");
        }
    }

    private void deposit() {
        String accNum = accNumField.getText();
        double amount = Double.parseDouble(amountField.getText());
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            account.deposit(amount);
        } else {
            JOptionPane.showMessageDialog(frame, "Account not found.");
        }
    }

    private void withdraw() {
        String accNum = accNumField.getText();
        double amount = Double.parseDouble(amountField.getText());
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            account.withdraw(amount);
        } else {
            JOptionPane.showMessageDialog(frame, "Account not found.");
        }
    }

    private void checkBalance() {
        String accNum = accNumField.getText();
        BankAccount account = accounts.get(accNum);
        if (account != null) {
            JOptionPane.showMessageDialog(frame, "Balance: " + account.getBalance());
        } else {
            JOptionPane.showMessageDialog(frame, "Account not found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankGUI());
    }

}

