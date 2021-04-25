package com.example.quizmaster;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QuizMasterApplication extends Application {

    public final long SCORING_FACTOR = 10000000;

    private static final String DB_NAME = "db_quiz_master";
    private static final int DB_VERSION = 1;

    private SQLiteOpenHelper helper;

    @Override
    public void onCreate() {
        final Toast toast = Toast.makeText(this, "Application Start onCreate!!!!!!!", Toast.LENGTH_LONG);
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                /*
                    quiz_category: math, history
                    quiz_type: TF(True / False), MC(Multi Choice), WORDS(write words)
                */

                //toast.show();
                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_quiz_questions(" +
                        "quest_no INTEGER, quest_category TEXT, quest_type TEXT, quest_text TEXT, option1 TEXT, " +
                        "option2 TEXT, option3 TEXT, option4 TEXT, answer TEXT )");

                db.execSQL("CREATE TABLE IF NOT EXISTS tbl_quiz_result(" +
                        "quiz_category TEXT, correct_answer INTEGER, elapsed_time REAL, score INTEGER, time_stamp REAL)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                //No Operation
            }
        };

        super.onCreate();
    }

    public void insertQuiz(){
        //Toast.makeText(this, "Insert Quiz", Toast.LENGTH_LONG).show();
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) AS COUNT " +
                        "FROM tbl_quiz_questions", null);

        cursor.moveToFirst();
        if(cursor.getInt(0) == 0){
            db = helper.getWritableDatabase();

            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "1,'history','MC','Which country gifted the United States the Statue of Liberty?','England','Spain','France', 'Italy','France')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "2,'history','MC','This was the president of the Confederate states during the U.S. Civil War.','George Washington', 'Jefferson Davis', 'Abraham Lincoln','John Adams','Jefferson Davis')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "3,'history','TF','True or False? The Civil War ended in 1861.','True','False','', '', 'False')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "4,'history','MC','Which city was attacked by the first atomic bomb?','Nagasaki','Paris','Hiroshima', 'Mexico City', 'Hiroshima')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "5,'history','MC','Who first discovered America?','Leif Eriksson','Christopher Columbus','Pierre de Monts', 'Amerigo Vespucci', 'Leif Eriksson')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "6,'history','MC','Who was the architect who rebuilt London after the Great Fire of 1666?','I.M. Pei','Sir Christopher Wren','Christopher Columbus', 'Sir Christopher Robin', 'Sir Christopher Wren')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "7,'history','MC','Which of these people once ruled Norway?','Vikings','Aztecs','Irish', 'Romans', 'Vikings')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "8,'history','MC','Which of these nations was neutral in World War I?','England','Germany','Italy', 'Norway', 'Norway')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "9,'history','MC','World War I began in which year?','1923','1938','1917', '1914', '1914')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "10,'history','MC','Adolf Hitler was born in which country?','France','Germany','Austria', 'Hungary', 'Austria')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "11,'history','MC','John F. Kennedy was assassinated in:','New York','Austin','Dallas', 'Miami', 'Dallas')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "12,'history','MC','Which general famously stated ''I shall return''?','Bull Halsey','George Patton','Douglas MacArthur', 'Omar Bradley', 'Douglas MacArthur')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "13,'history','MC','American involvement in the Korean War took place in which decade?','1930s','1940s','1950s', '1960s', '1950s')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "14,'history','MC','The Battle of Hastings in 1066 was fought in which country?','France','Russia','England', 'Norway', 'England')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "15,'history','MC','The Magna Carta was published by the King of which country?','Fance','Austria','Italy', 'England', 'England')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "16,'history','MC','Who is the man who developed the first successful printing press?','Johannes Gutenberg','Benjamin Franklin','Sir Isaac Newton', 'Martin Luther', 'Johannes Gutenberg')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "17,'history','MC','The disease that ravaged and killed a third of Europe''s population in the 14th century is known as:','The White Death','The Black Plague','Smallpox', 'The Bubonic Plague', 'The Bubonic Plague')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "18,'history','MC','The Hundred Years War was fought between what two countries?','Italy and Carthage','England and Germany','France and England', 'Spain and France', 'France and England')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "19,'history','MC','Which Roman Emperor built a massive wall across Northern Britain in 122 A.D.?','Marcus Aurelius','Hadrian','Nero', 'Augustus', 'Hadrian')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "20,'history','MC','This man wrote a document known as the 95 Theses','Martin Luther','Saint Augustus','HEnry David Thoreau', 'Voltaire', 'Martin Luther')");


            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "1,'math','WORDS','1 hundred, 6 tens and 2 ones?','','','', '', '162')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "2,'math','WORDS','A factory makes 9.8 metres of masking tape every minute. How many metres of masking tape can the factory make in 6 minutes?','','','', '', '58.8')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "3,'math','WORDS','A factory makes 9.8 metres of masking tape every minute. How many metres of masking tape can the factory make in 6 minutes?','','','', '', '58.8')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "4,'math','WORDS','Each cement block weighs 0.7 kilograms. How much do 4 blocks weigh in total?','','','', '', '2.8')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "5,'math','WORDS','Multiply: 14×0.3=','','','', '', '4.2')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "6,'math','WORDS','Multiply: 8×54=','','','', '', '432')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "7,'math','WORDS','Multiply: 692/100=','','','', '', '6.92')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "8,'math','MC','Which of the angles is an obtuse angle?','10','90','120', '180', '120')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "9,'math','MC','Which of the angles is an acute angle?','70','90','120', '180', '70')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "10,'math','WORDS','How many years are in a decade','','','', '', '10')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "11,'math','WORDS','How many seconds are in a minute?','','','', '', '60')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "12,'math','WORDS','What is the greatest common factor of 3 and 7?','','','', '', '1')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "13,'math','WORDS','Convert: ( ) millilitres = 1 cubic centimetre','','','', '', '1')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "14,'math','WORDS','Vera has 3 times as many female cats as male cats. If Vera has total 24 cats, how many of these are female?','','','', '', '18')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "15,'math','WORDS','Given 2a+b=19, 2c+d=23, b+=14, what is a+b+c+d?','','','','', '28')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "16,'math','WORDS','What is 14th term in the arithmetic sequence -8,-5,-2,1,...','','','', '', '31')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "17,'math','MC','How many digits are there in 1000','One','Two','Three', 'Four', 'Four')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "18,'math','MC','1 dime = ( ) dollar','0.20','10','0.10', '0.01', '0.10')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "19,'math','MC','What is the largest two digits prime number?','96','97','98', '99', '97')");
            db.execSQL("INSERT INTO tbl_quiz_questions VALUES (" +
                    "20,'math','MC','What is the average value of 25,20,23 and 22?','20','21.5','22.5', '24', '22.5')");
        }

        cursor.close();
    }

    /**
     * Get 10 quiz questions randomly
     * @param quizCategory 'math' or 'history'
     * @param requestedNumberOfQuestions how many questions need to be returned
     * @return ArrayList<HashMap> HashMap's keys are same to tbl_quiz_questions table columns' name
     */
    public ArrayList<HashMap> getQuizQuestions(String quizCategory, int requestedNumberOfQuestions){

        ArrayList<HashMap> rtnArrayList = new ArrayList<>();
        ArrayList<Integer> quizNumList = new ArrayList<Integer>();

        //Retrieve all questions of the quizCategory from db
        SQLiteDatabase db =  helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * " +
                        "FROM tbl_quiz_questions WHERE quest_category = '" + quizCategory + "'", null);

        int totalFetchedRecords = cursor.getCount();
        if (totalFetchedRecords < 1) {  // There is no record
            cursor.close();
            db.close();
            return null;
        }

        int questionsToReturn = Math.min(requestedNumberOfQuestions, totalFetchedRecords);  // How many questions will be returned

        // Create a list of number from 0 until totalFetchedRecords and shuffle it
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < totalFetchedRecords; i++) list.add(i);
        Collections.shuffle(list);

        // Once suffled, get as many as questionsToReturn from cursor
        for (int i=0; i<questionsToReturn; i++) {
            if(cursor.moveToPosition(list.get(i))){
                HashMap quizMap = new HashMap();
                quizMap.put("quest_no", cursor.getInt(0));
                quizMap.put("quest_category", cursor.getString(1));
                quizMap.put("quest_type", cursor.getString(2));
                quizMap.put("quest_text", cursor.getString(3));
                quizMap.put("quest_option1", cursor.getString(4));
                quizMap.put("quest_option2", cursor.getString(5));
                quizMap.put("quest_option3", cursor.getString(6));
                quizMap.put("quest_option4", cursor.getString(7));
                quizMap.put("quest_option5", cursor.getString(8));

                rtnArrayList.add(quizMap);
            }
        }
        cursor.close();
        db.close();

        return rtnArrayList;
    }

    public HashMap getStats(String category){
        SQLiteDatabase db =  helper.getReadableDatabase();
        HashMap rtnMap = new HashMap();
        Cursor cursor_last = db.rawQuery(
                "SELECT COUNT(*) AS COUNT, SUM(correct_answer) AS CORRECT, AVG(elapsed_time) AS TIME, " +
                        " AVG(score) AS SCORE FROM tbl_quiz_result WHERE quiz_category = '" + category + "' AND time_stamp > " +
                        (System.currentTimeMillis()/1000 - 24*60*60), null);

        Cursor cursor_all = db.rawQuery(
                "SELECT COUNT(*) AS COUNT, SUM(correct_answer) AS CORRECT, AVG(elapsed_time) AS TIME, " +
                        " AVG(score) AS SCORE FROM tbl_quiz_result WHERE quiz_category = '" + category + "'", null);

        int last_num_quiz_set;
        int last_num_correct;
        int last_avg_elapsed_time;
        double last_avg_score;
        int all_num_quiz_set;
        int all_num_correct;
        int all_avg_elapsed_time;
        double all_avg_score;

        cursor_last.moveToFirst();
        cursor_all.moveToFirst();
        last_num_quiz_set = cursor_last.getInt(0);
        last_num_correct = cursor_last.getInt(1);
        last_avg_elapsed_time = cursor_last.getInt(2);
        last_avg_score = cursor_last.getDouble(3);
        all_num_quiz_set = cursor_all.getInt(0);
        all_num_correct = cursor_all.getInt(1);
        all_avg_elapsed_time = cursor_all.getInt(2);
        all_avg_score = cursor_all.getDouble(3);

        int last_ratio = 0;
        int all_ratio = 0;
        if(last_num_quiz_set == 0)
            last_ratio = 0;
        else
            last_ratio = Math.round(last_num_correct/last_num_quiz_set);

        if(all_num_quiz_set == 0)
            all_ratio = 0;
        else
            all_ratio = Math.round(all_num_correct/all_num_quiz_set);

        rtnMap.put("LAST_PLAYED", last_num_quiz_set * 10);
        rtnMap.put("LAST_RATIO", last_ratio);
        rtnMap.put("LAST_AVG_TIME", last_avg_elapsed_time);
        rtnMap.put("LAST_AVG_SCORE", Math.round(last_avg_score));
        rtnMap.put("ALL_PLAYED", all_num_quiz_set * 10);
        rtnMap.put("ALL_RATIO", all_ratio);
        rtnMap.put("ALL_AVG_TIME", all_avg_elapsed_time);
        rtnMap.put("ALL_AVG_SCORE", Math.round(all_avg_score));

        cursor_last.close();
        cursor_all.close();

        return rtnMap;
    }

    public void resetTableStats(){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM tbl_quiz_result");
    }

    /**
     * insert quiz result
     * @param quizCategory 'math' or 'history'
     * @param correct_answer correct number of answer
     * @param elapsedTime elpased seconeds
     */
    public void updateQuizResult(String quizCategory, int correct_answer, long elapsedTime){
        SQLiteDatabase db = helper.getWritableDatabase();

        int score = getScore(correct_answer, elapsedTime);

        String query = "INSERT INTO tbl_quiz_result VALUES('" + quizCategory + "', "+ correct_answer + ", " + elapsedTime + "," + score + ", " + System.currentTimeMillis() + ")";

        db.execSQL(query);
    }

    // Returns the score corresponding to the parameters
    // elapsedTime must be in milliseconds
    public int getScore(int correctAnswers, long elapsedTime) {
        return (int)(correctAnswers*SCORING_FACTOR/ elapsedTime + correctAnswers);
    }

}
