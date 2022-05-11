package ru.aikozhaev;
//import org.jsoup.Connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.*;
import java.sql.Connection;


public class MySQLConnection {
    private String userName;
    private String password;
    private String connectionURL;

    private static int count = 2;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getUserName() {
        return userName;
    }


    public String getPassword() {
        return password;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public MySQLConnection() {
    }

    public Connection getMySQLConnection() throws ClassNotFoundException {
        Connection connection = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {
            connection = DriverManager.getConnection(this.connectionURL, this.userName, this.password);
            if (connection != null)
                System.out.println("Successful connected to the database");
            else System.out.println("Connection is failed");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public boolean hasRecord() {
        String sql = "SELECT context from message";
        try {
            PreparedStatement ps = this.getMySQLConnection().prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) return true;
            else return false;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
  public void insertIntoDatabase(String textMessage) {
        StringBuilder sql = new StringBuilder("INSERT INTO message (context) VALUES " + textMessage);
        try {
            PreparedStatement statement = this.getMySQLConnection().prepareStatement(sql.toString());
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void insertIntoDatabaseHeaders(String messageId, String destination, int deliveryMode,
                                          long timeStamp, long expiration, int priority, String correlationId, String type, boolean redelivered) {
        StringBuilder sql = new StringBuilder("INSERT INTO headers (id_message, destination, deliverymode, timestamp," +
                "expiration, priority, id_correlation, type, redelivered) VALUES " + messageId + ", " + destination + ", " + deliveryMode + ", " + timeStamp
                + ", " + expiration +", " + priority +", " +correlationId + ", " + type+ ", " + redelivered);
        try {
            PreparedStatement statement = this.getMySQLConnection().prepareStatement(sql.toString());
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}