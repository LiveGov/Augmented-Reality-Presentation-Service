var trooper = undefined;
var timerIDMarker1 = undefined;
var timerIDMarker2 = undefined;
var AllObjects = new Array();
var NObj = undefined;

// if the scene is ready start your javascript
arel.sceneReady(function() {

	$("#top").hide();
	$("#down").hide();
	$("#left").hide();
	$("#right").hide();
	
	//Just for Debuging purposes
	//arel.Debug.activate();
	//arel.Debug.activateArelLogStream();
	
	AllObjects = arel.Scene.getObjects();
	NObj       = arel.Scene.getNumberOfObjects();

	//set a listener to tracking to get information about when the image is tracked
	arel.Events.setListener(arel.Scene, function(type, param){trackingHandler(type, param);});

	setEventListener();
});


function trackingHandler(type, param)
{
	//check if there is tracking information available
	if(param[0] !== undefined)
	{
		//if the pattern is found, hide the information to hold your phone over the pattern
		if(type && type == arel.Events.Scene.ONTRACKING && param[0].getState() == arel.Tracking.STATE_TRACKING)
		{
            trooper  = AllObjects[param[0].getCoordinateSystemID()];
        	$("#top").show();
        	$("#down").show();
        	$("#left").show();
        	$("#right").show();
        }
		//if the pattern is lost tracking, show the information to hold your phone over the pattern
		else if(type && type == arel.Events.Scene.ONTRACKING && param[0].getState() == arel.Tracking.STATE_NOTTRACKING)
		{
			$("#top").hide();
			$("#down").hide();
			$("#left").hide();
			$("#right").hide();
		}
	}
};


//based on which marker being tracked, the model is being rotated
function turnModel(direction)
{
	//turn the model in the "direction"
	//get the old rotation value
	var oldRotation = trooper.getRotation().getEulerAngleDegrees();
	
	oldRotation.setZ(oldRotation.getZ() + 4*direction);
	
//	document.write(Math.round(oldRotation.getX()) + " " + 
//			       Math.round(oldRotation.getY()) + " " + 
//			       Math.round(oldRotation.getZ()) + "<br>");
	
	//get the new rotation and apply it to the lego man
	var newRotation = new arel.Rotation();
	newRotation.setFromEulerAngleDegrees(oldRotation);
	trooper.setRotation(newRotation);
	
	timerIDMarker1 = setTimeout(function(){turnModel(direction);}, 2);
}


//based on which marker being tracked, the model is being rotated
function scaleModel(direction)
{
	
	if (trooper.getScale().getX() + 3*direction >= 0)
	  trooper.setScale(new arel.Vector3D(trooper.getScale().getX() + 3*direction,
									     trooper.getScale().getY() + 3*direction,
									     trooper.getScale().getZ() + 3*direction));
	
	timerIDMarker2 = setTimeout(function(){scaleModel(direction);}, 2);
}




function setEventListener() {
	

	// left button
	$("#left").bind("touchstart", function() {
		turnModel(1);
	});

	$("#left").bind("touchend", function() {
		clearTimeout(timerIDMarker1);
	});

	
	$("#right").bind("touchstart", function() {
		turnModel(-1);
	});

	$("#right").bind("touchend", function() {
		clearTimeout(timerIDMarker1);
	});
	

	// top button
	$("#top").bind("touchstart", function() {
		scaleModel(1);
	});

	$("#top").bind("touchend", function() {
		clearTimeout(timerIDMarker2);
	});

	// down button
	$("#down").bind("touchstart", function() {
		scaleModel(-1);
	});

	$("#down").bind("touchend", function() {
		clearTimeout(timerIDMarker2);
	});

}