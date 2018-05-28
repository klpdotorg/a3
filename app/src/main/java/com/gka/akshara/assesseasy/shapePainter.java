package com.gka.akshara.assesseasy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by suresh on 11-02-2018.
 */

public class shapePainter extends View {


    public shapePainter(Context context) {

        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
  Log.d("shapePainter","in onDraw");
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        //setShader();
        canvas.drawCircle(250,250,150, paint);
    }
}
