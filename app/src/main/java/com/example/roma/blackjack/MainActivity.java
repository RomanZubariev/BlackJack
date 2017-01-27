package com.example.roma.blackjack;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private LinearLayout dealerLayout;
    private LinearLayout playerLayout;
    private LinearLayout controlLayout;
    private Button button;
    private ImageButton deckButton;
    private TextView noteText;
    private TextView dealerScoreView;
    private TextView playerScoreView;
    private Hand playerHand;
    private Hand dealerHand;
    private Deck deck;
    private Deck.DECK_SIZE deckSize;
    public enum GamePhase {VICTORY, DEFEAT, TIE, PLAYER_PHASE, DEALER_PHASE, PREPHASE};
    public static GamePhase gamePhase;
    private SoundPool soundPool;
    private int MAX_STREAMS = 4;
    private int cardSoundID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_land);
        }
        //create soundpool and get sound ID from assets
        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        try {
            cardSoundID = soundPool.load(getAssets().openFd("cardPlace3.wav"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bind all views to xml layout
        dealerLayout = (LinearLayout) findViewById(R.id.dealerLayout);
        playerLayout = (LinearLayout) findViewById(R.id.playerLayout);
        controlLayout = (LinearLayout) findViewById(R.id.controlLayout);
        button = (Button) findViewById(R.id.button);
        noteText = (TextView) findViewById(R.id.noteText);
        dealerScoreView = (TextView) findViewById(R.id.dealerScore);
        playerScoreView = (TextView) findViewById(R.id.playerScore);
        deckButton = (ImageButton) findViewById(R.id.imageButton);
        if (savedInstanceState==null) {
            //get deck Size from preferences and create new Deck
            preferences = getPreferences(MODE_PRIVATE);
            switch (preferences.getInt("deck_size", 0)) {
                case 0:
                    deckSize = Deck.DECK_SIZE.STANDARD52;
                    break;
                case 1:
                    deckSize = Deck.DECK_SIZE.SMALLER36;
                    break;
                default:
                    deckSize = Deck.DECK_SIZE.STANDARD52;
                    break;
            }
            deck = new Deck(deckSize);// create new deck
            prephase();//start the game
        } else { //restore savedInstanceState
            //restore deck
            deck = (Deck) savedInstanceState.getSerializable("Deck");
            //restore hands
            playerHand = (Hand) savedInstanceState.getSerializable("Player_Hand");
            dealerHand = (Hand) savedInstanceState.getSerializable("Dealer_Hand");
            //inflate hand layouts with ImageViews
            for (int counter=0; counter<dealerHand.getHandSize();counter++)
                dealerLayout.addView(getImageView(dealerHand.getCard(counter)));
            for (int counter=0;counter<playerHand.getHandSize();counter++)
                playerLayout.addView(getImageView(playerHand.getCard(counter)));
            //show scores
            dealerScoreView.setText(Integer.toString(dealerHand.getScore()));
            playerScoreView.setText(Integer.toString(playerHand.getScore()));
            switch (savedInstanceState.getInt("Game_Phase")){
                case 0: prephase();//I don't expect it ever triggers
                    break;
                case 1: playerPhase();
                    break;
                case 2: dealerPhase();
                    break;
                case 3: gamePhase=GamePhase.VICTORY; results();
                    break;
                case 4: gamePhase=GamePhase.DEFEAT; results();
                    break;
                case 5: gamePhase=GamePhase.TIE; results();
                    break;
                default: prephase();
                    break;
            }
        }
    }

    public void prephase(){
        deck.shuffle();//shuffle it
        gamePhase=GamePhase.PREPHASE;
        playerHand = new Hand(true);//create player's hand in this activity context
        dealerHand = new Hand(false);//create dealer's hand in this activity context
        //deal two cards to the player
        dealCards(2,playerHand,playerLayout,deck);
        playerScoreView.setText(Integer.toString(playerHand.getScore()));
        //deal two cards to the dealer
        dealCards(2,dealerHand,dealerLayout,deck);
        dealerScoreView.setText(Integer.toString(dealerHand.getScore()));
        playerPhase();//go to the next phase
    }
    public void playerPhase(){
        gamePhase = GamePhase.PLAYER_PHASE;
        noteText.setText("Click the deck to deal card\nor deal to dealer");
        deckButton.setEnabled(true);
        deckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealCards(1,playerHand,playerLayout,deck);//deal 1 card to player when clicking on deck
                playerScoreView.setText(Integer.toString(playerHand.getScore()));
                if (playerHand.getScore()>21) {
                    gamePhase = GamePhase.DEFEAT;
                    results();//go to result phase
                }
                if (playerHand.getScore()==21) dealerPhase();
            }
        });
        button.setText("Deal to dealer");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealerPhase();
            }
        });//let the dealer to draw
        if (playerHand.getScore()==21) dealerPhase();//let the dealer to draw
        if (playerHand.getScore()>21) {
            gamePhase = GamePhase.DEFEAT;
            results();//go to result phase
        }
    }
    public void dealerPhase(){
        gamePhase=GamePhase.DEALER_PHASE;
        dealerLayout.removeViewAt(1);
        dealerHand.faceAllCardsUp();
        dealerLayout.addView(getImageView(dealerHand.getCard(1)));
        noteText.setText("");
        while (dealerHand.getScore()<17) {
            dealCards(1, dealerHand, dealerLayout, deck);
            dealerScoreView.setText(Integer.toString(dealerHand.getScore()));
        }
        if (dealerHand.getScore()>21)
            gamePhase=GamePhase.VICTORY;
        results();
    }
    public void results(){
        deckButton.setEnabled(false);
        button.setText("Start new game");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if ((gamePhase!=GamePhase.DEFEAT)&&(gamePhase!=GamePhase.VICTORY)){
            if (playerHand.getScore()>dealerHand.getScore()) gamePhase=GamePhase.VICTORY;
            if (playerHand.getScore()<dealerHand.getScore()) gamePhase=GamePhase.DEFEAT;
            if (playerHand.getScore()==dealerHand.getScore()) gamePhase=GamePhase.TIE;
        }
        if (gamePhase==GamePhase.DEFEAT)
            noteText.setText("Defeat!");
        if (gamePhase==GamePhase.VICTORY)
            noteText.setText("Victory!");
        if (gamePhase==GamePhase.TIE)
            noteText.setText("Push!");


    }
    private ImageView getImageView(Card card){
        ImageView cardImage = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight=1f;
        cardImage.setAdjustViewBounds(true);
        cardImage.setLayoutParams(params);
        cardImage.setImageResource(getResources().getIdentifier(
                card.getImageId(),null, cardImage.getContext().getPackageName()));
        return  cardImage;
    }
    private void dealCards(int numberToDeal, Hand hand, LinearLayout layout, Deck deck){
        for (int counter = 0; counter < numberToDeal; counter++){
            Card temporaryCard = deck.dealCard();
            hand.addCard(temporaryCard);
            layout.addView(getImageView(temporaryCard));
            //play sound
            soundPool.play(cardSoundID,1,1,0,0,1);
        }
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        menu.add(0, 0, 0, "52 cards in a deck");
        menu.add(0, 1, 1 , "36 cards in a deck");
        menu.getItem(0).setCheckable(true);
        menu.getItem(1).setCheckable(true);
        if (deckSize == Deck.DECK_SIZE.STANDARD52) {
            menu.getItem(0).setChecked(true);
            menu.getItem(0).setEnabled(false);
        }
        if (deckSize == Deck.DECK_SIZE.SMALLER36){
            menu.getItem(1).setChecked(true);
            menu.getItem(1).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        preferences.edit().putInt("deck_size", item.getItemId()).commit();
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putSerializable("Deck", deck);
        state.putSerializable("Dealer_Hand", dealerHand);
        state.putSerializable("Player_Hand", playerHand);
        int intPhase;
        switch(gamePhase){
            case PREPHASE: intPhase=0;
                break;
            case PLAYER_PHASE: intPhase=1;
                break;
            case DEALER_PHASE: intPhase=2;
                break;
            case VICTORY: intPhase=3;
                break;
            case DEFEAT: intPhase=4;
                break;
            case TIE: intPhase=5;
                break;
            default: intPhase=0;
                break;
        }
        state.putInt("Game_Phase",intPhase);
    }
}
