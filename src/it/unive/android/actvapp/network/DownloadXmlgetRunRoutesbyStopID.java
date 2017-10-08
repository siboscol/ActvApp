package it.unive.android.actvapp.network;

import it.unive.android.actvapp.DetailStopActivity;
import it.unive.android.actvapp.ListDetailStopsActivity;
import it.unive.android.actvapp.StopsList;
import it.unive.android.actvapp.database.XmlParserGetNextPassages;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


public class DownloadXmlgetRunRoutesbyStopID extends AsyncTask< String, Void, List<StopsDesc> > {

	ProgressDialog progress; 
	private final OnTaskFinished callBack;
	
	public ArrayList<String> cv = null;
	
	public DownloadXmlgetRunRoutesbyStopID( StopsList c ){
		progress = new ProgressDialog( c.getActivity() );
		callBack = (OnTaskFinished) c;
	}
	
	public DownloadXmlgetRunRoutesbyStopID( FragmentActivity c ){
		progress = new ProgressDialog( c );
		callBack = (OnTaskFinished) c;
	}
	
    @Override
    protected void onPreExecute() { 
        progress.setMessage("Caricamento Corsa");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();    
    }

    @Override
    protected List<StopsDesc> doInBackground(String... urls) {
        try {
            return loadXmlRunRoutebyStopIdFromNetwork(urls[0]);
        } catch (IOException e) {
        	return null;
//            return activity.getResources().getString(R.string.connection_error);
        } catch (XmlPullParserException e) {
//            return activity.getResources().getString(R.string.xml_error);
        	return null;
        }
    }
    
    @Override
    protected void onPostExecute( List<StopsDesc> result) {
        if(progress.isShowing())
            progress.dismiss();
//    	final Calendar c = Calendar.getInstance();
//    	ListDetailStopsActivity.runRoute = result;
//
//    	if ( result != null )
//    		Toast.makeText( activity, "RunId: " + result.get(0).runid, Toast.LENGTH_SHORT).show();

    	
    	callBack.onTaskFinished(null, result);
    }
    

    
    
    public List<StopsDesc> loadXmlRunRoutebyStopIdFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        XmlParserGetRunRoutesbyStopID runRouteParser = new XmlParserGetRunRoutesbyStopID();
        
        List<StopsDesc> runRoute = null;

        try {
            stream = downloadUrl(urlString);
            runRoute = runRouteParser.parse(stream);
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        // Dentro desc è presente la lista di fermate con relativa descrizione secondo la classe StopDesc ( stopId, StopDesc ) 
        // Con questa lista io posso aggiornare i mio database
        return runRoute;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000 /* milliseconds */);
//        conn.setConnectTimeout(15000 /* milliseconds */);
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
    
    
    

}
