// Entity.java
package eu.liveandgov.ar.utilities;

import android.location.Location;

/**
 * This is the structure to store information of an AR Entity.
 * Either LBS (Billboards or 3d models) or IBS (3d models). 
 *      
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
	
public class Entity {
	 /** Id of the Entity id e.g. 
	 *        for LBS billboard:  1 
	 *        for LBS 3d model :  3D1
	 *        for IBS          :  1   
	 */
	public String id;            
	
	/** Title of the entity */
	public String title;
	
	/** URL of the icon image */ 
	public String iconurl;
	
	/** Local file of the icon */
	public String iconfile;
	
	/** GPS location */
    public Location location;   
    
    /** Description of the Entity */
	public String description;
	
	/** remote file (url) of first 3d model (other files can be found by string replacing 1 with 2, and 2 with 3 */
	public String modelurl;
	
	/** local file of first 3d model */ 
	public String modelfile;
	
	/** AR type data: "LBS BILLBOARD" or "LBS 3D" or "IBS" */
	public String type;        
	/** GPS or file.xml  depending if it is LBS or IBS, respectively */
	public String trackingurl; 
	
	/** number of alternative 3d models for the 3d type case (1 or 2 or 3) */
	public String nModels3d;
	
	/** Rotation of model (used only in IBS) */
	public int trackImRot;

	/** Scale of model (used only in IBS) */
	public float trackImScale;

	
	public Entity(String id, String title, String iconurl, String iconfile, 
			Location location, String description, String modelurl, String modelfile, String type, String trackingurl, String nModels3d, int trackImRot, float trackImScale) {
		
		this.id          = id;
		this.title       = title;
		this.iconurl     = iconurl;
		this.iconfile    = iconfile;
		this.location    = location;
		this.description = description ;
		this.modelurl    = modelurl ;
		this.modelfile   = modelfile ;
		this.type        = type ;
		this.trackingurl = trackingurl ;
		this.nModels3d = nModels3d;
		this.trackImRot = trackImRot;
		this.trackImScale = trackImScale;
	}
}


