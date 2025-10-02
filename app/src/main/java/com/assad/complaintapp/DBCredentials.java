package com.assad.complaintapp;

public class DBCredentials {
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@//192.168.0.0:1521/DB";
    String username = "master";
    String password = "master";

    public DBCredentials() {

    }
    public String getDriver() {
        return driver;
    }
    public String getUrl() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }


}
