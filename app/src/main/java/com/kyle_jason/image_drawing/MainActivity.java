package com.kyle_jason.image_drawing;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ColorPickerDialog.OnColorChangedListener {
    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dv = findViewById(R.id.drawingView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<small>Paintacalifragilisticexpialadocious</small>"));
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void colorChanged(int color) {
        dv.setCurrentColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Undo");
        menu.add(0, 1, 0, "Redo");
        menu.add(0, 2, 0, "Color");
        menu.add(0, 3, 0, "Size");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                dv.undoLast();
                return true;
            case 1:
                dv.redoLast();
                return true;
            case 2:
                ColorPickerDialog colorPicker = new ColorPickerDialog(this, this,
                        Color.BLACK);
                colorPicker.show();
                colorPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                return true;
            case 3:
                // strokeWidthDialog & setStrokeWidth
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
