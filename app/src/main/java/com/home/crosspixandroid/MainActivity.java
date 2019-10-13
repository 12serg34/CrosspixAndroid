package com.home.crosspixandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import message.MessageListener;
import message.MessageSender;
import message.MessageService;
import message.Notifier;
import message.request.CreateGameRequest;
import message.response.GameCreatedResponse;
import message.response.PongResponse;
import picture.StashedPicture;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private MessageSender sender;
    private StashedPicture stashedPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button connectButton = findViewById(R.id.connectButton);
        final Notifier notifier = new Notifier();
        notifier.subscribe(PongResponse.class, new MessageListener<PongResponse>() {
            @Override
            public void accept(PongResponse pong) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectButton.setBackgroundColor(Color.GREEN);
//                        sender.send(GamesInfoRequest.getInstance());
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
                        stashedPicture = response.getStashedPicture();
                        GameContext.stashedPicture = stashedPicture;
                        gameCreatedTextView.setText("game created " + stashedPicture);
                    }
                });
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender = MessageService.connect("crosspix.hopto.org", 14500, notifier);
                System.out.println("clicked");
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

    public void sendMessage(View view) {
        Intent intent = new Intent(this, GameFieldActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
