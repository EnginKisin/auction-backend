package com.example.auction.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void init() {
        String serverUrl = datasourceUrl.split(";")[0];
        String dbName = "auctionDB";

        try (Connection connection = DriverManager.getConnection(serverUrl + ";encrypt=true;trustServerCertificate=true", username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = '" + dbName + "') CREATE DATABASE " + dbName);
            
        } catch (SQLException e) {
            //
        }
    }
}
