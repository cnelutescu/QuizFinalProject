package com.example.android.quizfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Method calls Quiz Activity
    public void startQuiz(View view) {
        EditText nameEditText = findViewById(R.id.name);
        String name = nameEditText.getText().toString().trim();
        if (name.equals("")) {
            Toast.makeText(this, R.string.msg_missing_name, Toast.LENGTH_LONG).show();
        } else {
            long lStartTime = new Date().getTime();
            String startTime = "" + lStartTime;
            Intent intent = new Intent(this, SecondActivity.class);
            Bundle extras = new Bundle();
            extras.putString("EXTRA_NAME", name);
            extras.putString("EXTRA_START_TIME", startTime);
            intent.putExtras(extras);
            startActivity(intent);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.activity_not_resolved, Toast.LENGTH_LONG).show();
            }
        }
    }
}
