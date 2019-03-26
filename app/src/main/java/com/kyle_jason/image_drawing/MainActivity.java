package com.kyle_jason.image_drawing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dv = findViewById(R.id.drawingView);
    }

    @Override
    public void onBackPressed() {
        dv.undoLast();
    }
}
