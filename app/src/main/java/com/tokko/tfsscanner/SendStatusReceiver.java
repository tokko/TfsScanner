package com.tokko.tfsscanner;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class SendStatusReceiver extends IntentService {
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_PBI_ID = "id";

    public SendStatusReceiver() {
        super("SendStatusReceiver");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), String.format("User: %s, Id: %s", intent.getStringExtra(EXTRA_USER), intent.getStringExtra(EXTRA_PBI_ID)), Toast.LENGTH_SHORT).show();

    }
}
