package tech.tkys.mqttsubscriber;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    TextView outputTextView;

    String outputText = "";

    public void appendOutputText(String text) {
        outputText = String.format("%s%n%s", outputText, text);
        outputTextView.setText(outputText);
        Log.w("MQTT", outputText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        outputTextView = findViewById(R.id.outputTextView);

        final Context applicationContext = this.getApplicationContext();

        findViewById(R.id.backgroundLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.subscribeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.unsubscribeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.startMqtt();
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                // Do nothing (MqttHelper handles this.)
            }

            @Override
            public void connectionLost(Throwable throwable) {
                appendOutputText("Connection lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                appendOutputText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                appendOutputText("Delivery Completed.");
            }
        });

        mqttHelper.connect();
    }
}
