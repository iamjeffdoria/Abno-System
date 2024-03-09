package mypackage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class User {
    private Map<String, Integer> bikePrices = new HashMap<>();

    private JTable ordersTable;
    private DefaultTableModel tableModel;
    JFrame frame;
    JPanel contentPanel;
    String[] bikeTypes = {"Mountain Bike - $500", "Road Bike - $700", "City Bike - $400", "Electric Bike - $1000"};
    int[] bikePricesArray = {500, 700, 400, 1000};

    private String username;
    private String fullName;
    private String address;

    public User(String username, String fullName, String address) {
        for (int i = 0; i < bikeTypes.length; i++) {
            bikePrices.put(bikeTypes[i], bikePricesArray[i]);
        }

        initializeOrdersTable();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Bike Type");
        tableModel.addColumn("Price");
        tableModel.addColumn("Address");

        ordersTable = new JTable(tableModel);

        bikePrices.put("Mountain Bike", 500);
        bikePrices.put("Road Bike", 700);
        bikePrices.put("City Bike", 400);
        bikePrices.put("Electric Bike", 1000);

        this.username = username;
        this.fullName = fullName;
        this.address = address;
        frame = new JFrame("User Home Page");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30)); // Dark blue background

        // Create and add components to the frame
        JLabel welcomeLabel = new JLabel("User Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE); // White text color

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(50, 50, 50)); // Dark gray background

        JButton viewProfileButton = createStyledButton("Your profile");
        JButton viewOrdersButton = createStyledButton("Your orders");
        JButton chooseBikeButton = createStyledButton("Choose Bike");
        JButton logoutButton = createStyledButton("Logout");

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(40, 40, 40)); // Slightly lighter dark blue background

        navigationPanel.add(viewProfileButton);
        navigationPanel.add(viewOrdersButton);
        navigationPanel.add(chooseBikeButton);
        navigationPanel.add(logoutButton);

        frame.add(welcomeLabel, BorderLayout.NORTH);
        frame.add(navigationPanel, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
        viewProfileButton.addActionListener(e -> {
            // Logic for viewing profile
            displayUserProfile();
        });

        viewOrdersButton.addActionListener(e -> {
            // Logic for viewing orders
            displayOrders();
        });

        chooseBikeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateContentPanelWithBikeTypes();
            }
        });

        logoutButton.addActionListener(e -> {
            // Logic for logout
            int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // Perform logout actions
                frame.setVisible(false);
                new Login(); // Open login window again
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayOrders();
            }
        });
        viewOrdersButton.addActionListener(e -> {
            // Logic for viewing orders
            displayOrders();
        });
    }
    

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(new Color(70, 130, 180)); // SteelBlue background color
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        button.setMaximumSize(new Dimension(150, 30)); // Set maximum size
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Lighter blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // SteelBlue on exit
            }
        });
        return button;
    }

    private void updateContentPanelWithBikeTypes() {
        contentPanel.removeAll();

        DefaultListModel<String> bikeListModel = new DefaultListModel<>();
        JList<String> bikeList = new JList<>(bikeListModel);
        JScrollPane scrollPane = new JScrollPane(bikeList);

        // Fetch available bikes and prices from the 'bikes' table
        List<String[]> availableBikesAndPrices = getAvailableBikesAndPricesFromDatabase();

        for (String[] bikeInfo : availableBikesAndPrices) {
            String bikeType = bikeInfo[0];
            String bikePrice = bikeInfo[1];
            bikeListModel.addElement(bikeType + " - $" + bikePrice);
        }

        bikeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bikeList.setBackground(new Color(40, 40, 40)); // Slightly lighter dark blue background
        bikeList.setForeground(Color.WHITE);
        bikeList.setFont(new Font("Arial", Font.PLAIN, 16));

        bikeList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click action, similar to button click
                    String selectedBikeType = bikeList.getSelectedValue().split(" - ")[0];
                    storeOrder(selectedBikeType);
                }
            }
        });

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private List<String[]> getAvailableBikesAndPricesFromDatabase() {
        // Fetch the current list of available bikes and prices from the 'bikes' table in the database
        List<String[]> availableBikesAndPrices = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT bike_type, price FROM bikes";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String bikeType = resultSet.getString("bike_type");
                String bikePrice = resultSet.getString("price");
                availableBikesAndPrices.add(new String[]{bikeType, bikePrice});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Notify the user about the error
            notifyOrderPlacement("Failed to fetch available bikes", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return availableBikesAndPrices;
    }


    private void storeOrder(String bikeType) {
        // Store the order details in the 'orders' table
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "INSERT INTO orders (full_name, bike_type, address, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Fetch the price for the selected bike type from the 'bikes' table
            int bikePrice = getPriceForBikeFromDatabase(bikeType);

            if (bikePrice > 0) {
                // Set values in the prepared statement
                preparedStatement.setString(1, fullName);
                preparedStatement.setString(2, bikeType);
                preparedStatement.setString(3, address);
                preparedStatement.setInt(4, bikePrice);

                // Execute the update
                preparedStatement.executeUpdate();

                // Notify the user about the successful order
                notifyOrderPlacement("Order placed successfully!", query, bikePrice);

                // After placing the order, update the "View Orders" section
                displayOrders();
            } else {
                // Notify the user about the error
                notifyOrderPlacement("Failed to fetch bike price", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Notify the user about the error
            notifyOrderPlacement("Failed to place order", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getPriceForBikeFromDatabase(String bikeType) {
        // Fetch the price for the given bike type from the 'bikes' table
        int bikePrice = 0;

        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT price FROM bikes WHERE bike_type = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        	
            preparedStatement.setString(1, bikeType);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                bikePrice = resultSet.getInt("price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Notify the user about the error
            notifyOrderPlacement("Failed to fetch bike price", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return bikePrice;
    }

    

    private void notifyOrderPlacement(String message, String title, int messageType) {
        int messageTypeToUse;

        // Check if the provided messageType is one of the expected values
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
            case JOptionPane.INFORMATION_MESSAGE:
            case JOptionPane.WARNING_MESSAGE:
            case JOptionPane.QUESTION_MESSAGE:
            case JOptionPane.PLAIN_MESSAGE:
                messageTypeToUse = messageType;
                break;
            default:
                // If not, default to INFORMATION_MESSAGE
                messageTypeToUse = JOptionPane.INFORMATION_MESSAGE;
        }

        JOptionPane.showMessageDialog(frame, message, title, messageTypeToUse);
    }

    private void updateContentPanel(String message) {
        contentPanel.removeAll();
        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.WHITE); // White text color
        contentPanel.add(label, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public void updateBikeChoices(List<String> updatedBikes) {
        // Update the list of available bikes for the user
        for (String bike : updatedBikes) {
            bikePrices.put(bike, getPriceForBike(bike));
        }

        // You can call this method when the AdminPanel notifies about updated bike choices
        // For example, call this method after adding a new bike in AdminPanel
        updateContentPanelWithBikeTypes();
    }
    private int getPriceForBike(String bikeType) {
        // Retrieve the price for the given bike type from the map or database
        // For now, let's assume a static map for demonstration purposes
        Map<String, Integer> prices = new HashMap<>();
        prices.put("Mountain Bike", 500);
        prices.put("Road Bike", 700);
        prices.put("City Bike", 400);
        prices.put("Electric Bike", 1000);

        return prices.getOrDefault(bikeType, 0);
    }


    private void initializeOrdersTable() {
        // Fetch orders from the database
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT * FROM orders WHERE full_name = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, fullName);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Initialize the tableModel with columns
            tableModel = new DefaultTableModel();
            tableModel.addColumn("Bike Type");
            tableModel.addColumn("Price");
            tableModel.addColumn("Address");
            tableModel.addColumn("Delete");  // Add "Delete" column

            while (resultSet.next()) {
                String bikeType = resultSet.getString("bike_type");
                int orderPrice = resultSet.getInt("price");
                String orderAddress = resultSet.getString("address");

                // Create a delete button for each row
                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleDeleteOrderButton(bikeType);
                    }
                });

                // Add the data to the table model
                tableModel.addRow(new Object[]{bikeType, orderPrice, orderAddress, deleteButton});
            }

            // Initialize ordersTable before setting the model
            ordersTable = new JTable(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayUserProfile() {
        contentPanel.removeAll();

        JLabel usernameLabel = new JLabel("Username: " + username);
        JLabel fullNameLabel = new JLabel("Full Name: " + fullName);
        JLabel addressLabel = new JLabel("Address: " + address);
        Font largerFont = new Font("Arial", Font.PLAIN, 18);
        usernameLabel.setFont(largerFont);
        usernameLabel.setForeground(Color.WHITE);

        fullNameLabel.setFont(largerFont);
        fullNameLabel.setForeground(Color.WHITE);

        addressLabel.setFont(largerFont);
        addressLabel.setForeground(Color.WHITE);
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(new Color(40, 40, 40)); // Slightly lighter dark blue background
        profilePanel.add(usernameLabel);
        profilePanel.add(fullNameLabel);
        profilePanel.add(addressLabel);

        contentPanel.add(profilePanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void displayOrders() {
        contentPanel.removeAll();

        // Make sure ordersTable is initialized before calling methods on it
        if (ordersTable == null) {
            initializeOrdersTable();
        }

        // Fetch orders from the database
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT * FROM orders WHERE full_name = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, fullName);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the existing data in the table model
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String bikeType = resultSet.getString("bike_type");
                int orderPrice = resultSet.getInt("price");
                String orderAddress = resultSet.getString("address");

                // Add the data to the table model
                tableModel.addRow(new Object[]{bikeType, orderPrice, orderAddress});
            }

            // Set the updated model to the JTable
            ordersTable.setModel(tableModel);

            // Add the JTable to a scroll pane and then to the content panel
            JScrollPane scrollPane = new JScrollPane(ordersTable);
            contentPanel.add(scrollPane, BorderLayout.CENTER);

            // Create a delete button below the table
            JButton deleteButton = createStyledButton("Delete Selected Order");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the selected row index
                    int selectedRow = ordersTable.getSelectedRow();

                    // Check if any row is selected
                    if (selectedRow != -1) {
                        // Get the bike type from the selected row
                        String selectedBikeType = (String) ordersTable.getValueAt(selectedRow, 0);
                        handleDeleteOrderButton(selectedBikeType);
                    } else {
                        // Notify the user to select a row before clicking delete
                        notifyOrderPlacement("Please select an order to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            contentPanel.add(deleteButton, BorderLayout.SOUTH);

            frame.revalidate();
            frame.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleDeleteOrderButton(String bikeType) {
        // Handle the logic to delete the order corresponding to the clicked row
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String deleteQuery = "DELETE FROM orders WHERE full_name = ? AND bike_type = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, bikeType);

            // Execute the delete query
            preparedStatement.executeUpdate();

            // Notify the user about the successful deletion
            notifyOrderPlacement("Order deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // After deleting the order, update the "Your orders" section
            displayOrders();

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Notify the user about the error
            notifyOrderPlacement("Failed to delete order", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Example usage:
                new User("john_doe", "John Doe", "123 Main St");
            }
        });
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }
}