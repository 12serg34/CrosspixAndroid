package com.home.crosspixandroid;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import picture.Numbers;
import picture.NumbersSide;
import picture.StashedPicture;

@SuppressLint("SetTextI18n")
public class GameFieldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        initFieldTable();
        initLeftTable();
        initTopTable();
    }

    private void initLeftTable() {
        LinearLayout leftNumbersTable = findViewById(R.id.leftNumbersTable);
        StashedPicture stashedPicture = GameContext.stashedPicture;
        Numbers leftNumbers = new Numbers(stashedPicture, NumbersSide.LEFT);
        int depth = leftNumbers.getDepth();
        int size = leftNumbers.getSize();
        for (int i = 0; i < size; i++) {
            int[] row = leftNumbers.getVector(i);
            int leftPadding = depth - row.length;
            LinearLayout tableRow = new LinearLayout(this);
            tableRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < leftPadding; j++) {
                Space space = new Space(this);
                tableRow.addView(space, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }
            for (int j = 0; j < row.length; j++) {
                TextView textView = new TextView(this);
                textView.setText(Integer.toString(row[j]));
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(Color.GREEN);
                tableRow.addView(textView, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }
            leftNumbersTable.addView(tableRow, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }
    }

    private void initTopTable() {
        TableLayout topNumbersTable = findViewById(R.id.topNumbersTable);
        StashedPicture stashedPicture = GameContext.stashedPicture;
        Numbers topNumbers = new Numbers(stashedPicture, NumbersSide.TOP);
        int depth = topNumbers.getDepth();
        TableRow[] tableRows = new TableRow[depth];
        for (int i = 0; i < depth; i++) {
            tableRows[i] = new TableRow(this);
            topNumbersTable.addView(tableRows[i], new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }

        int size = topNumbers.getSize();
        for (int j = 0; j < size; j++) {
            int[] column = topNumbers.getVector(j);
            int topPadding = depth - column.length;
            for (int i = 0; i < topPadding; i++) {
                Space space = new Space(this);
                tableRows[i].addView(space, new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }
            for (int i = 0; i < column.length; i++) {
                TextView textView = new TextView(this);
                textView.setText(Integer.toString(column[i]));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setBackgroundColor(Color.GREEN);
                tableRows[i + topPadding].addView(textView, new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            }
        }
    }

    private void initFieldTable() {
        LinearLayout fieldTable = findViewById(R.id.fieldTable);
        StashedPicture stashedPicture = GameContext.stashedPicture;
        for (int i = 0; i < stashedPicture.getHeight(); i++) {
            LinearLayout tableRow = new LinearLayout(this);
            for (int j = 0; j < stashedPicture.getWidth(); j++) {
                Button button = new Button(this);
                tableRow.addView(button, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }
            fieldTable.addView(tableRow, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }
    }
}
