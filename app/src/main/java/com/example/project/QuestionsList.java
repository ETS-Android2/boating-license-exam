package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionsList extends AppCompatActivity implements QuestionsAdapter.OnQuestionListener {
    private ArrayList<String> questions;
    private ArrayList<Boolean> trueOrFalse;
    private int testId;
    private String code;

    private TextView message1;
    private TextView message2;
    private TextView message3;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
    }

    @Override
    public void onResume() {
        System.out.println("Resumed");
        questions = new ArrayList<>();
        trueOrFalse = new ArrayList<>();

        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        message3 = findViewById(R.id.message3);
        linear = findViewById(R.id.linear);

        code = getIntent().getStringExtra("code");
        if (code.equals("test_questions") || code.equals("previous_attempts"))
            testId = getIntent().getIntExtra("testId", 0);
        initQuestions();
        super.onResume();
    }

    private void initQuestions(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            int testScore = dbHandler.getTestScore(testId);
            if (testScore < 19) {
                linear.setBackgroundColor(getResources().getColor(R.color.reddish));
                message2.setText(getString(R.string.failure_message));
            }
            else {
                linear.setBackgroundColor(getResources().getColor(R.color.greenish));
                message2.setText(getString(R.string.success_message));
            }
            if (code.equals("test_questions"))
                message1.setText(getString(R.string.results));
            else message1.setText(getString(R.string.menu_previous_attempts));
            message3.setText(getString(R.string.your_score_was) + testScore + "/20");
            TestQuestionDB[] questionsDB;
            questionsDB = dbHandler.getPreviousTestQuestions(testId);
            for (int i = 0; i < 20; i++) {
                questions.add(questionsDB[i].getQuestion());
                trueOrFalse.add(questionsDB[i].getAnswer() == questionsDB[i].getCorrect_answer());
            }
        }
        else if (code.equals("saved_questions")) {
            int savedSize = dbHandler.getSavedSize();
            message1.setText(getString(R.string.menu_saved_questions));
            message2.setText(getString(R.string.number_of_saved_questions));
            message3.setText("" + savedSize);
            if (savedSize > 0) {
                QuestionDB[] saved = new QuestionDB[savedSize];
                saved = dbHandler.getSaved();
                for (int i = 0; i < savedSize; i++) {
                    questions.add(saved[i].getQuestion());
                    trueOrFalse.add(true);
                }
            }
        }

        initRecyclerView();
    }
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        QuestionsAdapter adapter = new QuestionsAdapter(questions, trueOrFalse, code, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onQuestionClick(int questionId) {
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            Intent i = new Intent(this, TestQuestion.class);
            i.putExtra("testQuestionId", questionId + 1);
            i.putExtra("testId", testId);
            i.putExtra("code", code);
            System.out.println("pos : " + questionId + " , test : " + testId);
            startActivity(i);
        }
        else if (code.equals("saved_questions")) {
            Intent i = new Intent(this, SavedQuestion.class);
            //i.putExtra("savedQuestionId", savedQuestions.get(questionId) );
            i.putExtra("savedQuestionPosition", questionId);
            startActivity(i);
        }

    }
}