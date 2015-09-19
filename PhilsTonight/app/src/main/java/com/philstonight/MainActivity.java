package com.philstonight;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.philstonight.Models.SquadMember;
import com.philstonight.ViewAdapters.SquadAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button philsButton;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private IntentFilter intentFilter;
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private SmsManager smsMgr;
    private ArrayList<SquadMember> squadList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        philsButton = (Button)findViewById(R.id.philsButton);

        intentFilter = new IntentFilter(SENT);
        intentFilter.addAction(DELIVERED);
        smsMgr = SmsManager.getDefault();
        final ListView squadListView = (ListView)findViewById(R.id.squad_list);


        squadList.add(new SquadMember("Vishal", "6473821508"));
        squadList.add(new SquadMember("Alex", "6136175398"));
        SquadAdapter squadAdapter = new SquadAdapter(squadList);
        squadListView.setAdapter(squadAdapter);

        squadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SquadMember squadMember = squadList.get(i);
                sendText(squadMember.getNumber(), squadMember.getName(), 0);
            }
        });

        philsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < squadList.size(); i++) {
                    SquadMember squadMember = squadList.get(i);
                    sendText(squadMember.getNumber(), squadMember.getName(), i);
                }

            }
        });
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    private void sendText(String conNumber, String conName, int requestCode)
    {
        Intent sentIntent = new Intent(SENT);
        Intent deliveredIntent = new Intent(DELIVERED);

        sentIntent.putExtra(EXTRA_NUMBER, conNumber);
        sentIntent.putExtra(EXTRA_NAME, conName);

        PendingIntent sentPI = PendingIntent.getBroadcast(this, requestCode, sentIntent, 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, requestCode, deliveredIntent, 0);

        smsMgr.sendTextMessage(conNumber, null, Globals.philsTonight, sentPI, deliveredPI);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (SENT.equals(intent.getAction()))
            {
                String name = intent.getStringExtra("name");
                String number = intent.getStringExtra("number");

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        toastShort("SMS sent to " + name + " & " + number);
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        toastShort("Generic failure");
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        toastShort("No service");
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        toastShort("Null PDU");
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        toastShort("Radio off");
                        break;
                }
            }
            else if (DELIVERED.equals(intent.getAction()))
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        toastShort("SMS delivered");
                        break;

                    case Activity.RESULT_CANCELED:
                        toastShort("SMS not delivered");
                        break;
                }
            }
        }
    };

    private void toastShort(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
