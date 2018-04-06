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

/**
 * Performs MQTT transactions.
 */
public class MqttHelper {

    private MainActivity mainActivity;
    private MqttAndroidClient mqttAndroidClient;
    private final String clientId = "ExampleAndroidClient";

    /**
     * Initializes an instance of  MqttHelper.
     * @param mainActivity The main GUI's activity.
     * @throws NullPointerException when {@code mainActivity == null}
     */
    public MqttHelper(MainActivity mainActivity) throws NullPointerException {
        if (mainActivity == null) {
            throw new NullPointerException("mainActivity");
        }

        this.mainActivity = mainActivity;
    }

    /**
     * Subscribes to the specified MQTT topic.
     * @param serverURI The MQTT server to connect.
     * @param topic     The topic to subscribe.
     * @throws NullPointerException when {@code serverURI == null || topic == null}
     */
    public void subscribe(String serverURI, String topic){
        if (serverURI == null) {
            throw new NullPointerException("serverURI");
        }

        if (topic == null) {
            throw new NullPointerException("topic");
        }

        MqttSubscriptionInfo subscriptionInfo = new MqttSubscriptionInfo(serverURI, topic);

        Context context = this.mainActivity.getApplicationContext();
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, clientId);
        mqttAndroidClient.setCallback(new MyMqttCallbackExtended(mainActivity));

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            // Try to connect.
            mqttAndroidClient.connect(mqttConnectOptions, subscriptionInfo, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    // Connected --> Continue transaction to subscribe to the topic.
                    MqttSubscriptionInfo subscriptionInfo = (MqttSubscriptionInfo)asyncActionToken.getUserContext();
                    MqttHelper.this.subscribeToTopic(subscriptionInfo.getTopic());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MqttSubscriptionInfo subscriptionInfo = (MqttSubscriptionInfo)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Failed to subscribe to: %s%n%s", subscriptionInfo.getServerURI(), exception.toString()));
                }
            });

        } catch (MqttException e){
            mainActivity.appendOutputText(e.toString());
        }
    }

    /**
     * Unsubscribes from the specified MQTT topic.
     * @param topic
     * @throws NullPointerException when {@code topic == null}
     */
    public void unsubscribe(String topic) throws NullPointerException {
        if (topic == null) {
            throw new NullPointerException("topic");
        }

        try {
            // Try to unsubscribe.
            mqttAndroidClient.unsubscribe(topic, topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Unsubscribed from topic: %s", topic));
                    mainActivity.toggleSubscriptionButtonState(false);

                    // Unsubscribed --> Continue transaction to disconnect.
                    MqttHelper.this.disconnect();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mainActivity.appendOutputText(String.format("Failed to unsubscribe: %s", exception.toString()));
                }
            });
        } catch (MqttException e) {
            mainActivity.appendOutputText(e.toString());
        }
    }

    /**
     * Subscribes to the specified MQTT topic.
     * @param topic - The topic to subscribe.
     * @throws NullPointerException when {@code topic == null}
     */
    private void subscribeToTopic(String topic) throws NullPointerException {
        if (topic == null) {
            throw new NullPointerException("topic");
        }

        try {
            // Try to subscribe.
            mqttAndroidClient.subscribe(topic, 0, topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Subscribed to topic: %s", topic));

                    mainActivity.toggleSubscriptionButtonState(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    String topic = (String)asyncActionToken.getUserContext();
                    mainActivity.appendOutputText(String.format("Failed to subscribe to topic: %s", topic));
                }
            });
        } catch (MqttException e) {
            mainActivity.appendOutputText(e.toString());
        }
    }

    /**
     * Disconnects from the current MQTT server.
     */
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
            mainActivity.appendOutputText(e.toString());
        }
    }

    /**
     * f MqttCallback to allow new callbacks without breaking the API for existing applications.
     */
    private class MyMqttCallbackExtended implements MqttCallbackExtended {

        private MainActivity mainActivity;

        /**
         * Initializes an instance of  MyMqttCallbackExtended.
         * @param mainActivity The main GUI's activity.
         * @throws NullPointerException when {@code mainActivity == null}
         */
        public MyMqttCallbackExtended(MainActivity mainActivity) throws NullPointerException {
            if (mainActivity == null) {
                throw new NullPointerException("mainActivity");
            }

            this.mainActivity = mainActivity;
        }

        /**
         * Called when the connection to the server is completed successfully.
         * @param reconnect - If true, the connection was the result of automatic reconnect.
         * @param serverURI - The server URI that the connection was made to.
         */
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            mainActivity.appendOutputText(String.format("Connected: %s", serverURI));
        }

        /**
         * This method is called when the connection to the server is lost.
         * @param cause - the reason behind the loss of connection.
         */
        @Override
        public void connectionLost(Throwable cause) {
            mainActivity.appendOutputText("Connection lost.");
        }

        /**
         * This method is called when a message arrives from the server.
         *
         * This method is invoked synchronously by the MQTT client.
         * An acknowledgment is not sent back to the server until this method returns cleanly.
         *
         * If an implementation of this method throws an Exception, then the client will be shut down.
         * When the client is next re-connected, any QoS 1 or 2 messages will be redelivered by the server.
         *
         * Any additional messages which arrive while an implementation of this method is running,
         * will build up in memory, and will then back up on the network.
         *
         * If an application needs to persist data, then it should ensure the data is persisted prior to returning from this method,
         * as after returning from this method, the message is considered to have been delivered, and will not be reproducible.
         *
         * It is possible to send a new message within an implementation of this callback (for example, a response to this message),
         * but the implementation must not disconnect the client,
         * as it will be impossible to send an acknowledgment for the message being processed, and a deadlock will occur.
         *
         * @param topic      - name of the topic on the message was published to
         * @param message    - the actual message.
         * @throws Exception - if a terminal error has occurred, and the client should be shut down.
         */
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            mainActivity.appendOutputText(String.format("[%s] %s", topic, message.toString()));
        }

        /**
         * Called when delivery for a message has been completed, and all acknowledgments have been received.
         * For QoS 0 messages it is called once the message has been handed to the network for delivery.
         * For QoS 1 it is called when PUBACK is received and for QoS 2 when PUBCOMP is received.
         * The token will be the same token as that returned when the message was published.
         * @param token - the delivery token associated with the message.
         */
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            mainActivity.appendOutputText("Delivery Completed.");
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
        public MqttSubscriptionInfo(String serverURI, String topic) throws NullPointerException {
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
         * Gets the MQTT server URI.
         * @return MQTT server URI
         */
        public String getServerURI() {
            return this.serverURI;
        }

        /**
         * Gets the topic to subscribe.
         * @return topic to subscribe
         */
        public String getTopic() {
            return this.topic;
        }
    }
}
