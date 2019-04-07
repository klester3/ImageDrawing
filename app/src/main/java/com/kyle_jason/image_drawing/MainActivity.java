package com.kyle_jason.image_drawing;

/*
Kyle Lester
Jason Casebier
CS 4020
Assignment 3
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        /*actionBar.setTitle(Html.fromHtml("<small>Paintacalifragilisticexpialidocious" +
                "</small>"));*/
        actionBar.setTitle(Html.fromHtml(""));

        verifyStoragePermissions(this);

        dv = findViewById(R.id.drawingView);
        dv.setBackground(new ColorDrawable(0xfffdfdfd));

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
        findViewById(R.id.squareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.mode = 3;
                if(dv.isGrey){
                    dv.isGrey = false;
                }else{
                    dv.isGrey = true;
                }
                dv.updateScreen();
            }
        });

        findViewById(R.id.eraseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dv.isErase) {
                    dv.isErase = false;
                }else{
                    dv.isErase = true;
                }
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

    private void pressedSquare() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.square_dialog, null);
        AlertDialog.Builder quitAlert = new AlertDialog.Builder(this);
        quitAlert.setView(alertLayout);
        quitAlert.setCancelable(true);
        final AlertDialog quitDialog = quitAlert.create();
        quitDialog.show();
        String sx = ((EditText) quitDialog.findViewById(R.id.editTextX)).getText().toString();
        Log.i("KYLE", "sx = "+sx);
        int x = Integer.parseInt(sx);
        Log.i("KYLE", "x = "+x);
        dv.squareX = x;
        String sy = ((EditText) quitDialog.findViewById(R.id.editTextY)).getText().toString();
        int y = Integer.parseInt(sy);
        dv.squareY = y;

        dv.mode = 2;
    }
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Pick Color");
        menu.add(0, 1, 0, "Add Image");
        menu.add(0, 2, 0, "Clear All");
        menu.add(0, 3, 0, "Save Image");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
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
                                dv.setCurrentColor(0xff008800);
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
                                dv.setCurrentColor(0xffffffff);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.grayColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffaaaaaa);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.limegreenColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff22ff00);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.ltpurpleColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffcc99ff);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.redorangeColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffff5500);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.tealColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff66ffff);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.pinkColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xffff00ff);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.ltbrownColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff996633);
                                colorPickerDialog.dismiss();
                            }
                        });
                colorPickerDialog.findViewById(R.id.brownColor).
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dv.setCurrentColor(0xff663300);
                                colorPickerDialog.dismiss();
                            }
                        });
                return true;
            case 1:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
                return true;
            case 2:
                dv.clearAll();
                dv.removeImage();
                return true;
            case 3:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                dv.clearAll();
                Uri imageUri = data.getData();
                Drawable image;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    image = Drawable.createFromStream(inputStream, imageUri.toString());
                    dv.loadImage(image);
                } catch (FileNotFoundException e) {
                    Log.i("OPEN_ERROR", e.getMessage());
                }
            }
        }
    }
}
