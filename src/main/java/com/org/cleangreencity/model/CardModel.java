package com.org.cleangreencity.model;
import com.org.cleangreencity.controller.DatabaseConnection;
import java.sql.*;
import static com.org.cleangreencity.controller.DashboardController.currentDashboard;

public class CardModel {
    public CardModel() {

    }


    public int getCardId() {
        return cardId;
    }

    private int cardId;
    private int dashboardId;
    private String name;
    private String description;
    private int personInCharge;
    private int position;



    public void setCardId(int cardId) {
        this.cardId = cardId;
    }


    public CardModel(int cardId, int dashboardId, String name, String description, int personInCharge, int position) {
        this.cardId = cardId;
        this.dashboardId = dashboardId;
        this.name = name;
        this.description = description;
        this.personInCharge = personInCharge;
        this.position = position;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(int personInCharge) {
        this.personInCharge = personInCharge;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return  "ID-"+cardId+
                "\n" + name +
                "\n" + getUserUserName(personInCharge) ;

    }

    public static String getUserUserName(int cardId) {

        String extractUserIdQuery = "SELECT username FROM t_user WHERE user_id = ?";
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            PreparedStatement extractUserIdQueryStatement = connectDB.prepareStatement(extractUserIdQuery);
            extractUserIdQueryStatement.setInt(1,cardId);
            ResultSet extractUserIdQueryCheck = extractUserIdQueryStatement.executeQuery();

            if (extractUserIdQueryCheck.next()) {

                System.out.println(extractUserIdQueryCheck.getString(1));
                return extractUserIdQueryCheck.getString(1);

            }

        } catch(SQLException e){
            throw new RuntimeException(e);

        }
        return "";
    }
    public static int getLastCardId() {
        String obtainMaxCardId = "SELECT MAX(card_id) FROM t_card";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statementMaxId = connection.createStatement();
             ResultSet resultSet = statementMaxId.executeQuery(obtainMaxCardId)) {

            if (resultSet.next()) {
                return resultSet.getInt(1)+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 9999; // Default value if no cardId is found
    }
    public static boolean cardExists(Connection connection, String cardName) throws SQLException {
        String verifyCardExists = "SELECT count(1) FROM t_card WHERE name = ? and dashboard_id = ?";
        try (PreparedStatement statementCardExist = connection.prepareStatement(verifyCardExists)) {
            statementCardExist.setString(1, cardName);
            statementCardExist.setInt(2, currentDashboard.getDashboardId());
            try (ResultSet queryResultStatementCardExist = statementCardExist.executeQuery()) {
                return queryResultStatementCardExist.next() && queryResultStatementCardExist.getInt(1) >= 1;
            }
        }
    }

    public static boolean addCard(Connection connection, CardModel newTask) throws SQLException {
        String addCardQuery = "INSERT INTO t_card (dashboard_id, name, description, user_id, position) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statementAddCard = connection.prepareStatement(addCardQuery)) {
            statementAddCard.setInt(1, newTask.getDashboardId());
            statementAddCard.setString(2, newTask.getName());
            statementAddCard.setString(3, newTask.getDescription());
            statementAddCard.setInt(4, newTask.getPersonInCharge());
            statementAddCard.setInt(5, newTask.getPosition());
            int rowsAffected = statementAddCard.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
