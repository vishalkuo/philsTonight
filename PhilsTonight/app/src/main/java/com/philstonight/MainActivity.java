package com.philstonight;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.philstonight.Models.SquadMember;
import com.philstonight.Util.SharedPrefsUtils;
import com.philstonight.Util.UIUtils;
import com.philstonight.ViewAdapters.SquadAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_SELECT_CONTACT = 1;
    private Button philsButton;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private IntentFilter intentFilter;
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private SmsManager smsMgr;
    private ArrayList<SquadMember> squadList = new ArrayList<>();
    private Button contactButton;
    private SquadAdapter squadAdapter;
    private Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        philsButton = (Button)findViewById(R.id.philsButton);

        intentFilter = new IntentFilter(SENT);
        intentFilter.addAction(DELIVERED);
        smsMgr = SmsManager.getDefault();
        final ListView squadListView = (ListView)findViewById(R.id.squad_list);

        SquadMember squadMember = new SquadMember("Vishal", "6473821508");
        SharedPrefsUtils.saveToSharedPrefs(squadMember, c);

        SharedPrefsUtils.loadSharedPrefs(this, squadList);



        squadAdapter = new SquadAdapter(squadList, c);
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

        contactButton = (Button)findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFromContacts(view);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFromContacts(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String contactName;
            String contactId;
            int hasPhoneNum;
            String phoneNumber = "";
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                hasPhoneNum = Integer.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNum != 0) {
                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

                    while (pCur.moveToNext())
                    {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phone;
                    }

                    pCur.close();

                    SquadMember squadMember = new SquadMember(contactName, phoneNumber);
                    squadList.add(squadMember);
                    squadAdapter.appendToSquad(squadMember);
                    SharedPrefsUtils.saveToSharedPrefs(squadMember, c);
                }
            }
            cursor.close();
        }
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
                        UIUtils.toastShort("SMS sent to " + name + " & " + number, c);
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        UIUtils.toastShort("Generic failure", c);
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        UIUtils.toastShort("No service", c);
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        UIUtils.toastShort("Null PDU", c);
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        UIUtils.toastShort("Radio off", c);
                        break;
                }
            }
            else if (DELIVERED.equals(intent.getAction()))
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        UIUtils.toastShort("SMS delivered", c);
                        break;

                    case Activity.RESULT_CANCELED:
                        UIUtils.toastShort("SMS not delivered", c);
                        break;
                }
            }
        }
    };
}
