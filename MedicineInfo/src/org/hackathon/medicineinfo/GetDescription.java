package org.hackathon.medicineinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;

public class GetDescription{

	MainActivity obj = null;
	public void search(String code, MainActivity pObj){
		obj = pObj;
		new AsyncGetDescription().execute(code);
	}

	private class AsyncGetDescription extends AsyncTask<String,Void,String> {

		@Override
		protected String doInBackground(String... params) {
			return getDescription(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.equalsIgnoreCase("Code not found !"))
				obj.productInfoNotAvailable(result);
			else
				obj.productNameFound(result);
		}

		public String getDescription(String code){
			String searchTerm = code;
			URL url;
			try {
				url = new URL("http://api.upcdatabase.org/json/50d5137fd3b356a84fdf874ba8e3f06c/"	+ searchTerm);
				String retval = "";
				URLConnection yc;
				yc = url.openConnection();

				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) 
					retval = retval + inputLine;
				in.close();

				JSONObject respObj;
				respObj = new JSONObject(retval);

				if(respObj.getString("valid").equals("true")){
					if(respObj.getString("itemname").length()>1)
						return respObj.getString("itemname");
					else
						return respObj.getString("description");
				}
			} catch (MalformedURLException e) {
				return e.getMessage();
			}catch (IOException e) {
				return e.getMessage();
			}catch (JSONException e) {
				return e.getMessage();
			}catch (Exception e){
				return e.getMessage();
			}

			return "Code not found !";
		}
	}
}