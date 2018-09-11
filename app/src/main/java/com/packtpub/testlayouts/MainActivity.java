package com.packtpub.testlayouts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPlayClick(View view) {
        Intent i;
        i = new Intent(this, Level1Activity.class);
        startActivity(i);
    }


    public void onResultsClick(View view) {
        //TODO! Results page
        Toast toast = Toast.makeText(getApplicationContext(), "Results are really great!!!", Toast.LENGTH_LONG);
        toast.show();
    }
}
