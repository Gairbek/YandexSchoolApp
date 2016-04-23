package com.gair.yandexschoolapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gair.yandexschoolapp.entity.Cover;
import com.gair.yandexschoolapp.entity.Singer;

import java.util.ArrayList;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Gairbek on 21.04.2016.
 */
public class SingersOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "singersapp.db";
    private static final int DATABASE_VERSION = 1;

    static {
        setUpCupboard();
        cupboard().register(Singer.class);
        cupboard().register(Cover.class);
    }

    public SingersOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

    public static void dropSingerTable(SQLiteDatabase db) {
        db.delete("Singer", null, null);
    }

    public static void saveSingersToDB(SQLiteDatabase db, ArrayList<Singer> singers) {
        dropSingerTable(db);
        cupboard().withDatabase(db).put(singers);
    }

    public static ArrayList<Singer> getAllSingersFromDB(SQLiteDatabase db) {
        return (ArrayList<Singer>) cupboard().withDatabase(db).query(Singer.class).query().list();
    }

    public static ArrayList<Singer> getSingersByGenreFromDB(SQLiteDatabase db, String genre) {
        return (ArrayList<Singer>) cupboard().withDatabase(db).query(Singer.class).withSelection("genres LIKE '%" + genre + "%'").query().list();
    }

    public static Singer getSingerById(SQLiteDatabase db, String id) {
        return cupboard().withDatabase(db).query(Singer.class).withSelection("id = ?", id).get();
    }

    private static void setUpCupboard() {
        Cupboard cupboard = new CupboardBuilder()
                .registerFieldConverter(String[].class, CupboardFieldConverter.stringArrayConverter)
                .registerFieldConverter(Cover.class, CupboardFieldConverter.coverFieldConverter)
                .build();
        CupboardFactory.setCupboard(cupboard);
    }
}
