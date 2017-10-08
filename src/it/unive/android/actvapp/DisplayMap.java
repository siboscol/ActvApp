package it.unive.android.actvapp;

import com.example.android.navigationdrawerexample.R;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DisplayMap extends Fragment {
	Activity mactivity;
	ImageView map;
	
	
    @Override
    public void onAttach(Activity activity) {
            super.onAttach(activity);
            mactivity = activity;   
    }
    
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        
        final View v = inflater.inflate(R.layout.mappa, container, false);
               TouchImageView map = (TouchImageView) v.findViewById(R.id.imageView1);
        map.setMaxZoom(8);
        return v;
    }
    

}
