package com.tokko.tfsscanner;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class SendStatusReceiver extends IntentService {
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PBI_ID = "id";

    public SendStatusReceiver() {
        super("SendStatusReceiver");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://burning-fire-7618.firebaseio.com/");
        Firebase posts = ref.child("posts");
        Map<String, String> post = new HashMap<>();
        post.put(intent.getStringExtra(EXTRA_USER), intent.getStringExtra(EXTRA_PBI_ID));
        posts.push().setValue(post);
        Toast.makeText(getApplicationContext(), String.format("User: %s, Id: %s", intent.getStringExtra(EXTRA_USER), intent.getStringExtra(EXTRA_PBI_ID)), Toast.LENGTH_SHORT).show();

    }
}
