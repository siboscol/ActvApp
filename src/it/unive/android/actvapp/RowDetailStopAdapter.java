package it.unive.android.actvapp;

import it.unive.android.actvapp.database.DataBase;
import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ParseException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;

public class RowDetailStopAdapter extends BaseAdapter{

	private Context context;
	private List<Passage> data;
    private static LayoutInflater inflater=null;
	
	public RowDetailStopAdapter ( Context c, List<Passage> array ){
		context = c;
		data = array;
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
    public int getItemViewType(int position) {
		return position;
		
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		View row = inflater.inflate(R.layout.detail_stop_row, arg2, false );
		
		TextView StartStop = (TextView) row.findViewById(R.id.from);
		TextView lastStop = (TextView) row.findViewById(R.id.to);
		TextView ArrivalTime = (TextView) row.findViewById(R.id.hourminute);
		
		Passage entry = data.get(arg0);
		
		lastStop.setText( entry.lastStopName );
		
		StartStop.setText( entry.stopDesc );
		
		SimpleDateFormat sIn = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sOut = new SimpleDateFormat("HH:mm");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime( sIn.parse( entry.passageTime ) );
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String time = sOut.format(c.getTime());
		//O uso l'ora oppure tra quanti minuti passa
		
		if ( entry.isReal.equals("true") )
			ArrivalTime.setText( entry.arrivalMinute + " min");
		else
			ArrivalTime.setText(time);
       
        return row;
	}


}
