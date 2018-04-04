package tech.tkys.mqttsubscriber;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    Button subscribeButton;

    Button unsubscribeButton;

    EditText brokerIpEditText;

    EditText brokerPortEditText;

    EditText topicEditText;

    TextView outputTextView;

    ConstraintLayout backgroundLayout;

    String outputText = "";

    public void onSubscriptionStatusChanged(boolean isSubscribed) {
        if (isSubscribed) {
            subscribeButton.setEnabled(false);
            unsubscribeButton.setEnabled(true);
        } else {
            subscribeButton.setEnabled(true);
            unsubscribeButton.setEnabled(false);
        }
    }

    public void appendOutputText(String text) {
        outputText = String.format("%s%n%s", outputText, text);
        outputTextView.setText(outputText);
        Log.w("MQTT", outputText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        subscribeButton = findViewById(R.id.subscribeButton);
        unsubscribeButton = findViewById(R.id.unsubscribeButton);
        brokerIpEditText = findViewById(R.id.brokerIpEditText);
        brokerPortEditText = findViewById(R.id.brokerPortEditText);
        topicEditText = findViewById(R.id.topicEditText);
        outputTextView = findViewById(R.id.outputTextView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverIpAddress = brokerIpEditText.getText().toString();
                String serverPort = brokerPortEditText.getText().toString();
                String serverURI = String.format("tcp://%s:%s", serverIpAddress, serverPort);
                String topic = topicEditText.getText().toString();

                mqttHelper.subscribe(serverURI, topic);
            }
        });

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = topicEditText.getText().toString();

                mqttHelper.unsubscribe(topic);
            }
        });

        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close software keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mqttHelper = new MqttHelper(this);
        unsubscribeButton.setEnabled(false);
    }
}
