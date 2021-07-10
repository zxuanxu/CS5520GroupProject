package edu.neu.madcourse.sticktothem.Model;

public class StickerReceiverPair {
    private String sticker;
    private String receiver;

    public StickerReceiverPair(String sticker, String receiver) {
        this.sticker = sticker;
        this.receiver = receiver;
    }

    public String getSticker() {
        return this.sticker;
    }

    public String getReceiver() {
        return this.receiver;
    }

}
