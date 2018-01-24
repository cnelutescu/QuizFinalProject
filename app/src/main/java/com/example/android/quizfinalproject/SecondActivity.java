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

public class SecondActivity extends AppCompatActivity {

    int nrCorrectAnswers = 0;               // count number of correct answers
    int nrInCorrectAnswers = 0;             // count number of incorrect answers
    int nrAnswersNotGiven = 0;              // count number of answers not given
    boolean pressSubmit = false;              // Submit quiz button not pressed
    // integer array of id's of image views for question numbers:
    int[] question_numbers_ID = {R.id.img_q1, R.id.img_q2, R.id.img_q3, R.id.img_q4, R.id.img_q5, R.id.img_q6, R.id.img_q7, R.id.img_q8, R.id.img_q9, R.id.img_q10};
    // integer array of id's of text views for question texts:
    int[] question_texts_ID = {R.id.q1_question, R.id.q2_question, R.id.q3_question, R.id.q4_question, R.id.q5_question, R.id.q6_question, R.id.q7_question, R.id.q8_question, R.id.q9_question, R.id.q10_question};
    // integer array to define the type of questions: 1 = radio button, 2 = checkbox, 3 = free text response:
    int[] question_type = {1, 1, 1, 1, 1, 1, 2, 2, 2, 3};
    // integer array to define the answer status: 0 = incorrect, 1 = correct
    int[] answerStatus = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // integer array of id's of RadioGroups for multiple choice question numbers:
    int[] radioGroups_ID = {R.id.radio_group1, R.id.radio_group2, R.id.radio_group3, R.id.radio_group4, R.id.radio_group5, R.id.radio_group6};
    // integer array of id's of radio button for correct answers:
    int[] correct_answers_ID = {R.id.answer1_3, R.id.answer2_4, R.id.answer3_2, R.id.answer4_3, R.id.answer5_3, R.id.answer6_2};
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
    // 2 dimensional integer array of id's of checkboxes:
    int[][] answers_ID2 = new int[][]{
            {R.id.answer7_1, R.id.answer7_2, R.id.answer7_3, R.id.answer7_4},
            {R.id.answer8_1, R.id.answer8_2, R.id.answer8_3, R.id.answer8_4},
            {R.id.answer9_1, R.id.answer9_2, R.id.answer9_3, R.id.answer9_4},
    };
    // 2 dimensional boolean array for state of checkboxes (0-unchecked, 1=checked):
    boolean[][] correct_answers_ID2 = new boolean[][]{
            {false, true, false, true},
            {false, true, true, false},
            {true, true, false, true}
    };
    // integer array of id's of Edit text views answers for free text response question type:
    int[] answers_ID3 = {R.id.answer10_1};
    // integer array of id's of for the text of correct answers for free text response question type:
    int[] correct_answers_ID3 = {R.string.answer10_1};
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
    private Toast quizToastStartQuiz, mToastToShow, quizToastTimeExpired, quizToastWarningTime;

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
        quizToastStartQuiz = Toast.makeText(this, R.string.quiz_started, Toast.LENGTH_LONG);
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
//        String messageQuestionsNotAnswered = getString(R.string.message_question_not_answered);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.message_question_not_answered));
        for (int k = 0; k < question_type.length; k++) {
            if (checked_answers_text[k].equals(getString(R.string.answer_not_given)))                 // question/s NOT ANSWERED?
            {
                all_questions = false;                                  // = false if at least one question have not an answer
                // messageQuestionsNotAnswered += (k + 1) + ", ";
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
            // Set the toast message duration
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

        quizResultsMessage = getString(R.string.submit_success, name);
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
            //messageSend += "\n\n   *** " + getString(R.string.question_nr) + (i + 1);
            sb.append("\n\n   *** ").append(getString(R.string.question_nr)).append(i + 1);
            if (answerStatus[i] == 0) {
                //messageSend += " - " + getString(R.string.incorrect_answered) + " ***";
                sb.append(" - ").append(getString(R.string.incorrect_answered)).append(" ***");
            } else {
                //messageSend += " - " + getString(R.string.correct_answered) + " ***";
                sb.append(" - ").append(getString(R.string.correct_answered)).append(" ***");
            }
            //messageSend += "\n" + questions_text[i];
            sb.append("\n").append(questions_text[i]);
            //messageSend += "\n- " + getString(R.string.selected_answer) + " " + checked_answers_text[i];
            sb.append("\n- ").append(getString(R.string.selected_answer)).append(" ").append(checked_answers_text[i]);
            //messageSend += "\n- " + getString(R.string.correct_answer) + "  " + correct_answers_text[i];
            sb.append("\n- ").append(getString(R.string.correct_answer)).append("  ").append(correct_answers_text[i]);
        }
        messageSend = "" + sb;
    }

    // Method to process Radio button question type
    private void processRadioButton(int x, int k) {

        RadioButton radioButton = findViewById(correct_answers_ID[x]);     // get ID of correct answer
        ImageView imageView = findViewById(question_numbers_ID[k]);        // get ID of question number
        // Check if the correct answer is checked
        if (radioButton.isChecked()) {
            // correct answer is checked
            nrCorrectAnswers++;
            answerStatus[k] = 1;
            correct_answers_text[k] = radioButton.getText().toString();    // store the correct answer
            imageView.setImageResource(R.drawable.ok);                 // set the ok check mark image on question number
            imageView.setVisibility(View.VISIBLE);
        } else {
            // incorrect answer is checked
            nrInCorrectAnswers++;
            answerStatus[k] = 0;
            correct_answers_text[k] = radioButton.getText().toString();    // store the correct answer
            imageView.setImageResource(R.drawable.notok);              // set the not ok mark image on question number
            imageView.setVisibility(View.VISIBLE);
        }
        //check if the question has answer
        RadioGroup radio_group = findViewById(radioGroups_ID[x]);
        if (radio_group.getCheckedRadioButtonId() == -1) {
            // No answer
            nrAnswersNotGiven++;
            checked_answers_text[k] = getString(R.string.answer_not_given);                   // store NOT ANSWERED
        } else {
            // Has answer
            checked_answers_text[k] = ((RadioButton) findViewById(radio_group.getCheckedRadioButtonId())).getText().toString();   // store the checked answer
        }
    }

    // Method to process Check box button question type
    private void processCheckBox(int i, int k) {

        CheckBox checkBox1 = findViewById(answers_ID2[i][0]);     // get check box 1
        CheckBox checkBox2 = findViewById(answers_ID2[i][1]);     // get check box 2
        CheckBox checkBox3 = findViewById(answers_ID2[i][2]);     // get check box 3
        CheckBox checkBox4 = findViewById(answers_ID2[i][3]);     // get check box 4
        // store the checked answers
        int notAnswered = 1;
        if (checkBox1.isChecked()) {
            checked_answers_text[k] = "\n   - " + checkBox1.getText().toString();
            notAnswered = 0;
        }
        if (checkBox2.isChecked()) {
            checked_answers_text[k] += "\n   - " + checkBox2.getText().toString();
            notAnswered = 0;
        }
        if (checkBox3.isChecked()) {
            checked_answers_text[k] += "\n   - " + checkBox3.getText().toString();
            notAnswered = 0;
        }
        if (checkBox4.isChecked()) {
            checked_answers_text[k] += "\n   - " + checkBox4.getText().toString();
            notAnswered = 0;
        }
        if (notAnswered == 1) {
            // No answer given
            checked_answers_text[k] = getString(R.string.answer_not_given);
            nrAnswersNotGiven++;
        }
        // store the correct answers
        if (correct_answers_ID2[i][0]) {
            correct_answers_text[k] = "\n   - " + checkBox1.getText().toString();
        }
        if (correct_answers_ID2[i][1]) {
            correct_answers_text[k] += "\n   - " + checkBox2.getText().toString();
        }
        if (correct_answers_ID2[i][2]) {
            correct_answers_text[k] += "\n   - " + checkBox3.getText().toString();
        }
        if (correct_answers_ID2[i][3]) {
            correct_answers_text[k] += "\n   - " + checkBox4.getText().toString();
        }
        ImageView imageView = findViewById(question_numbers_ID[k]);        // get ID of question number
        if ((checkBox1.isChecked() == correct_answers_ID2[i][0]) && (checkBox2.isChecked() == correct_answers_ID2[i][1]) &&
                (checkBox3.isChecked() == correct_answers_ID2[i][2]) && (checkBox4.isChecked() == correct_answers_ID2[i][3])) {
            // all correct answers are checked and incorrect are not checked
            imageView.setImageResource(R.drawable.ok);                 // set the OK check mark image on question number
            imageView.setVisibility(View.VISIBLE);
            nrCorrectAnswers++;
            answerStatus[k] = 1;
        } else {
            // not all correct answers are checked and incorrect are not checked
            imageView.setImageResource(R.drawable.notok);              // set the NOT OK mark image on question number
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
        correct_answers_text[k] = getString(correct_answers_ID3[j]);                // store the correct answer
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
}

