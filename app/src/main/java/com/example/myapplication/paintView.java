package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.io.ByteArrayOutputStream;


public class paintView extends View{

    public LayoutParams params;
    private Path path = new Path();
    private Paint brush = new Paint();

    //constructor for the creation of a drawable canvas
    public paintView(Context context) {
        super(context);

        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(5f);

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    //constructor for the creation of a bitmap on a view
    public paintView(Context context, String imageEncoded)
    {
        super(context);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        byte[] decodedByte = Base64.decode(imageEncoded, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        if(bitmap == null)
        {
            Log.e("decoding error", "could not decode image");
            return;
        }
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        this.setBackground(d);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
    }

    //
    public String convertCanvas()
    {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = this.getBackground();
        if(bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        this.draw(canvas);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] arr = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(arr, Base64.DEFAULT);
        if (imageEncoded.length() > 100000) Log.e("decoding error", "encoded image is too large");
        return imageEncoded;
    }

}

/*example inside an onCreate function
        //The output frame layout and input frame layout need to be the same size

        paintView = new paintView(this);
        FrameLayout frmLayout = (FrameLayout)findViewById(R.id.canvas_1);
        frmLayout.addView(paintView);


        Button button = findViewById(R.id.button_9);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "submit signature");
                String imageEncoded = paintView.convertCanvas();

                SQLConnection c = new SQLConnection("user1", "");
                String query = "INSERT INTO ImmunisationRecord VALUES (0, 0, 2, 'HepB', '2024-08-19', 53, '" + imageEncoded + "');";
                c.update(query);
                c.disconnect();
            }
        });

        Button button2 = findViewById(R.id.button_10);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLConnection c = new SQLConnection("user1", "");
                String query = "SELECT signature FROM ImmunisationRecord WHERE ID = 0;";
                HashMap<String, String[]> result = c.select(query);
                if(result.get("signature")[0] == null)
                {
                    Log.e("err", "no results in query");
                    c.disconnect();
                    return;
                }
                String imageEncoded = result.get("signature")[0];
                c.disconnect();

                FrameLayout frmLayout = (FrameLayout)findViewById(R.id.canvas_2);
                frmLayout.addView(new paintView(drawingExample.this, imageEncoded));
            }
        });
*/
