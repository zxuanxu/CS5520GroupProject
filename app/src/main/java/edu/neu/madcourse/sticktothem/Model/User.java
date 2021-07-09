package edu.neu.madcourse.sticktothem.Model;

import java.util.HashMap;
import java.util.List;

public class User {
    private String id;
    private String username;
    public int numOfStickersSent;
    public HashMap<String, List<String>> stickersReceived;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        numOfStickersSent = 0;
        stickersReceived = new HashMap<>();
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

    public HashMap<String, List<String>> getStickersReceived() {
        return new HashMap<>(stickersReceived);
    }

}
