package com.gka.akshara.assesseasy;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sures on 21-02-2018.
 */

public class DragAndDropListenerForImage  implements View.OnDragListener  {

    int choiceImageViewID = 0;  // ID of the Choice ImageView from which the image was dragged from
    int blankImageViewID = 0;   // ID of the Blank ImageView in which the dragged image was dropped in

    @Override
    public boolean onDrag(View v, DragEvent event) {

        switch (event.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                //no action necessary
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                //no action necessary
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                //no action necessary
                break;

            case DragEvent.ACTION_DROP:

                //handle the dragged view being dropped over a drop view
                View view = (View) event.getLocalState();

                //stop displaying the view where it was before it was dragged
                // view.setVisibility(View.INVISIBLE);

                //view dragged item is being dropped on
                ImageView dropTarget = (ImageView) v;

                //view being dragged and dropped
                ImageView dropped = (ImageView) view;

                this.choiceImageViewID = dropped.getId();
                this.blankImageViewID = dropTarget.getId();

                //update the text in the target view to reflect the data being dropped
                Bitmap droppedimage=((BitmapDrawable)dropped.getDrawable()).getBitmap();
                dropTarget.setImageBitmap(droppedimage);

                //make it bold to highlight the fact that an item has been dropped
                // dropTarget.setTypeface(Typeface.DEFAULT_BOLD);

                //if an item has already been dropped here, there will be a tag
                Object tag = dropTarget.getTag();

                //if there is already an item here, set it back visible in its original place
                //set the tag in the target view being dropped on - to the ID of the view being dropped
                // dropTarget.setTag(dropped.getId());
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                //no action necessary
                break;

            default:
                break;
        }
        return true;
    }

    public int getChoiceImageViewId() {
        return this.choiceImageViewID;
    }

    public int getBlankImageViewId() {
        return this.blankImageViewID;
    }

}
