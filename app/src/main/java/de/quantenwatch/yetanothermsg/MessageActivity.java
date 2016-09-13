package de.quantenwatch.yetanothermsg;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MessageActivity extends Activity {
    private Button sendButton;
    private EditText editToken;
    private EditText editMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        sendButton = (Button) findViewById(R.id.btnSend);
        editToken = (EditText) findViewById(R.id.editToken);
        editMsg = (EditText) findViewById(R.id.editMsg);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendMessage().execute(
                        editToken.getText().toString(),
                        editMsg.getText().toString()
                );
            }
        });
    }

    private class SendMessage extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String to = strings[0];
            String msg = strings[1];

            String charset = java.nio.charset.StandardCharsets.UTF_8.name();
            String query;
            try {
                query = String.format("to=%s&msg=%s",
                        URLEncoder.encode(to, charset),
                        URLEncoder.encode(msg, charset)
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            URLConnection connection = null;
            try {
                connection = new URL(MainActivity.APPSERVER_SENDMESSAGE).openConnection();
                connection.setDoOutput(true); // Triggers POST.
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                OutputStream output = connection.getOutputStream();
                output.write(query.getBytes(charset));
                InputStream response = connection.getInputStream();
                Log.d("Response", Integer.toString(response.read()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
