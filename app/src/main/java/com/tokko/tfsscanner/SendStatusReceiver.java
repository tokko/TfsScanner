package com.tokko.tfsscanner;

import android.app.IntentService;
import android.content.Intent;

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
        post.put(EXTRA_USER, intent.getStringExtra(EXTRA_USER));
        post.put(EXTRA_PBI_ID, intent.getStringExtra(EXTRA_PBI_ID));

        posts.push().setValue(new Post(intent.getStringExtra(EXTRA_USER), intent.getStringExtra(EXTRA_PBI_ID)));
    }

    public class Post{
        String User;
        String Id;

        public Post(String user, String id) {
            User = user;
            Id = id;
        }

        public String getUser() {
            return User;
        }

        public void setUser(String user) {
            User = user;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }
    }
}
