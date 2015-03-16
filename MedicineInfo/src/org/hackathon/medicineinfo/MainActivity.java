package org.hackathon.medicineinfo;

import org.hackathon.integration.DataIntegrator;
import org.hackathon.integration.IntentIntegrator;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hideEverything();
		findViewById(R.id.btnScan).setOnClickListener(scanProduct);
		findViewById(R.id.btnSearch).setOnClickListener(searchProduct);
		findViewById(R.id.btnSubscribe).setOnClickListener(subscribe);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void productNameFound(String medName){
		this.medName = medName;
		new DataIntegrator().fetchData(medName, this);
	}
	
	public void updateBitmap(Bitmap img){
		ImageView dangerMeter = (ImageView)findViewById(R.id.imgDanger);
		dangerMeter.setImageBitmap(img);
		dangerMeter.setVisibility(View.VISIBLE);
		dangerMeter.invalidate();
	}
	
	public void productInfoAvailable(String jsonString){
		
		JSONObject respObj;
		String mId="";
		String name="";
		String fda="";
		String link="";
		String safety="";
		String alt="";
		String manuf="";
		
		try{
			respObj = new JSONObject(jsonString);
			mId = respObj.getString("mId");
			name = respObj.getString("mName");
			fda = respObj.getString("lastFDA");
			link = respObj.getString("link");
			safety = respObj.getString("safetyRating");
			alt = respObj.getString("bestAlternate");
			manuf = respObj.getString("manufacturer");
		}catch(JSONException e){
			e.printStackTrace();
		}

		// Update the image file
		String imgUrl = "";
		switch(safety){
		case "low": imgUrl = "http://s12.postimg.org/4zph4ubvt/high.jpg";
			break;
		case "medium": imgUrl = "http://s12.postimg.org/g1uk3a5yh/med.jpg";
			// med image
			break;
		case "high": imgUrl = "http://s12.postimg.org/eyafrblbd/low.jpg";
			// low image
			break;
		}
		// update image
		new GetPicture().getImage(imgUrl, this);
		
		// Update the best alternate
		TextView bestAlt = (TextView)findViewById(R.id.txtBestAlternative);
		bestAlt.setVisibility(View.VISIBLE);
		// Get text from SQL
		bestAlt.setText(alt);

		// Set the description
		StringBuilder str = new StringBuilder();
		str.append("Name: " + name+"\n");
		str.append("Manufacturer: " + manuf+"\n");
		str.append("LastFDA: " + fda+"\n");
		str.append("Recent news: "+ link);
		TextView desc = (TextView)findViewById(R.id.txtDescription);
		desc.setVisibility(View.VISIBLE);
		desc.setText(str.toString());

		// Make subscribe visible
		Button subsc = (Button)findViewById(R.id.btnSubscribe);
		subsc.setVisibility(View.VISIBLE);
		
		// Invalidate all the views
		bestAlt.invalidate();
		desc.invalidate();
		subsc.invalidate();
	}

	public void productInfoNotAvailable(String text){

		// Set the description
		TextView desc = (TextView)findViewById(R.id.txtDescription);
		desc.setVisibility(View.VISIBLE);
		// Get text from SQL
		desc.setText(text);
	}

	public void hideEverything(){
		TextView bestAlt = (TextView)findViewById(R.id.txtBestAlternative);
		bestAlt.setVisibility(View.INVISIBLE);

		TextView desc = (TextView)findViewById(R.id.txtDescription);
		desc.setVisibility(View.INVISIBLE);

		Button subsc = (Button)findViewById(R.id.btnSubscribe);
		subsc.setVisibility(View.INVISIBLE);

		ImageView dangerMeter = (ImageView)findViewById(R.id.imgDanger);
		dangerMeter.setVisibility(View.INVISIBLE);
		
		// Invalidate all the views
		bestAlt.invalidate();
		desc.invalidate();
		subsc.invalidate();
	}
	
	public void subscriptionSuccess(String result2) {
		TextView desc = (TextView)findViewById(R.id.txtDescription);
		desc.setVisibility(View.VISIBLE);
		// Get text from SQL
		desc.setText(result2);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		getResults(result);
	}

	private final View.OnClickListener scanProduct = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			hideEverything();
			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
			integrator.addExtra("SCAN_WIDTH", 800);
			integrator.addExtra("SCAN_HEIGHT", 200);
			integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
			integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
			integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
		}
	};
	
	private void getResults(String code){
		new GetDescription().search(code, this);
	}
	
	public void subUser(String medName){
		new DataIntegrator().subscribeUser(medName, this);
	}

	private final View.OnClickListener searchProduct = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			hideEverything();
			String code = ((EditText)findViewById(R.id.termSearch)).getText().toString();
			getResults(code);
		}
	};

	private final View.OnClickListener subscribe = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			subUser(medName);
		}
	};
	
	private String result = null;
	private String userId = "ganuj";
	private String medName = "";
	
	public String getUserId(){
		return userId;
	}
}