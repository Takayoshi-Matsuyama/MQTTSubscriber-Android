package tech.tkys.mqttsubscriber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    EditText brokerIpEditText;

    EditText brokerPortEditText;

    EditText topicEditText;

    TextView outputTextView;

    String outputText = "";

    boolean subscribed;

    public void appendOutputText(String text) {
        outputText = String.format("%s%n%s", outputText, text);
        outputTextView.setText(outputText);
        Log.w("MQTT", outputText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        brokerIpEditText = findViewById(R.id.brokerIpEditText);
        brokerPortEditText = findViewById(R.id.brokerPortEditText);
        topicEditText = findViewById(R.id.topicEditText);
        outputTextView = findViewById(R.id.outputTextView);

        // Subscribe button
        findViewById(R.id.subscribeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverURI = brokerIpEditText.getText().toString();
                String topic = topicEditText.getText().toString();
                mqttHelper.subscribe(serverURI, topic);
            }
        });

        // Unsubscribe button
        findViewById(R.id.unsubscribeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // background
        findViewById(R.id.backgroundLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mqttHelper = new MqttHelper(this);
    }
}
