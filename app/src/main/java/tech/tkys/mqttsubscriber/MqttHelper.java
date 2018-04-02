package tech.tkys.mqttsubscriber;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {
    private MainActivity mainActivity;

    private MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://192.168.0.3:1883";

    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "MQTTTest";

//    final String username = "xxxxxxx";
//    final String password = "yyyyyyyyyy";

    public MqttHelper(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        Context context = this.mainActivity.getApplicationContext();
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
//        mqttConnectOptions.setUserName(username);
//        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    mainActivity.appendOutputText(String.format("Connected to broker: %s", serverUri));

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mainActivity.appendOutputText(String.format("Failed to connect to: %s%n%s", serverUri, exception.toString()));
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mainActivity.appendOutputText(String.format("Subscribed to topic: %s", subscriptionTopic));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mainActivity.appendOutputText(String.format("Failed to subscribe to topic: %s", subscriptionTopic));
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
}
