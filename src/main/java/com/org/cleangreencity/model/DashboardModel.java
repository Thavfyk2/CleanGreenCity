package com.org.cleangreencity.model;
import com.org.cleangreencity.controller.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"title", "users", "lists"})

public class DashboardModel {

    private int dashboardId;
    private String title;

    private ArrayList<UserModel> teamList=new ArrayList<UserModel>();
    private ArrayList<CardModel> tasks=new ArrayList<CardModel>();

    public DashboardModel() {

    }

    public ArrayList<UserModel> getTeamList() {
        return teamList;
    }

    public void setTeamList(UserModel teamMember) {
        this.teamList.add(teamMember);
    }

    public ArrayList<CardModel> getTasks() {
        return tasks;
    }

    public void setTasks(CardModel tasks) {
        this.tasks.add(tasks);
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static int getCurrentDashboardNameID(String currentDashboard) {

        String extractUserIdQuery = "SELECT dashboard_id FROM t_dashboard WHERE title = ?";
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            PreparedStatement extractUserIdQueryStatement = connectDB.prepareStatement(extractUserIdQuery);
            extractUserIdQueryStatement.setString(1, currentDashboard);
            ResultSet extractUserIdQueryCheck = extractUserIdQueryStatement.executeQuery();

            if (extractUserIdQueryCheck.next()) {

                return extractUserIdQueryCheck.getInt(1);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);

        }
        return -1;
    }

    public boolean memberExist(UserModel user) {
        return teamList.contains(user);
    }
}
