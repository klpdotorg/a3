package com.gka.akshara.assesseasy;

import android.app.AlertDialog;
import android.app.Dialog;
/*import android.app.DialogFragment; */
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by suresh on 18-05-2018.
 */

public class messageDisplayMgr extends DialogFragment {

        String positiveactivityname = ""; // Activity to invoke on Positive button press
        String negativeactivityname = ""; // Activity to invoke on Negative button press
        String message = ""; // Message to display
        String positivebuttonname = "";
        String negativebuttonname = "";


        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","messageDisplayMgr: in onCreate");
            this.positiveactivityname = getArguments().getString("positiveactivityname");
            this.negativeactivityname = getArguments().getString("negativeactivityname");
            this.positivebuttonname = getArguments().getString("positivebuttonname");
            this.negativebuttonname = getArguments().getString("negativebuttonname");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if(MainActivity.debugalerts)
                Log.d("EASYASSESS","messageDisplayMgr: in onCreateDialog");
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(this.message)
                    .setPositiveButton(this.positivebuttonname, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Clicked positive button
                        }
                    })
                    .setNegativeButton(this.negativebuttonname, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Clicked negative (cancel) button
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }


}
