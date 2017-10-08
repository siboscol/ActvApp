package it.unive.android.actvapp.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlParserGetNextPassages {
	
    private static final String ns = null;

    // We don't use namespaces

    public List<Passage> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private List<Passage> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Passage> entries = new ArrayList<Passage>();

        parser.require(XmlPullParser.START_TAG, ns, "ArrayOfNextPassage");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("NextPassage")) {
                entries.add(readPassage(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Passage{
        public final String stopID;
        public String stopDesc;
        //aggiornamento 07/11
        public final String aliasLine;
        public final String lastStopName;
        public final String aliasRoute;
        public final String passageTime;
        public final String arrivalMinute;
        public final String isReal;
        //fine modifica
        //aggiornamento input fuzione 04/11
        private Passage(String stopID, String aliasLine, String lastStopName, String aliasRoute, String passageTime, String arrivalMinute, String isReal ) {
            this.stopID = stopID;
        //aggiornamento 07/11    
            this.aliasLine = aliasLine;
            this.lastStopName = lastStopName;
            this.aliasRoute = aliasRoute;
            this.passageTime = passageTime;
            this.arrivalMinute = arrivalMinute;
            this.isReal = isReal;
        //fine aggiornamento
        }
        
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private Passage readPassage(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "NextPassage");
        String stopID = null;
        //aggiornamento 07/11
        String aliasLine = null;
        String lastStopName = null;
        String aliasRoute = null;
        String passageTime = null;
        String arrivalMinute = null;
        String isReal = null;
        //fine aggiornamento
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("stopID")) {
            	stopID = readstopID(parser);
            } else if (name.equals("lastStopName")){
            	lastStopName = readlastStopName(parser);
            } else if (name.equals("aliasLine")) {
            	aliasLine= readAliasLine(parser);
            } else if (name.equals("aliasRoute")){ 
            	aliasRoute=readAliasRoute(parser);
            } else if ( name.equals("passageTime")){
            	passageTime=readPassageTime(parser);
            } else if ( name.equals("arrivalMinute")){
            	arrivalMinute=readarrivalMinute(parser);
            }  else if ( name.equals("isReal")){
            	isReal=readisReal(parser);
            }else{ 
            	skip(parser);
            }
        }
        return new Passage(stopID, aliasLine, lastStopName,  aliasRoute, passageTime, arrivalMinute, isReal);
    }
    
  private String readisReal(XmlPullParser parser) throws IOException, XmlPullParserException{
      parser.require(XmlPullParser.START_TAG, ns, "isReal");
      String isReal = readText(parser);
      parser.require(XmlPullParser.END_TAG, ns, "isReal");
      return isReal;
	}

private String readarrivalMinute(XmlPullParser parser) throws IOException, XmlPullParserException{
      parser.require(XmlPullParser.START_TAG, ns, "arrivalMinute");
      String stopDesc = readText(parser);
      parser.require(XmlPullParser.END_TAG, ns, "arrivalMinute");
      return stopDesc;
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
	//fine nuovi metodi aggiornamento 07/11
	// Processes stopID tags in the feed.
    private String readstopID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stopID");
        String hourMinute = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stopID");
        return hourMinute;
    }
    
    // Processes stopDesc tags in the feed.
    private String readlastStopName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lastStopName");
        String stopDesc = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lastStopName");
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
