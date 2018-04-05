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

    final String clientId = "ExampleAndroidClient";

    public MqttHelper(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void subscribe(String serverURI, String topic){
        MqttSubscriptionInfo subscriptionInfo = new MqttSubscriptionInfo(serverURI, topic);

        Context context = this.mainActivity.getApplicationContext();
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                // Do nothing (MqttHelper handles this.)
            }

            @Override
            public void connectionLost(Throwable throwable) {
                mainActivity.appendOutputText("Connection lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                mainActivity.appendOutputText(String.format("[%s] %s", topic, mqttMessage.toString()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                mainActivity.appendOutputText("Delivery Completed.");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mainActivity.appendOutputText("Connecting");
            mqttAndroidClient.connect(mqttConnectOptions, subscriptionInfo, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mainActivity.appendOutputText("Connect OK");
                    MqttSubscriptionInfo subscriptionInfo = (MqttSubscriptionInfo)asyncActionToken.getUserContext();

                    mainActivity.appendOutputText(String.format("Connected to broker: %s", subscriptionInfo.getServerURI()));

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    subscribeToTopic(subscriptionInfo.getTopic());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MqttSubscriptionInfo subscriptionInfo = (MqttSubscriptionInfo)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Failed to subscribe to: %s%n%s", subscriptionInfo.getServerURI(), exception.toString()));
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void unsubscribe(String topic) {
        try {
            mqttAndroidClient.unsubscribe(topic, topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Unsubscribed from topic: %s", topic));
                    mainActivity.onSubscriptionStatusChanged(false);

                    disconnect();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mainActivity.appendOutputText(String.format("Failed to unsubscribe: %s", exception.toString()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Subscribed to topic: %s", topic));

                    mainActivity.onSubscriptionStatusChanged(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Failed to subscribe to topic: %s", topic));
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mainActivity.appendOutputText(String.format("Disconnected."));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mainActivity.appendOutputText(String.format("Failed to disconnect: %s", exception.toString()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Represents information for MQTT subscription.
     */
    private class MqttSubscriptionInfo {
        private String serverURI;
        private String topic;

        /**
         * Constructs an instance of MqttSubscriptionInfo.
         * @param serverURI MQTT server URI
         * @param topic     Topic to subscribe
         * @throws NullPointerException when {@code serverURI == null || topic == null}
         */
        public MqttSubscriptionInfo(String serverURI, String topic) {
            if (serverURI == null) {
                throw new NullPointerException("serverURI");
            }

            if (topic == null) {
                throw new NullPointerException("topic");
            }

            this.serverURI = serverURI;
            this.topic = topic;
        }

        /**
         * Gets the MQTT server URI
         * @return MQTT server URI
         */
        public String getServerURI() {
            return this.serverURI;
        }

        /**
         * Gets the topic to subscribe
         * @return topic to subscribe
         */
        public String getTopic() {
            return this.topic;
        }
    }
}
