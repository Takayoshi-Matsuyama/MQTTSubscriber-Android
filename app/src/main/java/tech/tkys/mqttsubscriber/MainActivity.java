package tech.tkys.mqttsubscriber;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        findViewById(R.id.testPublishButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brokerURI = "tcp://192.168.0.3:1883";
                String topic = "MQTTTest";
                int qos = 2;    // Quality of Service (2: Exactly once delivery)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                String messageString = String.format("Hello from MQTT Publisher (Xperia): %s", sdf.format(new Date()));

                publishToBroker(applicationContext, brokerURI, topic, messageString, qos);
            }
        });
    }

    private static void publishToBroker(android.content.Context context, String brokerURI, String topic, String messageString, int qos){

        try {
            // Create a client to publish.
            MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, brokerURI, MqttClient.generateClientId());

            // Connect to the MQTT server.
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttAndroidClient.connect(mqttConnectOptions);

            // Publish the message.
            MqttMessage message = new MqttMessage(messageString.getBytes());
            message.setQos(qos);
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
