import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class BurgerShopApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeFrame(new OrderManager()).setVisible(true));
    }

    enum OrderStatus {
        PREPARING, DELIVERED, CANCELLED
    }

    static class Customer {
        private final String customerID;
        private final String name;

        public Customer(String customerID, String name) {
            this.customerID = customerID;
            this.name = name;
        }

        public String getCustomerID() { return customerID; }
        public String getName() { return name; }
    }

    static class Order {
        private final String orderID;
        private final Customer customer;
        private int quantity;
        private OrderStatus status;
        private final int unitPrice = 500;

        public Order(String orderID, Customer customer, int quantity, OrderStatus status) {
            this.orderID = orderID;
            this.customer = customer;
            this.quantity = quantity;
            this.status = status;
        }

        public String getOrderID() { return orderID; }
        public Customer getCustomer() { return customer; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int q) { this.quantity = q; }
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
        public int getTotal() { return quantity * unitPrice; }
    }

    static class OrderManager {
        private final List<Order> orders = new ArrayList<>();
        private final Map<String, Customer> customers = new HashMap<>();
        private int orderCounter = 0;
        private int customerCounter = 0;

        public String generateOrderID() {
            orderCounter++;
            return String.format("O%03d", orderCounter);
        }

        public String generateCustomerID() {
            customerCounter++;
            return String.format("C%03d", customerCounter);
        }

        public Order addOrder(String customerName, int qty) {
            String cID = generateCustomerID();
            Customer c = new Customer(cID, customerName);
            customers.put(cID, c);

            String oID = generateOrderID();
            Order o = new Order(oID, c, qty, OrderStatus.PREPARING);
            orders.add(o);
            return o;
        }

        public Order searchOrder(String orderID) {
            for (Order o : orders) {
                if (o.getOrderID().equalsIgnoreCase(orderID)) return o;
            }
            return null;
        }

        public List<Order> searchCustomerOrders(String customerID) {
            List<Order> result = new ArrayList<>();
            for (Order o : orders) {
                if (o.getCustomer().getCustomerID().equalsIgnoreCase(customerID)) result.add(o);
            }
            return result;
        }

        public List<Order> viewOrdersByStatus(OrderStatus status) {
            List<Order> list = new ArrayList<>();
            for (Order o : orders) {
                if (o.getStatus() == status) list.add(o);
            }
            return list;
        }

        public boolean updateOrderQuantity(String orderID, int newQty) {
            Order o = searchOrder(orderID);
            if (o == null) return false;
            if (o.getStatus() != OrderStatus.PREPARING) return false;
            o.setQuantity(newQty);
            return true;
        }

        public boolean updateOrderStatus(String orderID, OrderStatus newStatus) {
            Order o = searchOrder(orderID);
            if (o == null) return false;
            if (o.getStatus() != OrderStatus.PREPARING) return false;
            o.setStatus(newStatus);
            return true;
        }

        public Map<Customer, Integer> getCustomerTotals() {
            Map<Customer, Integer> totals = new HashMap<>();
            for (Order o : orders) {
                Customer c = o.getCustomer();
                totals.put(c, totals.getOrDefault(c, 0) + o.getTotal());
            }
            return totals;
        }

        public List<Map.Entry<Customer, Integer>> getCustomersByTotalDesc() {
            List<Map.Entry<Customer, Integer>> list = new ArrayList<>(getCustomerTotals().entrySet());
            list.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
            return list;
        }

        public List<Order> getAllOrders() {
            return new ArrayList<>(orders);
        }
    }


    static class HomeFrame extends JFrame {
        private final OrderManager manager;

        HomeFrame(OrderManager manager) {
            this.manager = manager;
            setTitle("iHungry Burger Shop - Home");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(850, 500);
            setLocationRelativeTo(null);
            initUI();
        }

        private void initUI() {
            JPanel mainPanel = new JPanel(new GridLayout(1, 2));

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBackground(Color.WHITE);

            JLabel welcome = new JLabel("Welcome to Burgers", SwingConstants.CENTER);
            welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
            welcome.setForeground(new Color(200, 150, 0));
            welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

            ImageIcon icon = new ImageIcon("assets/burgershop.jpg");
            Image scaledImg = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel footer = new JLabel("@ICET", SwingConstants.CENTER);
            footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);

            leftPanel.add(Box.createVerticalStrut(30));
            leftPanel.add(welcome);
            leftPanel.add(Box.createVerticalStrut(20));
            leftPanel.add(imgLabel);
            leftPanel.add(Box.createVerticalGlue());
            leftPanel.add(footer);
            leftPanel.add(Box.createVerticalStrut(10));

            JPanel rightPanel = new JPanel();
            rightPanel.setBackground(new Color(245,245,245));
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

            JButton placeOrderBtn = makeButton("Place Order");
            JButton searchBtn = makeButton("Search");
            JButton viewOrdersBtn = makeButton("View Orders");
            JButton updateOrderBtn = makeButton("Update Order Details");
            JButton exitBtn = makeButton("Exit");

            rightPanel.add(Box.createVerticalGlue());
            rightPanel.add(placeOrderBtn);
            rightPanel.add(Box.createVerticalStrut(15));
            rightPanel.add(searchBtn);
            rightPanel.add(Box.createVerticalStrut(15));
            rightPanel.add(viewOrdersBtn);
            rightPanel.add(Box.createVerticalStrut(15));
            rightPanel.add(updateOrderBtn);
            rightPanel.add(Box.createVerticalStrut(30));
            rightPanel.add(exitBtn);
            rightPanel.add(Box.createVerticalGlue());

            placeOrderBtn.addActionListener(e -> new PlaceOrderDialog(this, manager).setVisible(true));
            searchBtn.addActionListener(e -> {
                String[] options = {"Search Best Customer", "Search Order", "Search Customer"};
                String choice = (String) JOptionPane.showInputDialog(this, "Choose search option:",
                        "Search Menu", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice == null) return;
                if (choice.equals("Search Best Customer")) new BestCustomerDialog(this, manager).setVisible(true);
                if (choice.equals("Search Order")) new SearchOrderDialog(this, manager).setVisible(true);
                if (choice.equals("Search Customer")) new SearchCustomerDialog(this, manager).setVisible(true);
            });
            viewOrdersBtn.addActionListener(e -> new ViewOrdersDialog(this, manager).setVisible(true));
            updateOrderBtn.addActionListener(e -> new UpdateOrderDialog(this, manager).setVisible(true));
            exitBtn.addActionListener(e -> System.exit(0));

            mainPanel.add(leftPanel);
            mainPanel.add(rightPanel);
            add(mainPanel);
        }

        private JButton makeButton(String text) {
            JButton b = new JButton(text);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBackground(new Color(220, 80, 70));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.setMaximumSize(new Dimension(220, 40));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }
    }


    static class PlaceOrderDialog extends JDialog {
        private final OrderManager manager;
        private JTextField tfCustomerID;
        private JTextField tfQuantity;
        private JTextField tfCustomerName;
        private JLabel lblOrderID;
        private JLabel lblTotal;
        private final String preGeneratedOrderID;
        private final String preGeneratedCustomerID;

        PlaceOrderDialog(JFrame parent, OrderManager manager) {
            super(parent, "Place Order", true);
            this.manager = manager;

            preGeneratedOrderID = String.format("O%03d", manager.orderCounter + 1);
            preGeneratedCustomerID = String.format("C%03d", manager.customerCounter + 1);

            setSize(500, 450);
            setLocationRelativeTo(parent);
            setResizable(false);
            initUI();
        }

        private void initUI() {
            setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(200, 80, 70));
            headerPanel.setPreferredSize(new Dimension(500, 60));
            JLabel headerLabel = new JLabel("Place Order", SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            add(headerPanel, BorderLayout.NORTH);

            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel("Order Id :"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            lblOrderID = new JLabel(preGeneratedOrderID);
            lblOrderID.setFont(new Font("SansSerif", Font.BOLD, 14));
            mainPanel.add(lblOrderID, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel("Customer Id :"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            tfCustomerID = new JTextField(preGeneratedCustomerID);
            tfCustomerID.setEditable(false);
            tfCustomerID.setBackground(new Color(240, 240, 240));
            mainPanel.add(tfCustomerID, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel("Customer Name :"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            tfCustomerName = new JTextField(20);
            tfCustomerName.setFont(new Font("SansSerif", Font.PLAIN, 15));
            mainPanel.add(tfCustomerName, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            mainPanel.add(new JSeparator(), gbc);
            gbc.gridwidth = 1;
            row++;

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            JLabel lblQtyText = new JLabel("Burger QTY :");
            lblQtyText.setFont(new Font("SansSerif", Font.PLAIN, 14));
            mainPanel.add(lblQtyText, gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            tfQuantity = new JTextField(15);
            tfQuantity.setFont(new Font("Arial", Font.PLAIN, 18));
            tfQuantity.setPreferredSize(new Dimension(200, 35));
            tfQuantity.setEditable(true);
            tfQuantity.setEnabled(true);
            tfQuantity.setFocusable(true);
            tfQuantity.setRequestFocusEnabled(true);
            tfQuantity.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            tfQuantity.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    calculateTotal();
                }
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                        e.consume();
                    }
                }
            });
            tfQuantity.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    tfQuantity.getCaret().setVisible(true);
                }
            });
            mainPanel.add(tfQuantity, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel("Order Status :"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            JLabel lblStatus = new JLabel("Pending.");
            lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));
            mainPanel.add(lblStatus, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            mainPanel.add(new JLabel("NET Total :"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
            lblTotal = new JLabel("0.00");
            lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblTotal.setForeground(new Color(220, 80, 70));
            mainPanel.add(lblTotal, gbc);

            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
            mainPanel.add(new JLabel(""), gbc);
            gbc.gridwidth = 1;

            add(mainPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

            JButton placeBtn = new JButton("Place Order");
            placeBtn.setBackground(new Color(76, 175, 80));
            placeBtn.setForeground(Color.WHITE);
            placeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            placeBtn.setPreferredSize(new Dimension(120, 35));
            placeBtn.addActionListener(e -> placeOrder());

            JButton backBtn = new JButton("Back to home Page");
            backBtn.setBackground(new Color(220, 80, 70));
            backBtn.setForeground(Color.WHITE);
            backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            backBtn.setPreferredSize(new Dimension(160, 35));
            backBtn.addActionListener(e -> dispose());

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(new Color(220, 80, 70));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            cancelBtn.setPreferredSize(new Dimension(100, 35));
            cancelBtn.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel?", "Cancel Order",
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) dispose();
            });

            buttonPanel.add(placeBtn);
            buttonPanel.add(backBtn);
            buttonPanel.add(cancelBtn);
            add(buttonPanel, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        tfQuantity.requestFocusInWindow();
                        tfQuantity.getCaret().setVisible(true);
                    });
                }
            });

            calculateTotal();
        }

        private void calculateTotal() {
            try {
                String text = tfQuantity.getText().trim();
                if (text.isEmpty()) {
                    lblTotal.setText("0.00");
                    return;
                }
                int q = Integer.parseInt(text);
                if (q < 0) q = 0;
                double total = q * 500.0;
                lblTotal.setText(String.format("%.2f", total));
            } catch (NumberFormatException ex) {
                lblTotal.setText("0.00");
            }
        }

        private void placeOrder() {
            try {
                String qtyText = tfQuantity.getText().trim();
                String nameText = tfCustomerName.getText().trim();
                if (qtyText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter burger quantity!");
                    tfQuantity.requestFocus();
                    return;
                }
                if (nameText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter customer name!");
                    tfCustomerName.requestFocus();
                    return;
                }
                int q = Integer.parseInt(qtyText);
                if (q <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid quantity (greater than 0)!");
                    tfQuantity.requestFocus();
                    return;
                }
                Order o = manager.addOrder(nameText, q);
                JOptionPane.showMessageDialog(this,
                    "Order Placed Successfully!\n\n" +
                    "Order ID: " + o.getOrderID() + "\n" +
                    "Customer ID: " + o.getCustomer().getCustomerID() + "\n" +
                    "Customer Name: " + o.getCustomer().getName() + "\n" +
                    "Quantity: " + q + " burgers\n" +
                    "Total: Rs. " + o.getTotal(),
                    "Order Confirmation", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity!");
                tfQuantity.requestFocus();
            }
        }
    }

    static class BestCustomerDialog extends JDialog {
        private final OrderManager manager;

        BestCustomerDialog(JFrame parent, OrderManager manager) {
            super(parent, "Search Best Customers", true);
            this.manager = manager;
            setSize(650, 420);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(200, 80, 70));
            headerPanel.setPreferredSize(new Dimension(650, 60));
            JLabel headerLabel = new JLabel("Search Best Customers", SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            add(headerPanel, BorderLayout.NORTH);

            String[] columns = {"Customer ID", "Name", "Total"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);
            table.setFont(new Font("SansSerif", Font.PLAIN, 15));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
            table.setRowHeight(28);
            table.setBackground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(new Color(220,220,220));

            List<Map.Entry<Customer, Integer>> customers = manager.getCustomersByTotalDesc();
            for (Map.Entry<Customer, Integer> entry : customers) {
                Customer customer = entry.getKey();
                Integer total = entry.getValue();
                model.addRow(new Object[]{
                        customer.getCustomerID(),
                        customer.getName(),
                        String.format("%.2f", (double)total)
                });
            }
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
            add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setBackground(Color.WHITE);
            JButton backBtn = new JButton("Back");
            backBtn.setBackground(new Color(220, 80, 70));
            backBtn.setForeground(Color.WHITE);
            backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
            backBtn.setFocusPainted(false);
            backBtn.setPreferredSize(new Dimension(100, 35));
            backBtn.addActionListener(e -> dispose());
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }


    static class SearchOrderDialog extends JDialog {
        private final OrderManager manager;
        private JTextField tfOrderID;
        private JTextArea resultArea;

        SearchOrderDialog(JFrame parent, OrderManager manager) {
            super(parent, "Search Order Details", true);
            this.manager = manager;
            setSize(500, 400);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(200, 80, 70));
            headerPanel.setPreferredSize(new Dimension(500, 60));
            JLabel headerLabel = new JLabel("Search Order Details", SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            add(headerPanel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 20, 10, 20);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            JLabel lblOrderId = new JLabel("Enter OrderID :");
            lblOrderId.setFont(new Font("SansSerif", Font.PLAIN, 15));
            contentPanel.add(lblOrderId, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            tfOrderID = new JTextField(10);
            tfOrderID.setFont(new Font("SansSerif", Font.PLAIN, 15));
            contentPanel.add(tfOrderID, gbc);

            gbc.gridx = 2; gbc.weightx = 0;
            JButton searchBtn = new JButton("Search");
            searchBtn.setBackground(new Color(76, 175, 80));
            searchBtn.setForeground(Color.WHITE);
            searchBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            searchBtn.setFocusPainted(false);
            searchBtn.addActionListener(e -> searchOrder());
            contentPanel.add(searchBtn, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            addOrderDetailsLabels(detailsPanel, null);
            contentPanel.add(detailsPanel, gbc);

            add(contentPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.setBackground(Color.WHITE);
            JButton backBtn = new JButton("Back");
            backBtn.setBackground(new Color(220, 80, 70));
            backBtn.setForeground(Color.WHITE);
            backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
            backBtn.setFocusPainted(false);
            backBtn.setPreferredSize(new Dimension(100, 35));
            backBtn.addActionListener(e -> dispose());
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);

            this.detailsPanel = detailsPanel;
        }

        private JPanel detailsPanel;
        private void addOrderDetailsLabels(JPanel panel, Order order) {
            panel.removeAll();
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            Font labelFont = new Font("SansSerif", Font.PLAIN, 15);
            if (order == null) {
                JLabel info = new JLabel("Enter an Order ID and click Search.");
                info.setFont(labelFont);
                panel.add(info);
            } else {
                JLabel cid = new JLabel("Customer ID : " + order.getCustomer().getCustomerID());
                JLabel name = new JLabel("Name : " + order.getCustomer().getName());
                JLabel qty = new JLabel("QTY : " + order.getQuantity());
                JLabel total = new JLabel("Total : " + String.format("%.2f", (double)order.getTotal()));
                JLabel status = new JLabel("Order Status : " + order.getStatus().toString().toLowerCase());
                cid.setFont(labelFont);
                name.setFont(labelFont);
                qty.setFont(labelFont);
                total.setFont(labelFont);
                status.setFont(labelFont);
                panel.add(cid);
                panel.add(Box.createVerticalStrut(5));
                panel.add(name);
                panel.add(Box.createVerticalStrut(5));
                panel.add(qty);
                panel.add(Box.createVerticalStrut(5));
                panel.add(total);
                panel.add(Box.createVerticalStrut(5));
                panel.add(status);
            }
            panel.revalidate();
            panel.repaint();
        }

        private void searchOrder() {
            String orderID = tfOrderID.getText().trim();
            if (orderID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an Order ID!");
                return;
            }
            Order order = manager.searchOrder(orderID);
            if (order == null) {
                addOrderDetailsLabels(detailsPanel, null);
                JLabel notFound = new JLabel("Order not found!");
                notFound.setFont(new Font("SansSerif", Font.BOLD, 15));
                notFound.setForeground(new Color(220, 80, 70));
                detailsPanel.add(Box.createVerticalStrut(10));
                detailsPanel.add(notFound);
                detailsPanel.revalidate();
                detailsPanel.repaint();
            } else {
                addOrderDetailsLabels(detailsPanel, order);
            }
        }
    }


    static class SearchCustomerDialog extends JDialog {
        private final OrderManager manager;
        private JTextField tfCustomerID;

        SearchCustomerDialog(JFrame parent, OrderManager manager) {
            super(parent, "Search Customer", true);
            this.manager = manager;
            setSize(600, 400);
            setLocationRelativeTo(parent);
            initUI();
        }

        private void initUI() {
            setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel(new FlowLayout());
            inputPanel.add(new JLabel("Customer ID:"));
            tfCustomerID = new JTextField(10);
            inputPanel.add(tfCustomerID);

            JButton searchBtn = new JButton("Search");
            searchBtn.addActionListener(e -> searchCustomer());
            inputPanel.add(searchBtn);

            add(inputPanel, BorderLayout.NORTH);

            String[] columns = {"Order ID", "Quantity", "Status", "Total"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeBtn);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void searchCustomer() {
            String customerID = tfCustomerID.getText().trim();
            if (customerID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Customer ID!");
                return;
            }

            List<Order> orders = manager.searchCustomerOrders(customerID);
            DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView()).getModel();
            model.setRowCount(0);

            if (orders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No orders found for Customer ID: " + customerID);
            } else {
                for (Order order : orders) {
                    model.addRow(new Object[]{
                            order.getOrderID(),
                            order.getQuantity(),
                            order.getStatus(),
                            "Rs. " + order.getTotal()
                    });
                }
            }
        }
    }


    static class ViewOrdersDialog extends JDialog {
        private final OrderManager manager;

        ViewOrdersDialog(JFrame parent, OrderManager manager) {
            super(parent, "View Orders", true);
            this.manager = manager;
            setSize(900, 520);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new GridLayout(1, 2));

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBackground(Color.WHITE);
            JLabel welcome = new JLabel("Welcome to Burgers", SwingConstants.CENTER);
            welcome.setFont(new Font("SansSerif", Font.BOLD, 28));
            welcome.setForeground(new Color(255, 204, 0));
            welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
            ImageIcon icon = new ImageIcon("assets/burgershop.jpg");
            Image scaledImg = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel footer = new JLabel("@ICET", SwingConstants.CENTER);
            footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(Box.createVerticalStrut(30));
            leftPanel.add(welcome);
            leftPanel.add(Box.createVerticalStrut(20));
            leftPanel.add(imgLabel);
            leftPanel.add(Box.createVerticalGlue());
            leftPanel.add(footer);
            leftPanel.add(Box.createVerticalStrut(10));

            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            rightPanel.setBackground(new Color(245,245,245));

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(200, 80, 70));
            headerPanel.setPreferredSize(new Dimension(400, 60));
            JLabel headerLabel = new JLabel("View Orders", SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            rightPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(new Color(245,245,245));
            btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
            btnPanel.add(Box.createVerticalGlue());
            JButton deliveredBtn = makeOrderStatusButton("Delivered Orders");
            JButton processingBtn = makeOrderStatusButton("Processing Orders");
            JButton canceledBtn = makeOrderStatusButton("Canceled Orders");
            btnPanel.add(deliveredBtn);
            btnPanel.add(Box.createVerticalStrut(30));
            btnPanel.add(processingBtn);
            btnPanel.add(Box.createVerticalStrut(30));
            btnPanel.add(canceledBtn);
            btnPanel.add(Box.createVerticalGlue());
            rightPanel.add(btnPanel, BorderLayout.CENTER);

            JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            exitPanel.setBackground(new Color(245,245,245));
            JButton exitBtn = new JButton("Exit");
            exitBtn.setBackground(new Color(220, 80, 70));
            exitBtn.setForeground(Color.WHITE);
            exitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            exitBtn.setFocusPainted(false);
            exitBtn.setPreferredSize(new Dimension(120, 40));
            exitBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 70), 2, true));
            exitBtn.addActionListener(e -> dispose());
            exitPanel.add(exitBtn);
            rightPanel.add(exitPanel, BorderLayout.SOUTH);

            mainPanel.add(leftPanel);
            mainPanel.add(rightPanel);
            add(mainPanel, BorderLayout.CENTER);


            deliveredBtn.addActionListener(e -> new OrderListDialog(SwingUtilities.getWindowAncestor(this), manager, OrderStatus.DELIVERED).setVisible(true));
            processingBtn.addActionListener(e -> new OrderListDialog(SwingUtilities.getWindowAncestor(this), manager, OrderStatus.PREPARING).setVisible(true));
            canceledBtn.addActionListener(e -> new OrderListDialog(SwingUtilities.getWindowAncestor(this), manager, OrderStatus.CANCELLED).setVisible(true));
        }

        private JButton makeOrderStatusButton(String text) {
            JButton btn = new JButton(text);
            btn.setBackground(new Color(220, 80, 70));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(260, 50));
            btn.setMaximumSize(new Dimension(260, 50));
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 70), 2, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }

    static class OrderListDialog extends JDialog {
        OrderListDialog(Window parent, OrderManager manager, OrderStatus status) {
            super(parent, status==OrderStatus.PREPARING?"Processing Orders":status==OrderStatus.DELIVERED?"Delivered Orders":"Canceled Orders", ModalityType.APPLICATION_MODAL);
            setSize(800, 420);
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(200, 80, 70));
            headerPanel.setPreferredSize(new Dimension(800, 60));
            JLabel headerLabel = new JLabel(getTitle(), SwingConstants.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            add(headerPanel, BorderLayout.NORTH);

            String[] columns = {"Order Id", "Customer Id", "Name", "Order QTY", "Total"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);
            table.setFont(new Font("SansSerif", Font.PLAIN, 15));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
            table.setRowHeight(28);
            table.setBackground(Color.WHITE);
            table.setShowGrid(true);
            table.setGridColor(new Color(220,220,220));
            List<Order> orders = manager.viewOrdersByStatus(status);
            for (Order order : orders) {
                model.addRow(new Object[]{
                    order.getOrderID(),
                    order.getCustomer().getCustomerID(),
                    order.getCustomer().getName(),
                    order.getQuantity(),
                    String.format("%.2f", (double)order.getTotal())
                });
            }
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
            add(scrollPane, BorderLayout.CENTER);
            // Bottom panel with Back button
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setBackground(Color.WHITE);
            JButton backBtn = new JButton("Back");
            backBtn.setBackground(new Color(220, 80, 70));
            backBtn.setForeground(Color.WHITE);
            backBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
            backBtn.setFocusPainted(false);
            backBtn.setPreferredSize(new Dimension(100, 35));
            backBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 70), 2, true));
            backBtn.addActionListener(e -> dispose());
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }


    static class UpdateOrderDialog extends JDialog {
        private final OrderManager manager;
        private JTextField tfOrderID;
        private JTextField tfNewQuantity;
        private JComboBox<OrderStatus> statusCombo;
        private JButton updateQtyBtn;
        private JButton updateStatusBtn;

        UpdateOrderDialog(JFrame parent, OrderManager manager) {
            super(parent, "Update Order", true);
            this.manager = manager;
            setSize(400, 250);
            setLocationRelativeTo(parent);
            initUI();
        }

        private void initUI() {
            setLayout(new GridLayout(6, 2, 10, 10));

            add(new JLabel("Order ID:"));
            tfOrderID = new JTextField();
            add(tfOrderID);

            add(new JLabel("New Quantity:"));
            tfNewQuantity = new JTextField();
            add(tfNewQuantity);

            add(new JLabel("New Status:"));
            statusCombo = new JComboBox<>(OrderStatus.values());
            add(statusCombo);

            updateQtyBtn = new JButton("Update Quantity");
            updateStatusBtn = new JButton("Update Status");
            add(updateQtyBtn);
            add(updateStatusBtn);

            JButton closeBtn = new JButton("Close");
            add(new JLabel()); // Empty cell
            add(closeBtn);

            
            updateQtyBtn.addActionListener(e -> updateQuantity());
            updateStatusBtn.addActionListener(e -> updateStatus());
            closeBtn.addActionListener(e -> dispose());
        }

        private void updateQuantity() {
            String orderID = tfOrderID.getText().trim();
            String qtyText = tfNewQuantity.getText().trim();

            if (orderID.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Order ID and new quantity!");
                return;
            }

            try {
                int newQty = Integer.parseInt(qtyText);
                if (newQty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive!");
                    return;
                }

                boolean success = manager.updateOrderQuantity(orderID, newQty);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Quantity updated successfully!");
                    tfOrderID.setText("");
                    tfNewQuantity.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update! Order not found or not in PREPARING status.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity!");
            }
        }

        private void updateStatus() {
            String orderID = tfOrderID.getText().trim();

            if (orderID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Order ID!");
                return;
            }

            OrderStatus newStatus = (OrderStatus) statusCombo.getSelectedItem();
            boolean success = manager.updateOrderStatus(orderID, newStatus);

            if (success) {
                JOptionPane.showMessageDialog(this, "Status updated successfully!");
                tfOrderID.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update! Order not found or not in PREPARING status.");
            }
        }
    }
}
