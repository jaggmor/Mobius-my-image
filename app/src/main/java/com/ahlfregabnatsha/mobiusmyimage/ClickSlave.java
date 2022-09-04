package com.ahlfregabnatsha.mobiusmyimage;

import static java.lang.Math.atan2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.Toast;


public class ClickSlave {
    Bitmap original;
    Bitmap copy;
    //private Canvas canvas;

    int bitmapWidth;
    int bitmapHeight;

    public ComplexNumber z_new = new ComplexNumber(0,0);

    public boolean done = false;
    public int points_picked = 0;
    public ComplexNumber[] points = new ComplexNumber[6];

    int[] colors = {Color.RED, Color.GREEN, Color.BLUE};


    public ClickSlave(Bitmap bitmap) {
        // When object is created we save the old bitmap.
        this.original = bitmap;
        this.copy = bitmap.copy(bitmap.getConfig(), true);
        //this.canvas = new Canvas(bitmap);

        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();
    }

    public Bitmap drawPermLine(ComplexNumber z1, ComplexNumber z2) {
        int color = colors[(points_picked)/2 - 1];  // A pair of points was just chosen.
        this.z_new = z2;
        return drawLine(this.copy, z1, z2, color);
    }

    public Bitmap drawTempLine(ComplexNumber z1, ComplexNumber z2) {
        int color = colors[(points_picked + 1)/2 - 1];  // First in pair of points was chosen.
        Bitmap bitmap_tmp = this.copy.copy(this.copy.getConfig(), true);
        this.z_new = z2;
        return drawLine(bitmap_tmp, z1, z2, color);
    }

    private Bitmap drawLine(Bitmap bitmap, ComplexNumber z1, ComplexNumber z2, int color) {

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(18f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Canvas tempCanvas = new Canvas(bitmap);

        //Draw the image bitmap into the canvas
        //tempCanvas.drawBitmap(bitmap, 0, 0, null);

        // For easy access.
        int x1 = x_normal((int) z1.getReal());
        int y1 = y_normal((int) z1.getImaginary());
        int x2 = x_normal((int) z2.getReal());
        int y2 = y_normal((int) z2.getImaginary());

        // Drawing arrow shaft.
        tempCanvas.drawLine(x1, y1, x2, y2, paint);

        // Draw arrowhead pointing in the (1, 0) direction.
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float w = 80f;
        float h = 100f;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0, 0);
        path.lineTo(-h, w/2);
        path.lineTo(-h, -w/2);
        path.close();

        //Make a rotation matrix.
        double theta = 180.0/Math.PI * atan2(y2-y1, x2-x1);
        Matrix rotation = new Matrix();
        rotation.setRotate((float) theta);

        // Rotate and offset path to final point.
        path.transform(rotation);
        path.offset(x2, y2);

        tempCanvas.drawPath(path, paint);

        return bitmap;
    }

    public Bitmap transform() {
        MobiusTransformation mt = new MobiusTransformation(
                points[0], points[1], points[2], points[3], points[4], points[5]);
        this.done = true;
        return transformBitmap(this.original, mt);
    }

    public void addPoint(ComplexNumber z) {
        points[points_picked] = z;
        this.points_picked++;
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
