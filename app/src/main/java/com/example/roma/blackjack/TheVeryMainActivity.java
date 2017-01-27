package com.example.roma.blackjack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by roma on 09.08.16.
 */
public class TheVeryMainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
    }
    @Override
    protected void onStart(){
        super.onStart();
        startActivity(new Intent(this, MainActivity.class));

    }
}
