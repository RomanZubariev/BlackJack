package com.example.roma.blackjack;


import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.Serializable;
import java.security.SecureRandom;

public class Deck implements Serializable{
    public enum DECK_SIZE {STANDARD52, SMALLER36};
    private int currentCard;
    private static int numberOfCards;
    private Card[] deck;
    private static final SecureRandom random = new SecureRandom();
    private static String[] faces;
    private static final String[] SUITS = { "Hearts", "Diamonds", "Clubs", "Spades" };
    public Deck(){
        numberOfCards = 52;
        faces = new String[] { "Ace", "Deuce", "Three", "Four", "Five", "Six","Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King" };
        deck = new Card[numberOfCards];
        for (int count = 0; count<deck.length; count++)
            deck[count] = new Card(faces[count% faces.length], SUITS[count/ faces.length]);
        currentCard = 0;

    }
    public Deck(DECK_SIZE deckSize){
        if (deckSize==DECK_SIZE.STANDARD52) {
            numberOfCards = 52;
            faces = new String[] { "Ace", "Deuce", "Three", "Four", "Five", "Six","Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King" };
        }
        if (deckSize == DECK_SIZE.SMALLER36) {
            numberOfCards = 36;
            faces = new String[] { "Ace", "Six","Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King" };
        }
        deck = new Card[numberOfCards];
        for (int count = 0; count<deck.length; count++)
            deck[count] = new Card(faces[count% faces.length], SUITS[count/ faces.length]);
        currentCard = 0;
    }
    public void shuffle(){
        for (int count = 0; count < deck.length; count++){
            int replaceFor = random.nextInt(numberOfCards);
            Card temporary = deck[count];
            deck[count] = deck[replaceFor];
            deck[replaceFor] = temporary;
        }
        currentCard = 0;
    }
    public Card dealCard(){
        if (currentCard< numberOfCards) {
            Card toDeal = deck[currentCard];
            currentCard++;
            return toDeal;
        } else return null;
    }
}
