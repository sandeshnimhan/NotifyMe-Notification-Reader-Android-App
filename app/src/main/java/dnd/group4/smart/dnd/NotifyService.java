package dnd.group4.smart.dnd;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.Locale;

/**
 * Created by rishi_lenovo on 5/10/2016.
 */
public class NotifyService extends Service implements SensorEventListener {

    TextToSpeech notifyTextToSpeech;

    private SensorManager notifyManager;
    private Sensor notifyProximity;
    private Sensor notifyGravity;
    private boolean isGravityOn;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize all sensors
        notifyManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        notifyProximity = notifyManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        notifyTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    notifyTextToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        notifyGravity = notifyManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        notifyManager.registerListener(this, notifyProximity, SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int trackSensor = event.sensor.getType();
        switch (trackSensor) {
            case Sensor.TYPE_PROXIMITY:
                float location = event.values[0];
                if (isGravityOn == false && location <= 1.0f) {
                    notifyManager.registerListener(this, notifyGravity, SensorManager.SENSOR_DELAY_FASTEST);
                    isGravityOn = true;
                } else {
                    isGravityOn = false;
                }
                break;
            case Sensor.TYPE_GRAVITY:
                if (isGravityOn == true) {
                    isGravityOn = false;
                    if (event.values[2] > 9.0f) {
                        handGesture();
                    }
                }
                notifyManager.unregisterListener(this, notifyGravity);
        }
    }

    public int checkCalls(Context context) {
        int countCalls = 0;
        String[] project = {CallLog.Calls.TYPE};
        String select = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + "=1";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, project, select, null, null);
        if(null!=c){
            countCalls = c.getCount();
        }
        c.close();
        return countCalls;
    }

    public int checkMessage(Context context) {
        int countMessages = 0;
        Uri messageUri = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(messageUri, null, "read = 0", null, null);
        if(null != c){
            countMessages = c.getCount();
        }
        c.close();
        return countMessages;
    }

    @Override
    public void onDestroy() {
        notifyManager.unregisterListener(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void handGesture(){
        notifyTextToSpeech.speak("You have " + String.valueOf(checkMessage(this))  + "unread messages and " + String.valueOf(checkCalls(this)) + "  missed calls", TextToSpeech.QUEUE_FLUSH, null);
    }

}

