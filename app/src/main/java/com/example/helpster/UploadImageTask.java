package com.example.helpster;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;

class UploadImageTask extends AsyncTask<String, Void, Boolean> {
    File photo;
    HttpURLConnection conn;
    User user;

    public UploadImageTask(File file, User user) {
        this.photo = file;
        this.user = user;
    }

    @Override
    protected Boolean doInBackground(String... usernames) {
        String username = usernames[0];
        return uploadPhoto(username);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            System.out.println("File uploaded!");
            user.setInitialPicture(user.getPicture());
        } else {

            System.out.println("File could not be uploaded");
        }
    }

    private boolean uploadPhoto(String username) {
        String urlString = "http://ythogh.com/helpster/photos/upload_photo.php";
        String Tag = "UPLOAD";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            // ------------------ CLIENT REQUEST

            Log.e(Tag, "Inside second Method");

            FileInputStream fileInputStream = new FileInputStream(this.photo);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: post-data; name=uploaded_file;filename="
                    + username + "" + lineEnd);
            dos.writeBytes(lineEnd);
            Log.e(Tag, "Headers are written");

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1000;
            byte[] buffer = new byte[bytesAvailable];
            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bytesAvailable);
                bytesAvailable = fileInputStream.available();
                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            Log.e(Tag, "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception ex) {
            Log.e(Tag, "error: " + ex.getMessage(), ex);
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            boolean b = false;
            while ((line = rd.readLine()) != null) {
                Log.e("Dialoge Box", "Message: " + line);
                b = line.equals("uploaded");
            }

            rd.close();
            return b;

        } catch (IOException ioex) {
            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
            return false;
        }
    }
}