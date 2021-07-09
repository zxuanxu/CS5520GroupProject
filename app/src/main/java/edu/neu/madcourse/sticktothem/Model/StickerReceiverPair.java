package edu.neu.madcourse.sticktothem.Model;

public class StickerReceiverPair {
    private String sticker;
    private String receiver;

    public StickerReceiverPair(String sticker, String sender) {
        this.sticker = sticker;
        this.receiver = sender;
    }

    public String getSticker() {
        return this.sticker;
    }

    public String getReceiver() {
        return this.receiver;
    }

}
