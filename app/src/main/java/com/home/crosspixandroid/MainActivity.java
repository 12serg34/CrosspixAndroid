package com.home.crosspixandroid;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import message.MessageListener;
import message.MessageSender;
import message.MessageService;
import message.Notifier;
import message.response.PongResponse;

public class MainActivity extends AppCompatActivity {

    private MessageSender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("hello");

        final View button = findViewById(R.id.button);
        final Notifier notifier = new Notifier();
        notifier.subscribe(PongResponse.class, new MessageListener<PongResponse>() {
            @Override
            public void accept(PongResponse pong) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setBackgroundColor(Color.GREEN);
                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sender = MessageService.connect("crosspix.hopto.org", 14500, notifier);
                System.out.println("clicked");
            }
        });
    }
}
