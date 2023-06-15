package org.example.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StationsDB {
    //establishes connection to the postgres DB running in a docker container

    private final static String DRIVER = "postgresql";
    private final static String HOST = "localhost";
    private final static int PORT = 30083;
    private final static String DATABASE_NAME = "customerdb";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl());
    }

    private static String getUrl(){
        // jdbc:Driver://HOST:PORT/DATABASE_NAME?username=USERNAME?password=PASSWORD
        return String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s",
                DRIVER,
                HOST,
                PORT,
                DATABASE_NAME,
                USERNAME,
                PASSWORD
        );
    }


}
