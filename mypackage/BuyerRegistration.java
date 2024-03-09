package mypackage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BuyerRegistration {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField addressField;
    private JButton registerButton;
    private JButton backButton; // Added back button

    public BuyerRegistration() {
        frame = new JFrame("Buyer Registration");
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30)); // Dark blue background

        JPanel registrationPanel = new JPanel(new GridBagLayout());
        registrationPanel.setBackground(new Color(30, 30, 30)); // Dark blue background
        registrationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "User Registration",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18),
                Color.WHITE)); // Set label color to white

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Set insets for spacing

        // Back button moved to the most upper left
        backButton = createStyledButton("Back");
        registrationPanel.add(backButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.WHITE);
        registrationPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        registrationPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        registrationPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        registrationPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fullNameLabel.setForeground(Color.WHITE);
        registrationPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        fullNameField = new JTextField();
        fullNameField.setPreferredSize(new Dimension(200, 25));
        registrationPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addressLabel.setForeground(Color.WHITE);
        registrationPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 25));
        registrationPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        registerButton = createStyledButton("Register");
        registrationPanel.add(registerButton, gbc);

        frame.add(registrationPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180)); // SteelBlue background color
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30)); // Set preferred size
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Lighter blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // SteelBlue on exit
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == backButton) {
                    frame.dispose(); // Close the registration window
                    new Login(); // Open the login window
                } else if (e.getSource() == registerButton) {
                    // Handle registration logic
                    String username = usernameField.getText();
                    char[] passwordChars = passwordField.getPassword();
                    String password = new String(passwordChars);
                    String fullName = fullNameField.getText();
                    String address = addressField.getText();

                    if (!username.isEmpty() && passwordChars.length > 0 && !fullName.isEmpty() && !address.isEmpty()) {
                        boolean registrationSuccess = storeRegistrationData(username, password, fullName, address);

                        if (registrationSuccess) {
                            JOptionPane.showMessageDialog(frame, "Registration successful!");
                            frame.dispose(); // Close the registration window
                            new Login(); // Open the login window
                        } else {
                            JOptionPane.showMessageDialog(frame, "Registration failed. Please try again.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    }
                }
            }
        });
        return button;
    }

    private boolean storeRegistrationData(String username, String password, String fullName, String address) {
        // JDBC database connection parameters for MySQL
        String url = "jdbc:mysql://localhost:3306/bikedb";
        String user = "root";
        String pass = "";
        String query = "INSERT INTO buyers (username, password, full_name, address) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, fullName);
            preparedStatement.setString(4, address);

            preparedStatement.executeUpdate();
            return true; // Registration successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Registration failed
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BuyerRegistration();
            }
        });
    }
}
