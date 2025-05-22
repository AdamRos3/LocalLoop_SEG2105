package com.example.localloop;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
    }

    public void ValidateCredentials (View view) {
        setContentView(R.layout.activity_main);
    }

}