package com.example.roma.blackjack;

public class Card {
    private boolean isFaceUp;
    private final String face;
    private final String suit;
    public Card(String cardFace, String cardSuit){
        face = cardFace;
        suit = cardSuit;
        isFaceUp=false;
    }
    public String getFace(){
        return face;
    }
    public String getSuit(){
        return suit;
    }
    public String getImageId(){
        String imageId = "";
        if (isFaceUp) {
            imageId += "@drawable/" + face.toLowerCase() + "_of_" + suit.toLowerCase();
        } else imageId = "@drawable/cardback";
        return imageId;
    }
    public void setFaceUp(boolean isFaceUp){
        this.isFaceUp = isFaceUp;
    }
}
