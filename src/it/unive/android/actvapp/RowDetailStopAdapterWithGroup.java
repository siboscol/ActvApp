package it.unive.android.actvapp;

import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;

public class RowDetailStopAdapterWithGroup extends BaseAdapter{

	private static final int VIEW_TYPE_GROUP_START = 0;
    private static final int VIEW_TYPE_GROUP_CONT = 1;
	private Context context;
	private List<Passage> data;
    private static LayoutInflater inflater=null;
	
	public RowDetailStopAdapterWithGroup ( Context c, List<Passage> array ){
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
		
		// There is always a group header for the first data item
		 
        if (position == 0) {
            return VIEW_TYPE_GROUP_START;
        }

        // For other items, decide based on lastStop data
         
        boolean newGroup = isNewGroup(position);

        // Check item grouping

        if (newGroup) {
            return VIEW_TYPE_GROUP_START;
        } else {
            return VIEW_TYPE_GROUP_CONT;
        }

		
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int nViewType;

        if (arg0 == 0) {
            // Group header for position 0

            nViewType = VIEW_TYPE_GROUP_START;
        } else {
            // For other positions, decide based on data
        	Passage entry = data.get(arg0);
            boolean newGroup = isNewGroup(arg0);

            if (newGroup) {
                nViewType = VIEW_TYPE_GROUP_START;
            } else {
                nViewType = VIEW_TYPE_GROUP_CONT;
            }
        }
 
        View v;

        if (nViewType == VIEW_TYPE_GROUP_START) {
            // Inflate a layout to start a new group

            v = inflater.inflate(R.layout.message_list_item_with_header, arg2, false);
            TextView Header = (TextView) v.findViewById(R.id.list_item_section_text);
             
            if ( Header != null ){
            	Header.setText( "Direzione: " + data.get(arg0).lastStopName );
            	Header.setSelected(false);
            }
            
    		TextView StartStop = (TextView) v.findViewById(R.id.from);

    		TextView ArrivalTime = (TextView) v.findViewById(R.id.hourminute);
    		
    		Passage entry = data.get(arg0);
    		
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
    		
//    		if ( entry.isReal.equals("true") )
//    			ArrivalTime.setText( entry.arrivalMinute + " min");
//    		else
    			ArrivalTime.setText(time);

//            // Ignore clicks on the list header

//            View vHeader = v.findViewById(R.id.list_item_section_text);
//            
//            vHeader.setSelected(false);
        } else {
            // Inflate a layout for "regular" items

            v = inflater.inflate(R.layout.message_list_item, arg2, false);
            
    		TextView StartStop = (TextView) v.findViewById(R.id.from);
    		
    		TextView ArrivalTime = (TextView) v.findViewById(R.id.hourminute);
    		
    		Passage entry = data.get(arg0);
    		
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

    		SimpleDateFormat s = new SimpleDateFormat("HH");
            String currentHour = s.format(new Date());
            
            String[] part = time.split(":");
            String selectedHour = part[0];
            
//            Log.e("currentHour: ", currentHour );
//            Log.e("SelectedHour: ", selectedHour );
            
            //Faccio vedere l'orario in minuti solo se è reale e se è la stessa ora corrente
            //altrimenti viene visualizzato l'oraro in HH:mm
            
            if ( !currentHour.equals(selectedHour) )
            	ArrivalTime.setText(time);
            else {
        		if ( entry.isReal.equals("true") )
        			ArrivalTime.setText( entry.arrivalMinute + " min");
        		else
        			ArrivalTime.setText(time);
            }

        }
        
        return v;
	}
	
    private boolean isNewGroup(int position) {
        // Get date values for current and previous data items

        String lastStopCurr = data.get(position).lastStopName;
        
        String lastStopPrev = data.get(position-1).lastStopName;

        // Compare lastStop values
        
        if ( lastStopPrev.equals( lastStopCurr ) )
        	return false;
        return true;
    }


}