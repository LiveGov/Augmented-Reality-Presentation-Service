package eu.liveGov.libraries.livegovtoolkit.objects;

import java.util.ArrayList;

public class ServiceApiError
{
    public final static int STATUS_OK = 200;
    public final static int STATUS_BAD_REQUEST = 400;
    public final static int STATUS_NOT_AUTHORIZED = 401;
    public final static int STATUS_NOT_FOUND = 404;
    public final static int STATUS_UNPROCESSABLE_ENTITY = 422;
    public final static int STATUS_INTERNAL_SERVER_ERROR = 500;
    
    private String message;
    private ArrayList<ServiceApiErrorObject> errors;
    private int statuscode;
    
    public String getMessage()
    {
	return message;
    }
    
    public void setMessage( String message )
    {
	this.message = message;
    }
    
    public ArrayList<ServiceApiErrorObject> getErrors()
    {
	return errors;
    }
    
    public void setErrors( ArrayList<ServiceApiErrorObject> errors )
    {
	this.errors = errors;
    }

    public int getStatuscode()
    {
	return statuscode;
    }

    public void setStatuscode( int statuscode )
    {
	this.statuscode = statuscode;
    }
}
