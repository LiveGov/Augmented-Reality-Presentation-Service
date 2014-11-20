// ReadAREL.java
package eu.liveandgov.ar.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.location.Location;
import android.util.Xml;

/**
 * This is the AREL xml parser developed for the need of LiveandGov project.
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class ReadAREL {

	// We don't use namespaces
	private static final String ns = null;

	/**
	 * Start xml parser.
	 * 
	 * @param in Inputstream of content string data
	 * @return List of Entities constructed.
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Entity> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, "UTF-8");

			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}


	private List<Entity> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Entity> entities = new ArrayList<Entity>();

		parser.require(XmlPullParser.START_TAG, ns, "results");
		String trackingurl = parser.getAttributeValue(ns,"trackingurl");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("object")) 
				entities.add(readEntry(parser,parser.getAttributeValue(ns, "id"),trackingurl));
			else 
				skip(parser);
		}  
		return entities;
	}


	/**
	 * Read an entity
	 * 
	 * @param parser
	 * @param id    
	 * @param trackingurl
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Entity readEntry(XmlPullParser parser, String id, String trackingurl) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "object");

		String title = "";
		String iconurl = "";
		String iconfile = "";
		Location location = new Location("Object");
		String description = "";
		String[] modelurlRotScale = new String[]{"","0","1"};
		String modelfile = "";
		String[] customparams = null;


		while ( parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			if (name.equals("title"))
				title = readplain(parser, name);
			else if (name.equals("icon") || name.equals("thumbnail"))
				iconurl = readplain(parser, name);
			else if (name.equals("popup")) 
				description = readpopup(parser);
			else if (name.equals("location")) 
				location = readlocation(parser);
			else if (name.equals("assets3d")){ 
				modelurlRotScale = readmodelurl(parser);
			}else if (name.equals("parameters")) {
				customparams = readparameters(parser);
			} else
				skip(parser);
		}



		if (customparams[1].equals("LBS BILLBOARD")){

			modelfile = id + "/AR_"+id+"_1junaio.zip";
			iconfile  = id + "/AR_"+id+"_1.jpg";

			return new Entity(id, title, iconurl, iconfile, location, description, modelurlRotScale[0], 
					modelfile, customparams[1], trackingurl, "0", Integer.parseInt(modelurlRotScale[1]), 
											Float.parseFloat(modelurlRotScale[2]));
			
		}else if (customparams[1].equals("LBS 3D")){

			id = customparams[3];
			modelfile = id + "/AR_"+id+"_1junaio.zip";
			iconfile  = id + "/AR_"+id+"_1.jpg";

			return new Entity(id, title, iconurl, iconfile, location, description, modelurlRotScale[0], 
					modelfile, customparams[1], trackingurl, customparams[5], Integer.parseInt(modelurlRotScale[1]), Float.parseFloat(modelurlRotScale[2]));
		}else if (customparams[1].equals("IBS")){

			id = customparams[3];

			modelfile = id + "/AR_"+ id +"_1junaio.zip";
			iconfile  = id + "/AR_"+ id +"_1.jpg";

			return new Entity(id, title, iconurl, iconfile, location, description, modelurlRotScale[0], 
					modelfile, customparams[1], trackingurl, customparams[5], Integer.parseInt(modelurlRotScale[1]), 
					Float.parseFloat(modelurlRotScale[2])
					);
		}


		return null;
	}


	/**
	 * Read custom parameters in xml
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String[] readparameters(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "parameters");
		String[] res = new String[6];

		int i = 0;
		while ( parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals("parameter")){
				res[2*i ] = parser.getAttributeValue(0);
				res[2*i + 1] = readplain(parser, "parameter");
				i++;
			} else 
				skip(parser);
		}

		//Log.e("res", res[0] + " " + res[1] + " " + res[2] + " " + res[3] + " " + res[4] + " " +res[5] );
		return res;
	}




	/**
	 * Read 3d model field.
	 * @param parser
	 */
	private String[] readmodelurl(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "assets3d");
		String res = "";
		String[] y   = new String[]{"0","1"};
		while ( parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();

			if (name.equals("model"))
				res = readplain(parser, "model");
			else if (name.equals("transform")){
				y = readmodelRotationScale(parser);
			}	else{
				skip(parser);
			}


		}
		// y[0]: Rotation, y[1]: Scale
		return new String[]{res,y[0],y[1]};
	}

	private String[] readmodelRotationScale(XmlPullParser parser) throws IOException, XmlPullParserException {

		parser.require(XmlPullParser.START_TAG, ns, "transform");
		String[] res  = new String[]{"0","1"};

		while ( parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();

			if (name.equals("rotation")){
				res[0] = readpar(parser,"rotation","y");
			}else if (name.equals("scale")){
				res[1] = readpar(parser,"scale","x");
			}else
				skip(parser);
		}
		return res;
	}


	private String readpar(XmlPullParser parser, String father, String child) throws IOException, XmlPullParserException {

		parser.require(XmlPullParser.START_TAG, ns, father);
		String res = "";

		while ( parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();

			if (name.equals(child)){
				res  = readplain(parser, child);
			}else 
				skip(parser);
		}
		return res;
	}


	/**
	 * Read description information (in popup tag)
	 *     
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readpopup(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "popup");
		String description = "N/A";
		while ( parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();
			if (name.equals("description"))
				description = readplain(parser, "description"); 
			else 
				skip(parser);
		}
		return description;
	}


	/**
	 * Read GPS location of Entity 
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Location readlocation(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "location");
		Location location = new Location("Object");

		while ( parser.next() != XmlPullParser.END_TAG) {

			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();

			if (name.equals("lat"))
				location.setLatitude( Double.parseDouble(readplain(parser, "lat"))); 
			else if (name.equals("lon"))
				location.setLongitude(Double.parseDouble(readplain(parser, "lon")));
			else if (name.equals("alt"))
				location.setAltitude(Double.parseDouble(readplain(parser, "alt")));   
			else 
				skip(parser);
		}
		return location;
	}

	// Processes title tags in the feed.
	private String readplain(XmlPullParser parser, String mtag) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, mtag);
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, mtag);
		return text;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// skip a tag
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG)
			throw new IllegalStateException();

		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				//Log.e("parser A", " " + parser.getName());
				depth--;
				break;
			case XmlPullParser.START_TAG:
				//Log.e("parser B", " " + parser.getName());
				depth++;
				break;
			}
		}
	}

}
