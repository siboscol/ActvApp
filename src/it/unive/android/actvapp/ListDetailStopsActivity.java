package it.unive.android.actvapp;

import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;
import it.unive.android.actvapp.network.DownloadXmlGetNextPassages;
import it.unive.android.actvapp.network.DownloadXmlgetRunRoutesbyStopID;
import it.unive.android.actvapp.network.OnTaskFinished;

import java.util.ArrayList;
import java.util.List;

import com.example.android.navigationdrawerexample.R;



import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class ListDetailStopsActivity extends FragmentActivity implements OnTaskFinished{
	
	//Linea Selezionata
	public static final String LINE_NUMBER = "LINE";
	public static final String LINE_ICON = "icon";
	public static final String PREFS_NAME = "MyPrefs";
	//Fermata Selezionata
	public static final String STOP_DESC = "STOPDESC";
	public static final String STOP_NAME = "STOP";
	public static final String STOP_ID = "STOPID";
	//Orario e ultima fermata di quel orario
	public static final String TIME_SELECTED= "TIME";
	public static final String LAST_STOP= "LASTSTOP";
	
	public static List<StopsDesc> runRoute = new ArrayList<StopsDesc>();
	
	//Variabili contenenti la Selezione dell'utente
	private String lineSelected;
	private String stopSelected;
	private String stopIdSelected;
	private int lineIconSelected;
	private String lastStopSelected;
	private String timeSelected;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_detail_stops);
		// Show the Up button in the action bar.
		setupActionBar();
		
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        lineSelected = prefs.getString(LINE_NUMBER, "No line selected"); 
        stopSelected = prefs.getString(STOP_DESC, "No stop selected");
        lineIconSelected = prefs.getInt(LINE_ICON, 0);
        stopIdSelected = prefs.getString(STOP_ID, "No stopId selected");
        lastStopSelected = prefs.getString(LAST_STOP, "No lastStop selected");
        timeSelected = prefs.getString(TIME_SELECTED, "No lastStop selected");
        
        
        //Imposto come titolo il nome e l'icona della linea selezionata
        setTitle("Linea " + lineSelected);
        getActionBar().setIcon( lineIconSelected );
        
//        Toast.makeText( this, "lineSelected: " + lineSelected, Toast.LENGTH_SHORT).show();
//        Toast.makeText( this, "StopSelected: " + stopIdSelected, Toast.LENGTH_SHORT).show();
//        Toast.makeText( this, "timeSelected: " + timeSelected, Toast.LENGTH_SHORT).show();
        
        TextView line = (TextView) findViewById(R.id.textView3);
        line.setText( stopSelected );
        
        TextView stop = (TextView) findViewById(R.id.textView4);
        stop.setText( lastStopSelected );
        
        
        String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + stopIdSelected + 
		    						"&requestedDateTime=" + timeSelected;
        
        DownloadXmlgetRunRoutesbyStopID answere = new DownloadXmlgetRunRoutesbyStopID(this);
        answere.execute(URL);
        

	}
	
	@Override
	public void onTaskFinished(List<Passage> response, List<StopsDesc> answere) {
        // ho aggiunto fino alla graffa e le due classi customAdapter e la classe dettaglio
        ListView display = (ListView) findViewById(R.id.listView1);
        List<StopsDesc> list1 = new ArrayList<StopsDesc>();
        boolean firstStopPassed = false;
        
        if ( answere != null ){
        	for( StopsDesc PlannedStop: answere){
        		if ( PlannedStop.lastStopName.equals(lastStopSelected) ){
        			if ( PlannedStop.stopDesc.equals(stopSelected) )
        				firstStopPassed = true;
        			if ( firstStopPassed )
        				list1.add(PlannedStop);
        		}
        	}
        }
        
        
//		String firstRunId = "";
//		spo = new ArrayList<StopsDesc>();
//        for ( StopsDesc s: answere){
//        	if ( s.alisLine.equals(lineSelected) ){
//        		if ( firstRunId.equals("") )
//        			firstRunId = s.runid;
//        		if ( s.runid.equals(firstRunId) ) 
//	            	spo.add(s);
//        	}
//        }
		    

		     
		 RowShowStartAdapter adapter = new RowShowStartAdapter(this, list1);
		 display.setAdapter(adapter);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_detail_stops, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
