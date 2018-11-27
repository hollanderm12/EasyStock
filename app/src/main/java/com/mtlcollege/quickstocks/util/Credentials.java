package com.mtlcollege.quickstocks.util;

public class Credentials {

    //Replace the string values with the values retrieved from your Intrinio account
    final static String apiKey = "INSERT API KEY HERE";
    final static String username = "INSERT USERNAME HERE";
    final static String password = "INSERT PASSWORD HERE";

    public static String getApiKey() {
        return apiKey;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
}
