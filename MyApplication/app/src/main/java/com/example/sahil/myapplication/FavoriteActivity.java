package com.example.sahil.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class FavoriteActivity extends AppCompatActivity {

    public FavoriteActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.abs_layout);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView settings = (TextView)getSupportActionBar().getCustomView().findViewById(R.id.mytitle);
            settings.setText("Settings");
        }


        FavoriteFragment favoriteFragment = new FavoriteFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.FavoritesFragmentContainer, favoriteFragment).commit();
    }

}
