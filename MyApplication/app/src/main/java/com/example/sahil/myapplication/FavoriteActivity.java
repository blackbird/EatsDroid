package com.example.sahil.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FavoriteActivity extends AppCompatActivity {

    public FavoriteActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);


        FavoriteFragment favoriteFragment = new FavoriteFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.FavoritesFragmentContainer, favoriteFragment).commit();
    }

}
