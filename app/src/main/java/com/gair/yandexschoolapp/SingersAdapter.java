package com.gair.yandexschoolapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gair.yandexschoolapp.api.SingersLoader;
import com.gair.yandexschoolapp.db.SingersOpenHelper;
import com.gair.yandexschoolapp.entity.Singer;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class SingersAdapter extends RecyclerView.Adapter<SingersAdapter.SingersHolder> {

    private Activity mActivity;
    private SQLiteDatabase db;

    private ArrayList<Singer> mSingers = new ArrayList<>();

    //Цвет для genre textview
    private int colorPrimary;

    public SingersAdapter(Activity activity) {
        mActivity = activity;
        db = new SingersOpenHelper(mActivity).getWritableDatabase();
        loadAllSingers();
    }

    public SingersAdapter(Activity activity, String genre) {
        mActivity = activity;
        db = new SingersOpenHelper(mActivity).getWritableDatabase();
        loadSingersByGenre(genre);

    }

    @Override
    public SingersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        colorPrimary = ActivityCompat.getColor(mActivity, R.color.colorPrimary);
        return new SingersHolder(view);
    }

    @Override
    public void onBindViewHolder(final SingersHolder holder, int position) {
        final Singer singer = mSingers.get(position);

        Glide.with(mActivity.getBaseContext())
                .load(singer.getCover().getSmall())
                .crossFade()
                .into(holder.image);

        holder.name.setText(singer.getName());
        fillWithGenres(holder.genres_layout, singer);
        holder.songs.setText(singer.getAlbums() + " альбомов, " + singer.getTracks() + " песен");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SingerActivity.createIntent(mActivity, singer);
                ActivityCompat.startActivityForResult(mActivity, intent,SingerListActivity.SINGER_GENRE_CODE, ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity).toBundle());
            }
        });
    }

    /**
     * Добавляет жанры на layout в виде TextView ссылок
     *
     * @param layout
     * @param singer
     */
    private void fillWithGenres(LinearLayout layout, Singer singer) {
        String[] genres = singer.getGenres();
        layout.removeAllViewsInLayout();

        for (int i = 0; i < genres.length; i++) {
            TextView genreTextView = new TextView(mActivity);
            String genre = genres[i] + ", ";

            SpannableString string = new SpannableString(genre);
            string.setSpan(new ForegroundColorSpan(Color.DKGRAY), genre.length() - 2, genre.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (i == genres.length - 1)
                genre = string.toString().substring(0, genre.length() - 2);

            genreTextView.setText(genre);
            genreTextView.setClickable(true);
            genreTextView.setTextColor(colorPrimary);
            layout.addView(genreTextView);

            genreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String genre = ((TextView) v).getText().toString();
                    int lastComaIndex = genre.lastIndexOf(',');
                    // удаляем запятую с конца для некоторых textview
                    if (lastComaIndex > -1)
                        genre = genre.substring(0, lastComaIndex);
                    ((SingerListActivity) mActivity).showSingersByGenre(genre);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSingers.size();
    }


    private void loadAllSingers() {
        mSingers.clear();
        mSingers.addAll(SingersOpenHelper.getAllSingersFromDB(db));

        new SingersLoader().getSingersAsync(new SingersLoader.LoadStatus() {
            @Override
            public void success(ArrayList<Singer> singersList) {
                SingersOpenHelper.saveSingersToDB(db, singersList);
                mSingers.clear();
                mSingers.addAll(SingersOpenHelper.getAllSingersFromDB(db));
                notifyDataSetChanged();
            }

            @Override
            public void error(String message) {
                Log.i("Error", message);
            }
        });
    }

    private void loadSingersByGenre(String genre) {
        mSingers.clear();
        mSingers.addAll(SingersOpenHelper.getSingersByGenreFromDB(db, genre));
    }


    class SingersHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private LinearLayout genres_layout;
        private TextView songs;


        public SingersHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.singer_image);
            name = (TextView) itemView.findViewById(R.id.name);
            genres_layout = (LinearLayout) itemView.findViewById(R.id.genres_holder);
            songs = (TextView) itemView.findViewById(R.id.songs);
        }

    }
}
