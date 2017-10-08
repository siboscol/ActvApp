package it.unive.android.actvapp.database;

import it.unive.android.actvapp.network.XmlParser;
import it.unive.android.actvapp.network.XmlParser.Entry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper{
	
		// Logcat tag
	   private static final String LOG = "Database";

	   // Database Version
	   private static final int DATABASE_VERSION = 1;

	   // Database Name
	   private static final String DATABASE_NAME = "nuovo3";
	   
	   // Database Path
	   private static String DATABASE_PATH = "";
	   private final Context mContext;
	   private static String TAG = "DataBase"; // Tag just for the LogCat window

	   // Table Names
	   private static final String TABLE_STOPS = "Stops";
	   private static final String TABLE_LINES = "Lines";
	   private static final String TABLE_STOPSLINE = "StopsLine";

	   // Fermate Table - column names
	   private static final String KEY_STOPID = "StopId";
	   private static final String KEY_STOPDESC = "StopDesc";

	   // LINES Table - column names
	   private static final String KEY_LINES = "LineId";
	   private static final String KEY_LINEDESC = "LineDesc";  
	   private static final String KEY_LINEICON = "LineIcon"; 
	   
	   // StopsLine Table - column names
	   private static final String KEY_LINE = "LineId"; 
	   private static final String KEY_STOP = "StopId";  
	   private static final String KEY_ORDER = "Ordine";

	   // Table Create Statements
	   // Fermate table create statement
	   private static final String CREATE_TABLE_STOPS = "CREATE TABLE "
	           + TABLE_STOPS + "(" + KEY_STOPID + " INTEGER," + KEY_STOPDESC
	           + " TEXT)";

	   // Linee table create statement
	   private static final String CREATE_TABLE_LINES = "CREATE TABLE " + TABLE_LINES
	           + "(" + KEY_LINES + " TEXT, " + KEY_LINEDESC 
	           + " TEXT, " + KEY_LINEICON + " INTEGER, PRIMARY KEY(" + KEY_LINES + "))";
	   
	   // PAth table create statement
	   private static final String CREATE_TABLE_STOPSLINE = "CREATE TABLE " + TABLE_STOPSLINE
	           + "(" + KEY_LINE + " TEXT REFERENCES " + TABLE_LINES + "(" + KEY_LINES + "), "
	           + KEY_STOP + " TEXT REFERENCES "+ TABLE_STOPS + "(" + KEY_STOPID + "), " 
	           + KEY_ORDER + " INTEGER, " 
	           + "PRIMARY KEY(" + KEY_LINE + ", " + KEY_STOP + "))";
	   
	 public DataBase(Context context) {
	     super(context, DATABASE_NAME, null, DATABASE_VERSION);
	         
	     //Codice aggiunto per copiare il db dalla cartella assets
	     if(android.os.Build.VERSION.SDK_INT >= 17){
	         DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";         
	      }
	      else
	      {
	         DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	      }
	      this.mContext = context;
	 }
	
	 @Override
	 public void onCreate(SQLiteDatabase db) {
	
	     // creating required tables
	     db.execSQL(CREATE_TABLE_STOPS);
	     db.execSQL(CREATE_TABLE_LINES);
	     db.execSQL(CREATE_TABLE_STOPSLINE);
	 }
	
	 @Override
	 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	     // on upgrade drop older tables
	     db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
	     db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
	     db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPSLINE);
	
	     // create new tables
	     onCreate(db);
	 }
	 
	 public void createDataBase() throws IOException
	 {
	     //Se il database non esiste copialo dalla cartella assests
	
	     boolean mDataBaseExist = checkDataBase();
	     if(!mDataBaseExist)
	     {
	         this.getReadableDatabase();
	         this.close();
	         try 
	         {
	             //Copia dalla cartella assests
	             copyDataBase();
	             Log.e(TAG, "createDatabase database created");
	         } 
	         catch (IOException mIOException) 
	         {
	             throw new Error("ErrorCopyingDataBase");
	         }
	     }
	 }
 
	 //Metodi aggiunti per la copia del database dalla cartella assets
     //Check that the database exists here: /data/data/your package/databases/Db Name
     private boolean checkDataBase()
     {
         File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
         //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
         return dbFile.exists();
     }

     //Copy the database from assets
     private void copyDataBase() throws IOException
     {
         InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
         String outFileName = DATABASE_PATH + DATABASE_NAME;
         OutputStream mOutput = new FileOutputStream(outFileName);
         byte[] mBuffer = new byte[1024];
         int mLength;
         while ((mLength = mInput.read(mBuffer))>0)
         {
             mOutput.write(mBuffer, 0, mLength);
         }
         mOutput.flush();
         mOutput.close();
         mInput.close();
     }
 
 
 //CRUD (Create, Read, Update and Delete) Operations
 
 /*
  * Creating a stop
  * Da definire come creare una fermata nel database
  */
 
 
 
 //Da verificare se funziona
 public void createDB(Fermate stop) {
     SQLiteDatabase db = this.getWritableDatabase();
  
//     ContentValues valuesStops = new ContentValues();
//     valuesStops.put(KEY_STOPID, stop.getStopId());
//     valuesStops.put(KEY_STOPDESC, stop.getStopDesc());
     
//     ContentValues valuesLines = new ContentValues();
//     valuesLines.put(KEY_DESC, stop.getAliasRoute());
//     valuesLines.put (KEY_LINES, stop.getAliasLine());
     // insert row
     db.execSQL("INSERT OR REPLACE INTO " + TABLE_STOPS + "(" + KEY_STOPID +", " + KEY_STOPDESC + ") VALUES (?, ?)", 
      		new String[] { stop.getStopId(),stop.getStopDesc() } );
//     db.insert(TABLE_STOPS, null, valuesStops);
     
//     if (stop.getAliasLine() != null){
//     	db.insert(TABLE_LINES, null, valuesLines);
//     }
     db.close();
  
 }
	

 // Updating STOPS row, insede the paramater stop there should be 
 // the stopID we need to update
 //Gestisco il fatto di inserire linee oppure di aggiurnarle ma in ogni caso vedo eccezzioni per inserisco con chiave primaria 
 public int updateDB(Fermate stop) {
 	SQLiteDatabase db = this.getWritableDatabase();

     ContentValues valuesStops = new ContentValues();
     valuesStops.put(KEY_STOPID, stop.getStopId());
     
     if ( stop.getStopDesc() != null )    
     	valuesStops.put(KEY_STOPDESC, stop.getStopDesc());
     
     //Nel aggiornamento ce da dividere l'aggiornamento della tabella lines e stops
     
     
     //Inseriamo le linee se non ci sono o le aggiorniamo
     db.execSQL("INSERT OR REPLACE INTO " + TABLE_LINES + "(" + KEY_LINES +", " + KEY_LINEDESC + ") VALUES (?, ?)", 
     		new String[] { stop.getAliasLine(),stop.getAliasRoute() } );

 	// updating row   
 	return db.update(TABLE_STOPS, valuesStops, KEY_STOPID + " = ? ",
         new String[] { String.valueOf(stop.getStopId()) }); 	
 }
 
 public int updateStopsLine(Fermate stop) {
	 	SQLiteDatabase db = this.getWritableDatabase();

	     ContentValues valuesStops = new ContentValues();
	     valuesStops.put(KEY_STOPID, stop.getStopId());
	     
	     if ( stop.getStopDesc() != null )    
	     	valuesStops.put(KEY_STOPDESC, stop.getStopDesc());
	     
	     //Nel aggiornamento ce da dividere l'aggiornamento della tabella lines e stops
	     
	     
	     //Inseriamo le linee se non ci sono o le aggiorniamo
	     db.execSQL("INSERT OR REPLACE INTO " + TABLE_LINES + "(" + KEY_LINES +", " + KEY_LINEDESC + ") VALUES (?, ?)", 
	     		new String[] { stop.getAliasLine(),stop.getAliasRoute() } );

	   //Inseriamo le linee se non ci sono o le aggiorniamo
	     db.execSQL("INSERT OR REPLACE INTO " + TABLE_STOPSLINE + "(" + KEY_LINE +", " + KEY_STOP + ", " + KEY_ORDER + ") VALUES (?, ?, ?)", 
	     		new String[] { stop.getAliasLine(),stop.getStopId(), stop.getPassageTime() } );
	     
	 	// updating row   
	 	return db.update(TABLE_STOPS, valuesStops, KEY_STOPID + " = ? ",
	         new String[] { String.valueOf(stop.getStopId()) }); 	
	 }
 
 //Non ancora utilizzata, da capire se è più leggera della solita che chiamo
 public void updateStopsLineCheap(Fermate stop) {
	 	SQLiteDatabase db = this.getWritableDatabase();	     
	     
	     //Inseriamo le linee se non ci sono o le aggiorniamo
	     db.execSQL("INSERT OR REPLACE INTO " + TABLE_LINES + "(" + KEY_LINES +", " + KEY_LINEDESC + ") VALUES (?, ?)", 
	     		new String[] { stop.getAliasLine(),stop.getAliasRoute() } );

	   //Inseriamo le linee se non ci sono o le aggiorniamo
	     db.execSQL("INSERT OR REPLACE INTO " + TABLE_STOPSLINE + "(" + KEY_LINE +", " + KEY_STOP + ") VALUES (?, ?)", 
	     		new String[] { stop.getAliasLine(),stop.getStopId() } );
	    
	 }
 
 
 //recuperare gli StopId senza descrizione
 public ArrayList<String> getEmptyRowDB(){
 	ArrayList<String> empties = new ArrayList<String>();
 	String selectQuery = "SELECT " + KEY_STOPID + " FROM " + TABLE_STOPS + " WHERE " + KEY_STOPDESC + " = " + "'vuoto' AND " + KEY_STOPID + "< 6000";
 	
 	SQLiteDatabase db = this.getReadableDatabase();
 	Cursor c = db.rawQuery(selectQuery, null);
 	
 	// looping through all rows and adding to list
     if (c.moveToFirst()) {
         do {
             // adding to StopId to the list
             empties.add( c.getString( (c.getColumnIndex(KEY_STOPID)) ) );
         } while (c.moveToNext());
     }
  
     return empties;
 }
 
 
 //recuperare gli StopId senza descrizione ma contenenti anche quelli fittizi che servono per recuperare tutte le linee
 public ArrayList<String> getAllEmptyRowDB(){
 	ArrayList<String> empties = new ArrayList<String>();
 	String selectQuery = "SELECT " + KEY_STOPID + " FROM " + TABLE_STOPS + " WHERE " + KEY_STOPDESC + " = " + "'vuoto'";
 	
 	SQLiteDatabase db = this.getReadableDatabase();
 	Cursor c = db.rawQuery(selectQuery, null);
 	
 	// looping through all rows and adding to list
     if (c.moveToFirst()) {
         do {
             // adding to StopId to the list
             empties.add( c.getString( (c.getColumnIndex(KEY_STOPID)) ) );
         } while (c.moveToNext());
     }
  
     return empties;
 }
 
 
	    // Getting single stop
	public String getStopDesc(String id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_STOPS, new String[] { KEY_STOPDESC }, KEY_STOPID + "= ?",
	            new String[] { id }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();

	    // return stopDesc
	    return cursor.getString(0);
	}
	
	//correct StopId con una query cerchiamo tutti gli stopd id superiori a 5mila e chiamiamo 
		//il webservice per farci dare la descrizione giusta
		public void correctStopId()
		{
			SQLiteDatabase db= this.getWritableDatabase();
			String selectQuery = "SELECT " + KEY_STOPID + " FROM " + TABLE_STOPS + " WHERE " + KEY_STOPDESC + " = " + "'vuoto'" ;
	    	Cursor c = db.rawQuery(selectQuery, null);
	    	if (c.moveToFirst())
	    	{
	    		do
	    		{
	    			int number=c.getInt( c.getColumnIndex(KEY_STOPID));
	    			 if ((number/10000)!=0)	
	    			 {
//	    				 Log.d ("dentro correctstopId", String.valueOf(number));
	    				 int number1=number%10000;
//	    				 Log.d ("dentro correctstopId", String.valueOf(number1));
	    				
	    				 String selectQuery1 = "SELECT " + KEY_STOPDESC + " FROM " + TABLE_STOPS + " WHERE " + KEY_STOPID + " = " + "'"+number1+"'" ;
	    				 Cursor c1 = db.rawQuery(selectQuery1, null);
	    				 
	    				    if (c1 != null)
	    				        c1.moveToFirst();
	    				     				
	    				 
	    				 ContentValues valuesStops = new ContentValues();
	    			        valuesStops.put(KEY_STOPID, number);
	    			        
	    			        valuesStops.put(KEY_STOPDESC, c1.getString(c1.getColumnIndex(KEY_STOPDESC)));
	    				 db.update(TABLE_STOPS, valuesStops, KEY_STOPID + " = ? ",
	    			    			new String[] { String.valueOf(number) }); 	
//	    				 Log.d("ihja", c1.getString(c1.getColumnIndex(KEY_STOPDESC)));
	    			}
	    		}
	    		while (c.moveToNext());
	    	}
		}

		//Recupero la lista delle linee
	    public List<Linea> getAllLines(){
	        List<Linea> lines = new ArrayList<Linea>();
	        String getLines = "SELECT " +KEY_LINES+ ", " + KEY_LINEDESC + " FROM " + TABLE_LINES + " ORDER BY " + KEY_LINES +", (" + KEY_LINES + "*10)";
	     
	        Log.e(LOG, getLines);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getLines, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Linea l = new Linea();
	                l.setLinesId( c.getString(c.getColumnIndex(KEY_LINES) ));
	                l.setRoute( c.getString(c.getColumnIndex(KEY_LINEDESC) ));

	                // adding to lines list
	                lines.add(l);
	            } while (c.moveToNext());
	        }
	     
	        return lines;
	    }
	    
	    //Recupero tutti i dati delle linee (Linea, Descrizione e icona )
	    public List<Linea> getAllLinesWithIcon(){
	        List<Linea> lines = new ArrayList<Linea>();
	        String getLines = "SELECT * FROM " + TABLE_LINES + " ORDER BY " + KEY_LINE +", (" + KEY_LINE + "*10)";
	     
	        Log.e(LOG, getLines);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getLines, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Linea l = new Linea();
	                l.setLinesId( c.getString(c.getColumnIndex(KEY_LINES) ));
	                l.setRoute( c.getString(c.getColumnIndex(KEY_LINEDESC) ));
	                l.setIcon( c.getInt(c.getColumnIndex(KEY_LINEICON) ));

	                // adding to lines list
	                lines.add(l);
	            } while (c.moveToNext());
	        }
	     
	        return lines;
	    }
	    
	    //Recupero tutte le fermate 
	    public List<Fermate> getAllStops(){
	        List<Fermate> stops = new ArrayList<Fermate>();
	        String getStops = "SELECT * FROM " + TABLE_STOPS + " ORDER BY " + KEY_STOPDESC + " COLLATE NOCASE";
	     
	        Log.e(LOG, getStops);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getStops, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Fermate f = new Fermate();
	                f.setStopId( c.getString(c.getColumnIndex(KEY_STOPID) ));
	                f.setStopDesc( c.getString(c.getColumnIndex(KEY_STOPDESC) ));

	                // adding to lines list
	                stops.add(f);
	            } while (c.moveToNext());
	        }
	     
	        return stops;
	    }
	    
	    //Restituisco le fermate effettuate dalla linea in input
	    public List<Fermate> getAllGoStopsByLine( String line ){
	        List<Fermate> stops = new ArrayList<Fermate>();

	        String getListStops = "SELECT " + "s." + KEY_STOPID + ", " + "s." + KEY_STOPDESC + " FROM " + TABLE_STOPS + " s, " + TABLE_STOPSLINE + " sl WHERE " +
	        "sl." + KEY_LINE + " = " + "'"+line+"'" + " AND " + "sl."+ KEY_STOP + " = " + "s."+ KEY_STOPID + " ORDER BY s." + KEY_STOPDESC + " COLLATE NOCASE";
	     
	        Log.e(LOG, getListStops);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getListStops, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Fermate f = new Fermate();
	                f.setStopDesc( c.getString(c.getColumnIndex(KEY_STOPDESC) ));
	                f.setStopId( c.getString(c.getColumnIndex(KEY_STOPID) ));

	                // adding to lines list
	                stops.add(f);
	            } while (c.moveToNext());
	        }
	        
	      
	        return stops;
	    }
	    
	    
	    //Restituisco le fermate effettuate dalla linea in input con il nome della fermata selezionata
	    public List<Fermate> getAllStopsByLineAndName( String line, String stopName ){
	        List<Fermate> stops = new ArrayList<Fermate>();

	        String getListStops = "SELECT " + "s." + KEY_STOPID + ", " + "s." + KEY_STOPDESC + " FROM " + TABLE_STOPS + " s, " + TABLE_STOPSLINE +
	        " sl WHERE " + "sl." + KEY_LINE + " = " + "'"+line+"'" + " AND " + "sl."+ KEY_STOP + " = " + "s."+ KEY_STOPID + " AND "
	        		+ "s." + KEY_STOPDESC + " LIKE " + "'%"+ stopName + "%'";
	     
	        Log.e(LOG, getListStops);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getListStops, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Fermate f = new Fermate();
	                f.setStopDesc( c.getString(c.getColumnIndex(KEY_STOPDESC) ));
	                f.setStopId( c.getString(c.getColumnIndex(KEY_STOPID) ));

	                // adding to lines list
	                stops.add(f);
	            } while (c.moveToNext());
	        }
	        
	      
	        return stops;
	    }
    
	    //Mi faccio dare tutti i cambi per fermata
	    public List<Linea> getChangesLinesbyStopId( String stopId ){
	        List<Linea> lines = new ArrayList<Linea>();
	        String getChanges = "SELECT l.* FROM " + TABLE_STOPSLINE + " sl, " + TABLE_LINES + " l WHERE " + "sl."+KEY_STOP +" = '" + stopId + "'" +
	        		" AND sl." +KEY_LINE + " = l." + KEY_LINES;
	     
	        Log.e(LOG, getChanges);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getChanges, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Linea l = new Linea();
	                l.setLinesId( c.getString(c.getColumnIndex(KEY_LINES) ));
	                l.setIcon( Integer.parseInt( c.getString(c.getColumnIndex(KEY_LINEICON) ) ));
	                l.setRoute(c.getString(c.getColumnIndex(KEY_LINEDESC) ));

	                // adding to lines list
	                lines.add(l);
	            } while (c.moveToNext());
	        }
	     
	        return lines;
	    }
	    
	    //Mi faccio dare tutti i cambi per nome fermata
	    public List<Linea> getChangesLinesbyStopName( String stopName ){
	        List<Linea> lines = new ArrayList<Linea>();

	        String getChanges = "SELECT DISTINCT l.* FROM " + TABLE_STOPSLINE + " sl, " + TABLE_LINES + " l, " + TABLE_STOPS + " s " +
	        "WHERE " + "s." + KEY_STOPDESC + " LIKE " + "'%" + stopName + "%'" + " AND " +
	        		"s." + KEY_STOPID + " = " + "sl."+ KEY_STOP + " AND " +
	        		"sl."+ KEY_LINE + " = " + "l." + KEY_LINES;
	     
	        Log.e(LOG, getChanges);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getChanges, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Linea l = new Linea();
	                l.setLinesId( c.getString(c.getColumnIndex(KEY_LINES) ));
	                l.setIcon( Integer.parseInt( c.getString(c.getColumnIndex(KEY_LINEICON) ) ));
	                l.setRoute(c.getString(c.getColumnIndex(KEY_LINEDESC) ));

	                // adding to lines list
	                lines.add(l);
	            } while (c.moveToNext());
	        }
	     
	        return lines;
	    }
	    
	    //Inserisco icona della linea 
	    public int updateIconLine( String line, int icon ) {
		 	SQLiteDatabase db = this.getWritableDatabase();

		     ContentValues valuesStops = new ContentValues();
		     valuesStops.put(KEY_LINEICON, icon);

		 	// updating row   
		 	return db.update(TABLE_LINES, valuesStops, KEY_LINES + " = ? ",
		         new String[] { line }); 	
		 }
	    
	    //Aggiorno nel db la descrizione della linea presa dal xml dato da Bruson
	    public void updateLineDesc( List<Entry> desc ) {
		 	SQLiteDatabase db = this.getWritableDatabase();
		     ContentValues valuesLines = new ContentValues();
		     
		     
		 	List<Linea> lines = getAllLines();
		 	
		 	for ( Linea l: lines ){
		 		for ( Entry e: desc ){

		 			Log.d("Linea: ", l.getLinesId());
		 			Log.d("Desc Linea: ", e.NomeLinea );
		 			Log.d("Descrizione: ", e.DescrizioneLinea );
		 			
		 			if ( e.NomeLinea.equals( l.getLinesId() ) ){
		 				valuesLines.put(KEY_LINEDESC, e.DescrizioneLinea);
		 				int i = db.update(TABLE_LINES, valuesLines, KEY_LINES + " = ? ",
		 				         new String[] { l.getLinesId() }); 
		 			}
		 		}   
		 	}
		 }
	    
	    public int updateOrderStopsLine(Fermate stop, int i, String line) {
		 	SQLiteDatabase db = this.getWritableDatabase();

		     ContentValues valuesStops = new ContentValues();
		     valuesStops.put(KEY_ORDER, i);
     
		 	// updating row   
		 	return db.update(TABLE_STOPSLINE, valuesStops, KEY_STOP + " = ? AND " + KEY_LINE + " = ?",
		         new String[] { String.valueOf(stop.getStopId()), line }); 	
		 }
	    
	    public ArrayList<String> searchStopIdByStopDesc ( String StopDesc ){
	    	ArrayList<String> Ids = new ArrayList<String>();
		    SQLiteDatabase db = this.getReadableDatabase();
			 
		    Cursor cursor = db.query(TABLE_STOPS, null, KEY_STOPDESC + " LIKE ?",
		            new String[] { "%"+StopDesc+"%" }, null, null, null, null);
		    
		    if (cursor != null)
		        cursor.moveToFirst();

	        if (cursor.moveToFirst()) {
	            do {
	                Ids.add( cursor.getString(cursor.getColumnIndex(KEY_STOPID) ));
	            } while (cursor.moveToNext());
	    	
	        }
	    
	        return Ids;
	    }
	    
	    //Recupero tutte le fermate 
	    public List<Fermate> getAllStopsLower(){
	        List<Fermate> stops = new ArrayList<Fermate>();
	        String getStops = "SELECT LOWER("+ KEY_STOPDESC +"), "+ KEY_STOPID +" FROM " + TABLE_STOPS + " ORDER BY " + KEY_STOPDESC;
	     
	        Log.e(LOG, getStops);
	     
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(getStops, null);
	     
	        // looping through all rows and adding to list
	        if (c.moveToFirst()) {
	            do {
	                Fermate f = new Fermate();
	                f.setStopId( c.getString(c.getColumnIndex(KEY_STOPID) ));
	                f.setStopDesc( c.getString(c.getColumnIndex( "LOWER("+ KEY_STOPDESC +")" ) ));

	                // adding to lines list
	                stops.add(f);
	            } while (c.moveToNext());
	        }
	     
	        return stops;
	    }
	    
	    

	
}
