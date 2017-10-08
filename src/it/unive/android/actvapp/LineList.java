package it.unive.android.actvapp;


import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.network.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
  
import org.xmlpull.v1.XmlPullParserException;

import com.example.android.navigationdrawerexample.R;

import android.app.Activity;
import android.app.SearchManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class LineList extends ListFragment {

	MainActivity mactivity;
    private List<Linea> linee = new ArrayList<Linea>();
    public static final String PREFS_NAME = "MyPrefs";
    public static final String LINE_NUMBER = "LINE";
    public static final String LINE_ICON = "icon";
    public static final String STOP_NAME = "STOP";
    
    private String stopIdSelected;
    private DataBase db;
    
    @Override
    public void onAttach(Activity activity) {
            super.onAttach(activity);
            mactivity = (MainActivity) activity;    
    }
         
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {    
     // Inflate the layout for this fragment
        return inflater.inflate(R.layout.linelist_layout, container, false);		
	} 
    
    @Override
    public void onResume(){
    	super.onResume();
    	
       	SharedPreferences prefs = mactivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    	stopIdSelected = prefs.getString(STOP_NAME, "No Stop request"); 
 
        mactivity.setTitle("Linee");
        mactivity.getActionBar().setIcon(R.drawable.ic_launcher);
        
        db = new DataBase(mactivity);
        //Ce da cercare di capire dove e quando andare a creare il db e poi avere un riferimento ad esso in modo tale da interrogarlo.
        
        if ( !stopIdSelected.equals("No Stop request") ){
        	
//        	Ho aggiunto un pò di casi particolari che capitano spesso
        	String[] santo = stopIdSelected.split(" ");
//        	if ( santo[0].toLowerCase().equals("san") || santo[0].toLowerCase().equals("santa") || santo[0].toLowerCase().equals("santo"))
//        		stopIdSelected = santo[1];
        	if ( stopIdSelected.toLowerCase().contains("sant'") ){
        		String[] sant = stopIdSelected.split("'");
        		stopIdSelected = sant[1];
        	}
      	
//    		if ( stopIdSelected.equals("s. marco ") || stopIdSelected.equals("s.marco ") || 
//    				stopIdSelected.equals("san marco") || stopIdSelected.equals("s. marco") || stopIdSelected.equals("s.marco"))
//    			stopIdSelected = "marco";

        	
        	//Ho aggiunto maggiore probabilità di trovare risultati
        	
        	//Aggiungo gli apici per poter fare le query
        	String apici = stopIdSelected.replaceAll("'", "''");
        	
        	//Sostituisco i punti con spazi per raccogliere più risultati possibile
        	String punti = apici.replaceAll("\\.", " ");
        	 
        	String[] parole = punti.split(" ");
        	
    		linee = db.getChangesLinesbyStopName( apici );

        	if ( linee.size() == 0 ){
        		if ( parole.length >= 2 ){
        			linee = db.getChangesLinesbyStopName( parole[1] );
        			stopIdSelected = parole[1];
        		}			
        	}
	
        	
        }else
        	linee = db.getAllLinesWithIcon();
        
        db.close();
        
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
        
        setListAdapter( new RowLineAdapter(mactivity, linee) );
    	
    }
   
	       
    /*Questo metodo provvede a disegnare sulla pagina principale una nuova lista con le 
     * fermate, inoltre, fornisce al nuovo fragment la stringa che l'utente ha cliccato
     * */
	  @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		  	super.onListItemClick(l, v, position, id);
		  	
		  	//Salvo la preferenza dell'utente ovvero la linea e la sua icona e la tengo per tutto il ciclo della app
			SharedPreferences settings = mactivity.getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();   
        	editor.putString(LINE_NUMBER, linee.get(position).getLinesId());
        	editor.putInt(LINE_ICON, linee.get(position).getIcon());
		    
        	
        	//In questo metodo ce da controllare cosa bisognerebbe fare quando l'utente clicca una linea 
        	//nel elenco, il seguente codice è un pò in confusione anche se fa quello che deve
        	
        	
		    if ( !stopIdSelected.equals("No Stop request") ){
		    	
		    	stopIdSelected.toLowerCase();

	    		if ( stopIdSelected.equals("s. marco ") || stopIdSelected.equals("s.marco ") || 
	    				stopIdSelected.equals("san marco") || stopIdSelected.equals("s. marco") || stopIdSelected.equals("s.marco"))
	    			stopIdSelected = "marco";
	    		
	    		String apici = stopIdSelected.replaceAll("'", "''");
	    		
	    		List<Fermate> stopsBylineAndStopName = db.getAllStopsByLineAndName(linee.get(position).getLinesId(), apici );
	    		
	    		String[] parts = stopsBylineAndStopName.get(0).getStopDesc().split("SX|DX|\"|\\(");
	    		
	    		editor.putString(STOP_NAME, parts[0] );
	    		editor.commit();
		    	
			  	Intent intent = new Intent(getActivity(), DetailStopActivity.class);
			    startActivity(intent);	   
			    
		    }
		    else {
			    editor.commit();
		        StopsList stoplistFragment = new StopsList();
		        
		        
		        FragmentManager fragmentManager = mactivity.getSupportFragmentManager();
		        fragmentManager.beginTransaction()
		        			   .replace(R.id.content_frame, stoplistFragment)
		        			   .addToBackStack("LinesList")
		        			   .commit();
		    }
		    
		    db.close();
					
	  }

	  
}
