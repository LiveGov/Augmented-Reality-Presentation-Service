package eu.liveGov.libraries.livegovtoolkit.objects.questionaire;

import java.util.ArrayList;

public class Questionaire
{
	private ArrayList<Category> categories;
	private String code;
	private String objectcode;
	private String usermessage;
	private int id;
	private String description;
	private String title;
	private String lat;
	private String lng;


	public Questionaire()
	{
		categories = new ArrayList<Category>();
	}

	public Questionaire( Questionaire q )
	{
		categories = q.categories;
		code = q.code;
		id = q.id;
		objectcode = q.objectcode;
		description = q.description;
		usermessage = q.usermessage;
		title = q.title;
		lat = q.lat;
		lng = q.lng;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean CategoryWithIdExists( int id )
	{
		return false;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode( String code )
	{
		this.code = code;
	}

	public String getObjectCode()
	{
		return objectcode;
	}

	public void setObjectCode( String objectCode )
	{
		this.objectcode = objectCode;
	}

	public String getDescription()
	{
		return description;
	}

	public String getLat()
	{
		return lat;
	}

	public String getLng()
	{
		return lng;
	}
	
	public void setDescription( String description )
	{
		this.description = description;
	}

	public void setLat( String lat )
	{
		this.lat = lat;
	}
	
	public void setLng( String lng )
	{
		this.lng = lng;
	}
	
	public ArrayList<Category> getCategories()
	{
		if ( categories == null )
			return new ArrayList<Category>();
		return categories;
	}

	public void setCategories( ArrayList<Category> categories )
	{
		this.categories = categories;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public boolean isFilledIn()
	{
		for(Category categorie : categories)
		{
			if( !categorie.isFilledIn() )
				return false;
		}
		return true;
	}

	public boolean isPartlyFilledIn()
	{
		for(Category categorie : categories)
		{
			if( categorie.isPartlyFilledIn() )
				return true;
		}
		return false;
	}

	public String getUsermessage()
	{
		return usermessage;
	}

	public void setUsermessage( String usermessage )
	{
		this.usermessage = usermessage;
	}

}
