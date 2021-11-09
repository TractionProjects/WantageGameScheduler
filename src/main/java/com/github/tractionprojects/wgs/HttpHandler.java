package com.github.tractionprojects.wgs;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler
{
    public static BufferedReader getRequest(URL url, OAuth2AccessToken currentUsersToken) throws Exception
    {
        HttpURLConnection conn = setupConnection(url);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", currentUsersToken.getTokenType().getValue() + " " + currentUsersToken.getTokenValue());
        conn.setRequestProperty("Content-Type", "application/json");
        return handleConnection(conn);
    }

    private static HttpURLConnection setupConnection(URL url) throws Exception
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "wgs");
        return conn;
    }

    private static BufferedReader handleConnection(HttpURLConnection conn) throws Exception
    {
        switch (conn.getResponseCode())
        {
            case 200:
                return new BufferedReader(new InputStreamReader((conn.getInputStream())));
            case 304:
                //no change
                return null;
            default:
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " From:" + conn.getURL());
        }
    }
}
