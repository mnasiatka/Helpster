package com.example.helpster;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PagePagerFragment extends Fragment {

	TextView tvname, tvrequest, tvwhere, tvprice, tvduration, tvcontact, tvdelivery;
	Item item;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_page, container, false);
		tvname = (TextView) rootView.findViewById(R.id.viewRequestName);
		tvrequest = (TextView) rootView.findViewById(R.id.viewRequestRequest);
		tvwhere = (TextView) rootView.findViewById(R.id.viewRequestWhere);
		tvprice = (TextView) rootView.findViewById(R.id.viewRequestPrice);
		tvduration = (TextView) rootView.findViewById(R.id.viewRequestDuration);
		tvcontact = (TextView) rootView.findViewById(R.id.viewRequestContact);
		tvdelivery = (TextView) rootView.findViewById(R.id.viewRequestDelivery);
		Bundle extras = this.getArguments();
		if (extras != null) {
			int position = extras.getInt("position");
			item = Main.items[position];
			System.out.println("position: " + position);
			tvrequest.setText(item.request);
			tvname.setText(item.name);
			tvwhere.setText(item.place);
			tvprice.setText(item.amount);
			tvduration.setText(item.endTime);
			tvcontact.setText(item.contact);
			tvdelivery.setText(item.delivery);
		} else {
			System.out.println("null");
		}
		return rootView;
	}
}
