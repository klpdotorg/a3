package com.gka.akshara.assesseasy;

import android.graphics.Typeface;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by suresh on 21-02-2018.
 */

public class DragAndDropDragListener implements View.OnDragListener {

    int choiceTextViewID = 0;  // ID of the Choice TextView from which the Text was dragged from
    int blankTextViewID = 0;   // ID of the Blank TextView in which the dragged Text was dropped in

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
                    TextView dropTarget = (TextView) v;

                    //view being dragged and dropped
                    TextView dropped = (TextView) view;

                    this.choiceTextViewID = dropped.getId();
                    this.blankTextViewID = dropTarget.getId();

                    //update the text in the target view to reflect the data being dropped
                    dropTarget.setText(dropped.getText());

                    //make it bold to highlight the fact that an item has been dropped
                    dropTarget.setTypeface(Typeface.DEFAULT_BOLD);

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

    public int getChoiceTextViewId() {
        return this.choiceTextViewID;
    }

    public int getBlankTextViewId() {
        return this.blankTextViewID;
    }
}
