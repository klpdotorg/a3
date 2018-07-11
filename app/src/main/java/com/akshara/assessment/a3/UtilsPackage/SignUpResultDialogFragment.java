package com.akshara.assessment.a3.UtilsPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.akshara.assessment.a3.LoginActivity;
import com.akshara.assessment.a3.R;



public class SignUpResultDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle args = getArguments();
        builder.setMessage(args.getString("result"))

                .setPositiveButton(args.getString("buttonText"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(args.getString("buttonText").equalsIgnoreCase(getResources().getString(R.string.login))) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().finish();
                            startActivity(intent);
                        }
                    }
                });

        // Create the AlertDialog object and return it
        builder.setCancelable(false);
        return builder.create();
    }
}
