package com.kyle_jason.image_drawing;

/*
Kyle Lester
Jason Casebier
CS 4020
Assignment 3
 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        ColorPickerDialog.OnColorChangedListener {

    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<small>Paintacalifragilisticexpialadocious" +
                "</small>"));

        verifyStoragePermissions(this);

        dv = findViewById(R.id.drawingView);

        Button undoButton = findViewById(R.id.undoButton);
        undoButton.setText(Html.fromHtml("&#8630;"));
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.undoLast();
            }
        });

        Button redoButton = findViewById(R.id.redoButton);
        redoButton.setText(Html.fromHtml("&#8631;"));
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.redoLast();
            }
        });

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
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

        menu.add(0, 0, 0, "Color");
        menu.add(0, 1, 0, "Clear");
        menu.add(0, 2, 0, "Save");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                /*
                ColorPickerDialog colorPicker = new ColorPickerDialog(this, this,
                        Color.BLACK);
                colorPicker.show();
                colorPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                */
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_color_picker, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setCancelable(true);
                final AlertDialog colorPickerDialog = builder.create();
                colorPickerDialog.show();
                colorPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                colorPickerDialog.findViewById(R.id.orangeColor).
                        setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffff8800);
                        colorPickerDialog.dismiss();
                    }
                });
                colorPickerDialog.findViewById(R.id.yellowColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffffee33);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.greenColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff00cc00);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.blueColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff0000cc);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.redColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffff0000);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.purpleColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff883399);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.blackColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff000000);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.whiteColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xfffdfdfd);
                                colorPickerDialog.dismiss();
                            }
                        });
                return true;
            case 1:
                dv.clearAll();
                return true;
            case 2:
                dv.setDrawingCacheEnabled(true);
                dv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = dv.getDrawingCache();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.US);
                Date date = new Date();
                String filename = "Painting_" + dateFormat.format(date) + ".jpg";
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "DCIM";
                File file = new File(filepath + File.separator + filename);
                FileOutputStream fileOutputStream;
                try {
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Drawing Saved", Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_LONG)
                            .show();
                    Log.i("SAVE_ERROR", e.getMessage());
                }
                dv.setDrawingCacheEnabled(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        dv.setStrokeWidth(i + 5);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // onStartTrackingTouch
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // onStopTrackingTouch
    }
}
