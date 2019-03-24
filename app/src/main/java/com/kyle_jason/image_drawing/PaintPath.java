package com.kyle_jason.image_drawing;

import android.graphics.Path;

public class PaintPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public PaintPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
