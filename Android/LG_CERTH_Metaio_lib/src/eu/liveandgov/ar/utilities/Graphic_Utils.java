/**
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 */
package eu.liveandgov.ar.utilities;

import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Generate the billboard texture. It is a bitmap that it is glued on a rectangle geometry on AR. 
 * 
 * @copyright   Copyright (C) 2012 - 2013 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 * 
 */
public class Graphic_Utils {

	//---------- variables -------
	/** Text size */
	public static int TEXT_SIZE       = 20;    // Textsize on billboard

	/** billboard texture size: 256x128. To be sure use only powers of 2. Keep low to save memory*/
	public static int billboard_W     = 256;   //

	public static int billboard_H     = 128;

	/** Horizontal margin for text */
	public static int TEXT_MARGIN_HOR = 4;

	/** Icon width equals height */
	public static int icon_W             = 56; // Icon width = height for the billboard

	/** Text size of distance textview */
	public static int TEXT_SIZE_DISTANCE = 16;

	/** Text Color  */
	public static int textColor = Color.BLACK;

	/**
	 * Create the texture to dress the billboard.
	 * 
	 * @param billBoardTitle   :  Text of the title
	 * @param distSTR             :  Text of the distance
	 * @param bmicon           :  icon as bitmap
	 * @return                 :  String of locally saved bitmap file of the texture
	 */

	public static Bitmap createBillboardTextureBitmap(String billBoardTitle, int dist, Bitmap bmicon, 
			Context ctx, Resources resources, int r_Backr_Drawable) {

		try {

			//-------- Distance string -----------
			String distSTR = "";
			if (dist < 1000)
				distSTR = Integer.toString((int) dist) + "m";
			else {
				int d = (int) (dist / 1000);
				distSTR = Integer.toString(d) + "km";
			}

			//-------------- ---------------------------

			final String texturepath = ctx.getCacheDir() + "/" + billBoardTitle + ".png";
			Paint mPaint = new Paint();

			//reading billboard background
			Bitmap mBackgroundImage = BitmapFactory.decodeResource(resources, r_Backr_Drawable) ;
			Bitmap billboard = mBackgroundImage.copy(Bitmap.Config.ARGB_8888, true);
			Canvas c = new Canvas(billboard);

			//-------------- Title---------------------
			mPaint.setColor(textColor);
			mPaint.setTextSize(TEXT_SIZE);
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setTypeface(Typeface.SANS_SERIF);
			mPaint.setTextAlign(Paint.Align.LEFT);
			int y = TEXT_SIZE; // start of text vertical

			int x = icon_W + 2*TEXT_MARGIN_HOR; // start of text horizontal

			// Draw POI name
			if (billBoardTitle.length() > 0) {
				String titleSTR = billBoardTitle.trim();

				final int maxWidth = billboard_W - x - 3*TEXT_MARGIN_HOR;   
				//-----------------------------------

				int indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);

				while (!(titleSTR.substring(indexBreak-1, indexBreak).equals(" ")) && indexBreak > 0){
					indexBreak -= 1;
				}

				if (indexBreak <= 1){
					indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);
				}


				//----------------------------------
				c.drawText(titleSTR.substring(0, indexBreak), x + 2*TEXT_MARGIN_HOR , y, mPaint);

				// Draw second line if valid
				if (indexBreak < titleSTR.length()) {
					titleSTR = titleSTR.substring(indexBreak);
					y += TEXT_SIZE;
					indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);

					if (indexBreak < titleSTR.length()) {
						indexBreak = mPaint.breakText(titleSTR, true, maxWidth - x, null);
						c.drawText(titleSTR.substring(0, indexBreak) + "...", x + 2*TEXT_MARGIN_HOR, y, mPaint);
					} else 	{
						c.drawText(titleSTR.substring(0, indexBreak), x+ 2*TEXT_MARGIN_HOR, y, mPaint);
					}
				}
			}

			//-------------- Distance -------------
			if (distSTR.length()>0){
				mPaint.setTextSize(TEXT_SIZE_DISTANCE);
				mPaint.setTextAlign(Paint.Align.CENTER);
				c.drawText(distSTR, billboard_W/2, billboard_H-40, mPaint);
			}

			//-------icon -------
			c.drawBitmap(bmicon, 2*TEXT_MARGIN_HOR, 2*TEXT_MARGIN_HOR, mPaint);

			// --- Save Bitmap to a file --------------
			return billboard;
			//			try {
			//				FileOutputStream out = new FileOutputStream(texturepath);
			//				billboard.compress(Bitmap.CompressFormat.PNG, 90, out);
			//				MetaioDebug.log("Texture file is saved to "+texturepath);
			//				return texturepath;
			//			} catch (Exception e) {
			//				MetaioDebug.log("Failed to save texture file");
			//				e.printStackTrace();
			//			}
			//
			//			billboard.recycle();
			//			billboard = null;

		} catch (Exception e) {
			
			Log.e("MetaioDebug", "Error creating billboard texture: " + e.getMessage());
//			MetaioDebug.log("Error creating billboard texture: " + e.getMessage());
//			MetaioDebug.printStackTrace(Log.DEBUG, e);
			return null;
		}
		//return null;

	}

	public static String createBillboardTexturePath(String billBoardTitle, int dist, Bitmap bmicon, 
			Context ctx, Resources resources, int r_Backr_Drawable) {

		try {

			//-------- Distance string -----------
			String distSTR = "";
			if (dist < 1000)
				distSTR = Integer.toString((int) dist) + "m";
			else {
				int d = (int) (dist / 1000);
				distSTR = Integer.toString(d) + "km";
			}

			//-------------- ---------------------------

			final String texturepath = ctx.getCacheDir() + "/" + billBoardTitle + ".png";
			Paint mPaint = new Paint();

			//reading billboard background
			Bitmap mBackgroundImage = BitmapFactory.decodeResource(resources, r_Backr_Drawable) ;
			Bitmap billboard = mBackgroundImage.copy(Bitmap.Config.ARGB_8888, true);
			Canvas c = new Canvas(billboard);

			//-------------- Title---------------------
			mPaint.setColor(textColor);
			mPaint.setTextSize(TEXT_SIZE);
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setTypeface(Typeface.SANS_SERIF);
			mPaint.setTextAlign(Paint.Align.LEFT);
			int y = TEXT_SIZE; // start of text vertical

			int x = icon_W + 2*TEXT_MARGIN_HOR; // start of text horizontal

			// Draw POI name
			if (billBoardTitle.length() > 0) {
				String titleSTR = billBoardTitle.trim();

				final int maxWidth = billboard_W - x - 3*TEXT_MARGIN_HOR;   
				//-----------------------------------

				int indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);

				while (!(titleSTR.substring(indexBreak-1, indexBreak).equals(" ")) && indexBreak > 0){
					indexBreak -= 1;
				}

				if (indexBreak <= 1){
					indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);
				}


				//----------------------------------
				c.drawText(titleSTR.substring(0, indexBreak), x + 2*TEXT_MARGIN_HOR , y, mPaint);

				// Draw second line if valid
				if (indexBreak < titleSTR.length()) {
					titleSTR = titleSTR.substring(indexBreak);
					y += TEXT_SIZE;
					indexBreak = mPaint.breakText(titleSTR, true, maxWidth, null);

					if (indexBreak < titleSTR.length()) {
						indexBreak = mPaint.breakText(titleSTR, true, maxWidth - x, null);
						c.drawText(titleSTR.substring(0, indexBreak) + "...", x + 2*TEXT_MARGIN_HOR, y, mPaint);
					} else 	{
						c.drawText(titleSTR.substring(0, indexBreak), x+ 2*TEXT_MARGIN_HOR, y, mPaint);
					}
				}
			}

			//-------------- Distance -------------
			if (distSTR.length()>0){
				mPaint.setTextSize(TEXT_SIZE_DISTANCE);
				mPaint.setTextAlign(Paint.Align.CENTER);
				c.drawText(distSTR, billboard_W/2, billboard_H-40, mPaint);
			}

			//-------icon -------
			c.drawBitmap(bmicon, 2*TEXT_MARGIN_HOR, 2*TEXT_MARGIN_HOR, mPaint);

			// --- Save Bitmap to a file --------------
			try {
				FileOutputStream out = new FileOutputStream(texturepath);
				billboard.compress(Bitmap.CompressFormat.PNG, 90, out);
				
				Log.v("MetaioDebug","Texture file is saved to "+texturepath);
				//MetaioDebug.log("Texture file is saved to "+texturepath);
				return texturepath;
			} catch (Exception e) {
				Log.e("MetaioDebug","Failed to save texture file");
				//MetaioDebug.log("Failed to save texture file");
				//e.printStackTrace();
			}

			billboard.recycle();
			billboard = null;

		} catch (Exception e) {
			Log.e("MetaioDebug","Error creating billboard texture: " + e.getMessage());
			//MetaioDebug.log("Error creating billboard texture: " + e.getMessage());
			//MetaioDebug.printStackTrace(Log.DEBUG, e);
			return null;
		}
		return null;
	}

}
