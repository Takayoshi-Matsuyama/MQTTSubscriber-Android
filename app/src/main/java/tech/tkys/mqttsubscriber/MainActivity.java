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

/**
 * Activity class for the main gui.
 */
public final class MainActivity extends AppCompatActivity {

    private MqttHelper mqttHelper;
    private Button subscribeButton;
    private Button unsubscribeButton;
    private EditText serverIpEditText;
    private EditText serverPortEditText;
    private EditText topicEditText;
    private TextView outputTextView;
    private ConstraintLayout backgroundLayout;
    private String outputText = "";

    /**
     * Toggles 'Subscribe' and 'Unsubscribe' buttons' state based on MQTT subscription status.
     * @param isSubscriptionActive true if the subscription is active.
     */
    public void toggleSubscriptionButtonState(boolean isSubscriptionActive) {
        if (isSubscriptionActive) {
            subscribeButton.setEnabled(false);
            unsubscribeButton.setEnabled(true);
        } else {
            subscribeButton.setEnabled(true);
            unsubscribeButton.setEnabled(false);
        }
    }

    /**
     * Appends text to the output message area.
     * @param text The text to be appended.
     */
    public void appendOutputText(String text) {
        if (text == null) {
            return;
        }

        outputText = String.format("%s%n%s", outputText, text);
        outputTextView.setText(outputText);
        Log.w("MQTT", outputText);
    }

    /**
     * Performs initialization of all fragments.
     * @param savedInstanceState A mapping from String keys to various Parcelable values.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        subscribeButton = findViewById(R.id.subscribeButton);
        unsubscribeButton = findViewById(R.id.unsubscribeButton);
        serverIpEditText = findViewById(R.id.serverIpEditText);
        serverPortEditText = findViewById(R.id.serverPortEditText);
        topicEditText = findViewById(R.id.topicEditText);
        outputTextView = findViewById(R.id.outputTextView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

        this.toggleSubscriptionButtonState(false);

        // Activate MQTT Helper
        mqttHelper = new MqttHelper(this);

        // 'Subscribe' button click handler
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverIpAddress = serverIpEditText.getText().toString();
                String serverPort = serverPortEditText.getText().toString();
                String serverURI = String.format("tcp://%s:%s", serverIpAddress, serverPort);
                String topic = topicEditText.getText().toString();

                mqttHelper.subscribe(serverURI, topic);
            }
        });

        // 'Unsubscribe' button click handler
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = topicEditText.getText().toString();

                mqttHelper.unsubscribe(topic);
            }
        });

        // Background click handler
        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close software keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }
}
