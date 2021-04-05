package com.example.quizmaster;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuizMasterApplication extends Application {

    private static final String DB_NAME = "db_quiz_master";
    private static final int DB_VERSION = 1;

    private SQLiteOpenHelper helper;

    @Override
    public void onCreate() {

        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                /*
                    quiz_category: math, history
                    quiz_type: TF(True / False), MC(Multi Choice), WORDS(write words)
                 */

                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_quiz(" +
                        "quiz_category TEXT, quie_type TEXT, quiz_text TEXT, option1 TEXT, " +
                        "option2 TEXT, option3 TEXT, option4 TEXT, answer TEXT )");

                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_result(" +
                        "quiz_category TEXT, correct_answer INTEGER, wrong_answer INTEGER, " +
                        "skip_answer INTEGER, answer_seconds REAL )");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                //No Operation
            }
        };
        super.onCreate();
    }

    public void addCount(int result){
        SQLiteDatabase db =  helper.getWritableDatabase();

        //1: Tie, 2: Lose, 3:Win
        if(result == 1){
            db.execSQL("INSERT INTO tbl_stats VALUES (" +
                    "1,0,0,1,0,0, " + Math.round(System.currentTimeMillis() / 1000)+")");
        }else if(result == 2){
            db.execSQL("INSERT INTO tbl_stats VALUES (" +
                    "0,1,0,0,1,0, " + Math.round(System.currentTimeMillis() / 1000)+")");
        }else if(result == 3){
            db.execSQL("INSERT INTO tbl_stats VALUES (" +
                    "0,0,1,0,0,1, " + Math.round(System.currentTimeMillis() / 1000)+")");
        }

    }

    public String getSumLastCount(){
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(lastTie) AS LTIE, SUM(lastLose) AS LLOSE, SUM(lastWin) AS LWIN " +
                        "FROM tbl_stats", null);

        int tie;
        int lose;
        int win;
        cursor.moveToFirst();
        tie = cursor.getInt(0);
        lose = cursor.getInt(1);
        win = cursor.getInt(2);
        cursor.close();

        return(win + "-" + lose + "-" + tie );
    }

    public String getSumAllCount(){
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(allTie) AS ATIE, SUM(allLose) AS ALOSE, SUM(allWin) AS AWIN FROM tbl_stats", null);

        int tie;
        int lose;
        int win;
        cursor.moveToFirst();
        tie = cursor.getInt(0);
        lose = cursor.getInt(1);
        win = cursor.getInt(2);
        cursor.close();

        return(win + "-" + lose + "-" + tie );
    }

    public void resetTableStats(){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM tbl_stats");
    }

    public void deleteLastCount(){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("UPDATE tbl_stats SET lastTie = 0, LastLose = 0, LastWin = 0");
    }


}
