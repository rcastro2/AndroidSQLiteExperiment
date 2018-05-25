package com.example.rcastro.mysqlapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.*;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db = null;
    EditText genre;
    Button search;
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genre = (EditText)findViewById(R.id.edtGenre);
        search = (Button)findViewById(R.id.btnSearch);
        output = (TextView)findViewById(R.id.txtOutput);
        output.setMovementMethod(new ScrollingMovementMethod());

        String DB_NAME = "chinook.db";
        String DB_PATH = this.getApplicationInfo().dataDir + "/";

        File f = new File(DB_PATH + DB_NAME);
        if(f.exists()) {
            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Log.d("help", "DB Found");
        }else{
            try {
                InputStream i = this.getAssets().open(DB_NAME);
                OutputStream o = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] b = new byte[1024];
                int len;
                while((len=i.read(b))>0){
                    o.write(b,0,len);
                }
                o.flush();
                o.close();
                i.close();
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                Log.d("help","DB Created");
            }catch(IOException ioe){

            }
        }
        search.setOnClickListener(searchCountry());
    }
    View.OnClickListener searchCountry(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String query = "SELECT genres.Name as genreName, tracks.Name as trackName, tracks.Composer as composer " +
                        "FROM tracks INNER JOIN genres ON tracks.GenreId = genres.GenreId " +
                        "WHERE genres.Name = '" + genre.getText() + "'";
                String build = "";
                Cursor c = db.rawQuery(query,null);
                c.moveToFirst();
                for(int i = 0; i < c.getCount(); i++) {
                    build += (i+1) + ")" + c.getString(c.getColumnIndex("trackName")) + "\nby ~ " + c.getString(c.getColumnIndex("composer")) + "\n\n";
                    c.moveToNext();
                }
                output.setText(build);
            }
        };
    };
}
