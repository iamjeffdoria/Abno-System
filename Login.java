package mypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeComboBox;

    private static User currentUser; // Store the current user

    public Login() {
        frame = new JFrame("Login");
        frame.setSize(500, 250);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30)); // Dark blue background

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(30, 30, 30)); // Dark blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");

        usernameLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);
        userTypeLabel.setForeground(Color.WHITE);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // Set preferred size for text fields
        usernameField.setPreferredSize(new Dimension(250, 25));
        passwordField.setPreferredSize(new Dimension(250, 25));

        String[] userTypes = {"User", "Admin"};
        userTypeComboBox = new JComboBox<>(userTypes);

        // Set preferred size for combo box
        userTypeComboBox.setPreferredSize(new Dimension(250, 25));

        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST; // Align to the right
        loginPanel.add(registerButton, gbc);

        frame.add(loginPanel, BorderLayout.CENTER);

     // Inside the loginButton.addActionListener method
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your login logic here
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String userType = (String) userTypeComboBox.getSelectedItem();

                // Check if the selected user type is "User"
                if ("User".equals(userType)) {
                    // Validate the credentials against the database
                    if (validateCredentials(username, String.valueOf(password))) {
                        // Successful login
                        currentUser = fetchUserDetails(username); // Store the current user
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                        frame.dispose(); // Close the login window
                        // Open the main application window for regular users
                    } else {
                        // Failed login
                        JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("Admin".equals(userType)) {
                    // Check if the entered username and password are "admin"
                    if ("admin".equals(username) && "admin".equals(String.valueOf(password))) {
                        // Successful admin login
                       
                        JOptionPane.showMessageDialog(frame, "Admin login successful!");
                        frame.dispose(); // Close the login window
                        // Open the admin panel
                        new AdminPanel(currentUser);
                    } else {
                        // Failed admin login
                        JOptionPane.showMessageDialog(frame, "Invalid admin username or password", "Admin Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the registration form
                new BuyerRegistration();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180)); // SteelBlue background color
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
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

    private boolean validateCredentials(String username, String password) {
        // JDBC database connection parameters for MySQL
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT * FROM buyers WHERE username = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if there is a matching record
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Validation failed
        }
    }

    private User fetchUserDetails(String username) {
        // Fetch user details from the database based on the username
        // You need to implement this method based on your database schema
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "SELECT * FROM buyers WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve user details from the result set
                    String fullName = resultSet.getString("full_name");
                    String address = resultSet.getString("address");

                    // Return a User object with the retrieved details
                    return new User(username, fullName, address);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Return a default User object if details retrieval fails
        return new User(username, "Unknown", "Unknown Address");
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	;
                new Login();
            }
        });
    }
}
