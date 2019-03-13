package com.example.androidandflutterforandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.androidandflutterforandroid.storage.LocalStorage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickMe(View view) {
        LocalStorage localStorage = new LocalStorage(this);
        localStorage.putString("yoren", "{\"route\":\"/web_page\"}");
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
