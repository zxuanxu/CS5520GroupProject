package edu.neu.madcourse.sticktothem.Model;

import java.util.ArrayList;

public class User {
    private String id;
    private String username;
    public int numOfStickersSent;
    public ArrayList<StickerReceiverPair> stickerReceiverPairArrayList;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        numOfStickersSent = 0;
        stickerReceiverPairArrayList = new ArrayList<>();
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
