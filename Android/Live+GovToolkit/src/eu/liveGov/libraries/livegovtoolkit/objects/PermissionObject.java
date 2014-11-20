package eu.liveGov.libraries.livegovtoolkit.objects;

public class PermissionObject {
	private String name;
	private String description;
	
	public PermissionObject(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	
}
