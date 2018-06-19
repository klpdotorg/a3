package com.gka.akshara.assesseasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class animpage1 extends AppCompatActivity {

    final int[] questionid = new int[1];
    final AnimationDrawable[] adrabbit = new AnimationDrawable[1]; // Defining as an array so that values can be assigned despite being 'final'. It is defined as 'final' so that it can be accessed from inner classes (runnable)
    final AnimationDrawable[] adboy = new AnimationDrawable[1];
    final ImageView[] ivrabbit = new ImageView[1];
    final ImageView[] ivboy = new ImageView[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.akshara.assessment.a3.R.layout.activity_animpage1);

        // saves the last Question ID passed to this activity to pass back to the 'assessment_manager' activity
        Intent intent = getIntent(); // get the Intent that started this activity
        questionid[0] =  intent.getIntExtra("EASYASSESS_QUESTIONID",0);

        // Set the background image for the layout
        int bkgrndimgindex = questionid[0]/globalvault.animationdisplayinterval;
        if(bkgrndimgindex >= globalvault.ANIMATION_BGRND_IMGS.length)
            bkgrndimgindex = globalvault.ANIMATION_BGRND_IMGS.length - 1;
        ConstraintLayout clayout = findViewById(com.akshara.assessment.a3.R.id.ConstraintLayoutParentAnimation);
        clayout.setBackgroundResource(globalvault.ANIMATION_BGRND_IMGS[bkgrndimgindex]);

        ivrabbit[0] = findViewById(com.akshara.assessment.a3.R.id.imageViewRabbitAnim);
        ivrabbit[0].setImageResource(com.akshara.assessment.a3.R.drawable.anim_rabbit); // XML file for rabbit animation frames
        adrabbit[0] = (AnimationDrawable)ivrabbit[0].getDrawable();

        ivboy[0] = findViewById(com.akshara.assessment.a3.R.id.imageViewBoyAnim);
        ivboy[0].setImageResource(com.akshara.assessment.a3.R.drawable.anim_man); // XML file for boy animation frames
        adboy[0] = (AnimationDrawable)ivboy[0].getDrawable();

        final int[] totalwidth = new int[1]; // Defined as an array to workaround the fact that innerclass can not directly modify the class variable and the inner class that get the width is asynchrouns
        totalwidth[0] = 0;

        // Clculate the total width of the parent constraint layout.
        // We can not get the width in the onCreate() function directly as the layout may not have been finished drawing at this stage
        // So, create a ViewTreeObserver and add a GlobalLayoutListener, which will be invoked when layout is finished
        // get the total width in the listner function

        final ConstraintLayout animparentlayout = findViewById(com.akshara.assessment.a3.R.id.ConstraintLayoutParentAnimation);
        ViewTreeObserver vto = animparentlayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) { // SDK above 16
                    animparentlayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    animparentlayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int total_layout_width  = animparentlayout.getMeasuredWidth();
                int total_layout_height = animparentlayout.getMeasuredHeight();
                totalwidth[0] = total_layout_width;

                Log.d("EASYASSESS", "In OnGlobalLayout: totalwidth "+total_layout_width+" totalheight:"+total_layout_height );

            }
        });

        // start the animation in a separate thread
        ivrabbit[0].post(new Runnable() {

            @Override
            public void run() {
                if(adrabbit[0] != null)
                    adrabbit[0].start();
            }

        });

        ivboy[0].post(new Runnable() {

            @Override
            public void run() {
                if(adboy[0] != null)
                    adboy[0].start();
            }

        });

        // Keep moving the ImageView where the animation is running by continously changing the X cordinate of the ImageView
        // This will give an illusion of the animation of running rabbit moving across the screen from start to end
        new Timer().schedule(new TimerTask() {

            int stepRabbit = 12 + new Random().nextInt(5);
            int stepBoy = 12 + new Random().nextInt(5);
            int startXrabbit = 0;
            int startXboy = 0;
            int currentXrabbit = startXrabbit;
            int currentXboy = startXboy;

            @Override
            public void run() {

                while(true) {

                    // Trying to call setX on view objects (ivrabbit and ivboy) like below runs fine on the emulator, buts gives error
                    // saying "Only the original thread that created a view hierarchy can touch its views" while running on the actual phone
                    // Ths solution is to run this code in a UI Thread by calling the method 'runOnUiThread' as below
                    /*
                    if (ivrabbit[0] != null)
                        ivrabbit[0].setX(currentXrabbit);
                    if (ivboy[0] != null)
                        ivboy[0].setX(currentXboy);
                    */

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ivrabbit[0] != null)
                                ivrabbit[0].setX(currentXrabbit);
                            if (ivboy[0] != null)
                                ivboy[0].setX(currentXboy);
                        }
                    });

                    // Vary the Step size for Rabbit and Boy randomly. So, it will be random who reaches first at the finishing point of the screen
                    int stepR = stepRabbit + new Random().nextInt(6);
                    currentXrabbit += stepR;
                    int stepB = stepBoy + new Random().nextInt(6);
                    currentXboy += stepB;

                    try {
                        Thread.sleep(100);
                    }
                    catch(Exception e) {
                    }

                    // totalwidth[0] will get set inside the GlobalLayoutListener function only after the layout is finished drawing itself
                    //Log.d("EASYASSESS", "In run. Totalwidth: "+totalwidth[0] );
                    int xbehind = currentXrabbit;
                    if(xbehind > currentXboy) xbehind = currentXboy;

                    if((totalwidth[0] > 0) && (xbehind >= totalwidth[0])) { // Exit the animation once the ImageView reaches end of the screen
                        break;
                    }
                }

                Context context = ivboy[0].getContext();
                Intent intent = new Intent(context, assessment_manager.class);
                String fromactivityname = "animpage1";
                intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
                intent.putExtra("EASYASSESS_QUESTIONID", questionid[0]);
                intent.putExtra("EASYASSESS_CLICKEDBACKARROW", false);
                startActivity(intent);
            }

        },10);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adrabbit[0] != null)
            adrabbit[0].stop();
        if(adboy[0] != null)
            adboy[0].stop();
    }

    @Override
    public void onBackPressed() {

        /*
        // super.onBackPressed();
        if(adrabbit[0] != null)
            adrabbit[0].stop();
        if(adboy[0] != null)
            adboy[0].stop();

        Context context = ivboy[0].getContext();
        Intent intent = new Intent(context, assessment_manager.class);
        String fromactivityname = "animpage1";
        intent.putExtra("EASYASSESS_FROMACTIVITY", fromactivityname);
        intent.putExtra("EASYASSESS_QUESTIONID", questionid[0]);
        intent.putExtra("EASYASSESS_CLICKEDBACKARROW", true);
        startActivity(intent);
        */
    }
}
