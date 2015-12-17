package com.example.helpster;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditUser extends AppCompatActivity {

	private static ImageView ivPROFILEPICTURE;
	private static TextView tvNAME, tvUSERNAME, tvEMAIL;
	private Button btSAVE, btCANCEL;
	private static final int SELECT_PHOTO = 100, TAKE_PHOTO = 101;
	private static int SCREEN_WIDTH, SCREEN_HEIGHT;
	private Bitmap bmp;
	private static User user;
	private File photo;
	private static String photo_url;
	private Bitmap initialPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user);
		user = ViewUsersList.selectedItem;
		// int scaling = 1;
		// Display display = getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// SCREEN_WIDTH = size.x;
		// SCREEN_HEIGHT = size.y;

		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		ivPROFILEPICTURE = (ImageView) findViewById(R.id.VIEWUSER_IMAGEVIEW_PROFILEPICTURE);
		tvNAME = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_NAME);
		tvUSERNAME = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_USERNAME);
		tvEMAIL = (TextView) findViewById(R.id.VIEWUSER_TEXTVIEW_EMAIL);
		btSAVE = (Button) findViewById(R.id.VIEWUSER_BUTTON_SAVE);
		btCANCEL = (Button) findViewById(R.id.VIEWUSER_BUTTON_CANCEL);

		// if (user == null) {
		// Toast.makeText(getApplicationContext(),
		// "Could not get user info:\ndisplaying logged user's info instead",
		// Toast.LENGTH_LONG).show();;
		// user = Login.getLoggedUser();
		// } else {
		// Toast.makeText(getApplicationContext(), "All went well!",
		// Toast.LENGTH_SHORT).show();;
		// }

		new GetUserInfoTask(user.getUsername(), user.getPassword()).execute(user);

		ivPROFILEPICTURE.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(
						getApplicationContext(),
						"Height: " + ivPROFILEPICTURE.getLayoutParams().height + "\nWidth: "
								+ ivPROFILEPICTURE.getLayoutParams().width + "\nSize: "
								+ user.getPicture().getByteCount(), Toast.LENGTH_LONG).show();

				return true;
			}
		});
		ivPROFILEPICTURE.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
		});

		btSAVE.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user.getInitialPicture() != user.getPicture()) {
					new UploadImageTask(photo, user).execute(user.getUsername());
				}
				System.out.println(user.getInitialPicture().toString());
				System.out.println(user.getPicture().toString());
			}
		});
		btCANCEL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				user.setPicture(user.getInitialPicture());
				ivPROFILEPICTURE.setImageBitmap(user.getPicture());
			}
		});

	}

	public static void onInfoTaskReturn(User result) {
		user = result;
		tvNAME.setText(user.getName());
		tvUSERNAME.setText(user.getUsername());
		tvEMAIL.setText(user.getEmail());
		photo_url = String.format("http://www.ythogh.com/helpster/photos/%s.jpg",
				user.getUsername());
		new DownloadImageTask(ivPROFILEPICTURE, user).execute(photo_url);
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
					ivPROFILEPICTURE.setImageBitmap(bmp);
					ivPROFILEPICTURE.requestLayout();
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

	public static void onDownloadTaskReturn() {
		ivPROFILEPICTURE.setImageBitmap(user.getPicture());
	}

}
