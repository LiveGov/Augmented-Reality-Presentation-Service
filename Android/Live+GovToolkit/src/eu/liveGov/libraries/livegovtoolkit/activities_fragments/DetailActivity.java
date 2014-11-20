package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import eu.liveGov.libraries.livegovtoolkit.R;

/**
 * 
 * Display details about an item from a list, the map, or from augmented reality fragments.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class DetailActivity extends FragmentActivity
{
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.details_activity );
	
	Bundle b = getIntent().getExtras();
	
	DetailFragment df = new DetailFragment();
	df.setArguments( b );
	
	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	ft.replace( R.id.root, df );
	ft.commit();
	
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
	super.onSaveInstanceState( outState );
    }
    
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }
}
