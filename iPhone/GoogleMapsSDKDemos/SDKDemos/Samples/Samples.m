#import "Samples.h"

// Map Demos
#import "SDKDemos/Samples/BasicMapViewController.h"
#import "SDKDemos/Samples/CustomIndoorViewController.h"
#import "SDKDemos/Samples/MapTypesViewController.h"
#import "SDKDemos/Samples/MyLocationViewController.h"
#import "SDKDemos/Samples/TrafficMapViewController.h"
#import "SDKDemos/Samples/IndoorViewController.h"
#import "SDKDemos/Samples/GestureControlViewController.h"

// Panorama Demos
#import "SDKDemos/Samples/PanoramaViewController.h"
#import "SDKDemos/Samples/FixedPanoramaViewController.h"

// Overlay Demos
#import "SDKDemos/Samples/MarkersViewController.h"
#import "SDKDemos/Samples/CustomMarkersViewController.h"
#import "SDKDemos/Samples/MarkerEventsViewController.h"
#import "SDKDemos/Samples/PolygonsViewController.h"
#import "SDKDemos/Samples/PolylinesViewController.h"
#import "SDKDemos/Samples/GroundOverlayViewController.h"
#import "SDKDemos/Samples/TileLayerViewController.h"

// Camera Demos
#import "SDKDemos/Samples/CameraViewController.h"
#import "SDKDemos/Samples/FitBoundsViewController.h"
#import "SDKDemos/Samples/MapLayerViewController.h"

// Services
#import "SDKDemos/Samples/GeocoderViewController.h"

@implementation Samples

+ (NSArray *)loadSections {
  return @[@"Map", @"Panorama", @"Overlays", @"Camera", @"Services"];
}

+ (NSArray *)loadDemos {
  NSArray *mapDemos =
  @[[self newDemo:[BasicMapViewController class]
        withTitle:@"Basic Map"
   andDescription:nil],
    [self newDemo:[MapTypesViewController class]
        withTitle:@"Map Types"
   andDescription:nil],
    [self newDemo:[TrafficMapViewController class]
        withTitle:@"Traffic Layer"
   andDescription:nil],
    [self newDemo:[MyLocationViewController class]
        withTitle:@"My Location"
   andDescription:nil],
    [self newDemo:[IndoorViewController class]
        withTitle:@"Indoor"
   andDescription:nil],
    [self newDemo:[CustomIndoorViewController class]
        withTitle:@"Indoor with Custom Level Select"
   andDescription:nil],
    [self newDemo:[GestureControlViewController class]
        withTitle:@"Gesture Control"
   andDescription:nil]];

  NSArray *panoramaDemos =
  @[[self newDemo:[PanoramaViewController class]
        withTitle:@"Street View"
   andDescription:nil],
    [self newDemo:[FixedPanoramaViewController class]
        withTitle:@"Fixed Street View"
   andDescription:nil]];

  NSArray *overlayDemos =
  @[[self newDemo:[MarkersViewController class]
        withTitle:@"Markers"
   andDescription:nil],
    [self newDemo:[CustomMarkersViewController class]
        withTitle:@"Custom Markers"
   andDescription:nil],
    [self newDemo:[MarkerEventsViewController class]
        withTitle:@"Marker Events"
   andDescription:nil],
    [self newDemo:[PolygonsViewController class]
        withTitle:@"Polygons"
   andDescription:nil],
    [self newDemo:[PolylinesViewController class]
        withTitle:@"Polylines"
   andDescription:nil],
    [self newDemo:[GroundOverlayViewController class]
        withTitle:@"Ground Overlays"
   andDescription:nil],
    [self newDemo:[TileLayerViewController class]
        withTitle:@"Tile Layers"
   andDescription:nil]];

  NSArray *cameraDemos =
  @[[self newDemo:[FitBoundsViewController class]
        withTitle:@"Fit Bounds"
   andDescription:nil],
    [self newDemo:[CameraViewController class]
        withTitle:@"Camera Animation"
   andDescription:nil],
    [self newDemo:[MapLayerViewController class]
        withTitle:@"Map Layer"
   andDescription:nil]];

  NSArray *servicesDemos =
  @[[self newDemo:[GeocoderViewController class]
        withTitle:@"Geocoder"
   andDescription:nil]];

  return @[mapDemos, panoramaDemos, overlayDemos, cameraDemos, servicesDemos];
}

+ (NSDictionary *)newDemo:(Class) class
                withTitle:(NSString *)title
           andDescription:(NSString *)description {
  return [[NSDictionary alloc] initWithObjectsAndKeys:class, @"controller",
          title, @"title", description, @"description", nil];
}
@end
