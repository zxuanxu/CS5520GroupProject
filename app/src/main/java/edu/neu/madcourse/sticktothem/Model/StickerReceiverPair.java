package edu.neu.madcourse.sticktothem.Model;

public class StickerReceiverPair {
    private String sticker;
    private String sender;

    public StickerReceiverPair(String sticker, String sender) {
        this.sticker = sticker;
        this.sender = sender;
    }

    public String getSticker() {
        return this.sticker;
    }

    public String getSender() {
        return this.sender;
    }

}
