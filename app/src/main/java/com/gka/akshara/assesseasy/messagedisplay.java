package com.gka.akshara.assesseasy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.akshara.assessment.a3.R;

public class messagedisplay extends AppCompatActivity {

    String gotoactivityname = "";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagedisplay);

        if (MainActivity.debugalerts)
            Log.d("EASYASSESS", "messagedisplay: in onCreate ");

        Intent intent = getIntent(); // get the Intent that started this activity
        Bundle databundle = intent.getExtras();

        if(databundle != null) {
            this.gotoactivityname = databundle.getString("EASYASSESS_GOTOACTIVITY");
            this.message = databundle.getString("EASYASSESS_MESSAGE");
        }

        TextView msgview = (TextView)findViewById(R.id.textViewMessage);
        msgview.setText(this.message);

    }

    public void clickedOK(View view) {

        this.invokeGOTOactivity();
    }

    public void invokeGOTOactivity() {

        try {
            Intent intentnxt = new Intent(this, Class.forName(this.gotoactivityname));
            startActivity(intentnxt);

            if (MainActivity.debugalerts)
                Log.d("EASYASSESS", "messagedisplay: clickedOK: gotoactivity: " + this.gotoactivityname);
        }
        catch (Exception e) {
            Log.e("EASYASSESS", "messagedisplay.invokeGOTOactivity: Exception."+e.toString());
        }
    }
}
