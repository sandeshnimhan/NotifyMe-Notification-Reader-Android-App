package dnd.group4.smart.dnd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Intent dndIntent, notifyIntent;
    private Button btnDnd, btnNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDnd = (Button) findViewById(R.id.btnDnd);
        btnNotify = (Button) findViewById(R.id.btnNotify);
        btnDnd.setOnClickListener((View.OnClickListener) this);
        btnNotify.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        dndIntent = new Intent(this, DNDService.class);
        notifyIntent = new Intent(this, NotifyService.class);
        switch(v.getId()){
            case R.id.btnDnd:
                this.startService(dndIntent);
                this.stopService(notifyIntent);
                break;
            case R.id.btnNotify:
                this.startService(notifyIntent);
                this.stopService(dndIntent);
                break;
            default:
                break;
        }
    }
}
