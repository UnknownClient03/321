package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class SignatureView extends View {

    private Paint paint;
    private Path path;
    private Bitmap signatureBitmap;
    private Canvas signatureCanvas;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Initialize paint and path objects
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10f);

        path = new Path();
    }

    // Handle touch events to draw the signature
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                signatureCanvas.drawPath(path, paint);  // Draw the path to the bitmap
                path.reset();  // Reset the path after lifting the finger
                break;
        }
        invalidate();  // Redraw the view
        return true;
    }

    // This method is called whenever the view needs to be redrawn
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (signatureBitmap == null) {
            // Create a new bitmap and associate it with a canvas
            signatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            signatureCanvas = new Canvas(signatureBitmap);
        }

        // Draw the signatureBitmap (previous strokes)
        canvas.drawBitmap(signatureBitmap, 0, 0, null);

        // Draw the current path (new stroke)
        canvas.drawPath(path, paint);
    }

    // Clear the signature from the canvas and bitmap
    public void clearSignature() {
        if (signatureBitmap != null) {
            signatureBitmap.eraseColor(Color.TRANSPARENT);  // Clear the bitmap
            invalidate();  // Redraw the view
        }
    }

    // Convert the signatureBitmap to a base64 string
    public String getSignatureData() {
        if (signatureBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);  // Convert to base64 string
        }
        return null;
    }
}