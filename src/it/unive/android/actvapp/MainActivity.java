/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unive.android.actvapp;

import it.unive.android.actvapp.DetailStopActivity.TimePickerFragment;
import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.network.CreateUpdateDB;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.example.android.navigationdrawerexample.R;
  
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager.OnDismissListener;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.Toast;   
 
/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/> 
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends FragmentActivity {
	  
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
    //Lista dei titoli delle attività della app (Fermate, Linee, Mappa, Ricerca, Info, ecc... )
    private String[] mFunctionTitles;
    
    //Preferenze nulle all'avvio della app
    public static final String PREFS_NAME = "MyPrefs";
//    public static final boolean firstTime = true;
    public static final String LINE_NUMBER = "LINE";
    public static final String LINE_ICON = "icon";
    
    public static final String STOP_NAME = "STOP";
       
    
    //URL
    private static final String URL =
            "http://10.128.20.21:8080/avmwebservice/AVMWebService.asmx/getStopLineTimeTable?aliasLine=1&stopID=5027&requestedDateTime=20131024110354&nPassages=100";
	private static final String URLSTOP =
            "http://10.128.20.21:8080/avmwebservice/AVMWebService.asmx/getNearStops?X_UTM=5035106&Y_UTM=290242&range=2100000000";
	private static final String URLRUNROUTE = 
			"http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=5011&requestedDateTime=20131107232500";
	    
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
        
        //Controllo se è il primo avvio dell'app e che la app non era già installato o reinstallata
        //Se si creo e aggiorno il database
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean first = settings.getBoolean("firstTime", false);
        Log.d( "First time:", ""+ first );
        if( !first ) {
        	Log.d( "PRIMO AVVIO", ""+ first );
        	Log.d( "First time:", ""+ first );
        	DataBase db = new DataBase(this);    
        	try {
				db.createDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	  
        	db.close();
//    		CreateUpdateDB xml = new CreateUpdateDB(this);
//    		xml.execute( URLRUNROUTE, URLSTOP );
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putBoolean("firstTime", true);
	        editor.commit();
        } 
          
//		CreateUpdateDB xml = new CreateUpdateDB(this);
//		xml.execute( URLRUNROUTE, URLSTOP );

         
        //Creo il database
//		DownloadXmlStop xml = new DownloadXmlStop(this);
//		xml.execute( URLRUNROUTE, URLSTOP );	
        
//		CreateUpdateDB xml = new CreateUpdateDB(this);
//		xml.execute( URLRUNROUTE, URLSTOP );
        
	    SharedPreferences.Editor editor = settings.edit();
    	editor.putString(LINE_NUMBER, "No line selected");
    	editor.putInt(LINE_ICON, 0);
    	editor.putString(STOP_NAME, "No Stop request");
    	editor.commit();
        
 
        mTitle = mDrawerTitle = getTitle();
        mFunctionTitles = getResources().getStringArray(R.array.function_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mFunctionTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                int lineIconSelected = prefs.getInt(LINE_ICON, 0);
                getActionBar().setTitle(mTitle);
                
                if ( lineIconSelected == 0 )
                	getActionBar().setIcon(R.drawable.ic_launcher);
                else
                	getActionBar().setIcon(lineIconSelected);
                
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setIcon(R.drawable.ic_launcher);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        }; 
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Con questo if viene impostato come prima vista l'elemento in posizione 0
        // Nella nostra implementazione futura vorremmo avere la mappa come primo elemento
        if (savedInstanceState == null) {
            selectItem(0);
        	mDrawerLayout.openDrawer(mDrawerList);
        	
        }
    }    
       
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          String query = intent.getStringExtra(SearchManager.QUERY);
          
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString(LINE_NUMBER, "No line selected");
        	editor.putString(STOP_NAME, query);
        	editor.commit();
          
	        LineList lineListfragment = new LineList();
	        // Sostituisco la prima pagina con la lista delle linee
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        
	        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) != null ){
		        fragmentManager.beginTransaction()
								.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
	        }
	        
	        fragmentManager.beginTransaction()
	        			   .replace(R.id.content_frame, lineListfragment)
	        			   .commit();  
        }  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        //Abbiamo aggiunto la azione nel actionbar per cambiare l'orario 
        //quando non viene visualizzato l'ordine delle fermate di una certa linea
        
        //Qui non lo facciamo vedere perchè non siamo nelle fermate di una certa linea
        menu.findItem(R.id.action_changetime).setVisible(false);
        
        return super.onCreateOptionsMenu(menu);
    }  

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//    	menu.findItem(R.id.action_changetime).setVisible(false);
    	
    	
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_dbsearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_dbsearch:
       	
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                   (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) item.getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));  
            
            return true;
            
        case R.id.action_changetime:
        	//In questo caso faccio apparire la selezione dell'orario per quando non viene visualizzata la lista di 
        	//fermate di una certa linea
        	DialogFragment newFragment = new StopsList.changeTimePickerFragment();			
		    newFragment.show( getSupportFragmentManager(), "timePicker");
        	
        	return true;
        	
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
 
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
   
    private void selectItem(int position) {
    	
    	// Con questo metodo gestisco il click sulla barra laterale e dovrei disegnare i relativi fragment
  	
        // update the main content by replacing fragments
    	// il contenuto principale è identificato dalla costante R.id.content_frame
    	
    	if (position == 0){
    		// Faccio vedere in prima pagina la lista delle linea
    		
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
        	editor.putString(STOP_NAME, "No Stop request");
        	editor.commit();
    		
    		// Creo la lista delle linee
	        LineList lineListfragment = new LineList();
	        // Sostituisco la prima pagina con la lista delle linee
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        
	        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) != null ){
		        fragmentManager.beginTransaction()
								.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
	        }
	        fragmentManager.beginTransaction()
	        			   .replace(R.id.content_frame, lineListfragment)
	        			   .commit();      
	        
    	}else if (position == 1){
    		// Faccio vedere la lista di tutte le fermate 
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
        	editor.putString(LINE_NUMBER, "No line selected");
        	editor.putInt(LINE_ICON, 0);
        	editor.commit();
    		// Creo la lista delle fermate
	        StopsList stopsListfragment = new StopsList();	
	        // Sostituisco la prima pagina con la lista delle fermate
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        
	        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) != null ){
		        fragmentManager.beginTransaction()
								.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
	        }
	        fragmentManager.beginTransaction()
	        			   .replace(R.id.content_frame, stopsListfragment)
	        			   .commit();
	         
    	}else if (position == 2){
    		// chiamata a mappa
    		DisplayMap mappa = new DisplayMap();
    		FragmentManager fragmentManager = getSupportFragmentManager();
    		
	        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) != null ){
		        fragmentManager.beginTransaction()
								.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
	        }
    						
    		getSupportFragmentManager().beginTransaction()
    						.replace(R.id.content_frame, mappa)
    						.commit();
    		

    	}
    	// Qui ci andrebbero gli altri casi per disegnare gli altri componenti della app

        // update selected item and title, then close the drawer 
        mDrawerList.setItemChecked(position, true);
        setTitle(mFunctionTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    
	@Override
	 public void onBackPressed(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString(LINE_NUMBER, "No line selected");
	    editor.putInt(LINE_ICON, 0);
	    editor.putString(STOP_NAME, "No Stop request");
	    getActionBar().setIcon(R.drawable.ic_launcher);
//      selectItem(0);
//    	mDrawerLayout.openDrawer(mDrawerList);
	    FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
						.remove(getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
    	
    	
	   	editor.commit();    	
	   	
	   	super.onBackPressed();
	 }
	

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    //Fine MainActivity
}