package com.tokko.tfsscanner;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler;

public class MainActivity extends AppCompatActivity implements ResultHandler{

    private static final String USER_PREFIX = "user: ";
    private static final String PBI_ID_PREFIX = "id: ";
    private ZXingScannerView scannerView;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result result) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception ignored) {}
        if(result.getText().startsWith(USER_PREFIX)){
            user = result.getText().substring(USER_PREFIX.length());
        }
        else{
            String id = result.getText().substring(PBI_ID_PREFIX.length());
            startService(new Intent(this, SendStatusReceiver.class).putExtra(SendStatusReceiver.EXTRA_USER, user).putExtra(SendStatusReceiver.EXTRA_PBI_ID, id));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerView.startCamera();
            }
        }, 500);
    }

}
