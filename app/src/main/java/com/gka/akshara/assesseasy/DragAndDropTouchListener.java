package com.gka.akshara.assesseasy;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sures on 21-02-2018.
 */

public class DragAndDropTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View vw, MotionEvent mEvent) {

            if(mEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder shadowBldr = new View.DragShadowBuilder(vw);
                vw.startDrag(data, shadowBldr, vw, 0);
                return true;
            }
            else {
                return false;
            }
        }
}
