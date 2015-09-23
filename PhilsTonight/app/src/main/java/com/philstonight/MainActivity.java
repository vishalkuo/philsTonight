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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.philstonight.Models.RestaurantSingleton;
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
    private TextView tonight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Load from prefs
         */
        loadInitialSettings();

        /**
         * Find assets
         */
        philsButton = (Button)findViewById(R.id.philsButton);
        placeSpinner = (Spinner)findViewById(R.id.spinner);
        tonight = (TextView)findViewById(R.id.tonight);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_adapter
                , RestaurantSingleton.getInstance());

        placeSpinner.setAdapter(adapter);
        squadListView = (ListView)findViewById(R.id.squad_list);
        contactButton = (Button)findViewById(R.id.contact_button);

        /**
         * List view
         */
        squadAdapter = new SquadAdapter(squadList, c);
        squadListView.setAdapter(squadAdapter);
        setTonightText();

        /**
         * SMS Data
         */
        intentFilter = new IntentFilter(Globals.SENT);
        intentFilter.addAction(Globals.DELIVERED);
        smsMgr = SmsManager.getDefault();

        philsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertSquad(null,"Squad Alerted");
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

        placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTonightText();
                SharedPrefsUtils.saveToPrefs(Globals.SPINNER_KEY, placeSpinner.getSelectedItem().toString(),
                        Globals.INITIAL_SETTINGS, c);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
                    SharedPrefsUtils.saveSquadMemberToSharedPrefs(squadMember, c);
                }
            }else{
                UIUtils.toastShort("Contact has no number", c);
            }
            cursor.close();
        }
    }

    public void sendText(String conNumber, String conName, int requestCode){
        sendText(conNumber, conName, requestCode, null);
    }

    public void sendText(String conNumber, String conName, int requestCode, String message)
    {
        Intent sentIntent = new Intent(Globals.SENT);
        Intent deliveredIntent = new Intent(Globals.DELIVERED);
        sentIntent.putExtra(EXTRA_NUMBER, conNumber);
        sentIntent.putExtra(EXTRA_NAME, conName);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, requestCode, sentIntent, 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, requestCode, deliveredIntent, 0);
        message = (message == null) ? placeSpinner.getSelectedItem().toString() + Globals.TONIGHT : message;
        smsMgr.sendTextMessage(conNumber, null, message, sentPI, deliveredPI);
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

    private void setTonightText(){
        philsButton.setText(placeSpinner.getSelectedItem().toString() + " tonight?");
    }

    private void alertSquad(String message, String toast){
        for (int i = 0; i < squadList.size(); i++) {
            SquadMember squadMember = squadList.get(i);
            sendText(squadMember.getNumber(), squadMember.getName(), i, message);
        }
        UIUtils.toastShort(toast, c);
    }

    private void loadInitialSettings(){
        SharedPrefsUtils.loadSquadMemberFromSharedPrefs(this, squadList);
        String val = SharedPrefsUtils.loadPrefValue(Globals.SPINNER_KEY, Globals.INITIAL_SETTINGS, c);
        if (val != null){
            placeSpinner.setSelection(RestaurantSingleton.getInstance().indexOf(val));
            philsButton.setText(val);
        }
    }
}
