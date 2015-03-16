package org.hackathon.medicineinfo;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class GetPicture {

	MainActivity obj = null;
	public void getImage(String url, MainActivity pObj){
		obj = pObj;
		new AsyncGetDescription().execute(url);
	}

	private class AsyncGetDescription extends AsyncTask<String,Void,Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			return getRemoteImage(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			obj.updateBitmap(result);
		}
		
		private Bitmap getRemoteImage(final String url) {
			try {
				final URLConnection conn = new URL(url).openConnection();
				conn.connect();
				BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				return bm;
			} catch (IOException e) {
				e.printStackTrace();
				return null;   
			}
		}
	}
}