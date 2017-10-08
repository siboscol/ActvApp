package it.unive.android.actvapp.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;


public class XmlParserGetRunRoutesbyStopID {
	
    private static final String ns = null;

    // We don't use namespaces

    public List<StopsDesc> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<StopsDesc> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<StopsDesc> entries = new ArrayList<StopsDesc>();

        parser.require(XmlPullParser.START_TAG, ns, "ArrayOfPlannedStopByStopID");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("PlannedStopByStopID")) {
                entries.add(readStops(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class StopsDesc {
        public final String stopID;
        public final String stopDesc;
        //aggiornamento 04/11
        public final String alisLine;
        public final String lastStopName;
        public final String aliasRoute;
        public final String passageTime;
        public final String runid;
        //fine modifica
        //aggiornamento input fuzione 04/11
        private StopsDesc(String stopID, String stopDesc, String aliasLine, String lastStopName, String aliasRoute, String passageTime, String runid) {
            this.stopID = stopID;
            this.stopDesc = stopDesc;
            this.lastStopName = lastStopName;
            this.alisLine=aliasLine;
            this.aliasRoute=aliasRoute;
            this.passageTime=passageTime;
            this.runid = runid;
        //fine aggiornamento
        }
        
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private StopsDesc readStops(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "PlannedStopByStopID");
        String stopID = null;
        String stopDesc = null;
        //Aggiunto il campo laststopname 09/11
        String aliasLine=null;
        String lastStopName = null;
        String aliasRoute= null;
        String passageTime=null;
        String runid=null;
        //fine aggiornamento
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("stopID")) {
            	stopID = readstopID(parser);
            } else if (name.equals("stopDesc")){
                stopDesc = readstopDesc(parser);
            } else if (name.equals("aliasLine")) {
            	aliasLine= readAliasLine (parser);
            } else if (name.equals("aliasRoute")){ 
            	aliasRoute=readAliasRoute(parser);
            } else if (name.equals("lastStopName")){ 
            	lastStopName=readlastStopName(parser);
            } else if ( name.equals("passageTime")){
            	passageTime=readPassageTime(parser);
            }else if ( name.equals("runid")){
            	runid=readrunid(parser);
            }else{ 
            	skip(parser);
            }
        }
        return new StopsDesc(stopID, stopDesc, aliasLine, lastStopName, aliasRoute, passageTime, runid);
    }
    
    
    private String readrunid(XmlPullParser parser) throws IOException, XmlPullParserException{
		// TODO Auto-generated method stub
	 	 parser.require(XmlPullParser.START_TAG, ns, "runid");
	      String runid = readText(parser);
	      parser.require(XmlPullParser.END_TAG, ns, "runid");
	      return runid;
	}

	private String readlastStopName(XmlPullParser parser) throws IOException, XmlPullParserException{
			// TODO Auto-generated method stub
	 	 parser.require(XmlPullParser.START_TAG, ns, "lastStopName");
	      String lastStopName = readText(parser);
	      parser.require(XmlPullParser.END_TAG, ns, "lastStopName");
	      return lastStopName;
	}

	// nuovi metodi aggiornamento 04/11
    private String readPassageTime(XmlPullParser parser) throws IOException, XmlPullParserException{
		// TODO Auto-generated method stub
    	 parser.require(XmlPullParser.START_TAG, ns, "passageTime");
         String passageTime = readText(parser);
         parser.require(XmlPullParser.END_TAG, ns, "passageTime");
         return passageTime;
	}

	private String readAliasRoute(XmlPullParser parser)throws IOException, XmlPullParserException {
		// TODO Auto-generated method stub
		parser.require(XmlPullParser.START_TAG, ns, "aliasRoute");
        String aliasRoute = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "aliasRoute");
        return aliasRoute;
		
	}

	private String readAliasLine(XmlPullParser parser)throws IOException, XmlPullParserException {
		// TODO Auto-generated method stub
		parser.require(XmlPullParser.START_TAG, ns, "aliasLine");
		String aliasLine = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "aliasLine");
		return aliasLine;
				
	}
	//fine nuovi metodi aggiornamento 04/11
	// Processes stopID tags in the feed.
    private String readstopID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stopID");
        String hourMinute = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stopID");
        return hourMinute;
    }
    
    // Processes stopDesc tags in the feed.
    private String readstopDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stopDesc");
        String stopDesc = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stopDesc");
        return stopDesc;
    }

    // For the tags stopID and stopDesc, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
