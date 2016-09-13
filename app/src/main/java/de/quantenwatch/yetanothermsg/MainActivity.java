package de.quantenwatch.yetanothermsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    public static final String GET_MSG_ACTION = "de.quantenwatch.yetanothermsg.in";
    public static final String APPSERVER_ADDTOKEN = "http://127.0.0.1:65000/addToken/";
    public static final String APPSERVER_SENDMESSAGE = "http://127.0.0.1:65000/sendMessage/";

    private MyBroadcastReceiver mReceiver;
    private LinearLayout ll;
    private SharedPreferences mSharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = mSharedPreferences.getString("preference_token", "");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        ll = (LinearLayout) findViewById(R.id.linearLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Ob das Senden geklappt hat?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver, new IntentFilter(GET_MSG_ACTION));

        if (token != "") registerToken(token);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_myToken) {
            Log.d("token", token);
            Intent intent=new Intent(getApplicationContext(), TokenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(GET_MSG_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    static public void registerToken(String freshToken) {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... strings) {
                String token = strings[0];

                String charset = java.nio.charset.StandardCharsets.UTF_8.name();
                String query;
                try {
                    query = String.format("id=%s", URLEncoder.encode(token, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
                URLConnection connection = null;
                try {
                    connection = new URL(MainActivity.APPSERVER_ADDTOKEN).openConnection();
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
        }.execute(freshToken);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            String from = intent.getStringExtra("from");
            TextView tv = new TextView(MainActivity.this);
            tv.setText(from + ": " + msg);
            ll.addView(tv);
            Log.d("broadcast", msg);
        }
    }
}
