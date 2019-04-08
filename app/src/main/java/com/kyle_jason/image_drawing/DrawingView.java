package com.kyle_jason.image_drawing;

/*
Kyle Lester
Jason Casebier
CS 4020
Assignment 3
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.RenderScript;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {
    private final float TOUCH_TOLERANCE = 4f;

    private int currentWidth;
    private int currentHeight;

    private ArrayList<PaintPath> paths;
    private ArrayList<PaintPath> redoPaths;
    private ArrayList<PaintRectangle> rectangles;
    private PaintPath paintPath;
    private Path path;
    private float pathX;
    private float pathY;
    private Bitmap image;
    private float scale;
    private float bufferY;
    private float bufferX;

    private Paint paint;
    private Paint imagePaint;
    private int strokeWidth;
    private int color;

    public boolean isErase = false;
    public boolean isGrey = false;
    public boolean isDashed = false;
    public boolean isGlow = false;
    private boolean isRectangle;
    private float rectStartX;
    private float rectStartY;
    private float rectEndX;
    private float rectEndY;
    private RectF rectangle;
    private PaintRectangle rect;
    public int squareX;
    public int squareY;
    public int mode = 1;

    public DrawingView(Context context) {
        super(context);
        setup(null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    public void setup(AttributeSet attrs) {
        paths = new ArrayList<>();
        redoPaths = new ArrayList<>();
        rectangles = new ArrayList<>();
        paint = new Paint();
        imagePaint = new Paint();
        strokeWidth = 5;
        color = 0xff000000;
        isRectangle = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        currentWidth = w;
        currentHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);
        imagePaint.setAntiAlias(true);
        if (image != null) {
            Matrix m = new Matrix();
            m.setTranslate(bufferX, bufferY);

            //for greyscale switch
            if(isGrey) {
                imagePaint.setColorFilter(new ColorMatrixColorFilter((getColorMatrixGrey())));
                Log.i("KYLE", "grey");
            }else{
                imagePaint.setColorFilter(null);
            }
            canvas.drawBitmap(image, m, imagePaint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        //makes all strokes dashed
        if(isDashed){
            makeDashed();
            Log.i("KYLE", "isDashed = true");
        }else{
            paint.setPathEffect(null);
            Log.i("KYLE", "isDashed = false");
        }


        for (PaintPath paintPath : paths) {
            paint.setColor(paintPath.color);
            paint.setStrokeWidth(paintPath.strokeWidth);
            canvas.drawPath(paintPath.path, paint);
        }

        for (PaintRectangle rectangle : rectangles) {
            paint.setColor(rectangle.color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(rectangle.rectangle, paint);
        }
    }

    public void pressedSquare() {
        isRectangle = !isRectangle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isErase) {
                    Log.i("KYLE", "isErase = true");
                    startErasePath(x, y);
                } else if (isRectangle) {
                    rectStartX = x;
                    rectStartY = y;
                }else{
                    startPath(x,y);
                    Log.i("KYLE", "isErase = false");
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRectangle) {
                    rectEndX = x;
                    rectEndY = y;
                } else {
                    movePath(x, y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isRectangle) {
                    rectEndX = x;
                    rectEndY = y;
                    rectangle = new RectF(rectStartX, rectStartY, rectEndX, rectEndY);
                    rect = new PaintRectangle(color, rectangle);
                    rectangles.add(rect);
                } else {
                    endPath();
                }
                invalidate();
                break;
        }

        return true;
    }

    private void startPath(float x, float y) {
        redoPaths.clear();
        path = new Path();
        paintPath = new PaintPath(color, strokeWidth, path);
        paths.add(paintPath);
        path.reset();
        path.moveTo(x, y);
        pathX = x;
        pathY = y;
    }

    private void startErasePath(float x, float y) {
        redoPaths.clear();
        path = new Path();
        paintPath = new PaintPath(Color.WHITE, strokeWidth, path);
        paths.add(paintPath);
        path.reset();
        path.moveTo(x, y);
        pathX = x;
        pathY = y;
    }

    private void movePath(float x, float y) {
        float moveX = Math.abs(x - pathX);
        float moveY = Math.abs(y - pathY);
        if (moveX >= TOUCH_TOLERANCE || moveY >= TOUCH_TOLERANCE) {
            path.quadTo(pathX, pathY, (x + pathX)/2, (y + pathY)/2);
            pathX = x;
            pathY = y;
        }
    }

    private void endPath() { path.lineTo(pathX, pathY); }

    public void undoLast() {
        if (paths.size() > 0) {
            redoPaths.add(paths.get(paths.size() - 1));
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public void redoLast() {
        if (redoPaths.size() > 0) {
            paths.add(redoPaths.get(redoPaths.size() - 1));
            redoPaths.remove(redoPaths.size() - 1);
            invalidate();
        }
    }

    public void setCurrentColor(int color) {
        this.color = color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void clearAll() {
        paths.clear();
        rectangles.clear();
        redoPaths.clear();
        invalidate();
    }

    public void loadImage(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap img;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix m = new Matrix();
            m.postRotate(90);
            img = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,
                    true);
        } else {
            img = bitmap;
        }
        float height = img.getHeight();
        float width = img.getWidth();
        if (height > currentHeight) {
            scale = currentHeight/height;
            if (width > currentWidth) {
                scale = currentWidth / width;
            }
        } else {
            scale = 1f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        image = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix,
                true);
        if (image.getHeight() < currentHeight) {
            bufferY = (currentHeight - image.getHeight()) / 2f;
        } else {
            bufferY = 0f;
        }
        if (image.getWidth() < currentWidth) {
            bufferX = (currentWidth - image.getWidth()) / 2f;
        } else {
            bufferX = 0f;
        }
        Log.i("SCALE", Float.toString(scale));
        invalidate();
    }

    public void removeImage() {
        image = null;
        invalidate();
    }

    private ColorMatrix getColorMatrixGrey() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return colorMatrix;
    }

    public void updateScreen(){
        invalidate();
    }

    public void makeDashed(){
        PathEffect dashed = new DashPathEffect(new float[]{strokeWidth * 3, strokeWidth}, 0);
        paint.setPathEffect(dashed);

        invalidate();
    }

    public void makeOuterGlow(DrawingView dv){
        if(dv == null) {
            paint.setMaskFilter(null);
        }else{
            float radius = strokeWidth / 2;
            BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
            paint.setMaskFilter(filter);
        }
    }


}
