package com.example.helpster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUser extends Activity implements OnClickListener {

	private static final int SELECT_PHOTO = 100, TAKE_PHOTO = 101;
	private static ImageView ivProfilePicture;
	private static TextView tvName, tvUsername, tvEmail;
	private Button btSave, btCancel;
	private static User user;
	private Bitmap bmp;
	private File photo;
	private static String name, email, password, username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user);
		user = ViewUsersList.selectedItem;

		ivProfilePicture = (ImageView) findViewById(R.id.VIEWUSER_IMAGEVIEW_PROFILEPICTURE);
		tvName = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_NAME);
		tvUsername = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_USERNAME);
		tvEmail = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_EMAIL);
		btSave = (Button) findViewById(R.id.VIEWUSER_BUTTON_SAVE);
		btCancel = (Button) findViewById(R.id.VIEWUSER_BUTTON_CANCEL);

		Log.e("Username", user.getUsername());
		Log.e("Logged", Login.getLoggedUser().getUsername());
		if (user.getUsername().equals(Login.getLoggedUser().getUsername())) {
			System.out.println("This is your page");
			user.setPassword(Login.getLoggedUser().getPassword());
			user.setInitialPicture(user.getPicture());
			new GetUserInfoTask(user.getUsername(), user.getPassword()).execute(user);
			ivProfilePicture.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(
							getApplicationContext(),
							"Height: " + ivProfilePicture.getLayoutParams().height + "\nWidth: "
									+ ivProfilePicture.getLayoutParams().width + "\nSize: "
									+ user.getPicture().getByteCount(), Toast.LENGTH_LONG).show();

					return true;
				}
			});
			ivProfilePicture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					System.out.println(user.getUsername().getBytes());
					System.out.println(Login.getLoggedUser().getUsername().getBytes());
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				}
			});

			btSave.setOnClickListener(this);
			btCancel.setOnClickListener(this);

		} else {
			new GetUserInfoTask(user.getUsername()).execute(user);
			System.out.println("This is not your page");
			btCancel.setOnClickListener(this);
		}

		tvName.setText(user.getName());
		ivProfilePicture.setImageBitmap(user.getPicture());
		tvUsername.setText(user.getUsername());

	}

	public static void onInfoTaskReturn(User result) {
		user = result;
		tvEmail.setText(user.getEmail());
		name = user.getName();
		email = user.getEmail();
		username = user.getUsername();
		if (user.getPassword() != null) {
			password = user.getPassword();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case SELECT_PHOTO:
					Uri selectedImage = imageReturnedIntent.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
							null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					// Bitmap bmp;
					bmp = BitmapFactory.decodeFile(filePath);
					try {
						int o = resolveBitmapOrientation(new File(filePath));
						bmp = applyOrientation(bmp, o);
					} catch (IOException e) {
						e.printStackTrace();
					}
					photo = new File(filePath);
					int width = bmp.getWidth();
					int height = bmp.getHeight();
					int newWidth = 200;
					int newHeight = 200;

					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;

					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					matrix.postRotate(0);

					bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
					String[] fs = filePath.split(".jpg");
					String newFilePath = fs[0] + "_compressed.jpg";
					try {
						FileOutputStream out = new FileOutputStream(newFilePath);
						photo = new File(newFilePath);
						bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					// ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// bmp.compress(Bitmap.CompressFormat.JPEG, 0, baos);
					// byte ba[] = baos.toByteArray();
					// bmp = BitmapFactory.decodeByteArray(ba, 0, ba.length);
					user.setPicture(bmp);
					ivProfilePicture.setImageBitmap(bmp);
					ivProfilePicture.requestLayout();
				break;
				case TAKE_PHOTO:
				break;
			}

		} else {
			Toast.makeText(getApplicationContext(), "Could not change profile picture",
					Toast.LENGTH_LONG).show();;
		}
	}

	private int resolveBitmapOrientation(File file) throws IOException {
		ExifInterface exif = null;
		exif = new ExifInterface(file.getAbsolutePath());

		return exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	}

	private Bitmap applyOrientation(Bitmap bitmap, int orientation) {
		int rotate = 0;
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
			break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
			break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
			break;
			default:
				return bitmap;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public void onDownloadTaskReturn() {
		ivProfilePicture.setImageBitmap(user.getPicture());
	}

	// check changes and restore as needed
	private boolean isUnchanged() {

		return true;
	}

	@Override
	public void onClick(View v) {
		System.out.println("here");
		switch (v.getId()) {
			case R.id.VIEWUSER_BUTTON_CANCEL:
				if (user.getUsername().equals(Login.getLoggedUser().getUsername())) {
					System.out.println("match");
					if (isUnchanged()) {
						System.out.println("unchanged");
						finish();
					} else {
						System.out.println("Changed");
						user.setPicture(user.getInitialPicture());
						email = user.getEmail();
						name = user.getName();
						if (password != null) {
							password = user.getPassword();
						}
						ivProfilePicture.setImageBitmap(user.getPicture());
						tvName.setText(name);
						tvEmail.setText(email);
					}
				} else {
					System.out.println("no match");
					finish();
				}
				break;
			case R.id.VIEWUSER_BUTTON_SAVE:
				if (user.getInitialPicture() != user.getPicture()) {
					new UploadImageTask(photo, user).execute(user.getUsername());
				}
				user.setName(name);
				user.setEmail(email);
				if (password != null) {
					new EditUserTask(getApplicationContext(), user).execute(password);
					user.setPassword(password);
				} else {
					new EditUserTask(getApplicationContext(), user).execute();
				}

				break;
		}
	}
}
