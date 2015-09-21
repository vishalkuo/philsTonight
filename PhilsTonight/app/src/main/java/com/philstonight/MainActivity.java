package com.philstonight;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.philstonight.Models.SquadMember;
import com.philstonight.Util.SMSUtils;
import com.philstonight.Util.SharedPrefsUtils;
import com.philstonight.Util.UIUtils;
import com.philstonight.ViewAdapters.SquadAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_CONTACT = 1;
    
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private Button philsButton;
    private IntentFilter intentFilter;
    private SmsManager smsMgr;
    private ArrayList<SquadMember> squadList = new ArrayList<>();
    private Button contactButton;
    private SquadAdapter squadAdapter;
    private Context c = this;
    private Spinner placeSpinner;
    private ListView squadListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Load from prefs
         */
        SharedPrefsUtils.loadSharedPrefs(this, squadList);

        /**
         * Find assets
         */
        philsButton = (Button)findViewById(R.id.philsButton);
        placeSpinner = (Spinner)findViewById(R.id.spinner);
        squadListView = (ListView)findViewById(R.id.squad_list);
        contactButton = (Button)findViewById(R.id.contact_button);

        /**
         * List view
         */
        squadAdapter = new SquadAdapter(squadList, c);
        squadListView.setAdapter(squadAdapter);

        /**
         * SMS Data
         */
        intentFilter = new IntentFilter(Globals.SENT);
        intentFilter.addAction(Globals.DELIVERED);
        smsMgr = SmsManager.getDefault();

        philsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < squadList.size(); i++) {
                    SquadMember squadMember = squadList.get(i);
                    sendText(squadMember.getNumber(), squadMember.getName(), i);
                }
                UIUtils.toastShort("Squad Alerted", c);

            }
        });

        /**
         * Load from contacts
         */
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
            }else{
                UIUtils.toastShort("Contact has no number", c);
            }
            cursor.close();
        }
    }




    public void sendText(String conNumber, String conName, int requestCode)
    {
        Intent sentIntent = new Intent(Globals.SENT);
        Intent deliveredIntent = new Intent(Globals.DELIVERED);
        sentIntent.putExtra(EXTRA_NUMBER, conNumber);
        sentIntent.putExtra(EXTRA_NAME, conName);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, requestCode, sentIntent, 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, requestCode, deliveredIntent, 0);
        smsMgr.sendTextMessage(conNumber, null, Globals.philsTonight, sentPI, deliveredPI);
    }

    private BroadcastReceiver receiver = SMSUtils.generateBroadcastReceiver();


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

    public void deleteUser(int position){
        squadList.remove(position);
    }
}
