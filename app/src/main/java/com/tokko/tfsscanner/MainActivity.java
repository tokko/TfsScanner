package com.tokko.tfsscanner;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.zxing.Result;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler;

public class MainActivity extends AppCompatActivity implements ResultHandler, ChildEventListener, ValueEventListener {

    private static final String USER_PREFIX = "user: ";
    private static final String PBI_ID_PREFIX = "id: ";
    private ZXingScannerView scannerView;
    private String user;
    private int cameraId = 0;
    private Query q;

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
        scannerView.startCamera(cameraId);
        scannerView.setKeepScreenOn(true);
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://burning-fire-7618.firebaseio.com/");
        q = ref.child("/apk").limitToLast(1);
        q.addChildEventListener(this);
        q.addListenerForSingleValueEvent(this);
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
        if (id == R.id.swap_camera) {
            scannerView.stopCamera();
            cameraId = (cameraId + 1) % 2;
            scannerView.startCamera(cameraId);
            return true;
        }
        if(id == R.id.update){
            q.addListenerForSingleValueEvent(this);
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
        String id = result.getText().substring(PBI_ID_PREFIX.length());
        startService(new Intent(this, SendStatusReceiver.class).putExtra(SendStatusReceiver.EXTRA_USER, user).putExtra(SendStatusReceiver.EXTRA_PBI_ID, id));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerView.startCamera();
            }
        }, 500);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            HashMap<String, Object> o = (HashMap<String, Object>) dataSnapshot.getValue();
            HashMap<String, String> h = (HashMap<String, String>) o.get("apk");
            new Installer(getApplicationContext()).execute(h);
        }
        catch (Exception ignored){}
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        onChildAdded(dataSnapshot, s);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        onChildAdded(dataSnapshot, null);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {    }

    private class Installer extends AsyncTask<HashMap<String, String>, Void, Uri>{

        private Context context;

        public Installer(Context context) {
            this.context = context;
        }

        @Override
        protected Uri doInBackground(HashMap<String, String>... params) {
            HashMap<String, String> d = params[0];
            if(d == null) return null;
            long releaseTime = Long.valueOf(d.get("timestamp"));
            long thisReleasetime = context.getSharedPreferences("downloader", MODE_PRIVATE).getLong("thisReleaseTime", 0);
            if(releaseTime < thisReleasetime) return null;
            String encodedString = d.get("data");
            byte[] data = Base64.decode(encodedString, Base64.DEFAULT);
            File file = context.getExternalFilesDir(null);
            if(file == null) return null;
            File actualFile = new File(file, d.get("filename"));
            if(actualFile.exists()) actualFile.delete();
            try {
                if(!actualFile.createNewFile()) return null;
                BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(actualFile));
                fw.write(data);
                fw.flush();
                fw.close();
                return Uri.fromFile(actualFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if(uri != null){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                context.getSharedPreferences("downloader", MODE_PRIVATE).edit().putLong("thisReleaseTime", System.currentTimeMillis()).apply();
            }
        }
    }
}
