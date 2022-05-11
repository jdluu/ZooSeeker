package com.example.zooseeker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    public TextView header;
    public TextView body;
    public Button   nextButton;
    public Button previousButton;
    public Button   exitButton;
    public int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        header = findViewById(R.id.header_txt);
        body = findViewById(R.id.body_txt);
        nextButton = findViewById(R.id.next_btn);
        previousButton = findViewById(R.id.previous_btn);
        exitButton = findViewById(R.id.exit_btn);
        i = 0;

        setDirection();

        nextButton.setOnClickListener(this::nextClicked);
        previousButton.setOnClickListener(this::previousClicked);
    }

    /*
    *   Name:       nextClicked
    *   Behavior:   When the next button is clicked, if the current direction is the last one,
    *               display a message notifying there are no more directions. Otherwise, increment the
    *               index of the current direction i and set the direction.
    *   @param      View     view       the view being called from
    *   @return
     */
    void nextClicked(View view) {
        if (this.i == DirectionTracker.directions.size() - 1) {
            Utilities.showAlert(this, "No More Directions!");
            return;
        }

        this.i++;
        setDirection();
    }

    /*
     *   Name:       previousClicked
     *   Behavior:   When the previous button is clicked, if the current direction is the first one,
     *               display a message notifying the user this is the case. Otherwise, decrement the
     *               index of the current direction i and set the direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void previousClicked(View view) {
        if (this.i == 0) {
            Utilities.showAlert(this, "This is the First Direction!");
            return;
        }

        this.i--;
        setDirection();
    }

    /*
     *   Name:       setDirection
     *   Behavior:   Update the header and body to reflect the details of the current direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void setDirection() {
        header.setText(DirectionTracker.directions.get(this.i).getStart() + " to " + DirectionTracker.directions.get(this.i).getEnd());

        String directionsString = "";
        List<String> steps = DirectionTracker.directions.get(i).getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            directionsString += steps.get(j) + "\n";
        }

        body.setText(directionsString);
    }
}