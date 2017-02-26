package dnd.group4.smart.dnd;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by rishi_lenovo on 5/10/2016.
 */
public class DNDService extends Service implements SensorEventListener {

    private SensorManager dndManager;
    private TelephonyManager telManager;
    private AudioManager audioManager;
    private SensorEventListener listener;
    private Sensor proximitySensor;
    private int startState;
    private int incomingCall;
    private String silentValue = "false";


    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            if(incomingCall == 1 && silentValue.equals("valid")){
                silentValue="true";
            }
            if(incomingCall == 1 && silentValue.equals("true")){
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            if(incomingCall == 1 && silentValue.equals("valid")){
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            startState = 1;
        } else {
            startState = 0;
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        dndManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        proximitySensor = dndManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        telManager.listen(new CallListener(), PhoneStateListener.LISTEN_CALL_STATE);
        dndManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onDestroy() {
        dndManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class CallListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if(state == TelephonyManager.CALL_STATE_IDLE){
                incomingCall=1;
                if(silentValue.equals("true"))
                    silentValue="false";
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }else if(state == TelephonyManager.CALL_STATE_RINGING){
                incomingCall=1;
                switch (startState){
                    case 0:
                        silentValue = "true";
                        break;
                    case 1:
                        silentValue = "valid";
                    default:
                        break;
                }
            }
        }
    }
}
