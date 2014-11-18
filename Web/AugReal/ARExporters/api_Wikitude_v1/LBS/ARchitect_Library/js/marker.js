var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;

function Marker(poiData) {

    this.poiData = poiData;
    this.isSelected = false;

    this.animationGroup_idle = null;
    this.animationGroup_selected = null;

    var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);

    var drawables = new Array();
    
    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.1, {
        enabled: false,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    this.radarCircle = new AR.Circle(0.03, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {fillColor: "#ffffff"}
    });

    this.radarCircleSelected = new AR.Circle(0.05, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {fillColor: "#0066ff"}
    });

    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);

    this.radardrawablesSelected = [];
    this.radardrawablesSelected.push(this.radarCircleSelected);
    
    this.markerDrawable_idle = new AR.HtmlDrawable({html:"<html>"+
	"<head><meta name='viewport' content='target-densitydpi=device-dpi, width=device-width, user-scalable=no' /></head>"+
	"<body style='margin-top:160px;'>"+
	"<div style='border-radius:50%;width:11px;height:11px;background:#aaa;margin-left:121px'></div>"+// The circle
    "<div style='border-left:3px solid #ccc; height:100px; margin-left:125px; margin-top:0px'></div>"+  // The line vertically
    "<div style='width:254px;height:100px;border-radius:5px;border:1px solid #444;background:#ddd;opacity:0.8'>" +
    "<table style='float:left;width:170px;height:100px'><tr><td>"+
    "<div style='margin-left:3px;font-size:16pt;font-family:Verdana;font-weight:bold;width:150px;min-height:52px;-ms-word-break: break-all;word-break:break-all;word-break:break-word;-webkit-hyphens:auto;-moz-hyphens:auto;hyphens:auto;'>"+
		poiData.title + "</div>" +
    "</td></tr><tr><td>"+
    "<div style='font-size:14pt;font-family:Verdana;text-align:right;margin-right:20px'>"+MetricDistConversion(markerLocation.distanceToUser())+
    "</div>" 
    +"</td></tr></table>"
		+"<img style='margin:2px;border-radius:8px;width:80px;max-width:80px;max-height:120px' src='"+poiData.markerImage+"'/>"+
			"</div></body></html>", 
	},2.5,{
		viewportWidth:264, // the Geo width
		viewportHeight:384, // the Geo height
		scale:2.5,
		zOrder: 1,
		onClick : Marker.prototype.getOnClickTrigger(this),
		offsetX:0,
		offsetY:0,
		updateRate:AR.HtmlDrawable.UPDATE_RATE.HIGH,
		clickThroughEnabled:true
	});
//    

//    this.markerDrawable_idle = new AR.HtmlDrawable({html:"<html>"+
//    	"<head><meta name='viewport' content='target-densitydpi=device-dpi, width=device-width, user-scalable=no' /></head>"+
//    	"<body><div style='position:absolute;"+
//    					"left:0;top:0;width:252px;height:128px;border-radius:25px;border:2px solid #333;background:#999;'>"+
//    					
//    		//"<img style=''>" + // src='components/com_ar/controllers/ARchitect_Library/assets/marker_idle.png'
//    		"<img style='position:absolute;top:5px;right:10px;border-radius:8px;' width='80px' height='80px' src='"+poiData.markerImage+"'>"+
//    		"<div style='margin:8px;display:block;overflow: hidden;font-size:18pt;font-family:Arial;font-weight:bold;color:#FFF;width:150px;height:128px;word-break:break-all;'>"+
//    		poiData.title + //.trunc(34)+
//    		"</div><div style='position:absolute;bottom:5px;right:10px;font-size:18pt;font-family:Arial;font-weight:bold;color:#FFF;text-align:center'>"
//    		+MetricDistConversion(markerLocation.distanceToUser())+"</div></body></html>",
//    	},2.5,{
//    		viewportWidth:256,
//    		scale:2.5,
//    		zOrder: 1,
//    		onClick : Marker.prototype.getOnClickTrigger(this),
//    		offsetX:0,
//    		offsetY:0,
//    		updateRate:AR.HtmlDrawable.UPDATE_RATE.HIGH,
//    		clickThroughEnabled:true
// 	});

    drawables.push(this.markerDrawable_idle);
    
//    this.markerDrawable_selected = new AR.ImageDrawable(World.markerDrawable_selected, 2.5, {
//        zOrder: 0,
//        opacity: 0.0,
//        onClick: null
//    });
//
//    this.titleLabel = new AR.Label(poiData.title.trunc(8), 0.35, {
//        zOrder: 1,
//        offsetY: 0.55,
//        offsetX:-0.7,
//        horizontalAnchor:AR.CONST.HORIZONTAL_ANCHOR.CENTER,
//        style: {
//            textColor: '#FFFFFF',
//            fontStyle: AR.CONST.FONT_STYLE.NORMAL
//        }
//    });
//
//    this.descriptionLabel = new AR.Label(poiData.description.trunc(20), 0.25, {
//        zOrder: 1,
//        offsetY: 0.15,
//        offsetX:-0.5,
//        horizontalAnchor:AR.CONST.HORIZONTAL_ANCHOR.CENTER,
//        style: {
//            textColor: '#FFFFFF'
//        }
//    });


    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.1, {
        enabled: false,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    this.radarCircle = new AR.Circle(0.03, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {
            fillColor: "#ffffff"
        }
    });

    this.radarCircleSelected = new AR.Circle(0.05, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {
            fillColor: "#0066ff"
        }
    });

    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);

    this.radardrawablesSelected = [];
    this.radardrawablesSelected.push(this.radarCircleSelected);

//    // Changed: 
//    this.markerObject = new AR.GeoObject(markerLocation, {
//        drawables: {
//            cam: [this.markerDrawable_idle, this.markerDrawable_selected, this.titleLabel, this.descriptionLabel],
//            indicator: this.directionIndicatorDrawable,
//            radar: this.radardrawables
//        }
//    });
    
    // Changed: 
    this.markerObject = new AR.GeoObject(markerLocation, {
        drawables: {
            cam: drawables, //this.markerDrawable_selected,this.descriptionLabel,
            indicator: this.directionIndicatorDrawable,
            radar: this.radardrawables
        }
    });

    return this;
}

Marker.prototype.getOnClickTrigger = function(marker) {

    return function() {

        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (marker.isSelected) {

                Marker.prototype.setDeselected(marker);

            } else {
                Marker.prototype.setSelected(marker);
                try {
                    World.onMarkerSelected(marker);
                } catch (err) {
                    alert(err);
                }

            }
        } else {
            AR.logger.debug('a animation is already running');
        }


        return true;
    };
};

Marker.prototype.setSelected = function(marker) {

    marker.isSelected = true;
    // New: 
    	if (marker.animationGroup_selected === null) {

            var hideIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.4, 
            		kMarker_AnimationDuration_ChangeDrawable);

            marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, 
            		[hideIdleDrawableAnimation]);
        }

        marker.markerDrawable_idle.onClick = null;

        marker.directionIndicatorDrawable.enabled = true;
        marker.animationGroup_selected.start();

        marker.markerObject.drawables.radar = marker.radardrawablesSelected;
        
//        var hideIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.0, kMarker_AnimationDuration_ChangeDrawable);
//        var showSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0.8, kMarker_AnimationDuration_ChangeDrawable);
//
//        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//
//        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//
//        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [hideIdleDrawableAnimation, showSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
//    }
//
//    marker.markerDrawable_idle.onClick = null;
//    marker.markerDrawable_selected.onClick = Marker.prototype.getOnClickTrigger(marker);
//
//    marker.directionIndicatorDrawable.enabled = true;
//    marker.animationGroup_selected.start();
//
//    marker.markerObject.drawables.radar = marker.radardrawablesSelected;
};

Marker.prototype.setDeselected = function(marker) {

	marker.isSelected = false;

    marker.markerObject.drawables.radar = marker.radardrawables;

    if (marker.animationGroup_idle === null) {
        var showIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle,
        		"opacity", null, 1, kMarker_AnimationDuration_ChangeDrawable);

        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [showIdleDrawableAnimation]);
    }

    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);

    marker.directionIndicatorDrawable.enabled = false;
    marker.animationGroup_idle.start();
	
//    marker.isSelected = false;
//
//    marker.markerObject.drawables.radar = marker.radardrawables;
//
//    if (marker.animationGroup_idle === null) {
//
//        var showIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.8, kMarker_AnimationDuration_ChangeDrawable);
//        var hideSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0, kMarker_AnimationDuration_ChangeDrawable);
//
//        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//
//        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
//            amplitude: 2.0
//        }));
//
//        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [showIdleDrawableAnimation, hideSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
//    }
//
//    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);
//    marker.markerDrawable_selected.onClick = null;
//
//    marker.directionIndicatorDrawable.enabled = false;
//    marker.animationGroup_idle.start();
};

Marker.prototype.isAnyAnimationRunning = function(marker) {

    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};

// will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo..."
String.prototype.trunc = function(n) {
    return this.substr(0, n - 1) + (this.length > n ? '...' : '');
};

function MetricDistConversion(m){
	if (m>1000){
		m = Math.floor(m)/1000;
		return Math.floor(m*10)/10 +' km';	
	} else {
		return Math.floor(m)+' m';
	}
};