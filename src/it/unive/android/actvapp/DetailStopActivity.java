package it.unive.android.actvapp;

import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;
import it.unive.android.actvapp.network.DownloadXmlGetNextPassages;
import it.unive.android.actvapp.network.OnTaskFinished;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.example.android.navigationdrawerexample.R;

import android.R.integer;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class DetailStopActivity extends FragmentActivity implements OnTaskFinished{
	
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
	
	//Fermate di andata e ritorno
    private List<Fermate> stopsByline;
    private static List<Fermate> stopsBylineAndStopName;  
	
	public static ArrayList<String> Table = new ArrayList<String>();
	
	//Array dove vengono salvati gli StopId della fermata selezionata dall'utente
	private static ArrayList<String> idStops;
	
	
	public static List<Passage> responde = new ArrayList<Passage>();
	public static List<List<Passage>> ResponseId = new ArrayList<List<Passage>>();

	//Variabili contenenti la Selezione dell'utente
	private String lineSelected;
	private String stopSelected;
	private int lineIconSelected;
	
	private String direction;
		
	private static DetailStopActivity activity;
	
	public static int hour = 0; // variabili statiche per memorizzare il cambio di ora e minuti per aggiornare la ListView
	public static int minute = 0;
	public static int orario = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_stop_layout);
		activity = this;
//		Toast.makeText( activity, "Entrato in OnCreate", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        lineSelected = prefs.getString(LINE_NUMBER, "No line selected"); 
        stopSelected = prefs.getString(STOP_NAME, "No stop selected");
        lineIconSelected = prefs.getInt(LINE_ICON, 0);
        
//        Toast.makeText( activity, "StopNAME: " + stopSelected, Toast.LENGTH_SHORT).show();
        
        //Imposto come titolo il nome e l'icona della linea selezionata
        setTitle("Linea " + lineSelected);
        getActionBar().setIcon( lineIconSelected );
//      String stopIdSelected = prefs.getString(STOP_NAME, "No stop selected");
        
		//Recupero il database
		DataBase db = new DataBase(this);
		
		//Sostituisco possibili caratteri non riconosciuti nel db		
		String apici = stopSelected.replaceAll("'", "''");
		
		stopsBylineAndStopName = db.getAllStopsByLineAndName(lineSelected, apici );
		
		db.close();
		
//		for ( Fermate f: stopsBylineAndStopName ){
//			Log.e("stopsBylineAndStopName in OnCreate:", f.getStopId() );
////	        Toast.makeText( activity, "Stop: " + f.getStopId() + ", " + f.getStopDesc(), Toast.LENGTH_SHORT).show();
//		}
			
			
		//Recupero la lista intera di fermate per linea selezionata e seleziono gli stopId
		//che mi servono per fare la chiamata al WebService
		
		TextView stopName = (TextView) findViewById(R.id.line);
        stopName.setText( stopSelected );
        
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = s.format(new Date());
        
        ResponseId.clear();

        for ( Fermate i: stopsBylineAndStopName ){
	        String URL2 = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
					"stopID="+ i.getStopId() + "&requestedDateTime=" + format + "&passagesNumber=4";
	        
	        DownloadXmlGetNextPassages answere = new DownloadXmlGetNextPassages(this);
	        answere.execute(URL2);
        } 
		
		
//        stopsByline = db.getAllGoStopsByLine( lineSelected );     
//        
//        idStops = new ArrayList<String>();
//        for ( Fermate f: stopsByline){
//        	if ( f.getStopDesc().toLowerCase().replaceAll(" ", "").indexOf(stopSelected.toLowerCase().replaceAll(" ", "")) != -1 )            	
//        		idStops.add( f.getStopId() );
//        }
//        
//        Toast.makeText( activity, "StopId: " + idStops.toString(), Toast.LENGTH_SHORT).show();
////        Backfermate = db.getAllBackStopsByLine( lineSelected );
// 
//        Toast.makeText( this, "SharedPreference: " + lineSelected + ", " + lineIconSelected, Toast.LENGTH_SHORT).show();
//        
//        //show the selected stop's name 
//        if (idStops.size() != 0 ){
//        	String[] desc = db.getStopDesc( idStops.get(0)).split("SX|DX|\"|\\(");
//	        TextView stopName = (TextView) findViewById(R.id.line);
//	        stopName.setText( desc[0] );
//	    }
//        else {
//	        TextView stopName = (TextView) findViewById(R.id.line);
//	        stopName.setText( stopSelected );
//        }
//        
//        //Direction Spinner
////        setDirectionLine(lineSelected);
//        db.close();
//        
//        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
//        String format = s.format(new Date());
//        
//        Toast.makeText( activity, "TimeStamp: " + format, Toast.LENGTH_SHORT).show();
////        Toast.makeText( activity, "Minuti: " + m, Toast.LENGTH_SHORT).show();
//        
//    	ResponseId.clear();
//        for ( String i: idStops){
//	        String URL2 = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
//					"stopID="+ i + "&requestedDateTime=" + format + "&passagesNumber=5";
//	        
//	        DownloadXmlGetNextPassages answere = new DownloadXmlGetNextPassages(this);
//	        answere.execute(URL2);
//        } 

        
		//Pulsante per la selezione di un orario di partenza da visualizzare nella lista orari
        Button changeLine = (Button) findViewById(R.id.button1);
        
        changeLine.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                // Do something in response to button click
//        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        	    SharedPreferences.Editor editor = settings.edit();
//        	    editor.putString(LINE_NUMBER, "No line selected");
//        	    editor.commit();
//			  	Intent intent = new Intent(activity, MainActivity.class);
//			    startActivity(intent);
            	
				LineListDialogFragment changeLine = new LineListDialogFragment();			
			    changeLine.show( getFragmentManager(), "timePicker");
            	        	    
			    
            }
        });
        
////        //FilterTime Spinner
//        setFilterTime(this);
		
		//Pulsante per la selezione di un orario di partenza da visualizzare nella lista orari
        Button selectTime = (Button) findViewById(R.id.button3);
        
        selectTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            	ResponseId.clear();
				DialogFragment newFragment = new TimePickerFragment();			
			    newFragment.show( getFragmentManager(), "timePicker");
			    
            }
        });
        

	}
	
	
	//Questo metodo viene eseguito alla fine della esecuzione del task
	@Override
	public void onTaskFinished(List<Passage> response, List<StopsDesc> answere) {
		
    	final Calendar c = Calendar.getInstance();
//    	DetailStopActivity.responde = response;
//    	if ( response != null )
//    		Toast.makeText( activity, "Arriva tra " + responde.get(0).arrivalMinute, Toast.LENGTH_SHORT).show();
//    	response = (List<Passage>) response;
    	if ( response.size() != 0 ){
			DataBase db = new DataBase(this);
			String stopdesc = db.getStopDesc(response.get(0).stopID);
	    	db.close();
			//Aggiungo la descrizione della fermata alla risposta del webservice
	    	for ( Passage p: response)
	    		p.stopDesc = stopdesc;
	    	
	    	ResponseId.add(response);
	    	displayNextPassages(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), this);
    	}else{
    	   	//Nell'else di questo if dovrei far apparire un messaggio all'utente dicendo che in quel orario non ci sono
	    	//passaggi di quella fermata
//	    	Toast.makeText( this, "La linea selezionata non passa per la fermata selezionata nell'orario selezionato.", Toast.LENGTH_SHORT).show();
    	}
	}
	
	  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_stop, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    
	  //Pulsante per aggiornare gli orari mostrati
	        case R.id.action_sync:
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
                String format = s.format(new Date());
                
//                Toast.makeText( activity, "TimeStamp: " + format, Toast.LENGTH_SHORT).show();
//                Toast.makeText( activity, "Minuti: " + m, Toast.LENGTH_SHORT).show();
                
            	ResponseId.clear();
                for ( Fermate i: stopsBylineAndStopName ){
        	        String URL2 = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
        					"stopID="+ i.getStopId() + "&requestedDateTime=" + format + "&passagesNumber=5";
        	        
        	        DownloadXmlGetNextPassages answere = new DownloadXmlGetNextPassages(activity);
        	        answere.execute(URL2);
                }
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	 public void onBackPressed(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putString(LINE_NUMBER, "No line selected");
//	    editor.putInt(LINE_ICON, 0);
	    editor.putString(STOP_NAME, "No Stop request");
    	editor.commit();   
    	
    	super.onBackPressed();
	 }

	
	//Quando l'utente decide di cambiare linea viene richiamata la stessa activity e riaggiornato l'orario con la nuova linea
	//il codice sotto è lo stesso del metodo oncreate
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        lineSelected = prefs.getString(LINE_NUMBER, "No line selected"); 
        stopSelected = prefs.getString(STOP_NAME, "No stop selected");
        lineIconSelected = prefs.getInt(LINE_ICON, 0);
        
        //Imposto come titolo il nome e l'icona della linea selezionata
        setTitle("Linea " + lineSelected);
        getActionBar().setIcon( lineIconSelected );
//      String stopIdSelected = prefs.getString(STOP_NAME, "No stop selected");
        
//		//Recupero il database
//		DataBase db = new DataBase(this);
//			
//		//Recupero la lista intera di fermate per linea selezionata e seleziono gli stopId
//		//che mi servono per fare la chiamata al WebService
//        stopsByline = db.getAllGoStopsByLine( lineSelected );
//        
//          
//        
//        idStops = new ArrayList<String>();
//        for ( Fermate f: stopsByline){
//        	if ( f.getStopDesc().toLowerCase().replaceAll(" ", "").indexOf(stopSelected.toLowerCase().replaceAll(" ", "")) != -1 )            	
//        		idStops.add( f.getStopId() );
//        }
//        
//        Toast.makeText( activity, "StopId: " + idStops.toString(), Toast.LENGTH_SHORT).show();
//        Backfermate = db.getAllBackStopsByLine( lineSelected );
 
//        Toast.makeText( this, "SharedPreference: " + lineSelected + ", " + lineIconSelected, Toast.LENGTH_SHORT).show();
        
        //show the selected stop's name 
//        if (idStops.size() != 0 ){
//        	String[] desc = db.getStopDesc( idStops.get(0)).split("SX|DX|\"|\\(");
//	        TextView stopName = (TextView) findViewById(R.id.line);
//	        stopName.setText( desc[0] );
//	    }
//        else {
	        TextView stopName = (TextView) findViewById(R.id.line);
	        stopName.setText( stopSelected );
//        }
        
        //Direction Spinner
//        setDirectionLine(lineSelected);
//        db.close();
	        
//	        Toast.makeText( activity, "StopNAME: " + stopSelected, Toast.LENGTH_SHORT).show();     
		//Recupero il database
		DataBase db = new DataBase(this);
			
		//Sostituisco possibili caratteri non riconosciuti nel db		
		String apici = stopSelected.replaceAll("'", "''");
			
		stopsBylineAndStopName = db.getAllStopsByLineAndName(lineSelected, apici );
			
		db.close();
        
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = s.format(new Date());
        
//        Toast.makeText( activity, "TimeStamp: " + format, Toast.LENGTH_SHORT).show();
//        Toast.makeText( activity, "Minuti: " + m, Toast.LENGTH_SHORT).show();
        
        ListView timetable = (ListView) activity.findViewById(R.id.timetable); 
		TextView empty = (TextView) activity.findViewById(R.id.empty1);
		timetable.setEmptyView(empty);			
		timetable.getEmptyView().setVisibility(ListView.GONE);
        timetable.setAdapter(null);
        
    	ResponseId.clear();
        for ( Fermate i: stopsBylineAndStopName){
        	Log.e("Entrato in OnNewIntent:", "stopsBylineAndStopName: " + i.getStopId() );
	        String URL2 = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
					"stopID="+ i.getStopId() + "&requestedDateTime=" + format + "&passagesNumber=5";
	        
	        DownloadXmlGetNextPassages answere = new DownloadXmlGetNextPassages(this);
	        answere.execute(URL2);
        } 
    }

		//funzione per visualizzare la ListView
		public static void displayNextPassages(int ora, int min, final DetailStopActivity activity) {
			
			ListView timetable = (ListView) activity.findViewById(R.id.timetable); 
			TextView empty = (TextView) activity.findViewById(R.id.empty1);
			timetable.setEmptyView(empty);			
//			timetable.getEmptyView().setVisibility(ListView.GONE);	
		    
		    final List<Passage> total = new ArrayList<Passage>();

		    //Inizio a scorrere il risultato del xml
		    if ( ResponseId != null ){
      	
	        	for ( List<Passage> lp: ResponseId ){
	        		for( Passage p: lp ){
		        		if ( p.aliasLine.equals( activity.lineSelected ) ){
		        			//Con questo if non faccio vedere nei risultati gli orari in cui partenza e destinazione sono uguali
//		        			if ( !p.stopDesc.equals(p.lastStopName) )
		        			
		        			//Non posso utilizzare l'if sopra perchè le linee circolari hanno partenza e destinazione uguale
		        				total.add(p);
		            	}
	        		}
		    }        	
//		        RowDetailStopAdapter adapter = new RowDetailStopAdapter( activity, total );
//		        timetable.setAdapter(adapter);
	        	
	        	Collections.sort(total, new Comparator<Passage>() {

					@Override
					public int compare(Passage lhs, Passage rhs) {
//						return lhs.lastStopName.compareTo(rhs.lastStopName);
						
						if ( lhs.lastStopName.compareTo(rhs.lastStopName) == 0 ){

							if ( Integer.parseInt( lhs.arrivalMinute ) > Integer.parseInt( rhs.arrivalMinute) )
								return 1;
							else if ( Integer.parseInt( lhs.arrivalMinute ) < Integer.parseInt( rhs.arrivalMinute) )
								return -1;
							else
								return 0;
						}else
							return lhs.lastStopName.compareTo(rhs.lastStopName);
						
					}
				});
//	        	
//	        	Collections.sort(empList, new Comparator<Employee>(){
//	        		  public int compare(Employee emp1, Employee emp2) {
//	        		    return emp1.getFirstName().compareToIgnoreCase(emp2.getFirstName());
//	        		  }
//	        		});
	        	
	        	//Se volessi la sicurezza che gli orari non vengano invertiti dall'ordinamento 
	        	//dovremmo modificare il metodo compare del Passage ( Consigliato da Bruson )
	        	//int same = lhs.lastStopName.compareTo(rhs.lastStopName);
	        	//if ( same == 0 )
	        	//return Integere.ParseInt(lhs.hourminute).compareTo(Integer.ParseInt(rhs.hourminute)
	        	
//	        	int i = 1;
//	        	for ( Passage p: total ){
//	        		Log.e("total:", i + "." + p.lastStopName );
//	        		i++;
//	        	} 
	        	 
		        RowDetailStopAdapterWithGroup adapter = new RowDetailStopAdapterWithGroup( activity, total );
		        timetable.setAdapter(adapter);

		        timetable.setOnItemClickListener( new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						arg1.setSelected(true);
						
						SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
					    SharedPreferences.Editor editor = settings.edit();
					    editor.putString(STOP_ID, total.get(arg2).stopID);
					    editor.putString(STOP_DESC, total.get(arg2).stopDesc);
			        	editor.putString(TIME_SELECTED, total.get(arg2).passageTime);
			        	editor.putString(LAST_STOP, total.get(arg2).lastStopName);
					    editor.commit();
						 
					  	Intent intent2 = new Intent( activity, ListDetailStopsActivity.class);
						activity.startActivity(intent2);
						
					}
				});

		    }
		}
		
		
		//Classe interna per la creazione di un Dialog per la selezione dell'orario
		public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
			
			DetailStopActivity parent;

		    @Override
		    public void onAttach(Activity activity) {
		            super.onAttach(activity);
		            parent = (DetailStopActivity) activity;
		    }
			
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
//				// Use the current time as the default values for the picker
				final Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
//				
//				// Create a new instance of TimePickerDialog and return it
//				return new TimePickerDialog( parent, this, hour, minute, DateFormat.is24HourFormat(parent) );
				
			    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			    final TimePicker timePicker = new TimePicker(parent);
			    timePicker.setIs24HourView(true);
			    timePicker.setCurrentHour(hour);
			    timePicker.setCurrentMinute(minute);
			    builder.setView(timePicker);
			    builder.setTitle("Imposta ora");
			    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			                onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());

					}
				});
			    builder.setNegativeButton(android.R.string.cancel, null);
			    return builder.create();
			}
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
				//Recupero la selezione dell'utente per chiamare il WebService
				final Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, hourOfDay);
				c.set(Calendar.MINUTE, minute);
				c.set(Calendar.SECOND, 0);
		        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
		        String format = s.format( c.getTime() );
		         
		        
		        ResponseId.clear();
		        for ( Fermate i: stopsBylineAndStopName ){
		        	Log.e("stopsBylineAndStopName:", i.getStopId() );
		        	
			        String URL2 = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
							"stopID="+ i.getStopId() + "&requestedDateTime=" + format + "&passagesNumber=5";
			        
			        DownloadXmlGetNextPassages answere = new DownloadXmlGetNextPassages(parent);
			        answere.execute(URL2);
		        } 

				
//				Toast.makeText( activity, "Select TimeStamp: " + format, Toast.LENGTH_SHORT).show();	
			}
	}
		

				
//Fine classe DetailStopActivity
}
