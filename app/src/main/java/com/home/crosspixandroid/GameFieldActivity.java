package com.home.crosspixandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import picture.Numbers;

import static android.graphics.Color.GREEN;
import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static picture.NumbersSide.LEFT;
import static picture.NumbersSide.TOP;

@SuppressLint("SetTextI18n")
public class GameFieldActivity extends AppCompatActivity {
    private LinearLayout.LayoutParams cellLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        cellLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        initTopNumbersLayout();
        initLeftNumbersLayout();
        initFieldLayout();
    }

    private void initLeftNumbersLayout() {
        LinearLayout leftNumbersLayout = findViewById(R.id.leftNumbersLeyout);
        Numbers leftNumbers = new Numbers(GameContext.stashedPicture, LEFT);
        int depth = leftNumbers.getDepth();
        int size = leftNumbers.getSize();
        for (int i = 0; i < size; i++) {
            int[] row = leftNumbers.getVector(i);
            int leftPadding = depth - row.length;
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(HORIZONTAL);
            for (int j = 0; j < leftPadding; j++) {
                rowLayout.addView(new Space(this), cellLayoutParams);
            }
            for (int value : row) {
                TextView textView = new TextView(this);
                textView.setText(Integer.toString(value));
                textView.setGravity(CENTER);
                textView.setBackgroundColor(GREEN);
                rowLayout.addView(textView, cellLayoutParams);
            }
            leftNumbersLayout.addView(rowLayout, cellLayoutParams);
        }
    }

    private void initTopNumbersLayout() {
        LinearLayout topNumbersLayout = findViewById(R.id.topNumbersLayout);
        Numbers topNumbers = new Numbers(GameContext.stashedPicture, TOP);
        int depth = topNumbers.getDepth();
        LinearLayout[] rowLayouts = new LinearLayout[depth];
        for (int i = 0; i < depth; i++) {
            rowLayouts[i] = new LinearLayout(this);
            rowLayouts[i].setOrientation(HORIZONTAL);
            topNumbersLayout.addView(rowLayouts[i], cellLayoutParams);
        }

        int size = topNumbers.getSize();
        for (int j = 0; j < size; j++) {
            int[] column = topNumbers.getVector(j);
            int topPadding = depth - column.length;
            for (int i = 0; i < topPadding; i++) {
                rowLayouts[i].addView(new Space(this), cellLayoutParams);
            }
            for (int i = 0; i < column.length; i++) {
                TextView textView = new TextView(this);
                textView.setText(Integer.toString(column[i]));
                textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                textView.setBackgroundColor(GREEN);
                rowLayouts[i + topPadding].addView(textView, cellLayoutParams);
            }
        }
    }

    private void initFieldLayout() {
        LinearLayout fieldTable = findViewById(R.id.fieldLayout);
        int height = GameContext.stashedPicture.getHeight();
        int width = GameContext.stashedPicture.getWidth();
        for (int i = 0; i < height; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(HORIZONTAL);
            for (int j = 0; j < width; j++) {
                rowLayout.addView(new Button(this), cellLayoutParams);
            }
            fieldTable.addView(rowLayout, cellLayoutParams);
        }
    }
}
