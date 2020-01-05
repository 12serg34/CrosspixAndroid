package com.home.crosspixandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import message.response.GameCreatedResponse;
import message.response.GamesInfoResponse;
import message.response.PongResponse;
import picture.MultiPlayerGuessedPicture;
import picture.StashedPicture;

public class MainActivity extends AppCompatActivity {
    private MessageSender sender;
    private RecyclerView.Adapter recyclerViewAdapter;
    private List<String> gameNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameNames = new ArrayList<>();

        recyclerViewAdapter = new MyRecycleViewAdapter(gameNames);
        recyclerView.setAdapter(recyclerViewAdapter);

        final Button connectButton = findViewById(R.id.connectButton);
        final Notifier notifier = new Notifier();
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

        final TextView gameCreatedTextView = findViewById(R.id.gameCreatedTextView);
        notifier.subscribe(GameCreatedResponse.class, new MessageListener<GameCreatedResponse>() {
            @Override
            public void accept(final GameCreatedResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StashedPicture stashedPicture = response.getStashedPicture();
                        GameContext.stashedPicture = stashedPicture;
                        GameContext.guessedPicture = new MultiPlayerGuessedPicture(stashedPicture,
                                sender, notifier);
                        gameCreatedTextView.setText("game created " + stashedPicture);
                    }
                });
                sender.send(GamesInfoRequest.getInstance());
            }
        });
        notifier.subscribe(GamesInfoResponse.class, new MessageListener<GamesInfoResponse>() {
            @Override
            public void accept(final GamesInfoResponse response) {
                gameNames.clear();
                for (GameInfo info : response.getGamesInfo()) {
                    gameNames.add(info.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sender = MessageService.connect("crosspix.hopto.org", 14500, notifier);
                sender = MessageService.connect("192.168.43.7", 14500, notifier);
            }
        });

        final Button createGameButton = findViewById(R.id.createGameButton);
        final EditText gameNameEditText = findViewById(R.id.gameNameEditText);
        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender.send(new CreateGameRequest(gameNameEditText.getText().toString()));
            }
        });
    }

    public void onStartButtonClick(View view) {
        startActivity(new Intent(this, GameFieldActivity.class));
    }
}
