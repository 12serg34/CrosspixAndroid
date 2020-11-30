package com.home.crosspixandroid;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.EnumMap;
import java.util.HashMap;

import entities.Answer;
import entities.CellState;
import entities.Numbers;
import function.Consumer;
import message.CellUpdatedNotification;

import static android.graphics.Color.GREEN;
import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.widget.LinearLayout.HORIZONTAL;

@SuppressLint("SetTextI18n")
public class GameFieldActivity extends AppCompatActivity {
    private static final EnumMap<Answer, Integer> answerToColor;
    private static final EnumMap<CellState, Integer> stateToColor;
    private static final int WAIT_COLOR = Color.BLUE;

    static {
        answerToColor = new EnumMap<>(Answer.class);
        answerToColor.put(Answer.SUCCESS, Color.BLACK);
        answerToColor.put(Answer.MISTAKE, Color.RED);

        stateToColor = new EnumMap<>(CellState.class);
        stateToColor.put(CellState.FULL, Color.BLACK);
        stateToColor.put(CellState.BLANK, Color.WHITE);
        stateToColor.put(CellState.EMPTY, Color.GRAY);
    }

    private LinearLayout.LayoutParams cellLayoutParams;
    private Button[][] cells;
    private HashMap<Button, Point> cellToPointMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        GameContext.guessedPicture.setUpdatedCellListener(new CellUpdatesListener());
        cellLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        initTopNumbersLayout();
        initLeftNumbersLayout();
        initFieldLayout();
    }

    private void initLeftNumbersLayout() {
        LinearLayout leftNumbersLayout = findViewById(R.id.leftNumbersLeyout);
        Numbers leftNumbers = GameContext.context.getLeftNumbers();
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
        Numbers topNumbers = GameContext.context.getTopNumbers();
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
        int height = GameContext.context.getLeftNumbers().getSize();
        int width = GameContext.context.getTopNumbers().getSize();
        cells = new Button[height][width];
        cellToPointMap = new HashMap<>(height * width);
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        ButtonLongClickListener buttonLongClickListener = new ButtonLongClickListener();
        for (int i = 0; i < height; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(HORIZONTAL);
            for (int j = 0; j < width; j++) {
                Button button = new Button(this);
                button.setBackground(getDrawable(R.drawable.field_button));
                button.setBackgroundColor(stateToColor.get(GameContext.context.getField().getCellState(i, j)));

                button.setOnClickListener(buttonClickListener);
                button.setOnLongClickListener(buttonLongClickListener);
                cells[i][j] = button;
                cellToPointMap.put(button, new Point(j, i));
                rowLayout.addView(button, cellLayoutParams);
            }
            fieldTable.addView(rowLayout, cellLayoutParams);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            @SuppressWarnings("SuspiciousMethodCalls") Point point = cellToPointMap.get(v);
            if (GameContext.guessedPicture.getCellState(point.y, point.x) == CellState.BLANK) {
                GameContext.guessedPicture.discoverRequest(point.y, point.x);
                v.setBackgroundColor(WAIT_COLOR);
            }
        }
    }

    private class ButtonLongClickListener implements View.OnLongClickListener {
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean onLongClick(final View v) {
            @SuppressWarnings("SuspiciousMethodCalls") final Point point = cellToPointMap.get(v);
            CellState state = GameContext.guessedPicture.toggleEmpty(point.y, point.x);
            v.setBackgroundColor(stateToColor.get(state));
            return true;
        }
    }

    private class CellUpdatesListener implements Consumer<CellUpdatedNotification> {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void accept(final CellUpdatedNotification notification) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Answer answer = notification.getAnswer();
                    if (answer != Answer.NOTHING) {
                        int i = notification.getI();
                        int j = notification.getJ();
                        cells[i][j].setBackgroundColor(answerToColor.get(answer));
                    }
                }
            });
        }
    }
}
