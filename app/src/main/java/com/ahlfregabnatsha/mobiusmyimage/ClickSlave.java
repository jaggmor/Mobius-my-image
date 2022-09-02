package com.ahlfregabnatsha.mobiusmyimage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;


public class ClickSlave {
    Bitmap original;
    Bitmap copy;
    //private Canvas canvas;

    int bitmapWidth;
    int bitmapHeight;

    public boolean done = false;
    private int points_picked = 0;
    private final ComplexNumber[] points = new ComplexNumber[6];

    public ClickSlave(Bitmap bitmap) {
        // When object is created we save the old bitmap.
        this.original = bitmap;
        this.copy = bitmap.copy(bitmap.getConfig(), true);
        //this.canvas = new Canvas(bitmap);

        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();
    }


    private Bitmap drawLine(Bitmap bitmap, ComplexNumber z1, ComplexNumber z2, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(18f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Canvas tempCanvas = new Canvas(bitmap);

        //Draw the image bitmap into the cavas
        //tempCanvas.drawBitmap(bitmap, 0, 0, null);

        //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        tempCanvas.drawLine(x_normal((int) z1.getReal()),
                        y_normal((int) z1.getImaginary()),
                        x_normal((int) z2.getReal()),
                        y_normal((int) z2.getImaginary()),
                        paint);
        return bitmap;
    }

    public Bitmap transform() {
        MobiusTransformation mt = new MobiusTransformation(
                points[0], points[1], points[2], points[3], points[4], points[5]);
        this.done = true;
        return transformBitmap(this.original, mt);
    }

    int[] colors = {Color.RED, Color.BLUE, Color.GREEN};
    public Bitmap addPoint(ComplexNumber z) {
        points[points_picked] = z;
        this.points_picked++;
        if (this.points_picked % 2 == 0) {
            return drawLine(this.copy,
                    points[points_picked-2],
                    points[points_picked-1],
                    colors[points_picked/2 - 1]);  // I.e. the first point 2/2-1 = 0 -> first color
        }
        else {
            return this.copy;
        }
    }

    public boolean allPointsChosen() {
        return points_picked >= 6;
    }

    //Returns the transformed bitmap.
    private Bitmap transformBitmap(Bitmap bitmap, MobiusTransformation mt) {

        Bitmap transformedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        ComplexNumber imageCoordinates, domainCoordinates;
        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                //Adjust to coordinates with origin in center of picture.
                imageCoordinates = new ComplexNumber(x_center(x), y_center(y));
                domainCoordinates = mt.transformInverse(imageCoordinates);
                if (        Math.round(domainCoordinates.getReal()) < bitmapWidth/2
                        &&  Math.round(domainCoordinates.getReal()) > -bitmapWidth/2
                        &&  Math.round(domainCoordinates.getImaginary()) < bitmapHeight / 2
                        &&  Math.round(domainCoordinates.getImaginary()) > -bitmapHeight/2) {

                    int xx = x_normal((int) Math.round(domainCoordinates.getReal()));
                    int yy = y_normal((int) Math.round(domainCoordinates.getImaginary()));
                    int newPixel = bitmap.getPixel(xx, yy);

                    transformedBitmap.setPixel(x, y, newPixel);
                }
                else {
                    transformedBitmap.setPixel(x, y, Color.BLACK);
                }
            }
        }
        this.done = true;
        return transformedBitmap;
    }


    public int x_center(int x) {
        return x - (this.bitmapWidth/2);
    }

    public int y_center(int y) {
        return (this.bitmapHeight/2) - y;
    }

    private int x_normal(int x_c) {
        return x_c + (this.bitmapWidth/2);
    }

    private int y_normal(int y_c) {
        return (this.bitmapHeight/2) - y_c;
    }


}
