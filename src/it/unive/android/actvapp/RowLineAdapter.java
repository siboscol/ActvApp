package it.unive.android.actvapp;

import it.unive.android.actvapp.database.Linea;
import it.unive.android.actvapp.network.XmlParser;
import it.unive.android.actvapp.network.XmlParser.Entry;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.example.android.navigationdrawerexample.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

public class RowLineAdapter extends BaseAdapter {

	private Context context;
	private List<Linea> data;
    private static LayoutInflater inflater=null;
    private List<Entry> descrizioni;
	
	public RowLineAdapter ( Context c, List<Linea> array ){
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
		
		View row = inflater.inflate(R.layout.row_line, arg2, false );
		
		TextView title_line = (TextView) row.findViewById(R.id.textView1);
        TextView description = (TextView) row.findViewById(R.id.textView2);
        ImageView icon_line = (ImageView) row.findViewById(R.id.iconline);
        
        Linea entry = data.get(arg0);
        
        title_line.setText( "Linea " + entry.getLinesId() );
   		description.setText( entry.getRoute() );
   		icon_line.setImageResource(entry.getIcon());
   
        return row;
	}

}
