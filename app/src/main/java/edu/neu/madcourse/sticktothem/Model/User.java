package edu.neu.madcourse.sticktothem.Model;

import java.util.HashMap;

public class User {
    private String id;
    private String username;
    public int numOfStickersSent;
    public HashMap<String, StickerReceiverPair> stickerReceiverPairs;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
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

}
