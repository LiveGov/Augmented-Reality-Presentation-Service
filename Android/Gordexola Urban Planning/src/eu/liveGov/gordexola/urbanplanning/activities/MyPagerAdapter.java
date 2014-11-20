package eu.liveGov.gordexola.urbanplanning.activities;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This is the adapter for changing tabs
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class MyPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Fragment> _fragments;
    
    public MyPagerAdapter( FragmentManager fm )
    {
	super( fm );
	_fragments = new ArrayList<Fragment>();
    }
    
    public void removeAll()
    {
	if ( _fragments.size() > 0 )
	{
	    _fragments.clear();
	    _fragments = new ArrayList<Fragment>();
	    notifyDataSetChanged();
	}
    }
    
    @Override
    public Fragment getItem( int position )
    {
	return _fragments.get( position );
    }
    
    @Override
    public int getCount()
    {
	return _fragments.size();
    }
    
    public void addFragment( Fragment f )
    {
	_fragments.add( f );
	notifyDataSetChanged();
    }
}