package eu.liveGov.libraries.livegovtoolkit.activities_fragments;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import eu.liveGov.libraries.livegovtoolkit.R;

/**
 * Display personal information about the user of the application. Only one user per device is allowed. 
 * Editing of personal information is also allowed. 
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class UserInformationActivity extends FragmentActivity
{
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
	super.onCreate( savedInstanceState );
	setContentView( R.layout.user_information_activity );
	
	Bundle b = getIntent().getExtras();
	
	UserInformationFragment uif = new UserInformationFragment();
	uif.setArguments( b );
	
	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	ft.replace( R.id.user_info_root, uif );
	ft.commit();
	
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
