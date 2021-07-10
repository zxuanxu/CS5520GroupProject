package edu.neu.madcourse.sticktothem.Model;

import java.util.HashMap;

public class User {
    private String id;
    private String username;
    private String token;
    public int numOfStickersSent;
    public HashMap<String, StickerSenderPair> stickerReceiverPairs;

    public User(String id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
        numOfStickersSent = 0;
        stickerReceiverPairs = new HashMap<>();
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getNumOfStickersSent() {
        return numOfStickersSent;
    }

    public String getToken() {
        return token;
    }
}
