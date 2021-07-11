package edu.neu.madcourse.sticktothem.Model;

public class StickerSenderPair {
    private String sticker;
    private String sender;

    public StickerSenderPair(String sticker, String sender) {
        this.sticker = sticker;
        this.sender = sender;
    }

    public StickerSenderPair() {
    }

    public String getSticker() {
        return this.sticker;
    }

    public String getSender() {
        return this.sender;
    }

}
