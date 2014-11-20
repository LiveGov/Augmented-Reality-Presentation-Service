package eu.liveGov.libraries.livegovtoolkit.objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Store item (urban plan) for the list or details view.
 * 
 * @copyright   Copyright (C) 2012 - 2014 Information Technology Institute ITI-CERTH. All rights reserved.
 * @license     GNU Affero General Public License version 3 or later; see LICENSE.txt
 * @author      Dimitrios Ververidis for the Multimedia Group (http://mklab.iti.gr). 
 *
 */

public class ProposalObject implements Parcelable
{
	private int id;
	private String code;
	private String objectCode;
	private int type;
	private String title;
	private String description;
	private double lat;
	private double lng;
	private byte[] imageData = new byte[0];
	private String image;

	private static final Logger logger = LoggerFactory.getLogger( ProposalObject.class );

	public ProposalObject()
	{
	}

	public int get_id()
	{
		return id;
	}

	public void set_id( int _id )
	{
		this.id = _id;
	}

	public String get_code()
	{
		return code;
	}

	public void set_code( String code )
	{
		this.code = code;
	}

	public String get_objectCode()
	{
		return objectCode;
	}

	public void set_objectCode( String objectCode )
	{
		this.objectCode = objectCode;
	}

	public int get_type()
	{
		return type;
	}

	public void set_type( int _type )
	{
		this.type = _type;
	}

	public String get_title()
	{
		return title;
	}

	public void set_title( String _title )
	{
		this.title = _title;
	}

	public String get_description()
	{
		return description;
	}

	public void set_description( String _description )
	{
		this.description = _description;
	}

	public double get_lat()
	{
		return lat;
	}

	public void set_lat( double _lat )
	{
		this.lat = _lat;
	}

	public double get_lng()
	{
		return lng;
	}

	public void set_lng( double _lng )
	{
		this.lng = _lng;
	}

	public byte[] get_imageData()
	{

		if ( imageData != null || imageData.length > 0 )
		{
			return imageData;
		}

		return null;
	}

	public void set_imageData( byte[] _imageData )
	{
		this.imageData = _imageData;
	}

	public Bitmap get_image( int width_pixels, int height_pixels )
	{
		Bitmap bitImage = null;
		if ( imageData != null && imageData.length > 0 )
		{
			try
			{
				bitImage = BitmapFactory.decodeByteArray( imageData, 0, imageData.length );
			} catch ( Exception ex )
			{
				Log.e( "MobileApplicationBase.ProposalObject", "get_image", ex );
			}
		} else if ( image != null && !image.isEmpty() )
		{
			try
			{
				int scale = 2;
				BitmapFactory.Options options = new BitmapFactory.Options();
				if ( width_pixels != 0 && height_pixels != 0 )
				{
					scale = 1;
					options.inJustDecodeBounds = true;
					
					BitmapFactory.decodeFile( image, options );

					int width_tmp = options.outWidth;
					int height_tmp = options.outHeight;

					while ( scale < 10 )
					{ // find the correct scaling.
						if ( ( width_tmp / 2 ) < width_pixels || ( height_tmp / 2 ) < height_pixels )
							break;
						width_tmp /= 2;
						height_tmp /= 2;
						scale++;
					}
				}
				options.inJustDecodeBounds = false;
				options.inSampleSize = scale;
	
				bitImage = BitmapFactory.decodeFile( image, options );
			} catch ( OutOfMemoryError e )
			{
				logger.error( "get_image;Out of mem: {}", e.getCause() );
				return null;
			} catch ( Exception e )
			{
				logger.error( "get_image;Exception: {}", e.getMessage() );
				return null;
			}
		}
		return bitImage;
	}

	public void set_imageURL( String _image )
	{
		this.image = _image;
	}

	public boolean HasImage()
	{
		if ( ( image != null ) || ( ( imageData != null ) && ( imageData.length > 0 ) ) )
		{
			return true;
		}
		return false;
	}

	/*
	 * parcelable functions
	 */

	public ProposalObject( Parcel parcel )
	{
		id = parcel.readInt();
		code = parcel.readString();
		objectCode = parcel.readString();
		type = parcel.readInt();
		title = parcel.readString();
		description = parcel.readString();
		lat = parcel.readDouble();
		lng = parcel.readDouble();
		parcel.readByteArray( imageData );
		image = parcel.readString();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel( Parcel dest, int flags )
	{
		dest.writeInt( id );
		dest.writeString( code );
		dest.writeString( objectCode );
		dest.writeInt( type );
		dest.writeString( title );
		dest.writeString( description );
		dest.writeDouble( lat );
		dest.writeDouble( lng );
		dest.writeByteArray( imageData );
		dest.writeString( image );
	}

	public static Creator<ProposalObject> CREATOR = new Creator<ProposalObject>()
			{
		public ProposalObject createFromParcel( Parcel parcel )
		{
			return new ProposalObject( parcel );
		}

		public ProposalObject[] newArray( int size )
		{
			return new ProposalObject[size];
		}
			};

}
