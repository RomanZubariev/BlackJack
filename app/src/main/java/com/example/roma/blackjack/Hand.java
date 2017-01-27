package com.example.roma.blackjack;

import java.io.Serializable;
import java.util.ArrayList;

public class Hand implements Serializable{
    private boolean isPlayer; //true -> it is the hand of player, false - of dealer
    private int score;
    private ArrayList<Card> currentHand;
    public Hand(boolean isPlayer){
        this.isPlayer = isPlayer;
        score = 0;
        currentHand = new ArrayList<Card>();
    }
    public void addCard(Card cardToDeal){
        if (isPlayer)
            cardToDeal.setFaceUp(true);//for player card is always faced up
        else
            if ((MainActivity.gamePhase== MainActivity.GamePhase.PREPHASE)&&(currentHand.size()==1))
                cardToDeal.setFaceUp(false); // second card of the dealer in preface is faced down
            else cardToDeal.setFaceUp(true);

        currentHand.add(cardToDeal);
    }
    public int getHandSize(){
        return currentHand.size();
    }
    public int getScore(){
        score = 0;
        int numberOfCardsToCount=0;
        if ((!isPlayer)&&(MainActivity.gamePhase== MainActivity.GamePhase.PREPHASE))
            numberOfCardsToCount = 1;
        else numberOfCardsToCount = currentHand.size();;

        for (int counter=0; counter < numberOfCardsToCount; counter++ ){
            switch (currentHand.get(counter).getFace()){
                case "Ace": score+=11;
                    break;
                case "Deuce": score+=2;
                    break;
                case "Three": score+=3;
                    break;
                case "Four": score+=4;
                    break;
                case "Five": score+=5;
                    break;
                case "Six": score+=6;
                    break;
                case "Seven": score+=7;
                    break;
                case "Eight": score+=8;
                    break;
                case "Nine": score+=9;
                    break;
                case "Ten": score+=10;
                    break;
                case "Jack": score+=10;
                    break;
                case "Queen": score+=10;
                    break;
                case "King": score+=10;
                    break;
                default: score=100;
                    break;
            }
        }
        return score;
    }
    public Card getCard (int i){
        if ((i>=0)&&(i<currentHand.size())) return currentHand.get(i);
            else return null;
    }
    public void faceAllCardsUp (){
        for (Card card:currentHand) {
            card.setFaceUp(true);
        }
    }
}
