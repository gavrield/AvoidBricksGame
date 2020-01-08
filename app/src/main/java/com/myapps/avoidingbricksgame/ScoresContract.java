package com.myapps.avoidingbricksgame;

import android.provider.BaseColumns;

public final class ScoresContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ScoresContract(){};

    /* Inner class that defines the table contents */
    public static class Scores implements BaseColumns {

        public static final String TABLE_NAME = "scores";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "long";
    }

}
