package com.zipcode.stardust.model;

public enum ReactionType {

    LIKE("👍"),
    DISLIKE("👎"),
    LOVE("❤️"),
    LAUGH("😂");

    private final String emoji;

    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}