package com.home.crosspixandroid;

import android.annotation.SuppressLint;
import android.content.res.Resources;
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
    private static final EnumMap<Answer, Integer> answerToBackground;
    private static final EnumMap<CellState, Integer> stateToBackground;

    static {
        answerToBackground = new EnumMap<>(Answer.class);
        answerToBackground.put(Answer.SUCCESS, R.drawable.field_button_full);
        answerToBackground.put(Answer.MISTAKE, R.drawable.field_button_mistake);

        stateToBackground = new EnumMap<>(CellState.class);
        stateToBackground.put(CellState.FULL, R.drawable.field_button_full);
        stateToBackground.put(CellState.BLANK, R.drawable.field_button_blank);
        stateToBackground.put(CellState.EMPTY, R.drawable.field_button_empty);
    }

    private LinearLayout.LayoutParams cellLayoutParams;
    private Button[][] cells;
    private HashMap<Button, Point> cellToPointMap;
    private int waitingColor;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        resources = getResources();
        waitingColor = resources.getColor(R.color.waiting_color, getTheme());

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
        LinearLayout fieldLayout = findViewById(R.id.field_layout);
        int height = GameContext.context.getLeftNumbers().getSize();
        int width = GameContext.context.getTopNumbers().getSize();

        int groupBy = 5;
        int verticalGroups = (int) Math.ceil(1.0 * height / groupBy);
        int horizontalGroups = (int) Math.ceil(1.0 * width / groupBy);

        cells = new Button[height][width];
        cellToPointMap = new HashMap<>(height * width);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        ButtonLongClickListener buttonLongClickListener = new ButtonLongClickListener();

        for (int groupI = 0; groupI < verticalGroups; groupI++) {
            int groupHeight = Math.min((groupI + 1) * groupBy, height) - groupI * groupBy;
            LinearLayout rowOfGroups = new LinearLayout(this);
            rowOfGroups.setOrientation(HORIZONTAL);
            for (int groupJ = 0; groupJ < horizontalGroups; groupJ++) {
                int groupWidth = Math.min((groupJ + 1) * groupBy, width) - groupJ * groupBy;
                LinearLayout group = new LinearLayout(this);
                group.setOrientation(LinearLayout.VERTICAL);
                group.setForeground(resources.getDrawable(R.drawable.field_group, getTheme()));
                for (int i = 0; i < groupHeight; i++) {
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(HORIZONTAL);
                    for (int j = 0; j < groupWidth; j++) {
                        int cellI = groupI * groupBy + i;
                        int cellJ = groupJ * groupBy + j;

                        Button button = new Button(this);
                        CellState state = GameContext.context.getField().getCellState(cellI, cellJ);
                        Integer background = stateToBackground.get(state);
                        button.setBackgroundResource(background);
                        button.setOnClickListener(buttonClickListener);
                        button.setOnLongClickListener(buttonLongClickListener);

                        cells[cellI][cellJ] = button;
                        cellToPointMap.put(button, new Point(cellJ, cellI));
                        row.addView(button, cellLayoutParams);
                    }
                    group.addView(row, cellLayoutParams);
                }
                rowOfGroups.addView(group, new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        groupWidth));
            }
            fieldLayout.addView(rowOfGroups, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    groupHeight));
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            @SuppressWarnings("SuspiciousMethodCalls") Point point = cellToPointMap.get(v);
            if (GameContext.guessedPicture.getCellState(point.y, point.x) == CellState.BLANK) {
                GameContext.guessedPicture.discoverRequest(point.y, point.x);
                v.setBackgroundColor(waitingColor);
            }
        }
    }

    private class ButtonLongClickListener implements View.OnLongClickListener {
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean onLongClick(final View v) {
            @SuppressWarnings("SuspiciousMethodCalls") final Point point = cellToPointMap.get(v);
            CellState state = GameContext.guessedPicture.toggleEmpty(point.y, point.x);
            v.setBackgroundResource(stateToBackground.get(state));
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
                        Integer background = answerToBackground.get(answer);
                        cells[i][j].setBackgroundResource(background);
                    }
                }
            });
        }
    }
}
