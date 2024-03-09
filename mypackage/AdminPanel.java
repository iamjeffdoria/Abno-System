
package mypackage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminPanel {
	 private JButton searchButton;
	    private JPanel searchPanel;
	    private JTextField searchField;
	    private JButton deleteButton; 
	 private List<User> activeUsers = new ArrayList<>();
    private JFrame frame;
    private JPanel contentPanel;

    public AdminPanel(User admin) {
    	
    	 searchPanel = new JPanel(new FlowLayout());
         searchField = new JTextField(20);
         searchButton = new JButton("Search");
         searchPanel.add(new JLabel("Search by Name:"));
         searchPanel.add(searchField);
         searchPanel.add(searchButton);
    	 activeUsers = new ArrayList<>();
        frame = new JFrame("Bike Selling Admin Panel");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add components to the frame
        JLabel titleLabel = new JLabel("Bike Selling Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE); // White text color
        titleLabel.setBackground(new Color(30, 30, 30)); // Dark blue background
        titleLabel.setOpaque(true);

        JPanel navigationBar = createNavigationBar();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(40, 40, 40)); // Slightly lighter dark blue background

        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(navigationBar, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        deleteButton = createStyledButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteButton();
            }
        });
        searchPanel.add(deleteButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createNavigationBar() {
        JPanel navigationBar = new JPanel();
        navigationBar.setBackground(new Color(30, 30, 30)); // Dark blue background
        navigationBar.setLayout(new BoxLayout(navigationBar, BoxLayout.Y_AXIS));

        String[] buttonLabels = {"Home", "View Orders", "Add Bike", "Bikes", "Logout"};
        for (String label : buttonLabels) {
            JButton button = createStyledButton(label);
            navigationBar.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(label);
                }
            });
        }

        return navigationBar;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180)); // SteelBlue background color
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40)); // Set preferred size
        button.setMaximumSize(new Dimension(150, 40)); // Set maximum size
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
    private void handleDeleteButton() {
        // Get the selected row index
        JTable ordersTable = getOrdersTable();
        int selectedRowIndex = ordersTable.getSelectedRow();

        if (selectedRowIndex != -1) {
            // Get the order information from the selected row
            String name = (String) ordersTable.getValueAt(selectedRowIndex, 0);
            String bikeType = (String) ordersTable.getValueAt(selectedRowIndex, 1);
            int orderPrice = (int) ordersTable.getValueAt(selectedRowIndex, 2);
            String orderAddress = (String) ordersTable.getValueAt(selectedRowIndex, 3);

            // Implement the logic to delete the selected row from the database
            deleteOrderFromDatabase(name, bikeType, orderPrice, orderAddress);

            // Refresh the displayed orders
            displayOrders();
        } else {
            // If no row is selected, show a message
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    private JTable getOrdersTable() {
        // Retrieve the ordersTable from the contentPanel
        for (Component component : contentPanel.getComponents()) {
            if (component instanceof JScrollPane) {
                JViewport viewport = ((JScrollPane) component).getViewport();
                if (viewport.getView() instanceof JTable) {
                    return (JTable) viewport.getView();
                }
            }
        }
        return null;
    }

    private void handleButtonClick(String buttonLabel) {
        contentPanel.removeAll();

        switch (buttonLabel) {
            case "Home":
                // Logic for home button
                JOptionPane.showMessageDialog(frame, "Home functionality not implemented yet.");
                break;
            case "View Orders":
                // Logic for view orders button
                displayOrders();
                break;
            case "Add Bike":
                // Logic for add bike button
                addNewBike();
                break;
            case "Bikes":
                // Logic for add bike button
                displayBikes();
                break;
            case "Logout":
                // Logic for logout button
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Perform logout actions
                    frame.setVisible(false);
                    new Login(); // Open login window again
                }
                break;
            default:
                // Handle unexpected button label
                break;
        }

        frame.revalidate();
        frame.repaint();
    }
    private void displayBikes() {
        // Fetch all bikes from the database
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT bike_type, price FROM bikes";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the existing data in the table model
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Bike Type");
            tableModel.addColumn("Price");
            tableModel.addColumn("Edit"); // Added a column for edit button
            tableModel.addColumn("Delete"); // Added a column for delete button

            while (resultSet.next()) {
                String bikeType = resultSet.getString("bike_type");
                int bikePrice = resultSet.getInt("price");

                // Add the data to the table model
                tableModel.addRow(new Object[]{bikeType, bikePrice, "Edit", "Delete"});
            }

            // Set the updated model to the JTable
            JTable bikesTable = new JTable(tableModel);

            // Add an action listener to the table to handle button clicks
            bikesTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int column = bikesTable.getColumnModel().getColumnIndexAtX(evt.getX());
                    int row = evt.getY() / bikesTable.getRowHeight();

                    // Check if the click is within the bounds of the table
                    if (row < bikesTable.getRowCount() && row >= 0 && column < bikesTable.getColumnCount() && column >= 0) {
                        // Check if the clicked column is the "Edit" column
                        if (bikesTable.getValueAt(row, column).equals("Edit")) {
                            handleEditButton(row);
                        } else if (bikesTable.getValueAt(row, column).equals("Delete")) {
                            handleDeleteButton(row);
                        }
                    }
                }
            });

            // Add the JTable to a scroll pane and then to the content panel
            JScrollPane scrollPane = new JScrollPane(bikesTable);
            contentPanel.removeAll(); // Clear existing content
            contentPanel.add(scrollPane, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void handleEditButton(int selectedRowIndex) {
        // Get the selected row index
        JTable bikesTable = getBikesTable();

        if (selectedRowIndex != -1) {
            // Get the bike information from the selected row
            String bikeType = (String) bikesTable.getValueAt(selectedRowIndex, 0);
            int bikePrice = (int) bikesTable.getValueAt(selectedRowIndex, 1);

            // Prompt the admin to enter updated details for the bike
            JTextField updatedBikeTypeField = new JTextField(bikeType);
            JTextField updatedBikePriceField = new JTextField(String.valueOf(bikePrice));

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Bike Type:"));
            panel.add(updatedBikeTypeField);
            panel.add(new JLabel("Price:"));
            panel.add(updatedBikePriceField);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Update Bike Details",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String updatedBikeType = updatedBikeTypeField.getText();
                String updatedPriceStr = updatedBikePriceField.getText();

                try {
                    int updatedBikePrice = Integer.parseInt(updatedPriceStr);

                    // Update the database with the new bike type and price
                    updateBikeInDatabase(bikeType, bikePrice, updatedBikeType, updatedBikePrice);

                    // Refresh the displayed bikes
                    displayBikes();
                } catch (NumberFormatException e) {
                    // Notify the admin about the invalid price
                    JOptionPane.showMessageDialog(frame, "Invalid price format. Please enter a valid number.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // If no row is selected, show a message
            JOptionPane.showMessageDialog(frame, "Please select a row to edit.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    
    private void handleDeleteButton(int selectedRowIndex) {
        // Implement the logic to delete the selected row from the database
        JTable bikesTable = getBikesTable();

        if (selectedRowIndex != -1) {
            // Get the bike information from the selected row
            String bikeType = (String) bikesTable.getValueAt(selectedRowIndex, 0);
            int bikePrice = (int) bikesTable.getValueAt(selectedRowIndex, 1);

            // Implement the logic to delete the selected row from the database
            deleteBikeFromDatabase(bikeType, bikePrice);

            // Refresh the displayed bikes
            displayBikes();
        } else {
            // If no row is selected, show a message
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void updateBikeInDatabase(String oldBikeType, int oldBikePrice, String updatedBikeType, int updatedBikePrice) {
        // Implement the logic to update the bike in the database
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String password = "";
        String updateQuery = "UPDATE bikes SET bike_type = ?, price = ? WHERE bike_type = ? AND price = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, updatedBikeType);
            preparedStatement.setInt(2, updatedBikePrice);
            preparedStatement.setString(3, oldBikeType);
            preparedStatement.setInt(4, oldBikePrice);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Notify the admin about the successful update
                JOptionPane.showMessageDialog(frame, "Bike updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Notify the admin about the failure
                JOptionPane.showMessageDialog(frame, "Failed to update bike. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Notify the admin about the SQL exception
            JOptionPane.showMessageDialog(frame, "Failed to update bike. SQL Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBikeFromDatabase(String bikeType, int bikePrice) {
        // Implement the logic to delete the bike from the database
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String password = "";
        String deleteQuery = "DELETE FROM bikes WHERE bike_type = ? AND price = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, bikeType);
            preparedStatement.setInt(2, bikePrice);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Notify the admin about the successful deletion
                JOptionPane.showMessageDialog(frame, "Bike deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Notify the admin about the failure
                JOptionPane.showMessageDialog(frame, "Failed to delete bike. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Notify the admin about the SQL exception
            JOptionPane.showMessageDialog(frame, "Failed to delete bike. SQL Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTable getBikesTable() {
        // Retrieve the bikesTable from the contentPanel
        for (Component component : contentPanel.getComponents()) {
            if (component instanceof JScrollPane) {
                JViewport viewport = ((JScrollPane) component).getViewport();
                if (viewport.getView() instanceof JTable) {
                    return (JTable) viewport.getView();
                }
            }
        }
        return null;
    }
    private void addNewBike() {
        // Prompt the admin to enter details for the new bike
        JTextField bikeTypeField = new JTextField();
        JTextField bikePriceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Bike Type:"));
        panel.add(bikeTypeField);
        panel.add(new JLabel("Price:"));
        panel.add(bikePriceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Bike Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newBikeType = bikeTypeField.getText();
            String priceStr = bikePriceField.getText();

            try {
                int newBikePrice = Integer.parseInt(priceStr);

                // Update the database with the new bike type and price
                addBikeToDatabase(newBikeType, newBikePrice);

                // Fetch the updated list of available bikes from the database
                List<String> updatedBikes = getAvailableBikes();

                // Update bike choices for all active users
                updateBikeChoicesForUsers();

                // Notify the admin about the successful addition
                notifyOrderPlacement("Bike added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                // Notify the admin about the invalid price
                notifyOrderPlacement("Invalid price format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void notifyOrderPlacement(String string, String string2, int informationMessage) {
		// TODO Auto-generated method stub
		
	}

    private void addBikeToDatabase(String bikeType, int bikePrice) {
        // JDBC URL, username, and password of MySQL server
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String password = "";

        // SQL query to insert a new bike into the 'bikes' table
        String insertQuery = "INSERT INTO bikes (bike_type, price) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, bikeType);
            preparedStatement.setInt(2, bikePrice);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Notify the admin about the successful addition
                notifyOrderPlacement("Bike added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Notify the admin about the failure
                notifyOrderPlacement("Failed to add bike. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Fetch the updated list of available bikes from the database
            List<String> updatedBikes = getAvailableBikes();

            // Update bike choices for all active users
            updateBikeChoicesForUsers();

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Notify the admin about the SQL exception
            notifyOrderPlacement("Failed to add bike. SQL Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateBikeChoicesForUsers() {
        // Get the updated list of available bikes
        List<String> updatedBikes = getAvailableBikes();

        // Iterate through the list of active users and notify them about the updated bike choices
        for (User user : activeUsers) {
            user.updateBikeChoices(updatedBikes);
        }
    }
    

	    public void addUser(User user) {
	        // Add a user to the list of active users
	        activeUsers.add(user);
	    }

	 // ... (other methods)

	    private void displayOrders() {
	        // Fetch all orders from the database
	        String url = "jdbc:mysql://localhost:3306/bikedb";
	        String user = "root";
	        String pass = "";
	        String query;

	        // Check if the search field is empty
	        if (searchField.getText().isEmpty()) {
	            // If empty, fetch all orders
	            query = "SELECT full_name, bike_type, price, address FROM orders";
	        } else {
	            // If not empty, fetch orders based on the search criteria
	            query = "SELECT full_name, bike_type, price, address FROM orders WHERE full_name LIKE ?";
	        }

	        try (Connection connection = DriverManager.getConnection(url, user, pass);
	             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	            // Set the search criteria for the prepared statement if needed
	            if (!searchField.getText().isEmpty()) {
	                String searchCriteria = "%" + searchField.getText() + "%";
	                preparedStatement.setString(1, searchCriteria);
	            }

	            ResultSet resultSet = preparedStatement.executeQuery();

	            // Clear the existing data in the table model
	            DefaultTableModel tableModel = new DefaultTableModel();
	            tableModel.addColumn("Name");
	            tableModel.addColumn("Bike Type");
	            tableModel.addColumn("Price");
	            tableModel.addColumn("Address");

	            while (resultSet.next()) {
	                String name = resultSet.getString("full_name");
	                String bikeType = resultSet.getString("bike_type");
	                int orderPrice = resultSet.getInt("price");
	                String orderAddress = resultSet.getString("address");

	                // Add the data to the table model
	                tableModel.addRow(new Object[]{name, bikeType, orderPrice, orderAddress});
	            }

	            // Set the updated model to the JTable
	            JTable ordersTable = new JTable(tableModel);

	            // Add the JTable to a scroll pane and then to the content panel
	            JScrollPane scrollPane = new JScrollPane(ordersTable);
	            contentPanel.removeAll(); // Clear existing content
	            contentPanel.add(searchPanel, BorderLayout.NORTH);
	            contentPanel.add(scrollPane, BorderLayout.CENTER);

	            // Add ActionListener for the search button
	            searchButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    displayOrders(); // Refresh the displayed orders based on the search criteria
	                }
	            });

	            contentPanel.revalidate();
	            contentPanel.repaint();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	    private void deleteOrderFromDatabase(String name, String bikeType, int orderPrice, String orderAddress) {
	        // Implement the logic to delete the order from the database
	        String url = "jdbc:mysql://localhost:3306/bikedb";
	        String user = "root";
	        String password = "";
	        String deleteQuery = "DELETE FROM orders WHERE full_name = ? AND bike_type = ? AND price = ? AND address = ?";

	        try (Connection connection = DriverManager.getConnection(url, user, password);
	             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

	            // Set parameters for the prepared statement
	            preparedStatement.setString(1, name);
	            preparedStatement.setString(2, bikeType);
	            preparedStatement.setInt(3, orderPrice);
	            preparedStatement.setString(4, orderAddress);

	            // Execute the update
	            int rowsAffected = preparedStatement.executeUpdate();

	            if (rowsAffected > 0) {
	                // Notify the admin about the successful deletion
	                notifyOrderPlacement("Order deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            } else {
	                // Notify the admin about the failure
	                notifyOrderPlacement("Failed to delete order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            // Notify the admin about the SQL exception
	            notifyOrderPlacement("Failed to delete order. SQL Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    // ... (other methods)

    private List<String> getAvailableBikes() {
        // Implement this method to fetch the current list of available bikes from the database or elsewhere
        // For demonstration, I'll return a static list
        return List.of("Mountain Bike", "Road Bike", "City Bike", "Electric Bike", "New Bike Type");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminPanel(null);
            }
        });
    }
}