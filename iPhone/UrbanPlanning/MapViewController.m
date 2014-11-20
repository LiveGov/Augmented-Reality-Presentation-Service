/*

 Copyright (c) 2013 Joan Lluch <joan.lluch@sweetwilliamsl.com>
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is furnished
 to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

*/

#import "MapViewController.h"
#import "SWRevealViewController.h"

@implementation MapViewController
{
    GMSMapView *mapView_;
}

@synthesize entityData;
#pragma mark - View lifecycle

- (void)viewDidLoad
{
	[super viewDidLoad];
	
//	self.title = NSLocalizedString(@"Map", nil);
    if ([self respondsToSelector:@selector(setNeedsStatusBarAppearanceUpdate)]) {
        // iOS 7
        [self performSelector:@selector(setNeedsStatusBarAppearanceUpdate)];
    } else {
        // iOS 6
        [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationSlide];
    }
    locationManager = [[CLLocationManager alloc] init];
    gotInitialLocation = NO;
    locationManager.delegate = self;
    [locationManager startUpdatingLocation];
    SWRevealViewController *revealController = [self revealViewController];
    
    //[self.navigationController.navigationBar addGestureRecognizer:revealController.panGestureRecognizer];
    
    UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
        style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
    
    self.navigationItem.leftBarButtonItem = revealButtonItem;
    
//    UIBarButtonItem *rightRevealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
//        style:UIBarButtonItemStyleBordered target:revealController action:@selector(rightRevealToggle:)];
//    
//    self.navigationItem.rightBarButtonItem = rightRevealButtonItem;
    // Create a GMSCameraPosition that tells the map to display the
    // coordinate -33.86,151.20 at zoom level 6.
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:mapView_.myLocation.coordinate.latitude
                                                            longitude:mapView_.myLocation.coordinate.longitude
                                                                 zoom:6];
    mapView_ = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    mapView_.myLocationEnabled = YES;
    mapView_.delegate = self;
    self.view = mapView_;
    
    [self.navigationController.navigationBar setBackgroundImage:[UIImage new]
                                                  forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.shadowImage = [UIImage new];
    self.navigationController.navigationBar.translucent = YES;
    self.navigationController.view.backgroundColor = [UIColor clearColor];
    
    AppDelegate *del = [UIApplication sharedApplication].delegate;
    self.entityData = del.entityData;
    if(self.entityData)
    {
        for(AREntity *entity in self.entityData.entities)
        {
            GMSMarker *marker = [GMSMarker markerWithPosition:CLLocationCoordinate2DMake(entity.latitude, entity.longitude)];
//            marker.title = entity.title_en;
            marker.title = [entity getLocalizedTitle];
//            marker.snippet = entity.description_en;
            marker.snippet = [entity getLocalizedDescription];
            marker.map = mapView_;
            marker.userData = entity;
        }
    }
}
-(void)mapView:(GMSMapView *)mapView didTapInfoWindowOfMarker:(GMSMarker *)marker
{
    DetailedViewController *detailedViewController = [[DetailedViewController alloc] initWithNibName:@"DetailedViewController" bundle:nil entity:marker.userData];
    [self.navigationController pushViewController:detailedViewController animated:YES];
}
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    if(!gotInitialLocation)
    {
        CLLocation *loc = [locations objectAtIndex:0];
        [mapView_ setCamera:[GMSCameraPosition cameraWithLatitude:loc.coordinate.latitude longitude:loc.coordinate.longitude zoom:13.0]];
        gotInitialLocation = YES;
        [locationManager stopUpdatingLocation];
    }
}

- (UIView *)mapView:(GMSMapView *)mapView markerInfoWindow:(GMSMarker *)marker
{
    NSArray *nibContents = [[NSBundle mainBundle] loadNibNamed:@"MapInfoView" owner:nil options:nil];
    NSString *titleText = [NSString stringWithFormat:@"%@", marker.title];
    CGSize size = [titleText sizeWithFont:[UIFont boldSystemFontOfSize:17.0]];
    CGFloat viewWidth;
    if(size.width > 256.0)
    {
        viewWidth = 336.0f;
    }
    else
    {
        viewWidth = size.width + 80.0f;
    }
    MapInfoView *plainView = [nibContents lastObject];
    plainView.frame = CGRectMake(0.0, 0.0, viewWidth, 89.0);
    plainView.titleLabel.text = marker.title;
    plainView.titleLabel.shadowColor = [UIColor lightGrayColor];
    plainView.titleLabel.shadowOffset = CGSizeMake(0.0, 1.0);
    plainView.addressLabel.text = marker.snippet;
    plainView.addressLabel.shadowColor = [UIColor lightGrayColor];
    plainView.addressLabel.shadowOffset = CGSizeMake(0.0, 1.0);
    plainView.backgroundColor = [UIColor clearColor];
    plainView.clipsToBounds = NO;
    CALayer *containerViewLayer = [plainView.containerView layer];
    containerViewLayer.shadowColor = [UIColor blackColor].CGColor;
    containerViewLayer.borderWidth = 1.0f;
    containerViewLayer.borderColor = [UIColor blackColor].CGColor;
    containerViewLayer.shadowOffset = CGSizeMake(0.0, 0.2);
    containerViewLayer.shadowOpacity = 0.5f;
    containerViewLayer.shadowRadius = 7.0f;
    containerViewLayer.cornerRadius = 7.0f;
    CAGradientLayer *gradient = [[CAGradientLayer alloc] init];
    gradient.colors = [NSArray arrayWithObjects:(id)[UIColor whiteColor].CGColor, (id)[UIColor colorWithWhite:0.3 alpha:1.0f].CGColor, nil];
    [containerViewLayer addSublayer:gradient ];
    containerViewLayer.masksToBounds = NO;
    return plainView;
}

-(BOOL)prefersStatusBarHidden
{
    return NO;
}

- (void)viewWillAppear:(BOOL)animated
{
    self.screenName = @"Map View";
}
//- (void)viewWillAppear:(BOOL)animated
//{
//    [super viewWillAppear:animated];
//    NSLog( @"%@: MAP", NSStringFromSelector(_cmd));
//}
//
//- (void)viewWillDisappear:(BOOL)animated
//{
//    [super viewWillDisappear:animated];
//    NSLog( @"%@: MAP", NSStringFromSelector(_cmd));
//}
//
//- (void)viewDidAppear:(BOOL)animated
//{
//    [super viewDidAppear:animated];
//    NSLog( @"%@: MAP", NSStringFromSelector(_cmd));
//}
//
//- (void)viewDidDisappear:(BOOL)animated
//{
//    [super viewDidDisappear:animated];
//    NSLog( @"%@: MAP", NSStringFromSelector(_cmd));
//}

@end