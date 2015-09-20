package com.philstonight.ViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.philstonight.Models.SquadMember;
import com.philstonight.R;

import java.util.ArrayList;

/**
 * Created by yisen_000 on 2015-09-19.
 */
public class SquadAdapter extends BaseAdapter {
    private final ArrayList<SquadMember> squadList = new ArrayList<>();

    public SquadAdapter(ArrayList<SquadMember> list) {
        squadList.addAll(list);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View endView;

        if (convertView == null){
            endView = LayoutInflater.from(parent.getContext()).inflate(R.layout.squad_adapter_list, parent, false);
        } else{
            endView = convertView;
        }

        SquadMember item = getItem(position);

        ((TextView)endView.findViewById(R.id.contact)).setText(item.getName());

        return endView;
    }
}
