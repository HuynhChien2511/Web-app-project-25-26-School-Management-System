package com.school.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            
            if (input != null) {
                props.load(input);
                URL = props.getProperty("db.url");
                USERNAME = props.getProperty("db.username");
                PASSWORD = props.getProperty("db.password");
                DRIVER = props.getProperty("db.driver");
                
                Class.forName(DRIVER);
            } else {
                // Fallback to default values
                URL = "jdbc:mysql://127.0.0.1:3306/school_management";
                USERNAME = "root";
                PASSWORD = "1234567890";
                DRIVER = "com.mysql.cj.jdbc.Driver";
                Class.forName(DRIVER);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
