package com.philstonight.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.philstonight.MainActivity;
import com.philstonight.Models.SquadMember;
import com.philstonight.R;
import com.philstonight.Util.SharedPrefsUtils;
import com.philstonight.Util.UIUtils;

import java.util.ArrayList;

/**
 * Created by yisen_000 on 2015-09-19.
 */
public class SquadAdapter extends BaseAdapter {
    private final ArrayList<SquadMember> squadList = new ArrayList<>();
    private Context c;

    public SquadAdapter(ArrayList<SquadMember> list, Context context) {
        squadList.addAll(list);
        c = context;
    }

    public void appendToSquad(SquadMember squadMember){
        squadList.add(squadMember);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return squadList.size();
    }

    @Override
    public SquadMember getItem(int position) {
        return squadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View endView;
        Button delete;
        Button sms;

        if (convertView == null){
            endView = LayoutInflater.from(parent.getContext()).inflate(R.layout.squad_adapter_list, parent, false);
        } else{
            endView = convertView;
        }

        final SquadMember item = getItem(position);

        ((TextView)endView.findViewById(R.id.contact)).setText(item.getName());
        ((TextView)endView.findViewById(R.id.num)).setText(item.getNumber());

        delete = (Button)endView.findViewById(R.id.deleteBtn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefsUtils.deleteFromSharedPrefs(c, squadList.get(position).getName());
                squadList.remove(position);
                notifyDataSetChanged();
            }
        });

        sms = (Button)endView.findViewById(R.id.smsBtn);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)c).sendText(squadList.get(position).getNumber(), squadList.get(position).getName(), 0);
                UIUtils.toastShort("SMS Sent", c);
            }
        });


        return endView;
    }
}
