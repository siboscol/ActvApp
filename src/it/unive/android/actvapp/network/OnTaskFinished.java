package it.unive.android.actvapp.network;

import it.unive.android.actvapp.database.XmlParserGetNextPassages.Passage;
import it.unive.android.actvapp.database.XmlParserGetRunRoutesbyStopID.StopsDesc;

import java.util.List;


public interface OnTaskFinished {
	void onTaskFinished( List<Passage> response, List<StopsDesc> answere );
}
