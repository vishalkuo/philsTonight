package com.philstonight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.philstonight.Models.SquadMember;
import com.philstonight.SMSServices.SMSSender;
import com.philstonight.ViewAdapters.SquadAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
}
