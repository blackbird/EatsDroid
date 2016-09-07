package com.example.sahil.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class FavoriteActivity extends AppCompatActivity {



    public FavoriteActivity() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            return true;
        }
        return super.onOptionsItemSelected(item);
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
