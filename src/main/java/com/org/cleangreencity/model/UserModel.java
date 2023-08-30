package com.org.cleangreencity.model;

import com.org.cleangreencity.controller.DatabaseConnection;
import static com.org.cleangreencity.controller.DashboardController.currentDashboard;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserModel {

    public static UserModel instance;
    private int user_id;
    private String username;
    private String password;
    private String email;

    public UserModel(int user_id, String username, String password, String firstname, String lastname, String email) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserModel() {
        // Private constructor to prevent instaciation
    }

    public static UserModel getInstance() {
        if (instance == null) {
            instance = new UserModel();
        }
        return instance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static boolean userExists(Connection connection, String username) throws SQLException {
        String obtainUserId = "SELECT user_id FROM t_user WHERE username = ?";
        try (PreparedStatement statementUsername = connection.prepareStatement(obtainUserId)) {
            statementUsername.setString(1, username);
            try (ResultSet queryResultStatementUsername = statementUsername.executeQuery()) {
                return queryResultStatementUsername.next() && queryResultStatementUsername.getInt(1) != 0;
            }
        }
    }

    public static boolean userIsMemberInCurrentDashboard(Connection connection, String username) throws SQLException {


        int obtainUserId= getUserID(username);
        String userIsMember ="SELECT Count(1) from r_user_dashboard where user_id= ? AND dashboard_id=?  ";
        try (PreparedStatement statementUsername = connection.prepareStatement(userIsMember)) {
            statementUsername.setInt(1, obtainUserId);
            statementUsername.setInt(2, currentDashboard.getDashboardId());
            try (ResultSet queryResultStatementUsername = statementUsername.executeQuery()) {
                return queryResultStatementUsername.next() && queryResultStatementUsername.getInt(1) != 0;
            }
        }
    }
    public static int getUserID(String username) {

        String extractUserIdQuery = "SELECT user_id FROM t_user WHERE username = ?";
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            PreparedStatement extractUserIdQueryStatement = connectDB.prepareStatement(extractUserIdQuery);
            extractUserIdQueryStatement.setString(1, username);
            ResultSet extractUserIdQueryCheck = extractUserIdQueryStatement.executeQuery();

            if (extractUserIdQueryCheck.next()) {

                return extractUserIdQueryCheck.getInt(1);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);

        }
        return -1;
    }




}
