package eu.liveGov.libraries.livegovtoolkit.Utils;

public class Constants {
	
	public static final String developerMail = "thisdeveloper@supercompany.com";
	
	public static final String EmptyString = "";

	public static final int LOCATION_MIN_DISTANCE = 20; //meter
	public static final int LOCATION_REMOVE_UPDATES = 1000; //ms
	public static final int LOCATION_REMOVE_UPDATES_LONG = 30000; //ms
	public static final int LOCATION_POLLING_INTERVAL = 10000;// ms
	
	// Predefined location
	public static float locUserPred_Lat = 43.1780f;  
	public static float locUserPred_Long=   -3.0753f;
	
	// Download Helper variables
	public static final String SERVICE_CENTER = "https://urbanplanning.yucat.com/servicecenter/api/";// "https://testserviceCenter.yucat.com/ServiceCenter/api/";
	public static final String GET_PERMISSIONS = "account/permission";
	public static final String POST_CREATE_ANONYMOUS_USER = "user/anonymous";
	public static final String POST_LOG_FILE = "diagnostics/log";
	public static final int MODULE_ID = 3; // This application has module id 3

	// BasicAuth account Service Center
	public static final String UP_SERVICE_CENTER_USER_NAME = "up_client_account";
	public static final String UP_SERVICE_CENTER_PASSWORD = "tsyucat";

	public static final String DIALOG_AND_VISUALIZATION_SERVICE = "https://urbanplanning.yucat.com/dialogandvisualization/api/"; //"https://urbanplanning-test.yucat.com/DialogAndVisualization/api/";
	public static final String GET_QUESTIONNAIRE = "questionnaire/";
	public static final String POST_QUESTIONNAIRE = "questionnaire/";
	
	// BasicAuth account Dialog and visualization service
	public static final String UP_DIALOG_AND_VISUALIZATION_SERVICE_USER_NAME = UP_SERVICE_CENTER_USER_NAME;
	public static final String UP_DIALOG_AND_VISUALIZATION_SERVICE_PASSWORD = UP_SERVICE_CENTER_PASSWORD;
}
