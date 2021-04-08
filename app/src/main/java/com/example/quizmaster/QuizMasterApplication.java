package com.example.quizmaster;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class QuizMasterApplication extends Application {

    private static final String DB_NAME = "db_quiz_master";
    private static final int DB_VERSION = 1;

    private SQLiteOpenHelper helper;

    @Override
    public void onCreate() {
        final Toast toast = Toast.makeText(this, "Apoplication Start onCreate!!!!!!!", Toast.LENGTH_LONG);
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                /*
                    quiz_category: math, history
                    quiz_type: TF(True / False), MC(Multi Choice), WORDS(write words)
                */

                toast.show();
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_quiz(" +
                        "quz_no INTEGER, quiz_category TEXT, quie_type TEXT, quiz_text TEXT, option1 TEXT, " +
                        "option2 TEXT, option3 TEXT, option4 TEXT, answer TEXT )");

                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_result(" +
                        "quiz_category TEXT, correct_answer INTEGER, wrong_answer INTEGER, " +
                        "skip_answer INTEGER, answer_time REAL, score REAL, answer_current_date )");

                //insertQuiz();

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                //No Operation
            }
        };

        super.onCreate();
    }

    public void insertQuiz(){
        Toast.makeText(this, "Insert Quiz", Toast.LENGTH_LONG).show();
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) AS COUNT " +
                        "FROM tbl_quiz", null);

        cursor.moveToFirst();
        if(cursor.getInt(0) == 0){
            db = helper.getWritableDatabase();

            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "1,'history','MC','Which country gifted the United States the Statue of Liberty?','England','Spain','France', 'Italy','France')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "2,'history','MC','This was the president of the Confederate states during the U.S. Civil War.','George Washington', 'Jefferson Davis', 'Abraham Lincoln','John Adams','Jefferson Davis')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "3,'history','TF','True or False? The Civil War ended in 1861.','True','False','', '', 'False')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "4,'history','MC','Which city was attacked by the first atomic bomb?','Nagasaki','Paris','Hiroshima', 'Mexico City', 'Hirosima')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "5,'history','MC','Who first discovered America?','Leif Eriksson','Christopher Columbus','Pierre de Monts', 'Amerigo Vespucci', 'Leif Eriksson')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "6,'history','MC','Who was the architect who rebuilt London after the Great Fire of 1666?','I.M. Pei','Sir Christopher Wren','Christopher Columbus', 'Sir Christopher Robin', 'Sir Christopher Wren')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "7,'history','MC','Which of these peoples once ruled Norway?','Vikings','Aztecs','Irish', 'Romans', 'Vikings')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "8,'history','MC','Which of these nations was neutral in World War I?','England','Germany','Italy', 'Norway', 'Norway')");



            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "1,'math','WORDS','1 hundred, 6 tens and 2 ones?','','','', '', '162')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "2,'math','WORDS','A factory makes 9.8 metres of masking tape every minute. How many metres of masking tape can the factory make in 6 minutes?','','','', '', '58.8')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "3,'math','WORDS','A factory makes 9.8 metres of masking tape every minute. How many metres of masking tape can the factory make in 6 minutes?','','','', '', '58.8')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "4,'math','WORDS','Each cement block weighs 0.7 kilograms. How much do 4 blocks weigh in total?','','','', '', '2.8')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "5,'math','WORDS','Multiply: 14×0.3=','','','', '', '4.2')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "6,'math','WORDS','Multiply: 8×54=','','','', '', '432')");
            db.execSQL("INSERT INTO tbl_quiz VALUES (" +
                    "7,'math','WORDS','Multiply: 692/100=','','','', '', '6.92')");

        }




    }
    public void addResult(String category, int correct, int wrong, int skip, float time){
        SQLiteDatabase db =  helper.getWritableDatabase();

        //1: Tie, 2: Lose, 3:Win

        db.execSQL("INSERT INTO tbl_result VALUES (" +
                category + "," + correct + "," + wrong + "," + skip + "," + time + "," + correct * 100 / time +
                ", " + Math.round(System.currentTimeMillis() / 1000)+")");


    }

    public void updateResult(){

    }

    public String getSumLastCount(){
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(lastTie) AS LTIE, SUM(lastLose) AS LLOSE, SUM(lastWin) AS LWIN " +
                        "FROM tbl_result", null);

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
