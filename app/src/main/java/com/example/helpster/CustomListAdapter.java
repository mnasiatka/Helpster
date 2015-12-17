package com.example.helpster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final Item item[];

    public CustomListAdapter(Context context, Item i[]) {
        super(context, R.layout.lvlayout, new String[i.length]);
        this.context = context;
        this.item = i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lvlayout, parent, false);
        TextView whereview = (TextView) rowView.findViewById(R.id.LIST_where);
        TextView nameview = (TextView) rowView.findViewById(R.id.LIST_name);
        TextView priceview = (TextView) rowView.findViewById(R.id.LIST_price);
        System.out.println(item.length + " " + position);
        whereview.setText(item[position].place);
        nameview.setText(item[position].name);
        priceview.setText("$" + item[position].amount);

        return rowView;
    }
    
    @Override
    public int getCount(){
		return item.length;
    }
    
    
}
