package com.philstonight;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.philstonight.Models.SquadMember;
import com.philstonight.SMSServices.SMSSender;
import com.philstonight.ViewAdapters.SquadAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_SELECT_CONTACT = 1;
    private Button philsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        philsButton = (Button)findViewById(R.id.philsButton);

        philsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSSender.sendMessage("6473821508");
            }
        });
        ListView squadListView = (ListView)findViewById(R.id.squad_list);

        ArrayList<SquadMember> squadList = new ArrayList<>();
        squadList.add(new SquadMember("Vishal", "647 holla"));
        squadList.add(new SquadMember("Justin", "519 holla"));
        SquadAdapter squadAdapter = new SquadAdapter(squadList);
        squadListView.setAdapter(squadAdapter);

        Intent intent = getIntent();

        ArrayList<String> nameCollection = new ArrayList<String>();
        ContentResolver cr = getContentResolver();

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext())
        {
            String nameFromContacts = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d("nameFromContacts", nameFromContacts);
            nameCollection.add(nameFromContacts);
        }
        cursor.close();
        String[] names = new String[nameCollection.size()];
        nameCollection.toArray(names);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, names);
        AutoCompleteTextView nameField = (AutoCompleteTextView) findViewById(R.id.name_text);
        nameField.setAdapter(adapter);
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
            String contactName = null;
            boolean hasPhoneNum = false;
            String phoneNumber = "";
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

            if (cursor.moveToFirst()) {

                // DISPLAY_NAME = The display name for the contact.
                // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                hasPhoneNum = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if(hasPhoneNum) {
                    phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
            cursor.close();
            AutoCompleteTextView nameField = (AutoCompleteTextView) findViewById(R.id.name_text);
        }
    }
}
