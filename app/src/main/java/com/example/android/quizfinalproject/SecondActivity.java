package com.example.android.quizfinalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    int nrCorrectAnswers;               // count number of correct answers
    int nrInCorrectAnswers;             // count number of incorrect answers
    int nrAnswersNotGiven;              // count number of answers not given
    boolean pressSubmit = false;        // Submit quiz button not pressed
    // integer array of id's of image views for question numbers:
    int[] question_numbers_ID = {R.id.img_q1, R.id.img_q2, R.id.img_q3, R.id.img_q4, R.id.img_q5, R.id.img_q6, R.id.img_q7, R.id.img_q8, R.id.img_q9, R.id.img_q10};
    // integer array of id's of text views for question texts:
    int[] question_texts_ID = {R.id.q1_question, R.id.q2_question, R.id.q3_question, R.id.q4_question, R.id.q5_question, R.id.q6_question, R.id.q7_question, R.id.q8_question, R.id.q9_question, R.id.q10_question};
    // integer array of id's of RadioGroups for multiple choice question numbers:
    int[] radioGroups_ID = {R.id.radio_group1, R.id.radio_group2, R.id.radio_group3, R.id.radio_group4, R.id.radio_group5, R.id.radio_group6};
    // 2 dimensional integer array of id's of radio buttons:
    int[][] answers_ID1 = new int[][]{
            {R.id.answer1_1, R.id.answer1_2, R.id.answer1_3, R.id.answer1_4},
            {R.id.answer2_1, R.id.answer2_2, R.id.answer2_3, R.id.answer2_4},
            {R.id.answer3_1, R.id.answer3_2, R.id.answer3_3, R.id.answer3_4},
            {R.id.answer4_1, R.id.answer4_2, R.id.answer4_3, R.id.answer4_4},
            {R.id.answer5_1, R.id.answer5_2, R.id.answer5_3, R.id.answer5_4},
            {R.id.answer6_1, R.id.answer6_2, R.id.answer6_3, R.id.answer6_4},
    };
    // integer array of id's of radio button for correct answers:
    int[] correct_answers_ID1 = {0, 0, 0, 0, 0, 0};
    // 2 dimensional integer array of id's of checkboxes:
    int[][] answers_ID2 = new int[][]{
            {R.id.answer7_1, R.id.answer7_2, R.id.answer7_3, R.id.answer7_4},
            {R.id.answer8_1, R.id.answer8_2, R.id.answer8_3, R.id.answer8_4},
            {R.id.answer9_1, R.id.answer9_2, R.id.answer9_3, R.id.answer9_4},
    };
    // 2 dimensional boolean array for state of checkboxes (0-unchecked, 1=checked):
    boolean[][] correct_answers_ID2 = new boolean[3][4];
    // integer array of id's of Edit text views answers for free text response question type:
    int[] answers_ID3 = {R.id.answer10_1};
    // array of strings with the text of correct answers for edit text questions
    String[] correctAnswer3 = new String[10];     // store the correct answers texts of edit text questions at run time

    // integer array to define the type of questions: 1 = radio button, 2 = checkbox, 3 = free text response:
    int[] question_type = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    // integer array to define the answer status: 0 = incorrect, 1 = correct
    int[] answerStatus = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // array of strings with the text of checked answers
    String[] checked_answers_text = new String[10];     // store the given answers texts at run time
    // array of strings with the text of correct answers
    String[] correct_answers_text = new String[10];     // store the correct answers texts at run time
    // array of strings with the text of questions
    String[] questions_text = new String[10];

    // The name under which the quiz is taken
    String name;
    // The message text to display on toast
    String quizResultsMessage = "";
    // The message text to send by email quiz results
    String messageSend;

    long lStartTime;    // long start time in milliseconds
    long lEndTime;      // long end time in milliseconds
    long output;        // long difference in milliseconds
    String elapsedTime; // string elapsed time in milliseconds
    boolean all_questions = true;           // = true if all questions have an answer
    boolean yesButton = false;              // = true for yes confirmation, = false for Cancel submit quiz
    // toast to display final score message
    CountDownTimer toastCountDown;              // declare object for toast count down counter
    int toastDurationInMilliSeconds = 7000;     // Set the toast duration for 7 seconds
    CountDownTimer quizTimeCountDown;           // declare object for quiz time count down counter
    int quizMaxTime = 600000;                   // quiz max time duration in milliseconds (10 minutes)
    private Toast mToastToShow;
    private Toast quizToastTimeExpired;
    private Toast quizToastWarningTime;

    //---------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // Set on click listener for Submit button
        ImageButton imgSubmitButton = findViewById(R.id.submit_button);
        imgSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitQuiz();
            }
        });
        // Set on click listener for Send results button
        ImageButton imgSendButton = findViewById(R.id.send_button);
        imgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuizResults();
            }
        });
        // Set on click listener for Start new button
        ImageButton imgBackButton = findViewById(R.id.back_button);
        imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewQuiz();
            }
        });
        // Set the toast START quiz message
        Toast quizToastStartQuiz = Toast.makeText(this, R.string.quiz_started, Toast.LENGTH_LONG);
        quizToastStartQuiz.show();          // show toast Start quiz
        // Set the toast time expired message
        quizToastTimeExpired = Toast.makeText(this, R.string.time_expired, Toast.LENGTH_LONG);
        // Set the toast 1 minute left warning message
        quizToastWarningTime = Toast.makeText(this, R.string.warning_time_expire, Toast.LENGTH_LONG);
        // Set the countdown timer to display the toast message longer (7 sec. instead of 3.5 sec.)
        quizTimeCountDown = new CountDownTimer(quizMaxTime, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {  // one tick at each sec. (1000 milliseconds)
                // EditText editText = findViewById(answers_ID3[0]);                   // get ID of edit text
                // editText.setText("Seconds remaining: " + millisUntilFinished / 1000);
                if ((millisUntilFinished < 60000) & (millisUntilFinished > 58000)) {      // if one minute left?
                    quizToastWarningTime.show();        // show toast quiz time will expire warning
                }
            }

            public void onFinish() {
                quizToastTimeExpired.show();            // show toast quiz time expired
            }
        };
        quizTimeCountDown.start();                  // start quiz time counter
        // find and store the questions type
        for (int k = 0; k < question_type.length; k++) {
            TextView textView = findViewById(question_texts_ID[k]);  // get ID of question text
            String nr = "" + (k + 1);
            Object ddd = textView.getTag();
            if (ddd == null) {    // if android:tag is not defined for current question:
                // Set the toast Error in second_activity.xml at question k tag not found message
                quizToastStartQuiz = Toast.makeText(this, getString(R.string.error_in_xml, nr), Toast.LENGTH_LONG);
                quizToastStartQuiz.show();          // show toast Start quiz
                question_type[k] = 1;                               // store ID=1 of type for question
                //Timer Schedule function for delay 5 seconds:
                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                // Actions to do after 5 seconds
                                finish();              // finish second activity after 5 seconds
                            }
                        },
                        5000
                );
            } else {
                if (textView.getTag().equals("type_radioButtons")) {
                    question_type[k] = 1;                               // store ID=1 of type for question
                } else if (textView.getTag().equals("type_checkBoxes")) {
                    question_type[k] = 2;                               // store ID=2 of type for question
                } else if (textView.getTag().equals("type_edit_text")) {
                    question_type[k] = 3;                               // store ID=2 of type for question
                } else { // Set the toast Error in second_activity.xml at question k incorrect tag message
                    quizToastStartQuiz = Toast.makeText(this, getString(R.string.error_in_xml, nr), Toast.LENGTH_LONG);
                    quizToastStartQuiz.show();          // show toast Start quiz
                    question_type[k] = 1;                               // store ID=1 of type for question
                    //Timer Schedule function for delay 5 seconds:
                    new Timer().schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    // Actions to do after 5 seconds
                                    finish();              // finish second activity after 5 seconds
                                }
                            },
                            5000
                    );
                }
            }
        }
        int x = 0;                          // init counter for number of radio button questions
        int i = 0;                          // init counter for number of checkbox questions
        int j = 0;                          // init counter for number of free text questions
        for (int k = 0; k < question_type.length; k++) {
            switch (question_type[k]) {
                case 1:
                    // find and store the correct answers for radioButtons
                    for (int n = 0; n < 4; n++) {
                        RadioButton radioButton = findViewById(answers_ID1[x][n]);     // get radioButton n
                        if (radioButton.getTag().equals("true")) {
                            correct_answers_ID1[x] = answers_ID1[x][n]; // store ID of correct answer
                        }
                    }
                    x++;
                    break;
                case 2:
                    // find and store the correct answers for checkBoxes
                    for (int n = 0; n < 4; n++) {
                        CheckBox checkBox = findViewById(answers_ID2[i][n]);     // get check box n
                        if (checkBox.getTag().equals("true")) {
                            correct_answers_ID2[i][n] = true;
                        } else {
                            correct_answers_ID2[i][n] = false;
                        }
                    }
                    i++;
                    break;
                case 3:
                    // find and store the correct answers for edit text questions
                    for (int n = 0; n < 1; n++) {
                        EditText editText = findViewById(answers_ID3[j]);     // get ID of edit text
                        String stringNameOfResource = editText.getTag().toString();
                        correctAnswer3[j] = getStringResourceByName(stringNameOfResource);
                    }
                    j++;
                    break;
            }
        }
    }

    // Method to process the start new quiz (Start new button click)
    public void startNewQuiz() {
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // Method to process the send quiz results new quiz (Send results button click)
    public void sendQuizResults() {
        if (pressSubmit) {      // Submit quiz button pressed?
            Toast.makeText(this, "Processing send quiz results by email!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SENDTO);   // intent is handled only by an email app
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Java quiz - review results");     // email subject (email address manual input)
            intent.putExtra(Intent.EXTRA_TEXT, messageSend);    // email text
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, R.string.send_error, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to process the submitted quiz (Submit button click)
    public void submitQuiz() {
        Toast.makeText(this, R.string.processing_submit, Toast.LENGTH_LONG).show();
        nrCorrectAnswers = 0;               // init counter for correct answers
        nrInCorrectAnswers = 0;             // init counter for incorrect answers
        nrAnswersNotGiven = 0;              // init counter for answers not given
        int x = 0;                          // init counter for number of radio button questions
        int i = 0;                          // init counter for number of checkbox questions
        int j = 0;                          // init counter for number of free text questions
        for (int k = 0; k < question_type.length; k++) {    // init arrays
            answerStatus[k] = 0;
            checked_answers_text[k] = "";
            correct_answers_text[k] = "";
        }
        for (int k = 0; k < question_type.length; k++) {
            switch (question_type[k]) {
                case 1:
                    processRadioButton(x, k);   // process radio button questions type (single correct answer)
                    x++;
                    break;
                case 2:
                    processCheckBox(i, k);     // process check box questions type (multiple correct answers)
                    i++;
                    break;
                case 3:
                    processText(j, k);         // process free text questions type
                    j++;
                    break;
            }
        }
        all_questions = true;           // = true if all questions have an answer
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.message_question_not_answered));
        for (int k = 0; k < question_type.length; k++) {
            if (checked_answers_text[k].equals(getString(R.string.answer_not_given)))   // question/s NOT ANSWERED?
            {
                all_questions = false;                                  // = false if at least one question have not an answer
                sb.append(k + 1).append(", ");
            }
        }
        if (!(all_questions || yesButton)) {    // if not all questions answered or yes button is not pressed
            if (!all_questions) { // not all questions have answer?
                // make invisible all question check marks
                for (int k = 0; k < question_type.length; k++) {
                    ImageView imageView = findViewById(question_numbers_ID[k]);         // get ID of question number
                    imageView.setVisibility(View.INVISIBLE);                            // set invisible
                }
                // Display a Dialog box for confirmation submitting the quiz
                String messageQuestionsNotAnswered = sb + " " + getString(R.string.want_to_continue);
                AlertDialog.Builder dialog = new AlertDialog.Builder(SecondActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle(R.string.warning);
                dialog.setMessage(messageQuestionsNotAnswered);
                dialog.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Yes"
                        yesButton = true;
                        submitQuiz();
                    }
                })
                        .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel"
                                yesButton = false;
                            }
                        });
                final AlertDialog alert = dialog.create();
                alert.show();
            }
        } else {    // continue with process submit
            yesButton = false;                  // reset state for next Submit
            pressSubmit = true;                 // Submit quiz button pressed
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                name = extras.getString("EXTRA_NAME");                          // get the name from MainActivity
            }
            String startTime = null;
            if (extras != null) {
                startTime = "" + extras.getString("EXTRA_START_TIME");          // get the start time from MainActivity
            }
            lStartTime = Long.parseLong(startTime, 10);
            lEndTime = new Date().getTime();
            output = lEndTime - lStartTime;             // compute elapsed time as long in milliseconds
            getElapsedTime();                           // get elapsed time as a human-readable string
            quizResults();                              // prepare the quiz final score message
            // Set the final toast message and duration
            mToastToShow = Toast.makeText(this, quizResultsMessage, Toast.LENGTH_LONG);
            // Set the countdown timer to display the toast message longer (7 sec. instead of 3.5 sec.)
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
                public void onTick(long millisUntilFinished) {
                    mToastToShow.show();    // restart the toast after the Toast.LENGTH_LONG duration expired
                }

                public void onFinish() {
                    mToastToShow.cancel();  // cancel the toast after the set duration expired
                }
            };
            // Show the toast message and starts the countdown
            mToastToShow.show();
            toastCountDown.start();
        }
    }

    // Method get elapsed time as a human-readable string
    private void getElapsedTime() {
        elapsedTime = "";
        double seconds = (double) output / 1000.0;
        if (seconds < 1.0) {
            double milliseconds = seconds * 1000;
            elapsedTime = "" + milliseconds + " " + getString(R.string.milliseconds);
        } else if (seconds < 60.0) {
            int totalSecs = (int) seconds;
            elapsedTime = "" + totalSecs + " " + getString(R.string.seconds);
        } else if (seconds < 3600.0) {
            int totalSecs = (int) seconds;
            int minutes = (totalSecs % 3600) / 60;
            int sec = totalSecs % 60;
            elapsedTime = " " + minutes + " " + getString(R.string.minutes) + " " + sec + " " + getString(R.string.seconds);
        } else {
            int totalSecs = (int) seconds;
            int hours = totalSecs / 3600;
            int minutes = (totalSecs % 3600) / 60;
            int sec = totalSecs % 60;
            elapsedTime = " " + hours + " " + getString(R.string.hours) + " " + minutes + " " + getString(R.string.minutes) + " " + sec + " " + getString(R.string.seconds);
        }
    }

    // Method to prepare the quiz final score message
    private void quizResults() {

        quizResultsMessage = getString(R.string.submit_success, name) + " " + elapsedTime + "!";
        quizResultsMessage += "\n\n" + getString(R.string.your_score);
        quizResultsMessage += "\n- " + nrCorrectAnswers + " " + getString(R.string.nr_correct_answers) + " " + question_type.length;
        quizResultsMessage += "\n- " + nrInCorrectAnswers + " " + getString(R.string.nr_incorrect_answers) + " " + question_type.length;
        quizResultsMessage += "\n- " + nrAnswersNotGiven + " " + getString(R.string.nr_not_answered) + " " + nrInCorrectAnswers + " " + getString(R.string.incorrect_answers);
        messageSend = quizResultsMessage + "\n\n" + getString(R.string.review_results);
        TextView textView;
        StringBuilder sb = new StringBuilder();     // use string buffer for concatenation string in loops! much more efficient!
        sb.append(messageSend);
        for (int i = 0; i < question_type.length; i++) {
            textView = findViewById(question_texts_ID[i]);          // get ID of question text
            questions_text[i] = textView.getText().toString();      // store question text
            sb.append("\n\n   *** ").append(getString(R.string.question_nr)).append(i + 1);
            if (answerStatus[i] == 0) {
                sb.append(" - ").append(getString(R.string.incorrect_answered)).append(" ***");
            } else {
                sb.append(" - ").append(getString(R.string.correct_answered)).append(" ***");
            }
            sb.append("\n").append(questions_text[i]);
            sb.append("\n- ").append(getString(R.string.selected_answer)).append(" ").append(checked_answers_text[i]);
            sb.append("\n- ").append(getString(R.string.correct_answer)).append("  ").append(correct_answers_text[i]);
        }
        messageSend = "" + sb;
    }

    // Method to process Radio button question type
    private void processRadioButton(int x, int k) {

        RadioButton radioButton_correct = findViewById(correct_answers_ID1[x]);     // get ID of correct answer
        ImageView imageView = findViewById(question_numbers_ID[k]);        // get ID of question number
        // Check if the correct answer is checked
        if (radioButton_correct.isChecked()) {
            // correct answer is checked
            nrCorrectAnswers++;
            answerStatus[k] = 1;
            correct_answers_text[k] = radioButton_correct.getText().toString();    // store the correct answer
            imageView.setImageResource(R.drawable.ok);    // set the ok check mark image on question number
            imageView.setVisibility(View.VISIBLE);
        } else {
            // correct answer is not checked
            nrInCorrectAnswers++;
            answerStatus[k] = 0;
            correct_answers_text[k] = radioButton_correct.getText().toString();    // store the correct answer
            imageView.setImageResource(R.drawable.notok);   // set the not ok mark image on question number
            imageView.setVisibility(View.VISIBLE);
        }
        //check if the question has answer
        RadioGroup radio_group = findViewById(radioGroups_ID[x]);
        if (radio_group.getCheckedRadioButtonId() == -1) {
            // No answer
            nrAnswersNotGiven++;
            checked_answers_text[k] = getString(R.string.answer_not_given);    // store NOT ANSWERED
        } else {
            // Has answer
            checked_answers_text[k] = ((RadioButton) findViewById(radio_group.getCheckedRadioButtonId())).getText().toString();   // store the checked answer
        }
    }

    // Method to process Check box button question type
    private void processCheckBox(int i, int k) {

        int notAnswered = 1;
        int checkedOk = 1;
        checked_answers_text[k] = "\n";
        correct_answers_text[k] = "\n";
        for (int n = 0; n < 4; n++) {
            CheckBox checkBox = findViewById(answers_ID2[i][n]);     // get check box n
            // store the checked answers
            if (checkBox.isChecked()) {
                checked_answers_text[k] += "   - " + checkBox.getText().toString() + "\n";
                notAnswered = 0;
            }
            // store the correct answers
            if (checkBox.getTag().equals("true")) {
                correct_answers_text[k] += "   - " + checkBox.getText().toString() + "\n";
            }
            // is checked or not checked correct?
            if (!(checkBox.isChecked() == correct_answers_ID2[i][n])) {
                checkedOk = 0;
            }
        }
        if (notAnswered == 1) {
            // No answer given
            checked_answers_text[k] = getString(R.string.answer_not_given);
            nrAnswersNotGiven++;
        }
        ImageView imageView = findViewById(question_numbers_ID[k]);  // get ID of question number
        if (checkedOk == 1) {
            // all correct answers are checked and incorrect are not checked
            imageView.setImageResource(R.drawable.ok);  // set the OK check mark image on question number
            imageView.setVisibility(View.VISIBLE);
            nrCorrectAnswers++;
            answerStatus[k] = 1;
        } else {
            // not all correct answers are checked and incorrect are not checked
            imageView.setImageResource(R.drawable.notok); // set the NOT OK mark image on question number
            imageView.setVisibility(View.VISIBLE);
            nrInCorrectAnswers++;
            answerStatus[k] = 0;
        }
    }

    // Method to process free text question type
    private void processText(int j, int k) {

        EditText editText = findViewById(answers_ID3[j]);                   // get ID of edit text
        ImageView imageView = findViewById(question_numbers_ID[k]);         // get ID of question number
        if (editText.getText().toString().toUpperCase().equals("")) {
            // No answer given
            nrAnswersNotGiven++;
            checked_answers_text[k] = getString(R.string.answer_not_given);                            // store NOT ANSWERED
        } else {
            // Has answer
            checked_answers_text[k] = editText.getText().toString().toUpperCase();  // store the given answer
        }
        correct_answers_text[k] = correctAnswer3[j];                // store the correct answer
        // Check if the correct answer is given
        if (editText.getText().toString().toUpperCase().trim().equals(correct_answers_text[k])) {
            // correct answer is given
            nrCorrectAnswers++;
            answerStatus[k] = 1;
            imageView.setImageResource(R.drawable.ok);                  // set the ok check mark image on question number
            imageView.setVisibility(View.VISIBLE);
        } else {
            // incorrect answer is checked
            nrInCorrectAnswers++;
            answerStatus[k] = 0;
            imageView.setImageResource(R.drawable.notok);               // set the not ok mark image on question number
            imageView.setVisibility(View.VISIBLE);
        }
    }

    // Method to get string from resources using its name from strings.xml
    private String getStringResourceByName(String stringNameOfResource) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(stringNameOfResource, "string", packageName);
        return getString(resId);
    }

}
