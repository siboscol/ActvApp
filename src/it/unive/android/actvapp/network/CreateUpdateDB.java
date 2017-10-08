package it.unive.android.actvapp.network;

import it.unive.android.actvapp.LineList;
import it.unive.android.actvapp.MainActivity;
import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.database.XmlParserGetNearStops;
import it.unive.android.actvapp.database.XmlParserGetNextPassages;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID;
import it.unive.android.actvapp.database.XmlParserGetNearStops.Stops;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;
import it.unive.android.actvapp.network.XmlParser.Entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.android.navigationdrawerexample.R;

public class CreateUpdateDB extends AsyncTask<String, Void, String>{

	
	MainActivity activity;
	ProgressDialog progress;
	public ArrayList<String> stopid = new ArrayList<String>();
	public ArrayList<String> time = new ArrayList<String>();
	public List<StopsDesc> desc;
	public List<Passage> passages;

	//Questo riferimento sarà il db di tutta la app
	DataBase bd; 

	public CreateUpdateDB( MainActivity a ) {
		progress = new ProgressDialog( a );
		activity = a;

	}
	
    @Override
    protected void onPreExecute() { 
        progress.setMessage("Loading db");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();    
    }
	
    @Override
    protected String doInBackground(String... urls) {
            try {
            	String temp1 = loadXmlGetNearStopFromNetwork(urls[1]);
            	String temp2 = loadXmlStopDescFromNetwork(urls[0]);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	
        	Log.d("Entrato in ", "doInBackground");
        	
//        	if (doesDatabaseExist(activity, "nuovo3")){
//        		//Il database c'è allora lo aggiorno
//        		bd = new DataBase(activity);
//        		
//        		if ( desc != null && desc.size() != 0 ){
//	            	for (StopsDesc cnt: desc ){
//	            		bd.updateDB( new Fermate(cnt.stopID, cnt.stopDesc, cnt.alisLine, cnt.lastStopName,
//	            					cnt.aliasRoute, cnt.passageTime));         		   	
//	            	}
//        		}
//        	}else{
        		
        		Log.d("Entrato in ", "Non Ce il DB" );
        		
        		//Il database non c'è allora creo e aggiorno
        		bd = new DataBase(activity);
            	for (String cnt: stopid ){
            		Log.d("Entrato in ", "Ciclo Stopid" );
            		
            		int stopidaux=Integer.parseInt(cnt);
            		
            		//Filtro che mi limita il numero di stopId a 5xxx, togliendolo mi tengo anche le fermate doppioni,
            		//ovvero quelle a cui viene aggiunto il 15xxx o 25xxx ecc...
//            		if (stopidaux/10000==0) {
            			bd.createDB( new Fermate(cnt, "vuoto", null, null,null, null));
//            		}
            	}   
            	for (StopsDesc cnt: desc ){
            			bd.updateDB( new Fermate(cnt.stopID, cnt.stopDesc, cnt.alisLine, cnt.lastStopName,
            					cnt.aliasRoute, cnt.passageTime));            		
//            		bd.updateDB( new Fermate(Integer.parseInt(cnt.stopID), cnt.stopDesc, cnt.alisLine, cnt.lastStopName ,cnt.aliasRoute, cnt.passageTime));
            	}
//        	}//else di se esiste il db
        	   
        	Log.d("Finito di ", "Aggiornare DB" );
        	
        	
        	//Come faccio a vedere se la query funziona senza beccarmi le eccezioni dell' updateDB?
        	//Test per vedere se mi mostra nei log la query fatta al database per recuperare gli StopId senza descrizione
        	ArrayList<String> verificati = new ArrayList<String>();
        	
        	ArrayList<String> vuoti = bd.getEmptyRowDB();
        	
        	ArrayList<String> vuotitotali = bd.getAllEmptyRowDB();
        	
        	Log.d( "Totale vuoti:", ""+ vuoti.size() );
        	
    		String[] ore = new String[] { "00", "01","02","03","04","05","06","07","08","09","10",
    		        "11", "12","13","14","15","16","17","18","19","20","21","22","23"};
    		    String[] minuti = new String[] { "00", "05","10","15", "20","25","30","35", "40","45", "50", "55" };
    		    int o = 7;
    		    int m = 0;
    		    
        	
        	//In questo while si fa il vero e proprio aggiornamento del database, per ogni StopId di cui non abbiamo il 
        	//StopDesc fa una chiamata al server mettendo degli orari a caso per fare in modo di beccare quando 
        	//quella fermata viene effettuata e di conseguenza sapere avere i dati che ci servono
    
    		    //Questo while serve per far si che siamo sicuri che abbiamo nel database tutti gli StopDesc degli StopId > 6000
    		    while ( vuoti.size() > 0 ){
    		    	
    		    	//Questo for serve per far fare le chiamate al webservice con tutti gli StopId che abbiamo per essere sicuri di recuperare tutte le linee
	        		for ( int i = 0; i < vuotitotali.size(); i++){
		        		
		        		Log.d( "Totale vuoti con doppi cambio ID:", ""+ vuotitotali.size() ); 
		        		Log.d( "Totale vuoti cambio ID:", ""+ vuoti.size() ); 
		        		if ( m >= 11 ){
		        			m = 0;
		        		}
		        		
		        		if ( o >= 24){
		        			o = 3;      			
		        		}      
		        		
		        		
		        		
		        		try {	
	
		        			//Per avere tutte le linee
							String temp343 = loadXmlNextPassagesFromNetwork("http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
									"stopID="+ vuotitotali.get(i) + "&requestedDateTime=20131111" + ore[o] + minuti[m]+ "00&passagesNumber=250");
							
							//Mi salvo in un array i stopId che ho già esaminato con getNextPassages
							if ( !verificati.contains(vuotitotali.get(i)) )
								verificati.add(vuotitotali.get(i));
		        			
		        			//Per avere il maggior numero di fermate
								String temp = loadXmlStopDescFromNetwork(
		    						"http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + vuotitotali.get(i) + 
		    						"&requestedDateTime=20131111" + ore[o] + minuti[m]+"05" );

	
		        			
		    			} catch (XmlPullParserException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} catch (IOException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		        		 
		        		//Aggiorno le linee
			    		if ( passages != null && passages.size() != 0 ){
			            	for (Passage cnt1: passages ){
			            		bd.updateStopsLine( new Fermate(cnt1.stopID, cnt1.aliasLine, cnt1.aliasRoute, cnt1.passageTime));
			            	}
		        		}
			    		//Aggiorno le fermate
				    		if ( desc != null && desc.size() != 0 ){
				    			

				            	for (StopsDesc cnt: desc ){
				            		bd.updateStopsLine( new Fermate(cnt.stopID, cnt.stopDesc, cnt.alisLine, cnt.lastStopName,
				            					cnt.aliasRoute, cnt.passageTime));
				            	}
				            	
				            	vuotitotali = bd.getAllEmptyRowDB();
				            	vuoti = bd.getEmptyRowDB();
			        		}	  

				    	//Cambio l'orario
		        		int z = i+1;
		        		if ( z >= vuotitotali.size() ){
		        			o++;
		        			m++;
		        		}
	       		
		        	} // Fine For
	        		
    		    } //Fine While
	        	
	        	
	        Log.d( "Uscito da:", "ciclo for-while" );
	        
    		Log.d( "Totale vuoti con doppi cambio ID:", ""+ vuotitotali.size() ); 
    		Log.d( "Totale vuoti cambio ID:", ""+ vuoti.size() ); 
    		
	        bd.correctStopId();
        	Log.d( "Totale vuoti corretti:", ""+ vuoti.size() );
        	int nonverificati = stopid.size() - verificati.size();
        	Log.d( "Totale da verificare:", "" + nonverificati );
        	int i = 0;
        	for (String cnt: stopid ){
//        		i++;
        		Log.d( "StopId Totali:", "" + cnt );
        		Log.d( "StopId verificati:", "" + verificati.contains(cnt) );
//        		Log.d( "Numero visualizzato:", "" + i );

        		//Controllo di non aver già fatto una richiesta al server con quel stopId in modo di velocizzare la cosa
        		if ( !verificati.contains(cnt) ){
        			i++;
	        		Log.d("Entrato in StopId2", "n. " + i );
	        		
	        		int stopidaux=Integer.parseInt(cnt);
	        		
	    			//Per avere tutte le linee
					try {					
						String temp343 = loadXmlNextPassagesFromNetwork("http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getNextPassages?" +
								"stopID="+ cnt + "&requestedDateTime=20131120000000&passagesNumber=300");
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
	        		//Aggiorno la lista di fermate per linea
		    		if ( passages != null && passages.size() != 0 ){
		            	for (Passage cnt1: passages ){
		            		bd.updateStopsLine( new Fermate(cnt1.stopID, cnt1.aliasLine, cnt1.aliasRoute, cnt1.passageTime));
		            	}
	        		}
        		}
        	} 
        	
    		Log.d( "Uscito da:", "Riempimento StopsLine" );
    		
    	bd = new DataBase(activity);
          updateIconLine();
          Log.d( "Uscito da:", "Aggiornamento Icone" );
          List<Entry> LineeXML = null;
          try {
			LineeXML = loadXmlFromAssets();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
          bd.updateLineDesc( LineeXML );
          Log.d( "Uscito da:", "Aggiornamento Descrizioni" );
          
//          Log.d( "Entrato in:", "Aggiornamento Ordine" );
////          List<Linea> linee = bd.getAllLines();
////          for ( Linea l: linee ){
//          bd = new DataBase(activity);
//        	  List<Fermate> lStops = bd.getAllGoStopsByLine("4.1");
//        	  try {
//				String temp = loadXmlStopDescFromNetwork(
//							"http://10.128.20.21:8080/AVMwebservice/AVMWebService.asmx/getRunRoutesbyStopID?stopID=" + "5088" + 
//							"&requestedDateTime=20131202092500" );
//			} catch (XmlPullParserException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	  
//	    		//Aggiorno le fermate
//	    		if ( desc != null && desc.size() != 0 ){
//	    			int y = 1;
//	    			String run = desc.get(0).runid;
//	            	for (StopsDesc cnt: desc ){
//	            		if ( cnt.runid.equals("1207") ){
//		            		bd.updateOrderStopsLine( new Fermate(cnt.stopID, cnt.stopDesc, cnt.alisLine, cnt.aliasRoute, cnt.runid), y, "4.1");
//		            		y++;
//	            		}
//	            	}
//	            	
//      		}
//	    		
//	    		Log.d( "Uscito da:", "Aggiornamento Ordine" );
        	  
        	  
//          }

			return "ciao ";
    }

    @Override
    protected void onPostExecute(String result) {
    	
        if(progress.isShowing())
            progress.dismiss();
        bd.close();
        LineList lineListfragment = new LineList();
        // Sostituisco la prima pagina con la lista delle linee
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
        			   .replace(R.id.content_frame, lineListfragment)
        			   .commit();  
    	// Qui dentro devo avvertire il thread principale di aver finito l'aggiornamento
    	
    }
    
    
    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile=context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    
    
    
    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    public String loadXmlGetNearStopFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParserGetNearStops getstopParser = new XmlParserGetNearStops();
        
        List<Stops> listStopID = null;

        try {
            stream = downloadUrl(urlString);
            listStopID = getstopParser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        
        for (Stops stop : listStopID) {
        	stopid.add(stop.stopID);
        }

        return listStopID.toString();
    }
    
    
    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    public String loadXmlStopDescFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParserGetRunRoutesbyStopID RunRouteParser = new XmlParserGetRunRoutesbyStopID();
        
        List<StopsDesc> listDescStop = null;

        try {
            stream = downloadUrl(urlString);
            listDescStop = RunRouteParser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        desc = listDescStop;
        // Dentro desc è presente la lista di fermate con relativa descrizione secondo la classe StopDesc ( stopId, StopDesc ) 
        // Con questa lista io posso aggiornare i mio database
        return listDescStop.toString();
    }
    
    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    public String loadXmlNextPassagesFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParserGetNextPassages nextPassagesParser = new XmlParserGetNextPassages();
        
        List<Passage> listPassages = null;

        try {
            stream = downloadUrl(urlString);
            listPassages = nextPassagesParser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        passages = listPassages;
        // Dentro desc è presente la lista di fermate con relativa descrizione secondo la classe StopDesc ( stopId, StopDesc ) 
        // Con questa lista io posso aggiornare i mio database
        return listPassages.toString();
    }
 
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//      conn.setReadTimeout(10000 /* milliseconds */);
//      conn.setConnectTimeout(15000 /* milliseconds */);
      //Abbiamo aggiunto del tempo di timeup più lungo perchè non arrivavano delle risposte dal WebServices
      conn.setReadTimeout(100000 /* milliseconds */);
      conn.setConnectTimeout(150000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
    
    
    
//    //Esperimento
// // Uploads XML from stackoverflow.com, parses it, and combines it with
//    // HTML markup. Returns HTML string.
//    public List<StopsDesc> loadXmlGetRunRoutesbyStopIdFromNetwork(String urlString) throws XmlPullParserException, IOException {
//        InputStream stream = null;
//        XmlParserGetRunRoutesbyStopID RunRouteParser = new XmlParserGetRunRoutesbyStopID();
//        
//        List<StopsDesc> listDescStop = null;
//
//        try {
//            stream = downloadUrl(urlString);
//            listDescStop = RunRouteParser.parse(stream);
//        // Makes sure that the InputStream is closed after the app is
//        // finished using it.
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
//        // Dentro listdescstop è presente la lista di fermate con relativa descrizione secondo la classe StopDesc ( stopId, StopDesc ) 
//        // Con questa lista io posso aggiornare i mio database
//        return listDescStop;
//    }
    
    
    // Uploads XML from the folder assets, parses it, and combines it with
    // an arraylist that will include into the listview.
    private List<XmlParser.Entry> loadXmlFromAssets() throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParser xmlParser = new XmlParser();
        List<XmlParser.Entry> entries = null;

        AssetManager am = activity.getAssets();
        
        try {
            stream = am.open("data-set.xml");
            entries = xmlParser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // XmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
//        for (XmlParser.Entry entry : entries) {	        	
//        	linee.add(entry);
//        }
        return entries;
    }

    
    
    public void updateIconLine(){
    	
		  List<Linea> linee = bd.getAllLines();
	    	
	    
	    	for ( Linea l: linee ){
	    		if ( l.getLinesId().equals("1") )
			    	bd.updateIconLine(l.getLinesId(), R.drawable.line1 ); 
	    		if ( l.getLinesId().equals("2") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line2 );
	    		if ( l.getLinesId().equals("3") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line3 );
	    		if ( l.getLinesId().equals("4.1") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line41 );
	    		if ( l.getLinesId().equals("4.2") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line42 );
	    		if ( l.getLinesId().equals("5.1") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line51 );
	    		if ( l.getLinesId().equals("5.2") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line52 );
	    		if ( l.getLinesId().equals("6") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line6 );
	    		if ( l.getLinesId().equals("9") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line9 );
	    		if ( l.getLinesId().equals("10") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.line10 );
	    		if ( l.getLinesId().equals("11") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea11 );
	    		if ( l.getLinesId().equals("12") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea12 );
	    		if ( l.getLinesId().equals("13") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea13 );
	    		if ( l.getLinesId().equals("14") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea14);
	    		if ( l.getLinesId().equals("15") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea15 );
	    		if ( l.getLinesId().equals("16") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea16 );
	    		if ( l.getLinesId().equals("17") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea17 );
	    		if ( l.getLinesId().equals("20") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea20 );
	    		if ( l.getLinesId().equals("22") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linea22 );
	    		if ( l.getLinesId().equals("BLU") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.ic_launcher );
	    		if ( l.getLinesId().equals("N") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linean );
	    		if ( l.getLinesId().equals("NLN") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linean );
	    		if ( l.getLinesId().equals("NMU") )
		    		bd.updateIconLine(l.getLinesId(), R.drawable.linean );
	    	}
	    }

    
    
    
    
}
