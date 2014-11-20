package eu.liveGov.libraries.livegovtoolkit.helper;

import java.io.File;
import java.util.ArrayList;

import eu.liveGov.libraries.livegovtoolkit.objects.FetchResources;
import eu.liveGov.libraries.livegovtoolkit.objects.ProposalObject;
import eu.liveandgov.ar.utilities.Entity;

/**
 * Download items (Urban Plans).
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class ProposalHelper{
	protected static boolean isDownloading = false;

	public static ArrayList<ProposalObject> getProposals(){
		ArrayList<ProposalObject> list = new ArrayList<ProposalObject>();
		for ( Entity po : FetchResources.entitiesLBS ) 	{
			if ( po.type.equals( "LBS BILLBOARD" ) ) { // only need the non 3D objects
				list.add( convertEntityToProposalObject( po ) );
			}
		}
		return list;
	}

	public static ProposalObject getProposalById( int id ){
		for ( Entity po : FetchResources.entitiesLBS )
			if ( Integer.parseInt( po.id ) == id && po.type.equals( "LBS BILLBOARD" ) )
				return convertEntityToProposalObject( po );
		return null;
	}

	public static ProposalObject convertEntityToProposalObject( Entity entity )
	{
		ProposalObject p = new ProposalObject();
		p.set_id( Integer.parseInt( entity.id ) );
		p.set_title( entity.title );
		p.set_description( entity.description );
		p.set_lat( entity.location.getLatitude() );
		p.set_lng( entity.location.getLongitude() );

		String icfile =FetchResources.folder_Models3D + entity.iconfile ;
		
		File file = new File(icfile);

		if ( file.exists() )
			p.set_imageURL(icfile);

		return p;
	}
}