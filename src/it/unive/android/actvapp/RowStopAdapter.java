package it.unive.android.actvapp;

import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Fermate;
import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;

public class RowStopAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> data;
	private List<StopsDesc> reply;
    private static LayoutInflater inflater=null;
    private List<Fermate> stopsByLine;
    private DataBase db;
    public static final String PREFS_NAME = "MyPrefs";
    public static final String STOP_NAME = "STOP";

	
	public RowStopAdapter ( Context c, ArrayList<String> array, List<Fermate> stopsByLine ){
		context = c;
		data = array;
		reply = new ArrayList<StopsDesc>();
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.stopsByLine = stopsByLine;
		db = new DataBase(c);
	}
	
	public RowStopAdapter ( Context c, List<StopsDesc> answere, List<Fermate> stopsByLine ){
		context = c;
		reply = answere;
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.stopsByLine = stopsByLine;
		db = new DataBase(c);
	}
	
	@Override
	public int getCount() {
		if ( data != null)
			return data.size();
		else
			return reply.size();
	}

	@Override
	public Object getItem(int arg0) {
		if ( data != null)
			return data.get(arg0);
		else
			return reply.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		View row = inflater.inflate(R.layout.row_stops, arg2, false );
		
		LinearLayout lineIcon = (LinearLayout) row.findViewById(R.id.linearLayout1);
		
		TextView title_stop = (TextView) row.findViewById(R.id.fermata);  
		TextView time_stop = (TextView) row.findViewById(R.id.orario);
		
		String entry = "";
		if ( data != null)
			entry = data.get(arg0);
		
		if (reply.size() != 0 ){
            String[] parts = reply.get(arg0).stopDesc.split("SX|\"|DX");
			title_stop.setText( parts[0] );
			
			
			//Questa parte di codice serve per visualizzare gli orari di ogni fermata ma bisogna decidere se visualizzarli oppure no
			SimpleDateFormat sIn = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat sOut = new SimpleDateFormat("HH:mm");
			Calendar c = Calendar.getInstance();
			try {
				c.setTime( sIn.parse( reply.get(arg0).passageTime ) );
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String time = sOut.format(c.getTime());
			time_stop.setText(time );
			
		}else{			
			title_stop.setText( entry );
		}
		
		//Prova con utilizzo della nuova funzione del db
		db = new DataBase(context);  
		String[] parts = reply.get(arg0).stopDesc.split("SX|\"|DX");
        String apici = parts[0].replaceAll("'", "''");
		List<Linea> changes = db.getChangesLinesbyStopName(apici);
		db.close();
		
	    for ( Linea l: changes ){
	    	
	        ImageView imageView = new ImageView(context);
	        imageView.setImageResource( l.getIcon() );
	        imageView.setLayoutParams( new LayoutParams(55, 55) );
	        lineIcon.addView(imageView);
	    	
	    }
		
		
		
		//Recupero la lista intera di fermate per linea selezionata e seleziono gli stopId
		//che mi servono per fare la chiamata al WebService  
//        ArrayList<String> idStops = new ArrayList<String>();
//        for ( Fermate f: stopsByLine){
//        	if (data != null){
//	        	if ( f.getStopDesc().indexOf(entry) != -1 )            	
//	        		idStops.add( f.getStopId() );
//        	}else{
//        		
//                String[] parts = reply.get(arg0).stopDesc.split("SX|DX|\"|\\(");
//        		
//	        	if ( f.getStopDesc().indexOf( parts[0] ) != -1 )            	
//	        		idStops.add( f.getStopId() );
//        	}
//        }
        
        //Metodo alternativo per viualizzare i cambi direttamente sulle fermate, ma a volte non ci sono tutti ( da verificare )
        //Possibile metodo leggermente più efficiente
//        List<Linea> changes = new ArrayList<Linea>();
//        for ( String id: idStops ){
//        	changes.addAll(db.getChangesLinesbyStopId( id ));
//        	HashSet<Linea> listToSet = new HashSet<Linea>(changes);
//            changes = new ArrayList<Linea>(listToSet);
//        }
//        
//        for ( Linea l: changes ){
//        	
//	        ImageView imageView = new ImageView(context);
//	        imageView.setImageResource( l.getIcon() );
//	        imageView.setLayoutParams( new LayoutParams(45, 45) );
//	        lineIcon.addView(imageView);
//        	
//        }
        
        //Con questo doppio ciclo for aggiungo le icone delle linee passanti per gli id di una fermata
        
        //Pure questo non fornisce tutti i cambi effettivi di una fermata (da verificare)
//        ArrayList<String> noDoubleLine = new ArrayList<String>();
//        
//		for ( String id: idStops ){
//			
//	        List<Linea> changes = db.getChangesLinesbyStopId( id );
//	    
//			for ( Linea l: changes ){
//				if ( !noDoubleLine.contains(l.getLinesId()) ){
//					noDoubleLine.add(l.getLinesId());
//			        ImageView imageView = new ImageView(context);
//			        imageView.setImageResource( l.getIcon() );
//			        imageView.setLayoutParams( new LayoutParams(45, 45) );
//			        lineIcon.addView(imageView);
//				}
//			}
//		}

        return row;
	}

}
