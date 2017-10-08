/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package it.unive.android.actvapp.network;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses XML feeds from the folder assets.
 * Given an InputStream representation of a feed, it returns a List of record,
 * where each list element represents a single record (post) in the XML feed.
 */
public class XmlParser {
    private static final String ns = null;

    // We don't use namespaces

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "data-set");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("record")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "summary."
    public static class Entry {
        public final String NomeLinea;
        public final String DescrizioneLinea;
        public final String DescrittoreSettore;


        private Entry(String nomelinea, String descrizionelinea, String descrizionesettore) {
            this.NomeLinea = nomelinea;
            this.DescrizioneLinea = descrizionelinea;
            this.DescrittoreSettore = descrizionesettore;
        }
    }

    // Parses the contents of an entry. If it encounters a Nome_linea, , or Descrizione_linea or Descrittore_settore tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "record");
        String NomeLinea = null;
        String DescrizioneLinea = null;
        String DescrittoreSettore = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Nome_linea")) {
            	NomeLinea = readNomeLinea(parser);
            } else if (name.equals("Descrizione_linea")) {
            	DescrizioneLinea = readDescrizioneLinea(parser);
            } else if (name.equals("Descrittore_settore")) {
            	DescrittoreSettore = readDescrizioneSettore(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(NomeLinea, DescrizioneLinea, DescrittoreSettore);
    }

    // Processes title tags in the feed.
    private String readNomeLinea(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Nome_linea");
        String nome = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Nome_linea");
        return nome;
    }

    // Processes summary tags in the feed.
    private String readDescrizioneLinea(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Descrizione_linea");
        String descrizionelinea = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Descrizione_linea");
        return descrizionelinea;
    }

    // Processes summary tags in the feed.
    private String readDescrizioneSettore(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Descrittore_settore");
        String descrizionesettore = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Descrittore_settore");
        return descrizionesettore;
    }

    // For the tags Nome_linea, Descrizione_linea and Descrizione_settore, extracts their text values.
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
