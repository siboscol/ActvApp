package it.unive.android.actvapp.network;

import it.unive.android.actvapp.DetailStopActivity;
import it.unive.android.actvapp.database.XmlParserGetNextPassages;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;


public class DownloadXmlGetNextPassages extends AsyncTask< String, Void, List<Passage> > {

	ProgressDialog progress; 
	private final OnTaskFinished callBack;
	private DetailStopActivity activity;
	
	public ArrayList<String> cv = null;
	
	public DownloadXmlGetNextPassages( DetailStopActivity c ){
		progress = new ProgressDialog( c );
		activity = c;
		callBack = (OnTaskFinished) c;
	}
	
    @Override
    protected void onPreExecute() { 
        progress.setMessage("Caricamento Orari");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();    
    }

    @Override
    protected List<Passage> doInBackground(String... urls) {
        try {
            return loadXmlNextPassagesFromNetwork(urls[0]);
        } catch (IOException e) {
        	return null;
//            return activity.getResources().getString(R.string.connection_error);
        } catch (XmlPullParserException e) {
//            return activity.getResources().getString(R.string.xml_error);
        	return null;
        }
    }
    
    @Override
    protected void onPostExecute( List<Passage> result) {
        if(progress.isShowing())
            progress.dismiss();
    	
    	callBack.onTaskFinished(result, null);
    }
    

    
    
    public List<Passage> loadXmlNextPassagesFromNetwork(String urlString) throws XmlPullParserException, IOException {
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
        // Dentro desc è presente la lista di fermate con relativa descrizione secondo la classe StopDesc ( stopId, StopDesc ) 
        // Con questa lista io posso aggiornare i mio database
        return listPassages;
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
