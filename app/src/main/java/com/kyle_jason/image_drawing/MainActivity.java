package com.kyle_jason.image_drawing;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ColorPickerDialog.OnColorChangedListener,
        View.OnClickListener {
    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dv = findViewById(R.id.drawingView);

        TextView colorPickerTextView = findViewById(R.id.colorPicker);
        colorPickerTextView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        dv.undoLast();
    }

    @Override
    public void colorChanged(int color) {
        dv.setCurrentColor(color);
    }

    @Override
    public void onClick(View view) {
        ColorPickerDialog colorPicker = new ColorPickerDialog(this, this,
                Color.BLACK);
        colorPicker.show();
        colorPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
