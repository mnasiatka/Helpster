package com.example.helpster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ELV extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        expListView = (ExpandableListView) findViewById(R.id.FILTER_EXPANDABLELISTVIEW_ELV);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader,null);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Sort by");
        listDataHeader.add("Filter by");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> sortby = new ArrayList<String>();
        sortby.add("Place");
        sortby.add("Amount");
        sortby.add("End time");
        sortby.add("Name");

        List<String> filterby = new ArrayList<String>();
        filterby.add("Placef");
        filterby.add("Amountf");
        filterby.add("End timef");
        filterby.add("Namef");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), sortby); // Header, Child data
        listDataChild.put(listDataHeader.get(1), filterby);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
}