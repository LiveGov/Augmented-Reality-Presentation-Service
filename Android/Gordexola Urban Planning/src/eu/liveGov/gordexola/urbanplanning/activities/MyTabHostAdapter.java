package eu.liveGov.gordexola.urbanplanning.activities;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

/**
 * This is the adapter that creates tabs
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */
public class MyTabHostAdapter implements TabContentFactory {

	private final Context _context;

	public MyTabHostAdapter(Context context) {
		_context = context;
	}

	public View createTabContent(String tag) {
		View v = new View(_context);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}
}