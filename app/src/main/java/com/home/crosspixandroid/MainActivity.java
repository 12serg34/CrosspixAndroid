package com.home.crosspixandroid;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import entities.GameInfo;
import message.MessageListener;
import message.MessageSender;
import message.MessageService;
import message.Notifier;
import message.request.CreateGameRequest;
import message.request.GamesInfoRequest;
import message.request.JoinToGameRequest;
import message.response.GameCreatedResponse;
import message.response.GamesInfoResponse;
import message.response.JoinedToGameResponse;
import message.response.PongResponse;
import pictures.GuessedPicture;

public class MainActivity extends AppCompatActivity {
    private Notifier notifier;
    private MessageSender sender;
    private List<GameInfo> gamesInfo;

    private MyRecycleViewAdapter recyclerViewAdapter;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();

        final RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gamesInfo = new ArrayList<>();

        recyclerViewAdapter = new MyRecycleViewAdapter(gamesInfo);
        recyclerView.setAdapter(recyclerViewAdapter);

        int fieldInitialSize = resources.getInteger(R.integer.field_initial_size);
        SeekBar seekBar = findViewById(R.id.fieldSizeSeekBar);
        seekBar.setMax(resources.getInteger(R.integer.field_max_size) - resources.getInteger(R.integer.field_min_size));
        seekBar.setProgress(fieldInitialSize - resources.getInteger(R.integer.field_min_size));
        seekBar.setOnSeekBarChangeListener(this.new FieldSizeSeekBarListener());

        TextView fieldSizeTextView = findViewById(R.id.fieldSizeTextView);
        fieldSizeTextView.setText(String.valueOf(fieldInitialSize));

        notifier = new Notifier();
        final Button connectButton = findViewById(R.id.connectButton);
        notifier.subscribe(PongResponse.class, new MessageListener<PongResponse>() {
            @Override
            public void accept(PongResponse pong) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectButton.setBackgroundColor(Color.GREEN);
                        sender.send(GamesInfoRequest.getInstance());
                    }
                });
            }
        });
        notifier.subscribe(GameCreatedResponse.class, new MessageListener<GameCreatedResponse>() {
            @Override
            public void accept(final GameCreatedResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entities.GameContext context = response.getGameContext();
                        GameContext.guessedPicture = new GuessedPicture(context.getField(),
                                sender, notifier);
                        GameContext.context = context;
                        startGame();
                    }
                });
                sender.send(GamesInfoRequest.getInstance());
            }
        });
        notifier.subscribe(JoinedToGameResponse.class, new MessageListener<JoinedToGameResponse>() {
            @Override
            public void accept(final JoinedToGameResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entities.GameContext context = response.getGameContext();
                        GameContext.guessedPicture = new GuessedPicture(context.getField(),
                                sender, notifier);
                        GameContext.context = context;
                        startGame();
                    }
                });
                sender.send(GamesInfoRequest.getInstance());
            }
        });
        notifier.subscribe(GamesInfoResponse.class, new MessageListener<GamesInfoResponse>() {
            @Override
            public void accept(final GamesInfoResponse response) {
                gamesInfo.clear();
                gamesInfo.addAll(response.getGamesInfo());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void onConnectButtonClick(View view) {
        sender = MessageService.connect("crosspix.hopto.org", 14500, notifier);
    }

    public void onRefreshGameListButtonClick(View view) {
        sender.send(GamesInfoRequest.getInstance());
    }

    public void onJoinToGameButtonClick(View view) {
        int selectedPosition = recyclerViewAdapter.getSelectedPosition();
        int gameId = gamesInfo.get(selectedPosition).getId();
        sender.send(new JoinToGameRequest(gameId));
    }

    public void onCreateGameButtonClick(View view) {
        EditText gameNameEditText = findViewById(R.id.gameNameEditText);
        String gameName = gameNameEditText.getText().toString();
        SeekBar fieldSizeSeekBar = findViewById(R.id.fieldSizeSeekBar);
        int fieldSize = calcFieldSize(fieldSizeSeekBar.getProgress());
        sender.send(new CreateGameRequest(gameName, fieldSize, fieldSize));
    }

    public void startGame() {
        Intent intent = new Intent(this, GameFieldActivity.class);
        startActivity(intent);
    }

    class FieldSizeSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            TextView fieldSizeTextView = findViewById(R.id.fieldSizeTextView);
            String offsetSize = String.valueOf(calcFieldSize(i));
            fieldSizeTextView.setText(offsetSize);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    private int calcFieldSize(int progress) {
        return progress + resources.getInteger(R.integer.field_min_size);
    }
}
