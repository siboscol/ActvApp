package it.unive.android.actvapp;

import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.Linea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.android.navigationdrawerexample.R;

public class LineListDialogFragment extends DialogFragment {

		DetailStopActivity mactivity;
	    private List<Linea> linee = new ArrayList<Linea>();
	    public static final String PREFS_NAME = "MyPrefs";
	    public static final String LINE_NUMBER = "LINE";
	    public static final String LINE_ICON = "icon";
	    public static final String STOP_NAME = "STOP";
	    
	    private List<Fermate> stops;
	    private static ArrayList<String> idStops;
	    private String stopIdSelected;
	    private DataBase db;
	    
	    
	    @Override
	    public void onAttach(Activity activity) {
	            super.onAttach(activity);
	            mactivity = (DetailStopActivity) activity;
	          
	    }
	      
	      
	    @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	    	
	    	View v = inflater.inflate(R.layout.linelist_dialogfragment_layout, null);
	    	
	    	ListView changeLine = ( ListView ) v.findViewById(R.id.changeline);
	    	
	    	SharedPreferences prefs = mactivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	    	stopIdSelected = prefs.getString(STOP_NAME, "No Stop request"); 
	 
	    	getDialog().setTitle("Linee");
	        
	        db = new DataBase(mactivity);
	        //Ce da cercare di capire dove e quando andare a creare il db e poi avere un riferimento ad esso in modo tale da interrogarlo.
	        
	        if ( !stopIdSelected.equals("No Stop request") ){
//	        	linee = db.getChangesLinesbyStopId(stopIdSelected);
	    		//Recupero la lista intera di fermate e seleziono gli stopId
	    		//che mi servono per mostrare le linee che passano per quelle fermate
	            stops = db.getAllStopsLower();
	            
	            idStops = new ArrayList<String>();
	            for ( Fermate f: stops){
//	            	if ( f.getStopDesc().indexOf(stopIdSelected.toLowerCase()) != -1 )
	            	if( f.getStopDesc().toLowerCase().replaceAll(" ", "").contains(stopIdSelected.toLowerCase().replaceAll(" ", "") ) )             	
	            		idStops.add( f.getStopId() );
	            }
//	            idStops = db.searchStopIdByStopDesc(stopIdSelected);

	            for ( String id: idStops ){
	            	linee.addAll( db.getChangesLinesbyStopId(id) );		
	            }
	            
	            HashSet<Linea> listToSet = new HashSet<Linea>(linee);
	            linee = new ArrayList<Linea>(listToSet);
	            
	            if ( linee.size() == 0 ){
	    			SharedPreferences settings = mactivity.getSharedPreferences(PREFS_NAME, 0);
	    		    SharedPreferences.Editor editor = settings.edit();   
	            	editor.putString(STOP_NAME, "No Stop request");
	            	editor.commit();
	            }
	                    	
	        	
	        }else
	        	linee = db.getAllLinesWithIcon();
	        
	        
	        //Ordino la visualizzazione delle linee 
	        Collections.sort(linee, new Comparator<Linea>() {

	        	//Con questo compare riesco ad ordinare parole e double
				@Override
				public int compare(Linea lhs, Linea rhs) {

			        try {
			            double d1 = Double.parseDouble(lhs.getLinesId());
			            double d2 = Double.parseDouble(rhs.getLinesId());
			            
			            return Double.compare(d1, d2);
			            
			        } catch (NumberFormatException e) {
			        	return lhs.getLinesId().compareTo(rhs.getLinesId());
			        }
		        }
				
				
			});
	        
	        changeLine.setAdapter( new RowLineAdapter(mactivity, linee) );
	        
	        
		    /*Questo metodo provvede a disegnare sulla pagina principale una nuova lista con le 
		     * fermate, inoltre, fornisce al nuovo fragment la stringa che l'utente ha cliccato
		     * */
	        changeLine.setOnItemClickListener( new OnItemClickListener() {
	        	
	        	
	        	//Essendo questo metodo lo stesso della classe LineList, presenta lo stesso problema menzionato li

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
				  	//Salvo la preferenza dell'utente ovvero la linea e la sua icona e la tengo per tutto il ciclo della app
					SharedPreferences settings = mactivity.getSharedPreferences(PREFS_NAME, 0);
				    SharedPreferences.Editor editor = settings.edit();   
		        	editor.putString(LINE_NUMBER, linee.get(arg2).getLinesId());
		        	editor.putInt(LINE_ICON, linee.get(arg2).getIcon());
				    
				    
				    if ( !stopIdSelected.equals("No Stop request") ){
//				    	stops = db.getAllGoStopsByLine( linee.get(arg2).getLinesId() );
//				    	for ( String id1: idStops ){
//				    		String idDesc = db.getStopDesc(id1);	
//				    	}
//				    	//Non è stato trovato nessun risultato sulla ricerca
//				    	if (linee.size() != 0 )
//				    		editor.putString(STOP_NAME, stopIdSelected);
//				    	editor.commit();
				    	Log.e("Stop name: ", stopIdSelected  );
			    		if ( stopIdSelected.equals("S.Marco ") || stopIdSelected.equals("S. Marco "))			    			
			    			stopIdSelected = "marco";

				    	String apici = stopIdSelected.replaceAll("'", "''");
				    	
				    	List<Fermate> stopsBylineAndStopName = db.getAllStopsByLineAndName(linee.get(arg2).getLinesId(), apici );
				    	
			    		String[] parts = stopsBylineAndStopName.get(0).getStopDesc().split("SX|DX|\"|\\(");
			    		
			    		editor.putString(STOP_NAME, parts[0] );
			    		editor.commit();
				    	
				    	getDialog().dismiss();
					  	Intent intent = new Intent(mactivity, DetailStopActivity.class);
				    	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					    startActivity(intent);	    	

				    }

				    
				    db.close();
					
				}
			});
	        
	        
	     // Inflate the layout for this fragment
	        return v;
	 		
		}

		
}
