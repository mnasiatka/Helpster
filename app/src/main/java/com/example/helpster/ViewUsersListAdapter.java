package com.example.helpster;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUsersListAdapter extends ArrayAdapter<User> {
	private final Context context;
	private final User objects[];
	private final List<User> objlist;
	ViewHolder mViewHolder = null;
	private ImageView ivPicture;
	private TextView tvName;

	public ViewUsersListAdapter(Context context, User[] objects) {
		super(context, R.layout.view_users_list, objects);
		this.context = context;
		this.objects = objects;
		this.objlist = null;
	}

	public ViewUsersListAdapter(Context context, List<User> objlist) {
		super(context, R.layout.view_users_list, objlist);
		this.context = context;
		this.objlist = objlist;
		this.objects = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (convertView == null) {
		// mViewHolder = new ViewHolder();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.view_users_list, parent, false);
		ivPicture = (ImageView) convertView.findViewById(R.id.VIEWUSERSLIST_IMAGEVIEW_PROFILE);
		tvName = (TextView) convertView.findViewById(R.id.VIEWUSERSLIST_TEXTVIEW_NAME);
		// convertView.setTag(mViewHolder);
		// } else {
		// mViewHolder = (ViewHolder) convertView.getTag();
		// }
		if (objects == null) {
			ivPicture.setImageBitmap(objlist.get(position).getPicture());
			tvName.setText(objlist.get(position).getName());
		} else {
			ivPicture.setImageBitmap(objects[position].getPicture());
			tvName.setText(objects[position].getName());
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return objects == null ? objlist.size() : objects.length;
	}

	public final class ViewHolder {
		public TextView mtvName;
		public ImageView mivPicture;
		public User mobject;
	}

}
