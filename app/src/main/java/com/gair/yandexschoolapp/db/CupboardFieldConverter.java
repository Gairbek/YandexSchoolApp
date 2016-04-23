package com.gair.yandexschoolapp.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.gair.yandexschoolapp.entity.Cover;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * Created by Gairbek on 21.04.2016.
 */
public class CupboardFieldConverter {

    // Конвертер для перевода cupboard'ом  String[] genres в строку для сохранения в бд.
    public static FieldConverter<String[]> stringArrayConverter = new FieldConverter<String[]>() {
        @Override
        public String[] fromCursorValue(Cursor cursor, int columnIndex) {
            return cursor.getString(columnIndex).split(",");
        }

        @Override
        public void toContentValue(String[] value, String key, ContentValues values) {
            if (value.length > 0) {
                StringBuilder sb = new StringBuilder(1024);
                for (String s : value) {
                    sb.append(s);
                    sb.append(",");
                }
                // strip off trailing comma
                sb.setLength(sb.length() - 1);
                values.put(key, sb.toString());
            } else {
                values.put(key, "");
            }
        }

        @Override
        public EntityConverter.ColumnType getColumnType() {
            return EntityConverter.ColumnType.TEXT;
        }
    };

    // Для перевода Cover .
    public static FieldConverter<Cover> coverFieldConverter = new FieldConverter<Cover>() {
        @Override
        public Cover fromCursorValue(Cursor cursor, int columnIndex) {
            Cover cover = new Cover();
            String[] covers = cursor.getString(columnIndex).split(",");
            cover.setSmall(covers[0]);
            cover.setBig(covers[1]);
            return cover;
        }

        @Override
        public void toContentValue(Cover value, String key, ContentValues values) {
            StringBuilder sb = new StringBuilder(4);
            sb.append(value.getSmall()).append(",").append(value.getBig());
            values.put(key, sb.toString());
        }

        @Override
        public EntityConverter.ColumnType getColumnType() {
            return EntityConverter.ColumnType.TEXT;
        }
    };

}
