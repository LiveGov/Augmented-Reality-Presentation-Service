package eu.liveGov.libraries.livegovtoolkit.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.liveGov.libraries.livegovtoolkit.R;
import eu.liveGov.libraries.livegovtoolkit.objects.ProposalObject;

/**
 * Adapter for the Listview of items (Urban Plans)
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class Listview_Adapter extends BaseAdapter
{
	private ArrayList<ProposalObject> _proposalInfo;
	private LayoutInflater _layoutInflater;

	public Listview_Adapter( Context context )
	{
		_proposalInfo = new ArrayList<ProposalObject>();
		_layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}

	public void addProposal( ProposalObject proposal )
	{
		_proposalInfo.add( proposal );
		notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return _proposalInfo.size();
	}

	@Override
	public ProposalObject getItem( int position )
	{
		return _proposalInfo.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{

		ViewHolder myViewHolder;

		if ( convertView == null )
		{
			convertView = _layoutInflater.inflate( R.layout.item_listinfo, null );
			myViewHolder = new ViewHolder();
			myViewHolder.ivThumbnail = (ImageView) convertView.findViewById( R.id.detailsThumbnail );
			myViewHolder.tvDescription = (TextView) convertView.findViewById( R.id.detailsTvDescription );
			myViewHolder.tvTitle = (TextView) convertView.findViewById( R.id.detailstvTitle );
			convertView.setTag(myViewHolder);
		} else {
			myViewHolder = (ViewHolder) convertView.getTag();
		}

		ProposalObject li = getItem( position );
		int width = myViewHolder.ivThumbnail.getWidth();
		int height = myViewHolder.ivThumbnail.getHeight();
		myViewHolder.ivThumbnail.setImageBitmap( li.get_image(width, height) );
		myViewHolder.tvDescription.setText( li.get_description() );
		myViewHolder.tvTitle.setText( li.get_title() );

		return convertView;
	}

//	public void sortOnLocation( final Location currentLocation )
//	{
//		Comparator<ProposalObject> myComparator = new Comparator<ProposalObject>()
//				{
//			public int compare( ProposalObject obj1, ProposalObject obj2 )
//			{
//				Location lObj1 = new Location( "" );
//				lObj1.setLatitude( obj1.get_lat() );
//				lObj1.setLongitude( obj1.get_lng() );
//				float distObj1 = currentLocation.distanceTo( lObj1 );
//				Location lObj2 = new Location( "" );
//				lObj2.setLatitude( obj2.get_lat() );
//				lObj2.setLongitude( obj2.get_lng() );
//				float distObj2 = currentLocation.distanceTo( lObj2 );
//
//				return ( (int) distObj1 - (int) distObj2 );
//			}
//				};
//
//				Collections.sort( _proposalInfo, myComparator );
//				notifyDataSetChanged();	
//	}

	static class ViewHolder {
		ImageView ivThumbnail;
		TextView tvDescription;
		TextView tvTitle;
	}
}
