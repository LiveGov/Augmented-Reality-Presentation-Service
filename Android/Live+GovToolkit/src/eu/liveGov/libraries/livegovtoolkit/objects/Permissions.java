package eu.liveGov.libraries.livegovtoolkit.objects;

import java.util.ArrayList;

public class Permissions
{
    
    private ArrayList<PermissionObject> permissions;
    
    public ArrayList<PermissionObject> getPermissions()
    {
	return permissions;
    }
    
    public void setPermissions( ArrayList<PermissionObject> permissions )
    {
	this.permissions = permissions;
    }
}
