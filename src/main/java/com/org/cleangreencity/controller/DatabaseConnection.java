package com.org.cleangreencity.controller;
import java.sql.*;

public class DatabaseConnection {
    public static Connection databaseLink;

    public static Connection getConnection(){
        String databaseName = "trellu_db";
        String databaseUser = "roott";
        String databasePassword = "root";
        String url = "jdbc:mysql://localhost:3307/" + databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }

        catch(Exception e){
            e.printStackTrace();
        }
        if(databaseLink!=null)
            System.out.println("connected");
        return databaseLink;
    }
}
