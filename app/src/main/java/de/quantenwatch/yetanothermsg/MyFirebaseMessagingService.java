package de.quantenwatch.yetanothermsg;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String msg = remoteMessage.getData().get("msg");

        Log.d("msg", msg);
        Intent intent = new Intent(MainActivity.GET_MSG_ACTION);
        intent.putExtra("msg", msg);
        intent.putExtra("from", remoteMessage.getFrom());
        sendBroadcast(intent);
    }
}
