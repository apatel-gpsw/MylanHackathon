package org.hackathon.integration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hackathon.medicineinfo.MainActivity;

import android.os.AsyncTask;

public class DataIntegrator {

	MainActivity obj = null;
	public void fetchData(String medName, MainActivity pObj){
		obj = pObj;
		new AsyncFetchData().execute(medName,"select");
	}

	public void subscribeUser(String medName, MainActivity pObj){
		obj = pObj;
		new AsyncFetchData().execute(medName,"subscribe");
	}

	private class AsyncFetchData extends AsyncTask<String,Void,String> {

		@Override
		protected String doInBackground(String... params) {
			if(params[1].equalsIgnoreCase("select"))
				return getData(params[0]);
			else
				return updateData(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.length()<1)
				obj.subscriptionSuccess(result);
			else
				obj.productInfoAvailable(result);
		}

		public String updateData(String code){
			String url;
			OutputStream os;
			HttpURLConnection conn;
			BufferedReader serverResponse = null;
			try {
				url = "http://1-dot-mylanhackdata.appspot.com/medicinedata1";
				conn = ( HttpURLConnection ) new URL(url).openConnection(); 

				conn.connect();
				conn.setRequestMethod( "GET" );  
				conn.setDoOutput( true );
				conn.setDoInput(true);

				String op = "subscribe";

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("operation", op));
				params.add(new BasicNameValuePair("medName", code));
				params.add(new BasicNameValuePair("userid", obj.getUserId()));

				os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();

				serverResponse = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );  

				String line; 
				StringBuffer buff = new StringBuffer(); 
				while ( (line = serverResponse.readLine() ) != null )   
				{  
					buff.append( line );  
				}

				os.close();
				return buff.toString();
				// conn.connect();

			} catch (MalformedURLException e) {
				e.printStackTrace();;
			}catch (IOException e) {
				e.printStackTrace();
			}

			return "Error!";
		}

		public String getData(String code){
			String url;
			OutputStream os;
			HttpURLConnection conn;
			BufferedReader serverResponse = null;
			try {
			//	url ="http://1-dot-mylanhackdata.appspot.com/medicinedata1?operation=select&medName=Kellogs+NutriGrain+Bar&userid=ganuj";
				
				url = "http://1-dot-mylanhackdata.appspot.com/medicinedata1";
				String queryString = "?operation=select&medName="
						+ code.replace(" ", "+") + "&userid=" + obj.getUserId();
				conn = ( HttpURLConnection ) new URL(url+queryString).openConnection(); 
				conn.connect();
				
/*				conn.setRequestMethod( "GET" );  
				conn.setDoOutput( true );
				conn.setDoInput(true);

				String op = "select";

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("operation", op));
				params.add(new BasicNameValuePair("medName", code));
				params.add(new BasicNameValuePair("userid", obj.getUserId()));

				os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
*/
				serverResponse = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );  

				String line; 
				StringBuffer buff = new StringBuffer(); 
				while ( (line = serverResponse.readLine() ) != null )   
				{  
					buff.append( line );  
				}

				return buff.toString();
				// conn.connect();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}

			return "Error!";
		}
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params)
		{
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}
}