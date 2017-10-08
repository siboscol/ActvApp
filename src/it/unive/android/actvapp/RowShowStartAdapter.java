package it.unive.android.actvapp;

import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;

public class RowShowStartAdapter extends BaseAdapter{

	private Context context;
	private List<StopsDesc> data;
    private static LayoutInflater inflater=null;
	
	public RowShowStartAdapter ( Context c, List<StopsDesc> array ){
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		View row = inflater.inflate(R.layout.show_start_row, arg2, false );
		
		TextView Stop = (TextView) row.findViewById(R.id.textView1);
		TextView timetable = (TextView) row.findViewById(R.id.textView2);

		
		StopsDesc entry = data.get(arg0);
		
		Stop.setText(entry.stopDesc);
		
		SimpleDateFormat sIn = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sOut = new SimpleDateFormat("HH:mm");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime( sIn.parse( entry.passageTime ) );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String time = sOut.format(c.getTime());
		timetable.setText( time );
		
       
        return row;
	}


}
