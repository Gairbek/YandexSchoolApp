package com.gair.yandexschoolapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class SingerListActivity extends AppCompatActivity {

    public static final int SINGER_GENRE_CODE = 0;

    private static final String TAG = SingerListActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageButton clearSelectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singers_list);
        initRecycler();
        initToolbar();
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_singers));
        clearSelectionButton = (ImageButton) findViewById(R.id.clear_selection_button);
        clearSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });
    }

    /**
     * default recycler initialization
     */
    private void initRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.singers_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        SingersAdapter adapter = new SingersAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View noConnectionView = findViewById(R.id.no_connection_prompt);
        if (!hasInternetConnection()){
            noConnectionView.setVisibility(View.VISIBLE);
        } else {
            noConnectionView.setVisibility(View.GONE);
        }
    }

    public void showSingersByGenre(String genre) {
        toolbar.setTitle(genre);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        SingersAdapter adapter = new SingersAdapter(this, genre);
        recyclerView.setAdapter(adapter);
        clearSelectionButton.setVisibility(View.VISIBLE);
    }

    private void clearSelection(){
        SingersAdapter adapter = new SingersAdapter(this);
        recyclerView.setAdapter(adapter);
        clearSelectionButton.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.all_singers));
        toolbar.setTitleTextColor(Color.WHITE);
    }


    private boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SINGER_GENRE_CODE && resultCode == RESULT_OK && data.getExtras() != null){
            String genre = data.getExtras().getString("genre");
            showSingersByGenre(genre);
        }
    }
}
