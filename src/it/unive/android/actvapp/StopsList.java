package it.unive.android.actvapp;

import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;
import it.unive.android.actvapp.network.DownloadXmlGetNextPassages;
import it.unive.android.actvapp.network.DownloadXmlgetRunRoutesbyStopID;
import it.unive.android.actvapp.network.OnTaskFinished;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.example.android.navigationdrawerexample.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.TimePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.EventLogTags.Description;
import android.util.Log;
import android.app.SearchManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class StopsList extends ListFragment implements OnTaskFinished {

		FragmentActivity mactivity;
	    
		//Variabili contententi le fermate a seconda se l'utente ha selezionato la linea o no
		private static List<Fermate> stopsByLine;
	    private List<Fermate> allStops;
	    
	    //Variabile con fermate visualizzate all'utente
	    private ArrayList<String> listWithoutDuplicates;
	    private List<StopsDesc> spo;
	    
	    //Variabili contenenti la Selezione dell'utente
	    private String lineSelected;
	    private int lineIconSelected;
	    private String stopIdSelected;
	
	    //Chiavi utizzare per recuperare le preferenze dell'utente	    
	    public static final String PREFS_NAME = "MyPrefs";
	    public static final String LINE_NUMBER = "LINE";
	    public static final String LINE_ICON = "icon";
	    public static final String STOP_NAME = "STOP";
	    
	    @Override
	    public void onAttach(Activity activity) {
	            super.onAttach(activity);
	            mactivity = (FragmentActivity) activity;   
	    }
	    

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Inflate the layout for this fragment
			return inflater.inflate(R.layout.stoplist_layout, container, false);
		} 
		
		
	    @Override
	    public void onResume() {
	    	super.onResume();
	    	
	    	SharedPreferences prefs = mactivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            lineSelected = prefs.getString(LINE_NUMBER, "No line selected"); 
            lineIconSelected = prefs.getInt(LINE_ICON, 0);
            
//            Toast.makeText( mactivity, "SharedPreference: " + lineSelected + ", " + lineIconSelected, Toast.LENGTH_SHORT).show();
            
			if ( !lineSelected.equals("No line selected") ){
				
				getListView().getEmptyView().setVisibility(ListView.GONE);
				
				//Inserisco linea e icona linea nel titolo
	            mactivity.setTitle("Linea " + lineSelected);
	            mactivity.getActionBar().setIcon( lineIconSelected );
	            
	            //Questo metodo fa capire al fragment che deve essere visualizzato il pulsante per il cambio dell'orario
	            setHasOptionsMenu(true);
	            
	            //Recupero la lista delle fermate della linea selezionata
	            DataBase db = new DataBase(mactivity);
				stopsByLine = db.getAllGoStopsByLine( lineSelected );
				db.close();

//				Toast.makeText( mactivity, "RandomStopId: " + lineSelected + ", " + stopsByLine.get(0).getStopId(), Toast.LENGTH_SHORT).show();

		        ConnectivityManager connMgr = (ConnectivityManager) 
		        mactivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
			        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
			        String format = s.format(new Date());
		            
		            String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + stopsByLine.get(0).getStopId() + 
							"&requestedDateTime=" + format;

		            DownloadXmlgetRunRoutesbyStopID answere = new DownloadXmlgetRunRoutesbyStopID( this );
		            answere.execute(URL);
		        } else {
		        	DialogFragment newFragment = new FireMissilesDialogFragment();			
				    newFragment.show( mactivity.getSupportFragmentManager(), "No connection Alert");
//		        	Toast.makeText( mactivity, "Non c'è la connessione!", Toast.LENGTH_SHORT).show();
		        }
				
				
//				Toast.makeText( mactivity, "RandomStopId: " + lineSelected + ", " + stopsByLine.get(1).getStopId(), Toast.LENGTH_SHORT).show();
				
//		        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
//		        String format = s.format(new Date());
//	            
//	            String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + stopsByLine.get(1).getStopId() + 
//						"&requestedDateTime=" + format;
//
//	            DownloadXmlgetRunRoutesbyStopID answere = new DownloadXmlgetRunRoutesbyStopID( this );
//	            answere.execute(URL);
			}
			else {
				// Fuori dal primo if siamo nella parte dove dovremmo visualizzare tutte le fermate e poi gestire il
				// fatto che non è stata selezionata la linea.
				
				//Potremmo fare in modo che se l'utente clicca una linea debba selezionare una delle linee che passano per quella fermata generica
	            mactivity.setTitle("Tutte le Fermate");
	            mactivity.getActionBar().setIcon( R.drawable.ic_launcher );

				ArrayList<String> all = new ArrayList<String>();
				DataBase db = new DataBase(mactivity);
				
				allStops = db.getAllStops();
	            //Con questo ciclo accorcio i nomi delle fermate per mostrare i nomi generali
	            for ( Fermate f: allStops){
		            String[] parts = f.getStopDesc().split("SX|DX|\"|\\(");
		            all.add(parts[0]);
//	            	all.add(f.getStopDesc());	
	            }
	            //Tolgo i duplicati e passando questo array alla listview abbiamo la lista generale di fermate
	            HashSet<String> listToSet = new HashSet<String>(all);
	            listWithoutDuplicates = new ArrayList<String>(listToSet);
	            
	            Collections.sort(listWithoutDuplicates);
   
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>( mactivity, 
		        		android.R.layout.simple_list_item_1, listWithoutDuplicates );
		        /** Setting the list adapter for the ListFragment */
		        setListAdapter(adapter);
		        db.close();
			}
	    	
	    }
	    
		
	    /*Questo metodo provvede a disegnare sulla pagina principale una nuova lista con le 
	     * fermate, inoltre, fornisce al nuovo fragment la stringa che l'utente ha cliccato
	     * */
		  @Override
		 public void onListItemClick(ListView l, View v, int position, long id) {
			  	super.onListItemClick(l, v, position, id);
			  	
			  	//Mi faccio dare i parametri recuperati dalla lista di linee e dalla fermata selezionata
			  	//Diventeranno il titolo e sottotitolo della schermata dei dettagli della fermata
				SharedPreferences settings = mactivity.getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();
			    
			    if ( listWithoutDuplicates != null ){
			    	//Gestisco il caso di san marco perchè le scritte sono diverse nel db
			    	if ( listWithoutDuplicates.get(position).equals("S.Marco ") || listWithoutDuplicates.get(position).equals("S. Marco ") )
			    		editor.putString(STOP_NAME, "marco");
			    	else
			    		editor.putString(STOP_NAME, listWithoutDuplicates.get(position));
			    }else{
				    String[] parts = spo.get(position).stopDesc.split("SX|DX|\"|\\(");
				    editor.putString(STOP_NAME, parts[0]);
			    }
			    
			    
			    if ( !lineSelected.equals("No line selected") ){

				  	Intent intent = new Intent(getActivity(), DetailStopActivity.class);
				    startActivity(intent);
			    	
			    }else{
			    	editor.putString(LINE_NUMBER, "No line selected");

			        LineList lineListfragment = new LineList();
			        // Sostituisco la prima pagina con la lista delle linee
			        FragmentManager fragmentManager = getFragmentManager();
			        fragmentManager.beginTransaction()
			        			   .addToBackStack("StopList")
			        			   .replace(R.id.content_frame, lineListfragment)
			        			   .commit(); 
			    }
			    
			    editor.commit();

		  }


		@Override
		public void onTaskFinished(List<Passage> response,
				List<StopsDesc> answere) {
			
			String firstRunId = "";
			spo = new ArrayList<StopsDesc>();
            for ( StopsDesc s: answere){
            	if ( s.alisLine.equals(lineSelected) ){
            		if ( firstRunId.equals("") )
            			firstRunId = s.runid;
            		if ( s.runid.equals(firstRunId) ) 
		            	spo.add(s);
            	}
            }
			             
            setListAdapter( new RowStopAdapter(mactivity, spo, stopsByLine ));
			
		}
		
		//Questo metodo gestisce la presenza del pulsante per il cambio dell'orario
		//Viene attivato quanto viene settato un OptionMenyu con setHasOptionsMenu solo quando si visualizza una lista 
		//delle fermate di una linea selezionata
		@Override
		public void onPrepareOptionsMenu(Menu menu) {
		    MenuItem item= menu.findItem(R.id.action_changetime);
		    item.setVisible(true);
		    super.onPrepareOptionsMenu(menu);
		}
				
		//Classe interna per la creazione di un Dialog per la selezione dell'orario
		public static class changeTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
			
			MainActivity parent;

		    @Override
		    public void onAttach(Activity activity) {
		            super.onAttach(activity);
		            parent = (MainActivity) activity;
		    }
			
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
//				// Use the current time as the default values for the picker
				final Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);

				//Ho dovuto aggiungere questo codice perchè ce un bug nel codice di Google e onTimeSet veniva chiamata
				// due volte, mentre così ho scritto direttamente cosa deve fare ogni pulsante del dialog.
				
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
		        
//	            String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + stopsByLine.get(2).getStopId() + 
//						"&requestedDateTime=" + format;
//
//	            
//	            //Richiamo il fragment dopo aver modificato l'orario però a mio avviso questo è un comando rischioso
//	            //Mi faccio dare il fragment che è visualizzato in content_frame e supponiamo sia un StopsList
//	            DownloadXmlgetRunRoutesbyStopID answere = 
//	            		new DownloadXmlgetRunRoutesbyStopID( (StopsList) parent.getSupportFragmentManager().findFragmentById(R.id.content_frame) );
//	            answere.execute(URL);
		        
		        Random rand = new Random();
		        String randomStopId = stopsByLine.get( rand.nextInt(stopsByLine.size())).getStopId();
		        Log.e("StopId a caso: ", randomStopId  );
		        
		        	        
		        ConnectivityManager connMgr = (ConnectivityManager) 
		        parent.getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
		            
		            String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + randomStopId + 
							"&requestedDateTime=" + format;

		            DownloadXmlgetRunRoutesbyStopID answere = 
		            		new DownloadXmlgetRunRoutesbyStopID( (StopsList) parent.getSupportFragmentManager().findFragmentById(R.id.content_frame) );
		            answere.execute(URL);
		        } else {
		        	DialogFragment newFragment = new FireMissilesDialogFragment();			
				    newFragment.show( parent.getSupportFragmentManager(), "No connection Alert");
		        }
		        
		        
	             
			}
	}
		
		//Dialog che serve per avvertire l'utente che manca la connessione ad internet
		public static class FireMissilesDialogFragment extends DialogFragment {
			
			MainActivity parent;

		    @Override
		    public void onAttach(Activity activity) {
		            super.onAttach(activity);
		            parent = (MainActivity) activity;
		    }
			
		    @Override
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the Builder class for convenient dialog construction
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage("No Connection!")
		               .setPositiveButton( "Reload", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
//		       		        ConnectivityManager connMgr = (ConnectivityManager) 
//		       				        parent.getSystemService(Context.CONNECTIVITY_SERVICE);
//		       				        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//		       				        if (networkInfo != null && networkInfo.isConnected()) {
//		       					        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
//		       					        String format = s.format(new Date());
//		       				            
//		       				            String URL = "http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + stopsByLine.get(1).getStopId() + 
//		       									"&requestedDateTime=" + format;
//
//		       				            DownloadXmlgetRunRoutesbyStopID answere = new DownloadXmlgetRunRoutesbyStopID( parent );
//		       				            answere.execute(URL);
//		       				        } else {
//		       				        	DialogFragment newFragment = new FireMissilesDialogFragment();			
//		       						    newFragment.show( parent.getSupportFragmentManager(), "No connection Alert");
////		       				        	Toast.makeText( mactivity, "Non c'è la connessione!", Toast.LENGTH_SHORT).show();
//		       				        }
		                	   
		       		        StopsList stoplistFragment = new StopsList();
		       		        
		       		        FragmentManager fragmentManager = parent.getSupportFragmentManager();
		       		        
		       		        if (parent.getSupportFragmentManager().findFragmentById(R.id.content_frame) != null ){
		       			        fragmentManager.beginTransaction()
		       									.remove(parent.getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
		       		        }
		    		        
		    		        fragmentManager.beginTransaction()
		    		        			   .replace(R.id.content_frame, stoplistFragment)
		    		        			   .addToBackStack("LinesList")
		    		        			   .commit();

		                	   
		                   }
		               })
		               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       // User cancelled the dialog
		                   }
		               });
		        // Create the AlertDialog object and return it
		        return builder.create();
		    }
		}
		

}
