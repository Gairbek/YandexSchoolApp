package com.gair.yandexschoolapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gair.yandexschoolapp.db.SingersOpenHelper;
import com.gair.yandexschoolapp.entity.Singer;

/**
 * Created by Gairbek on 20.04.2016.
 */
public class SingerActivity extends AppCompatActivity {

    private static final String ID = "ID";
    private Singer mSinger;

    public static Intent createIntent(Activity activity, Singer singer) {
        Intent intent = new Intent(activity, SingerActivity.class);
        intent.putExtra(ID, singer.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);
        createSinger();
        setUpToolbar();
        initViews();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setTitle(mSinger.getName());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.finishAfterTransition(SingerActivity.this);
                }
            });
        }
    }

    private void createSinger() {
        Bundle extras = getIntent().getExtras();
        // finish activity if no data
        if (extras == null || !extras.containsKey(ID)) {
            ActivityCompat.finishAfterTransition(this);
            return;
        }
        String id = extras.getString(ID);
        SQLiteDatabase db = new SingersOpenHelper(this).getWritableDatabase();
        mSinger = SingersOpenHelper.getSingerById(db, id);
        db.close();
    }

    private void initViews() {
        ImageView profile = (ImageView) findViewById(R.id.profile_image);
        Glide.with(this)
                .load(mSinger.getCover().getBig())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .crossFade(400)
                .into(profile);

        LinearLayout layout = (LinearLayout) findViewById(R.id.genres_holder);
        fillWithGenres(layout, mSinger);
        TextView albums = (TextView) findViewById(R.id.albums);
        albums.setText(mSinger.getAlbums() + " альбомов");
        TextView songs = (TextView) findViewById(R.id.songs);
        songs.setText(mSinger.getTracks() + " песни");
        TextView description = (TextView) findViewById(R.id.description);
        String singerDescription = mSinger.getDescription().substring(0,1).toUpperCase() + mSinger.getDescription().substring(1);
        description.setText(singerDescription);
        TextView link = (TextView) findViewById(R.id.link);
        link.setText(mSinger.getLink());
    }

    /**
     *  Добавляет жанры в виде TextView ссылок на layout
     * @param layout
     * @param singer
     */
    private void fillWithGenres(LinearLayout layout, Singer singer){
        String[] genres = singer.getGenres();
        layout.removeAllViewsInLayout();

        for (int i = 0; i < genres.length; i++) {
            TextView genreTextView = new TextView(this);

            String genre = genres[i] + ", ";
            SpannableString string = new SpannableString(genre);
            string.setSpan(new ForegroundColorSpan(Color.DKGRAY), genre.length() - 2, genre.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (i == genres.length - 1)
                genre = string.toString().substring(0, genre.length() - 2);

            genreTextView.setText(genre);
            genreTextView.setClickable(true);
            int primaryColor = getResources().getColor(R.color.colorPrimary);
            genreTextView.setTextColor(primaryColor);
            layout.addView(genreTextView);

            genreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String genre = ((TextView) v).getText().toString();
                    int lastComaIndex = genre.lastIndexOf(',');
                    // удаляем запятую с конца для некоторых textview
                    if (lastComaIndex > -1)
                        genre = genre.substring(0, lastComaIndex);

                    Intent intent = new Intent();
                    intent.putExtra("genre", genre);
                    setResult(RESULT_OK, intent);
                    ActivityCompat.finishAfterTransition(SingerActivity.this);
                }
            });
        }
    }
}
