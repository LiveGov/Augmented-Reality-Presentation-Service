//
//  ARELViewController.m
//
// Copyright 2007-2013 metaio GmbH. All rights reserved.
//

#import "ARELViewController.h"


#include "TargetConditionals.h"		// to know if we're building for SIMULATOR
#include <metaioSDK/IMetaioSDKIOS.h>
#include <metaioSDK/IARELInterpreterIOS.h>


#import "EAGLView.h"
#import <UIKit/UIGestureRecognizerSubclass.h>


@interface MetaioTouchesRecognizer : UIGestureRecognizer
{
    UIViewController* theLiveViewController;
}
- (void) setTheLiveViewController:(UIViewController*) controller;
@end

@implementation MetaioTouchesRecognizer

- (void) setTheLiveViewController:(UIViewController*) controller
{
    theLiveViewController = controller;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    if( theLiveViewController )
    {
        [theLiveViewController touchesBegan:touches withEvent:event];
    }
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    if( theLiveViewController && ([self numberOfTouches] == 1) )
    {
        [theLiveViewController touchesMoved:touches withEvent:event];
    }
}


- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    if( theLiveViewController )
    {
        [theLiveViewController touchesEnded:touches withEvent:event];
    }
}

@end


@implementation ARELViewController


@synthesize arelWebView;
@synthesize arelFile;
@synthesize filterBillboards;
@synthesize idApp;
@synthesize visualRecognition;
@synthesize previousScaleF;

#define DLog(...) NSLog(__VA_ARGS__)


#pragma mark - Setup

- (id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil ARMode: (NSString*) mode arelInstructionsOrNil: (NSString*) arelConfigOrXmlStr
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if(self) {
        if([mode isEqualToString:@"vr"])
        {
            [self initObjectVisualRecognition];
        }
        else if([mode isEqualToString:@"imc"])
        {
            [self initCustomLocationBased];
        }
        else if([mode isEqualToString:@"ibs"])
        {
            [self initARELImageSearchWithArelFilePath:arelConfigOrXmlStr];
        }
        else if([mode isEqualToString:@"lbs"])
        {
            [self initARELLocationBasedWithArelFilePath:arelConfigOrXmlStr];
        }
        else if([mode isEqualToString:@"lbsnonarel"])
        {
            [self initNONARELLocationBasedWithXMLString:arelConfigOrXmlStr];
        }
        self.arMode = mode;
        busy = YES;
        timerRunning = NO;
        self.idApp = 0; // -2 used for debugging
    }
    return self;
}

- (void) initObjectVisualRecognition
{
    self.visualRecognition = YES;
    self.resultField.hidden = NO;
}

- (void) initCustomLocationBased //BETA
{
    self.visualRecognition = NO;
    self.resultField.hidden = YES;
}

- (void) initARELLocationBasedWithArelFilePath: (NSString*) arelFilePath
{
    self.visualRecognition = NO;
    self.resultField.hidden = YES;
    self.arelFile = [NSString stringWithString:arelFilePath];
}

- (void) initARELImageSearchWithArelFilePath: (NSString*) arelFilePath
{
    self.visualRecognition = NO;
    self.resultField.hidden = YES;
    self.arelFile = [NSString stringWithString:arelFilePath];
}

- (void) initNONARELLocationBasedWithXMLString: (NSString*) xmlString
{
    self.visualRecognition = NO;
    self.resultField.hidden = YES;
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_AREL/LBS/index.php"]];
    [request startSynchronous];
    NSString *xmlString2 = [request responseString];
    NSError *parseError;
    self.objects = (NSArray*) [[[XMLReader dictionaryForXMLString:xmlString2 error:&parseError] objectForKey:@"results"] objectForKey:@"object"];
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
//    [locationManager startUpdatingLocation];
}


#pragma mark - Start Activities
- (void) startObjectVisualRecognition
{
    self.resultField.hidden = NO;
    busy = NO;
}

- (void) startCustomLocationBased //BETA
{
    m_metaioSDK->setTrackingConfiguration("GPS");
//    billboardGroup = m_metaioSDK->createBillboardGroup(100.0, 800.0);
//    billboardGroup->setBillboardExpandFactors(0.8, 3, 10);
//    billboardGroup->setDistanceWeightFactor(1);
//    billboardGroup->setViewCompressionValues(500.0, 3000.0);
    m_metaioSDK->setLLAObjectRenderingLimits(5 * 1000, 1000 * 1000 * 1);
    m_metaioSDK->setRendererClippingPlaneLimits(5, 1000 * 1000 * 1);
    //Radar needs the latest metaio sdk to work  (version 5.0 +)
    
    m_radar = m_metaioSDK->createRadar();
    UIImage *image = [UIImage imageNamed:@"ic_radar.png"];
    CFDataRef rawData = CGDataProviderCopyData(CGImageGetDataProvider(image.CGImage));
    unsigned char* buf = (unsigned char*) CFDataGetBytePtr(rawData);
    metaio::ImageStruct imgStruct = metaio::ImageStruct();
    imgStruct.buffer = buf;
    imgStruct.height = image.size.height;
    imgStruct.width = image.size.width;
    imgStruct.colorFormat = metaio::common::ECF_A8R8G8B8;
    m_radar->setBackgroundTexture("rad_backg", imgStruct);
    
    UIImage *objImage = [UIImage imageNamed:@"red.png"];
    CFDataRef objRawData = CGDataProviderCopyData(CGImageGetDataProvider(objImage.CGImage));
    unsigned char* buf2 = (unsigned char*) CFDataGetBytePtr(objRawData);
    metaio::ImageStruct objStruct = metaio::ImageStruct();
    objStruct.buffer = buf2;
    objStruct.height = objImage.size.height;
    objStruct.width = objImage.size.width;
    objStruct.colorFormat = metaio::common::ECF_A8R8G8B8;
    m_radar->setObjectsDefaultTexture("rad_obj", objStruct);
    m_radar->setRelativeToScreen(metaio::IGeometry::ANCHOR_TC);
//    DataHandler *dHandler = [[DataHandler alloc] init];
//    dHandler.delegate = self;
//    for(AREntity *entity in entityData.entities)
//    {
//        [dHandler getModel:entity Number:0];
//    }
//    //Example data for the AR View - each issue has title and coordinates
////    for(AREntity *entity in entityData.entities)
////    {
//////        UIImage *image = [self getBillboardImageForTitle:[issue objectForKey:@"title"]];//Draws the title of the issue on top of the billboard image - Can be used to depict more information
////        NSLog(@"num models = %d", entity.numModels);
////        metaio::IGeometry *model = m_metaioSDK->createGeometry([[entity get3dPathForModel:1] UTF8String]);
////        metaio::LLACoordinate coord;
////        coord.latitude = entity.latitude;
////        coord.longitude = entity.longitude;
////        coord.altitude = entity.altitude;
////        model->setTranslationLLA(coord);
////        model->setLLALimitsEnabled(true);
////        model->setScale(1000.0);
////        model->setName([[NSString stringWithFormat:@"%@_1",entity.identifier] UTF8String]);
////        metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2, 0, 0));
//////            metaio::Vector3d *rot = *new metaio::Vector3d(M_PI_2, 0, 0);
////        model->setRotation(rot);
////        m_radar->add(model);
////    }
//    [self performSelectorInBackground:@selector(loadEntities) withObject:nil];
    
//    UIButton *fixMyPosition = [[UIButton alloc] initWithFrame:CGRectMake(40.0, 200.0, 100.0, 100.0)];
//    UIButton *fixMyPosition = [UIButton buttonWithType:UIButtonTypeCustom];
//    fixMyPosition.frame = CGRectMake(40.0, 200.0, 100.0, 100.0);
//    [fixMyPosition addTarget:self action:@selector(fixMyPosition) forControlEvents:UIControlEventTouchUpInside];
//    [fixMyPosition setTitle:@"Fix Position" forState:UIControlStateNormal];
//    fixMyPosition.layer.borderColor = [UIColor blueColor].CGColor;
//    [fixMyPosition setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];

//    [self.view addSubview:fixMyPosition];
//    [self.view bringSubviewToFront:fixMyPosition];
    [self loadEntities];
//    [locationManager startUpdatingHeading];
}

- (void) fixMyPosition
{
    [[[UIAlertView alloc] initWithTitle:@"Location" message:[NSString stringWithFormat:@"current location is = %f %f",lastLocation.coordinate.latitude, lastLocation.coordinate.longitude] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil] show];
    metaio::LLACoordinate manualLocation;
    manualLocation.latitude = lastLocation.coordinate.latitude;
    manualLocation.longitude = lastLocation.coordinate.longitude;
//    m_sensors->setManualLocation(manualLocation);
}

- (void)gotModelForEntity:(AREntity *)entity withPath:(NSString *)path
{
    
}

- (void) refreshDistances{
    bool shouldHide = (self.segmentCtrl3d.selectedSegmentIndex == 0) ? true : false;
    metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
    for(int i = 0; i < geometries.size(); i++){
        metaio::IGeometry *geometry = geometries[i];
        if(geometry->getType()== metaio::EGEOMETRY_TYPE_BILLBOARD){
            m_metaioSDK->unloadGeometry(geometry);
        }
    }
    for(AREntity *entity in entityData.entities){
        UIImage *billboardImageFinal;
        if(lastLocation){
            CLLocationDistance distance = [[[CLLocation alloc] initWithLatitude:entity.latitude longitude:entity.longitude] distanceFromLocation:lastLocation];
            billboardImageFinal = [self getBillboardImageForTitle:[entity getLocalizedTitle] Thumbnail:entity.dImage Distance:distance];
        }
        else
            billboardImageFinal = [self getBillboardImageForTitle:[entity getLocalizedTitle] Thumbnail:entity.dImage];
        metaio::LLACoordinate coord;
        coord.latitude = entity.latitude;
        coord.longitude = entity.longitude;
        metaio::IGeometry *billboard = m_metaioSDK->createGeometryFromCGImage([[entity getLocalizedTitle] UTF8String], billboardImageFinal.CGImage, true);
        billboard->setTranslationLLA(coord);
        billboard->setLLALimitsEnabled(true);
        billboard->setVisible(!shouldHide);
        billboard->setName([[NSString stringWithFormat:@"%@_1",entity.identifier] UTF8String]);
        billboardGroup->addBillboard(billboard);
        m_radar->add(billboard);
    }
}
- (void) loadEntities
{
    billboardGroup = m_metaioSDK->createBillboardGroup();
//    billboardGroup->setBillboardExpandFactors(0.8, 3, 10);
//    billboardGroup->setDistanceWeightFactor(1);
//    billboardGroup->setViewCompressionValues(500.0, 3000.0);
    for(AREntity *entity in entityData.entities)
    {
        //        UIImage *image = [self getBillboardImageForTitle:[issue objectForKey:@"title"]];//Draws the title of the issue on top of the billboard image - Can be used to depict more information
//        NSLog(@"num models = %d", entity.numModels);
        NSString *filename = [entity get3dPathForModel:1];
        if(!filename)
            continue;
        metaio::IGeometry *model = m_metaioSDK->createGeometry([filename UTF8String]);
        metaio::LLACoordinate coord;
        coord.latitude = entity.latitude;
        coord.longitude = entity.longitude;
        coord.altitude = entity.altitude;
        model->setTranslationLLA(coord);
        model->setLLALimitsEnabled(true);
        model->setScale(1000.0);
        model->setName([[NSString stringWithFormat:@"%@_1",entity.identifier] UTF8String]);
        metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2, 0, 0));
        //            metaio::Vector3d *rot = *new metaio::Vector3d(M_PI_2, 0, 0);
        model->setRotation(rot);
        m_radar->add(model);
        
        UIImage *billboardImageFinal;
        if(lastLocation){
            CLLocationDistance distance = [[[CLLocation alloc] initWithLatitude:coord.latitude longitude:coord.longitude] distanceFromLocation:lastLocation];
            billboardImageFinal = [self getBillboardImageForTitle:[entity getLocalizedTitle] Thumbnail:entity.dImage Distance:distance];
        }
        else{
            billboardImageFinal = [self getBillboardImageForTitle:[entity getLocalizedTitle] Thumbnail:entity.dImage];
        }
        metaio::IGeometry *billboard = m_metaioSDK->createGeometryFromCGImage([[entity getLocalizedTitle] UTF8String], billboardImageFinal.CGImage, true);
        billboard->setTranslationLLA(coord);
        billboard->setLLALimitsEnabled(true);
        billboard->setName([[NSString stringWithFormat:@"%@_1",entity.identifier] UTF8String]);
        billboardGroup->addBillboard(billboard);
        m_radar->add(billboard);
        
        
//        NSDictionary *location = [issue objectForKey:@"location"];
//        NSString *title = [[issue objectForKey:@"title"] objectForKey:@"text"];
//        coord.latitude = [[[location objectForKey:@"lat"] objectForKey:@"text"] floatValue];
//        coord.longitude = [[[location objectForKey:@"lon"] objectForKey:@"text"] floatValue];
//        coord.altitude = [[[location objectForKey:@"alt"] objectForKey:@"text"] floatValue];
//        NSData *billboardThumbnailData = [NSData dataWithContentsOfURL:[NSURL URLWithString:[[issue objectForKey:@"thumbnail"] objectForKey:@"text"]]];
//        UIImage *billboardThumbnailImage = [UIImage imageWithData:billboardThumbnailData];
//        UIImage *billboardImageFinal;
//        if(lastLocation){
//            CLLocation *issueLocation = [[CLLocation alloc] initWithLatitude:coord.latitude longitude:coord.longitude];
//            CLLocationDistance distance = [issueLocation distanceFromLocation:lastLocation];
//            billboardImageFinal = [self getBillboardImageForTitle:title Thumbnail:billboardThumbnailImage Distance:distance];
//            //                NSLog(@"inside if");
//        }
//        else
//        {
//            billboardImageFinal = [self getBillboardImageForTitle:title Thumbnail:billboardThumbnailImage];
//        }
//        //            billboard = m_metaioSDK->loadImageBillboard([title UTF8String], billboardImageFinal.CGImage);
//        billboard = m_metaioSDK->createGeometryFromCGImage([title UTF8String], billboardImageFinal.CGImage, true);
//        billboard->setTranslationLLA(coord);
//        billboard->setLLALimitsEnabled(true);
//        billboard->setName([issueId UTF8String]);
//        billboardGroup->addBillboard(billboard);
//        m_radar->add(billboard);
    }
    [self filter3dModels];
    arModeBtn.enabled = YES;
    self.segmentCtrl3d.hidden = NO;
    self.segmentCtrl3d.selectedSegmentIndex = 1;
}
- (void) startARELLocationBased
{
    m_ArelInterpreter->loadARELFile([self.arelFile UTF8String]);
}

- (void) nextModel
{
    NSString *filename = [curEntity get3dPathForModel:curModelNo+1];
    if(!filename)
        return;
    int curCoordinateid = curGeometry->getCoordinateSystemID();
    metaio::Vector3d curScale = curGeometry->getScale();
    metaio::Rotation curRotation = curGeometry->getRotation();
    m_metaioSDK->unloadGeometry(curGeometry);

    metaio::IGeometry *model = m_metaioSDK->createGeometry([filename UTF8String]);
    metaio::LLACoordinate coord;
    coord.latitude = curEntity.latitude;
    coord.longitude = curEntity.longitude;
    coord.altitude = curEntity.altitude;
    model->setTranslationLLA(coord);
    model->setLLALimitsEnabled(true);
//    model->setScale(1000.0);
    model->setScale(curScale);
    model->setName([[NSString stringWithFormat:@"%@_%d", curEntity.identifier,curModelNo +1] UTF8String]);
//    metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2,0,0));
    model->setRotation(curRotation);
    model->setCoordinateSystemID(curCoordinateid);
    m_radar->add(model);
    if(curModelNo+1 == curEntity.numModels)
    {
        [nextModel setEnabled:NO];
    }
    [previousModel setEnabled:YES];
    curGeometry = model;
    curModelNo ++;
}

- (void) modelView
{
    if(curEntity)
    {
        DetailedViewController *detailedViewController = [[DetailedViewController alloc] initWithNibName:@"DetailedViewController" bundle:nil entity:curEntity];
        [self.navigationController pushViewController:detailedViewController animated:YES];
    }
}

- (void) previousModel
{
    NSString *filename = [curEntity get3dPathForModel:curModelNo-1];
    if(!filename)
        return;
    int curCoordinateid = curGeometry->getCoordinateSystemID();
    metaio::Vector3d curScale = curGeometry->getScale();
    metaio::Rotation curRotation = curGeometry->getRotation();
    m_metaioSDK->unloadGeometry(curGeometry);
    metaio::IGeometry *model = m_metaioSDK->createGeometry([filename UTF8String]);
    metaio::LLACoordinate coord;
    coord.latitude = curEntity.latitude;
    coord.longitude = curEntity.longitude;
    coord.altitude = curEntity.altitude;
    model->setTranslationLLA(coord);
    model->setLLALimitsEnabled(true);
//    model->setScale(1000.0);
    model->setScale(curScale);
    model->setName([[NSString stringWithFormat:@"%@_%d", curEntity.identifier,curModelNo -1] UTF8String]);
    metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2,0,0));
//    model->setRotation(rot);
    model->setRotation(curRotation);
    model->setCoordinateSystemID(curCoordinateid);
    m_radar->add(model);
    if(curModelNo-1 == 1)
    {
        [previousModel setEnabled:NO];
    }
    [nextModel setEnabled:YES];
    curGeometry = model;
    curModelNo --;
}

//- (void) getModel:(NSInteger) modelNo ofGeometry:(metaio::IGeometry*) geometry forEntity:(AREntity*) entity
//{
//    m_metaioSDK->unloadGeometry(geometry);
//    metaio::IGeometry *model =  m_metaioSDK->createGeometry([[entity get3dPathForModel:modelNo] UTF8String]);
//    metaio::LLACoordinate coord;
//    coord.latitude = entity.latitude;
//    coord.longitude = entity.longitude;
//    coord.altitude = entity.altitude;
//    model->setTranslationLLA(coord);
//    model->setLLALimitsEnabled(true);
//    model->setScale(1000.0);
//    model->setName([[NSString stringWithFormat:@"%@_%d", entity.identifier, modelNo] UTF8String]);
//    metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2, 0, 0));
//    model->setRotation(rot);
//    m_radar->add(model);
//}

- (void) startNONARELLocationBased // not used in urban planning
{
    m_metaioSDK->setTrackingConfiguration("GPS");
    billboardGroup = m_metaioSDK->createBillboardGroup(100.0, 800.0);
    billboardGroup->setBillboardExpandFactors(0.8, 3, 10);
    billboardGroup->setDistanceWeightFactor(1);
    billboardGroup->setViewCompressionValues(500.0, 3000.0);
    m_metaioSDK->setLLAObjectRenderingLimits(10, 1000 * 1000 * 1);
    m_metaioSDK->setRendererClippingPlaneLimits(50, 1000 * 1000 * 1);
    
    //Radar needs the latest metaio sdk to work  (version 5.0 +)
    
    m_radar = m_metaioSDK->createRadar();
    UIImage *image = [UIImage imageNamed:@"ic_radar.png"];
    CFDataRef rawData = CGDataProviderCopyData(CGImageGetDataProvider(image.CGImage));
    unsigned char* buf = (unsigned char*) CFDataGetBytePtr(rawData);
    metaio::ImageStruct imgStruct = metaio::ImageStruct();
    imgStruct.buffer = buf;
    imgStruct.height = image.size.height;
    imgStruct.width = image.size.width;
    imgStruct.colorFormat = metaio::common::ECF_A8R8G8B8;
    m_radar->setBackgroundTexture("rad_backg", imgStruct);
    
    UIImage *objImage = [UIImage imageNamed:@"red.png"];
    CFDataRef objRawData = CGDataProviderCopyData(CGImageGetDataProvider(objImage.CGImage));
    unsigned char* buf2 = (unsigned char*) CFDataGetBytePtr(objRawData);
    metaio::ImageStruct objStruct = metaio::ImageStruct();
    objStruct.buffer = buf2;
    objStruct.height = objImage.size.height;
    objStruct.width = objImage.size.width;
    objStruct.colorFormat = metaio::common::ECF_A8R8G8B8;
    m_radar->setObjectsDefaultTexture("rad_obj", objStruct);
    m_radar->setRelativeToScreen(metaio::IGeometry::ANCHOR_TL);
    
    for(NSDictionary *issue in self.objects)
    {
        NSString *issueId = [issue objectForKey:@"id"];
//        NSLog(@"issueID = %@", issueId);
        if([issueId hasPrefix:@"3D"])
        {
            //Is 3d geometry
            NSDictionary *assets3d = [issue objectForKey:@"assets3d"];
            NSDictionary *model = [assets3d objectForKey:@"model"];
            NSString *modelUrlStr = [model objectForKey:@"text"];
            NSData *modelZipData = [NSData dataWithContentsOfURL:[NSURL URLWithString:modelUrlStr]];
            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            NSString *docDir = [paths objectAtIndex: 0];
            NSString *modelFileName = [NSString stringWithFormat:@"%@.zip", issueId];
            NSString *modelFilePath = [docDir stringByAppendingPathComponent: modelFileName];
            [modelZipData writeToFile: modelFilePath atomically: NO];
//            NSLog(@"modelFilePath = %s", [modelFilePath UTF8String]);
            metaio::IGeometry *issueGeometry  =  m_metaioSDK->createGeometry([modelFilePath UTF8String]);
//            NSLog(@"creating 3D Geometry");
            NSDictionary *location = [issue objectForKey:@"location"];
            metaio::LLACoordinate coord;
            coord.latitude = [[[location objectForKey:@"lat"] objectForKey:@"text"] doubleValue];
            coord.longitude = [[[location objectForKey:@"lon"] objectForKey:@"text"] doubleValue];
            coord.altitude = [[[location objectForKey:@"alt"] objectForKey:@"text"] doubleValue];
            issueGeometry->setTranslationLLA(coord);
            issueGeometry->setLLALimitsEnabled(true);
            issueGeometry->setScale(2500.0);
            issueGeometry->setRotation(metaio::Rotation(0.5 * M_PI, 0.0f, 0.0f),true);
            issueGeometry->setName([issueId UTF8String]);
            m_radar->add(issueGeometry);
        }
        else
        {
            //Is a billboard
            metaio::IGeometry *billboard;
            metaio::LLACoordinate coord;
            NSDictionary *location = [issue objectForKey:@"location"];
            NSString *title = [[issue objectForKey:@"title"] objectForKey:@"text"];
            coord.latitude = [[[location objectForKey:@"lat"] objectForKey:@"text"] floatValue];
            coord.longitude = [[[location objectForKey:@"lon"] objectForKey:@"text"] floatValue];
            coord.altitude = [[[location objectForKey:@"alt"] objectForKey:@"text"] floatValue];
            NSData *billboardThumbnailData = [NSData dataWithContentsOfURL:[NSURL URLWithString:[[issue objectForKey:@"thumbnail"] objectForKey:@"text"]]];
            UIImage *billboardThumbnailImage = [UIImage imageWithData:billboardThumbnailData];
            UIImage *billboardImageFinal;
            if(lastLocation){
                CLLocation *issueLocation = [[CLLocation alloc] initWithLatitude:coord.latitude longitude:coord.longitude];
                CLLocationDistance distance = [issueLocation distanceFromLocation:lastLocation];
                billboardImageFinal = [self getBillboardImageForTitle:title Thumbnail:billboardThumbnailImage Distance:distance];
//                NSLog(@"inside if");
            }
            else
            {
                billboardImageFinal = [self getBillboardImageForTitle:title Thumbnail:billboardThumbnailImage];
            }
            //            billboard = m_metaioSDK->loadImageBillboard([title UTF8String], billboardImageFinal.CGImage);
            billboard = m_metaioSDK->createGeometryFromCGImage([title UTF8String], billboardImageFinal.CGImage, true);
            billboard->setTranslationLLA(coord);
            billboard->setLLALimitsEnabled(true);
            billboard->setName([issueId UTF8String]);
            billboardGroup->addBillboard(billboard);
            m_radar->add(billboard);
        }
    }
    self.switch3d.hidden = NO;
    self.switch3d.on = NO;
    [self filter3dModels];
}

- (void) startARELImageBased
{
    m_ArelInterpreter->loadARELFile([self.arelFile UTF8String]);
}

- (void) onSDKReady
{
    if([self.arMode isEqualToString:@"vr"])
    {
        [self startObjectVisualRecognition];
    }
    else if([self.arMode isEqualToString:@"imc"]) //BETA
    {
        [self startCustomLocationBased];
    }
    else if([self.arMode isEqualToString:@"lbs"])
    {
        [self startARELLocationBased];
    }
    else if([self.arMode isEqualToString:@"ibs"])
    {
        [self startARELImageBased];
    }
    else if([self.arMode isEqualToString:@"lbsnonarel"])
    {
        [self startNONARELLocationBased];
    }
    [self.delegate sdkFinishedLoading];
}

- (void) onSceneReady
{
    if((!self.visualRecognition) && (self.idApp != -2))
    {
        [self.switch3d setHidden:NO];
        [self.switch3d setOn:NO];
        [self filter3dModels];
    }
    if([self.arMode isEqualToString:@"ibs"])
    {
        [self filterAllBillboards];
    }
    else if([self.arMode isEqualToString:@"lbs"])
    {
        [self filter3dModels];
    }
}

- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event
{
    if (motion == UIEventSubtypeMotionShake)
    {
        if(shakeToReload.alpha == 1.0f){
            [self fadeoutView];
            lastLocation = nil;
        }
//        [self shakeView:shakeToReload];
    }
}

- (void)shakeView:(UIView *)viewToShake
{
    CGFloat t = 2.0;
    CGAffineTransform translateRight  = CGAffineTransformTranslate(CGAffineTransformIdentity, t, 0.0);
    CGAffineTransform translateLeft = CGAffineTransformTranslate(CGAffineTransformIdentity, -t, 0.0);
    
    viewToShake.transform = translateLeft;
    
    [UIView animateWithDuration:0.07 delay:0.0 options:UIViewAnimationOptionAutoreverse|UIViewAnimationOptionRepeat animations:^{
        [UIView setAnimationRepeatCount:2.0];
        viewToShake.transform = translateRight;
    } completion:^(BOOL finished) {
        if (finished) {
            [UIView animateWithDuration:0.05 delay:0.0 options:UIViewAnimationOptionBeginFromCurrentState animations:^{
                viewToShake.transform = CGAffineTransformIdentity;
            } completion:NULL];
        }
    }];
}


- (void)viewDidLoad {
    [self becomeFirstResponder];
    initialUpdate = NO;
    dHandler = [[DataHandler alloc] init];
    self.segmentCtrl3d.hidden = YES;
    [self.segmentCtrl3d setTitle:[DataHandler getLocalizedString:@"Billboard"] forSegmentAtIndex:1];
    self.ibsRectView.hidden = YES;
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat screenHeight = screenRect.size.height;
    shakeToReload = [[UIView alloc] initWithFrame:CGRectMake(40.0, screenHeight - 140.0, 230.0, 50.0f)];
    shakeToReload.backgroundColor = [UIColor clearColor];
    shakeToReload.alpha = 0.0f;
    shakeToReload.layer.shadowColor = [[UIColor blackColor] CGColor];
    shakeToReload.layer.shadowOffset = CGSizeMake(0.0, 1.0);
    shakeToReload.layer.shadowOpacity = 0.9f;
    UILabel *shakeLabel = [[UILabel alloc] initWithFrame:CGRectMake(60.0, 10.0, 170.0, 40.0)];
//    shakeLabel.text = @"Shake to refresh position";
    shakeLabel.text = [DataHandler getLocalizedString:@"Shake to refresh position"];
    shakeLabel.textColor = [UIColor colorWithRed:0.0 green:122.0/255.0f blue:1.0 alpha:1.0f];
    shakeLabel.font = [UIFont italicSystemFontOfSize:13.0];
    shakeLabel.numberOfLines = 2;
    UIImageView *shakeImage = [[UIImageView alloc] initWithFrame:CGRectMake(10.0, 10.0, 40.0, 40.0)];
    shakeImage.image = [UIImage imageNamed:@"shake_black.png"];
    shakeImage.image = [shakeImage.image imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    shakeImage.tintColor = [UIColor colorWithRed:0.0 green:122.0/255.0 blue:1.0 alpha:1.0f];
    [shakeToReload addSubview:shakeLabel];
    [shakeToReload addSubview:shakeImage];
//    UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
//    [button setFrame:CGRectMake(50, 50, 100, 44)];
//    [button setImage:[UIImage imageNamed:@"img"] forState:UIControlStateNormal];
//    [button setImageEdgeInsets:UIEdgeInsetsMake(0, -30, 0, 0)];
//    [button setTitle:@"Abc" forState:UIControlStateNormal];
//    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
//    [button setBackgroundColor:[UIColor yellowColor]];
//    [view addSubview:button];
//    shakeToReload = [UIButton buttonWithType:UIButtonTypeCustom];
//    shakeToReload.frame = CGRectMake(0.0, screenHeight -140.0, 320.0, 50.0);
//    [shakeToReload setImage:[UIImage imageNamed:@"shake"] forState:UIControlStateNormal];
//    [shakeToReload setImageEdgeInsets:UIEdgeInsetsMake(0.0, -30.0, 0, 0)];
//    [shakeToReload setTitle:@"Shake to reload" forState:UIControlStateNormal];
//    shakeToReload.titleLabel.font = [UIFont italicSystemFontOfSize:13.0];
//    shakeToReload.titleLabel.textColor = [UIColor whiteColor];
//    shakeToReload.alpha = 0.0f;
//    shakeToReload.titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:shakeToReload];
    updateTime = [[NSDate date] timeIntervalSince1970];
//    gpsAccuracy = [[UILabel alloc] initWithFrame:CGRectMake(40.0, 250.0, 280.0, 100.0)];
//    gpsAccuracy.font = [UIFont systemFontOfSize:10.0f];
//    gpsAccuracy.textColor = [UIColor blueColor];
//    if(lastLocation)
//        gpsAccuracy.text = [NSString stringWithFormat:@"gps acc = %.1fm", lastLocation.horizontalAccuracy];
//    [self.view addSubview:gpsAccuracy];
//    [self.view bringSubviewToFront:gpsAccuracy];
    [super viewDidLoad];
    
    
    //    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:@"http://augreal.mklab.iti.gr/api_AREL/LBS/index.php"]];
    //    [request startSynchronous];
    //    NSString *xmlString = [request responseString];
    ////    NSLog(@"xmlString = %@", xmlString);
    //    // Parse the XML into a dictionary
    //    NSError *parseError = nil;
    ////    NSArray *arr = [(NSArray*)[[XMLReader dictionaryForXMLString:xmlString error:&parseError]objectForKey:@"results"]];
    //    self.objects = (NSArray*) [[[XMLReader dictionaryForXMLString:xmlString error:&parseError] objectForKey:@"results"] objectForKey:@"object"];
    //    NSLog(@"objects count = %d", [self.objects count]);
    //    NSDictionary *xmlDictionary = [[[XMLReader dictionaryForXMLString:xmlString error:&parseError] objectForKey:@"results"] objectForKey:@"object"];
    //    for(id key in xmlDictionary)
    //    {
    //        NSLog(@"key = %@", key);
    //        for(id key2 in [xmlDictionary objectForKey:key])
    //        {
    //            NSLog(@"key2 = %@", key2);
    //            for(id key3 in [[[xmlDictionary objectForKey:key] objectForKey:@"object"] ])
    //            {
    //                NSLog(@"key3 = %@", key3);
    //            }
    //        }
    //    }
    
    // Print the dictionary
    //    NSLog(@"%@", xmlDictionary);
	float version = [[[UIDevice currentDevice] systemVersion] floatValue];
    if( version >= 5.0 )
    {
        self.arelWebView.scrollView.bounces = NO;
    }
    else
    {
        for (id subview in self.arelWebView.subviews)
        {
            if ([[subview class] isSubclassOfClass: [UIScrollView class]])
            {
                ((UIScrollView *)subview).bounces = NO;
            }
        }
        
        [self.arelWebView setBackgroundColor:[UIColor clearColor]];
    }
    [self.resetButton setHidden:YES];
	lockObject = NO;
    MetaioTouchesRecognizer* recognizer = [[MetaioTouchesRecognizer alloc] init];
	[recognizer setTheLiveViewController:self];
    [recognizer setDelegate:self];
	[arelWebView addGestureRecognizer:recognizer];
   	[recognizer release];
    m_ArelInterpreter = metaio::CreateARELInterpreterIOS(arelWebView, self);
    m_ArelInterpreter->initialize( m_metaioSDK, NULL);
    
    m_ArelInterpreter->setRadarProperties(metaio::IGeometry::ANCHOR_TL, metaio::Vector3d(1), metaio::Vector3d(1));
	m_ArelInterpreter->registerDelegate(self);
    [self.resultField setHidden:YES];
    self.resultField.text = @"Tap screen to recognize";
    busy = YES;
    self.previousScaleF = 50.0;
    scaleFactor = 50.0;
    prevRecScale = 1.0;
    counter = 0;
    activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    self.resultField.alpha = 0.6f;
    self.resultField.layer.shadowColor = [UIColor blackColor].CGColor;
    self.resultField.layer.shadowOffset = CGSizeMake(0.0, -1.0f);
    self.resultField.layer.shadowOpacity = 1.0f;
    activityIndicator.center = self.view.center;
    [self.view addSubview:activityIndicator];
    if(self.idApp == -2 || self.visualRecognition)
    {
        [self.switch3d setHidden:YES];
    }
    else{
        [self.switch3d setHidden:YES];
        [self.switch3d setOnImage:[UIImage imageNamed:@"3dON.png"]];
        [self.switch3d setOffImage:[UIImage imageNamed:@"3dOFF.png"]];
    }
    SWRevealViewController *revealController = [self revealViewController];
    
    //[self.navigationController.navigationBar addGestureRecognizer:revealController.panGestureRecognizer];
    
    
    UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
                                                                         style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
    
    self.navigationItem.leftBarButtonItem = revealButtonItem;
    [self.navigationController.navigationBar setBackgroundImage:[UIImage new]
                                                  forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.shadowImage = [UIImage new];
    self.navigationController.navigationBar.translucent = YES;
    self.navigationController.view.backgroundColor = [UIColor clearColor];
    
//    [self.navigationController.toolbar setBackgroundColor:[UIColor colorWithWhite:0.0 alpha:0.2]];
//    self.navigationController.toolbar.barTintColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
//    self.navigationController.toolbar.tintColor = [[UIColor blackColor] colorWithAlphaComponent:0.1];
//    self.navigationController.toolbar
//    self.navigationController.toolbar.translucent = YES;
//    self.navigationController.toolbar.opaque = YES;
//    modelTitle = [[UIBarButtonItem alloc] initWithTitle:@""
//                                                                style:UIBarButtonSystemItemFlexibleSpace target:self action:@selector(myAction)];
    
    nextModel = [[UIBarButtonItem alloc] initWithTitle:@"->" style:UIBarButtonItemStyleDone target:self action:@selector(nextModel)];
    previousModel = [[UIBarButtonItem alloc] initWithTitle:@"<-" style:UIBarButtonItemStyleDone target:self action:@selector(previousModel)];
//    previousModel = [[[UIBarButtonItem alloc] initWithTitle:@"Item" style:UIBarButtonItemStyleBordered target:self action:@selector(btnItem1Pressed:)] autorelease];
    UIBarButtonItem *space1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
    UIBarButtonItem *space2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
    modelTitle = [[UIBarButtonItem alloc] initWithTitle:@"Change Model" style:UIBarButtonItemStyleDone target:self action:@selector(modelView)];
//    UIBarButtonItem *rightButton = [[[UIBarButtonItem alloc] initWithTitle:@"Item" style:UIBarButtonItemStyleBordered target:self action:@selector(btnItem2Pressed:)] autorelease];
    [self setToolbarItems:[NSArray arrayWithObjects:previousModel, space1, modelTitle, space2,nextModel,nil]];
    [self.navigationController.toolbar setBackgroundImage:[UIImage new] forToolbarPosition:UIBarPositionBottom barMetrics:UIBarMetricsDefault];
    self.navigationController.toolbar.translucent = YES;
   
    
    //method 2
//    [self.navigationController.toolbar setItems:[NSArray arrayWithObjects:button1,button2, button3,nil]];
    
    //method 3
//    self.navigationController.toolbarItems = [NSArray arrayWithObjects:button1,button2,button3, nil];
    
    //displaying toolbar
    [self.navigationController setToolbarHidden:YES];
    [self setNeedsStatusBarAppearanceUpdate]; //uncomment please
    AppDelegate *del = [UIApplication sharedApplication].delegate;
    entityData = del.entityData;
    
    arModeBtn = [[UIBarButtonItem alloc] initWithTitle:[DataHandler getLocalizedString:@"Scan"] style:UIBarButtonItemStyleDone target:self action:@selector(startIBS)];
    arModeBtn.enabled = NO;
//    [self.navigationController.navigationItem setRightBarButtonItem:arModeBtn];
    self.navigationItem.rightBarButtonItem = arModeBtn;
}

#pragma mark - Visual Recognition



- (IBAction)onResetBtnPressed:(id)sender {
    metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
    for( int i = 0; i < geometries.size(); i++)
    {
        m_metaioSDK->unloadGeometry(geometries[i]);
    }
    self.resultField.text = @"tap screen to recognize";
    lockObject = NO;
    [self.resetButton setHidden:YES];
    [self.resultField setHidden:NO];
    self.resultField.layer.backgroundColor = [UIColor whiteColor].CGColor;
    scaleFactor = 50.0;
    prevRecScale = 1.0;
}


- (IBAction)pinchDetected:(UIPinchGestureRecognizer*)sender {
    if(lockObject)
    {
        float currRatio = scaleFactor/50.0;
        scaleFactor = scaleFactor + 50*(sender.scale* prevRecScale - currRatio);
        scaleFactor = MAX(10.0, MIN(scaleFactor,1000.0));
        currentGeometry->setScale(scaleFactor);
        prevRecScale = sender.scale;
    }
}

- (IBAction)draggingAction:(UIPanGestureRecognizer*)sender {
    if(lockObject)
    {
        CGPoint speed = [sender velocityInView:self.view];
        float x = speed.y;
        float y = -speed.x;
        float z = 0.0;
        metaio::Vector3d newVec = *new metaio::Vector3d(x*3.14/180.0, y*3.14/180.0, z*3.14/180.0);
        metaio::Rotation rotation;
        rotation.setFromEulerAngleDegrees(newVec);
        currentGeometry->setRotation(rotation,true);
    }
}

- (void) requestAModelWithId:(NSNumber*)idex
{
    NSInteger ide = [idex integerValue];
    NSString *urlString = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%d/1/AR_%d_1junaio.zip",ide,ide];
    NSURL *url = [NSURL URLWithString:urlString];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    NSString *dir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filePath = [NSString stringWithFormat:@"%@/temp.zip", dir];
    [request setDownloadDestinationPath:filePath];
    [request startSynchronous];
    metaio::stlcompat::String file = [filePath UTF8String];
    currentGeometry = m_metaioSDK->createGeometry(file);
    currentGeometry->setCoordinateSystemID(0);
    metaio::Vector3d vector = *new metaio::Vector3d(0.0,0.0,-500.0);
    currentGeometry->setTranslation(vector);
    currentGeometry->setScale(50.0);

    
    lockObject = YES;
    [self.resetButton setHidden:NO];
}

- (BOOL)canBecomeFirstResponder
{
    return YES;
}
- (void) triggerSearch
{
    
    if(!busy && !lockObject)
    {
        busy = YES;
        NSString* dir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString* filePath = [NSString stringWithFormat:@"%@/targetImage.jpg", dir];
        m_metaioSDK->requestCameraImage([filePath UTF8String],480,640);
        [NSThread sleepForTimeInterval:0.8];
        UIImage *img = [[UIImage alloc] initWithContentsOfFile:filePath];
        NSData *jpegData = [NSData dataWithData:UIImageJPEGRepresentation(img, 0.8)];
        NSURL *url = [NSURL URLWithString:@"http://augreal.mklab.iti.gr/VisRec/recognizer.php"];
        ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:url];
        NSString *idAppStr = [NSString stringWithFormat:@"%d",5];
        [request setPostValue:idAppStr forKey:@"app_id"];
        [request setData:jpegData forKey:@"upload"];
        [request startSynchronous];
        if([request error] || ![request responseString])
        {
            busy = NO;
            return;
        }
//        NSLog(@"search response = %@", [request responseString]);
        NSString *response = [request responseString];
        NSArray *chunks = [response componentsSeparatedByString:@";"];
        if([chunks count] < 2)
        {
            busy = NO;
            NSArray *options = [NSArray arrayWithObjects:@"no result", [UIColor whiteColor], nil];
            [self performSelectorOnMainThread:@selector(updateResultLabelWithOptions:) withObject:options waitUntilDone:NO];
            return;
        }
        CGFloat prediction = [[chunks objectAtIndex:1] floatValue];
        NSInteger modelId;
        NSString *modelName;
        modelName = [chunks objectAtIndex:0];
        modelId = [[chunks objectAtIndex:2] integerValue];
        NSString *resultFieldText = [NSString stringWithFormat:@"result : %@ score:%f",modelName, prediction];
        CGFloat green,red,blue;
        if(prediction > 1.0)
        {
            green = 255.0;
            red = 0.0;
            blue = 0.0;
        }
        else if(prediction > 0.1)
        {
            green = 255.0;
            red = 255.0 - (255.0*prediction);
            blue = 255.0 - (255.0*prediction);
        }
        else if(prediction < -1.0)
        {
            green = 0.0;
            red = 255.0;
            blue = 0.0;
        }
        else if(prediction < -0.1)
        {
            green = 255.0 + (255.0*prediction);
            red = 255.0;
            blue = 255.0 + (255.0*prediction);
        }
        else{
            green = 255.0;
            red = 255.0;
            blue = 0.0;
        }
        UIColor *resultFieldColor = [UIColor colorWithRed:red/255.0 green:green/255.0 blue:blue/255.0 alpha:1.0];
        NSArray *options = [NSArray arrayWithObjects:resultFieldText, resultFieldColor, nil];
        [self performSelectorOnMainThread:@selector(updateResultLabelWithOptions:) withObject:options waitUntilDone:NO];
        if(prediction > 0.0 && modelId > 0)
        {
            [self performSelectorOnMainThread:@selector(requestAModelWithId:)  withObject:[NSNumber numberWithInt:modelId] waitUntilDone:NO];
        }
        busy = NO;
    }
}

- (void) updateResultLabelWithOptions: (NSArray*) options
{
    [UIView animateWithDuration:0.7f animations:^{
        self.resultField.text = [options objectAtIndex:0];
        self.resultField.layer.backgroundColor = [[options objectAtIndex:1] CGColor];
    }];
    [activityIndicator stopAnimating];
}

#pragma mark - Location Based Search
/*
 :currLocation
 if(!lastLocation){
    lastLocation = currLocation;
 }
 if(currLocation.acc < lastLocation.acc){
    updateLoc();
    lastLocation = currLocation;
 }
 else if(currLocation.acc == lastLocation.acc){
    distance = distance(currLocation,lastLocation);
    if(distance<=1.0f){
        updateLoc();
        lastLocation = currLocation;
    }
 }
 */
- (BOOL)locationManagerShouldDisplayHeadingCalibration:(CLLocationManager *)manager
{
    return YES;
}
- (void) playSound
{
    NSString* path = [[NSBundle mainBundle]
                      pathForResource:@"Beep" ofType:@"aiff"];
    NSURL* url = [NSURL fileURLWithPath:path];
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)url, &mBeep);
    
    // Play the sound
    AudioServicesPlaySystemSound(mBeep);
    
    // Dispose of the sound
    AudioServicesDisposeSystemSoundID(mBeep);
}
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    CLLocation *currLocation = [[locations objectAtIndex:0] copy];
    if(!lastLocation){
        //if there is no last location -> accept current location
        metaio::LLACoordinate manualLocation;
        manualLocation.latitude = currLocation.coordinate.latitude;
        manualLocation.longitude = currLocation.coordinate.longitude;
        m_sensors->setManualLocation(manualLocation);
        lastLocation = currLocation;
        if(!initialUpdate)
            initialUpdate = YES;
        else
            [self refreshDistances];
        //set update time
        updateTime = [[NSDate date] timeIntervalSince1970];
        return;
    }
    if(currLocation.horizontalAccuracy < lastLocation.horizontalAccuracy){
        //if received location with better accuracy accept it
        metaio::LLACoordinate manualLocation;
        manualLocation.latitude = currLocation.coordinate.latitude;
        manualLocation.longitude = currLocation.coordinate.longitude;
        m_sensors->setManualLocation(manualLocation);
        lastLocation = currLocation;
        //set update time
        updateTime = [[NSDate date] timeIntervalSince1970];
        //if shake view is shown -> hide it
        if(shakeToReload.alpha == 1.0f)
            [self fadeoutView];
    }
    else if(currLocation.horizontalAccuracy == lastLocation.horizontalAccuracy){
        //received location with same accuracy
        CGFloat distance = [currLocation distanceFromLocation:lastLocation];
        if(distance <= 1.0f){
            //if distance is less than 1 meter accept it (no jump effect on 3d models)
            lastLocation = currLocation;
            metaio::LLACoordinate manualLocation;
            manualLocation.latitude = currLocation.coordinate.latitude;
            manualLocation.longitude = currLocation.coordinate.longitude;
            m_sensors->setManualLocation(manualLocation);
            //set update time
            updateTime = [[NSDate date] timeIntervalSince1970];
            //if shake view is shown -> hide it
            if(shakeToReload.alpha == 1.0f)
                [self fadeoutView];
        }
        else{
            //if distance is more than 1 meter deny new location
            double now = [[NSDate date] timeIntervalSince1970];
            //if more than 15 sec have passed since last update -> show shake view
            if(now - updateTime > 15.0){
                if(shakeToReload.alpha == 0.0f){
                    //if enough time has passed and shake view is not shown -> show it
                    [self fadeinView];
                }
                else{
                    /*if enough time has passed and shake view is shown and distance is > 30m ->
                     shake the shake view (animation) to remind user to shake because
                     the accuracy of the AR location is too inaccurate
                     */
                    if(distance > 30.0f){
                        [self shakeView:shakeToReload];
                    }
                }
            }
        }
    }
    else
    {
        //accuracy of curr loc is worse
        double now = [[NSDate date] timeIntervalSince1970];
        //if 15 sec have passed since last update, show the shake view
        if(now - updateTime > 15.0){
            [self fadeinView];
            updateTime = now;
        }
    }
}

- (void) fadeinView{
    [UIView animateWithDuration:0.6f animations:^{
        shakeToReload.alpha = 1.0f;
    } completion:^ (BOOL finished){
        [self shakeView:shakeToReload];
    }];
}

- (void) fadeoutView {
    [UIView animateWithDuration:0.6f animations:^{
        shakeToReload.alpha = 0.0f;
    }];
}

- (IBAction)onSwitchValueChanged:(id)sender {
    if([sender isOn])
    {
        [self filterAllBillboards];
    }
    else{
        [self filter3dModels];
    }
}

- (IBAction)onSegmentCtrlChange:(id)sender {
    UISegmentedControl *sc = (UISegmentedControl*) sender;
    if(sc.selectedSegmentIndex == 0)
    {
        [self filterAllBillboards];
    }
    else{
        [self filter3dModels];
    }
}

- (void) filterAllBillboards
{
    metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
    for( int i = 0; i < geometries.size(); i++)
    {
        if(geometries[i]->getType()!=metaio::EGEOMETRY_TYPE_3D)
        {
            geometries[i]->setVisible(false);
        }
        else
        {
            geometries[i]->setVisible(true);
        }
    }
    self.resultField.text = @"tap screen to recognize";
    busy = NO;
}

- (void) filter3dModels
{
    metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
    for (int i = 0; i < geometries.size(); i++)
    {
        if(geometries[i]->getType()!= metaio::EGEOMETRY_TYPE_BILLBOARD)
        {
            geometries[i]->setVisible(false);
//            NSLog(@"hiding billboard");
        }
        else{
//            NSLog(@"showing 3d");
            geometries[i]->setVisible(true);
        }
    }
}


#pragma mark - Actions
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{

//    if(self.idApp == -2)
//        return;
//    if(self.visualRecognition){
//        if(!busy && !lockObject){
//            [activityIndicator startAnimating];
//            [self performSelectorInBackground:@selector(triggerSearch) withObject:nil];
//        }
//        return;
//    }
//    if([self.arMode isEqualToString:@"lbsnonarel"])
//    {
//        UITouch *touch = [touches anyObject];
//        CGPoint loc = [touch locationInView:glView];
//        int screenFactor;
//        if ([[UIScreen mainScreen] respondsToSelector:@selector(displayLinkWithTarget:selector:)] &&
//            ([UIScreen mainScreen].scale == 2.0)) {
//            // Retina display
//            screenFactor = 2;
//        } else {
//            // non-Retina display
//            screenFactor = 1;
//        }
//        int newx = (int) loc.x * screenFactor;
//        int newy = (int) loc.y * screenFactor;
//        metaio::IGeometry *geometry = m_metaioSDK->getGeometryFromScreenCoordinates(newx, newy);
//        if(geometry)
//        {
//            NSString *geometryId = [NSString stringWithUTF8String:geometry->getName().c_str()];
//            if([geometryId hasPrefix:@"3D"])
//            {
//                geometryId = [geometryId substringFromIndex:2];
//            }
//            NSLog(@"touched geometry with id %@", geometryId);
//            //define your own ViewController class here
//            TestViewController *detailController = [[TestViewController alloc] initWithNibName:@"TestViewController" bundle:nil modelId:[geometryId integerValue]];
//            [self presentViewController:detailController animated:YES completion:nil];
//            [detailController release];
//            // launch another view based on the geometry's name
//        }
//    }
//    else if([self.arMode isEqualToString:@"lbs"])
//    {
//        /* AREL based location based (using m_arelInterpreter) doesn't add recognizable tags to the geometries so there is no way to recognize which geometry has been touched.
//         A workaround has been implemented here to identify the touched geometry by the floating part of its altitude like it is shown below but it is recommended to use the NONAREL way instead.
//         Of course for this solution to work, it is required that the server passes the id of the entity in its altitude field.
//         */
//        UITouch *touch = [touches anyObject];
//        CGPoint loc = [touch locationInView:glView];
//        int screenFactor;
//        if ([[UIScreen mainScreen] respondsToSelector:@selector(displayLinkWithTarget:selector:)] &&
//            ([UIScreen mainScreen].scale == 2.0)) {
//            // Retina display
//            screenFactor = 2;
//        } else {
//            // non-Retina display
//            screenFactor = 1;
//        }
//        int newx = (int) loc.x * screenFactor;
//        int newy = (int) loc.y * screenFactor;
//        metaio::IGeometry *geometry = m_metaioSDK->getGeometryFromScreenCoordinates(newx, newy);
//        if(geometry)
//        {
//            metaio::LLACoordinate lla  = geometry->getTranslationLLA();
//            double geom_identifier = ((lla.altitude - floor(lla.altitude))*1000000);
//            NSNumber *number = [NSNumber numberWithDouble:geom_identifier];
//            NSInteger geomIdentifier = [number integerValue];
//            if(geomIdentifier%10 >0)
//            {
//                geomIdentifier++;
//            }
//            NSInteger geomIdentifierFinal = geomIdentifier/10;
//            TestViewController *detailController = [[TestViewController alloc] initWithNibName:@"TestViewController" bundle:nil modelId:geomIdentifierFinal];
//            [self presentViewController:detailController animated:YES completion:nil];
//            [detailController release];
//            
//        }
//    }
    // Here's how to pick a geometry
	UITouch *touch = [touches anyObject];
	CGPoint loc = [touch locationInView:glView];
    
    // get the scale factor (will be 2 for retina screens)
    float scale = glView.contentScaleFactor;
    int xCenter = loc.x * scale;
    int yCenter = loc.y * scale;
	// ask sdk if the user picked an object
	// the 'true' flag tells sdk to actually use the vertices for a hit-test, instead of just the bounding box
    for(int i = 0; i < 25; i++){
        int xPen = i/5 - 2;
        int yPen = i%5 - 2;
        int x = xPen + xCenter;
        int y = yPen + yCenter;
        curGeometry = m_metaioSDK->getGeometryFromScreenCoordinates(x, y, true);
        if(curGeometry)
            break;
    }
//    curGeometry = m_metaioSDK->getGeometryFromScreenCoordinates(loc.x * scale, loc.y * scale, true);
    
//    if ( model == m_metaioMan)
//	{
//		// we have touched the metaio man
//		// let's start an animation
//		model->startAnimation( "shock_down" , false);
//	}
//    UITouch *touch = [touches anyObject];
//    CGPoint loc = [touch locationInView:glView];
////    loc.x = loc.x *2;
////    loc.y = loc.y *2;
//    metaio::IGeometry *geometry = m_metaioSDK->getGeometryFromScreenCoordinates(loc.x, loc.y);
//    NSLog(@"x y = %f %f", loc.x, loc.y);
    if(curGeometry)
    {
//        NSLog(@"tapped geometry with name = %s", model->getName().c_str());
        NSString *name = [NSString stringWithUTF8String:curGeometry->getName().c_str()];
        NSString *_id = [[name componentsSeparatedByString:@"_"] objectAtIndex:0];
        curModelNo = [[[name componentsSeparatedByString:@"_"] objectAtIndex:1] integerValue];
        curEntity = [entityData getEntityByID:_id];
//        [modelTitle setTitle:curEntity.title_en];
        [modelTitle setTitle:[curEntity getLocalizedTitle]];
//        NSLog(@"entity title_en = %@", curEntity.title_en);
        if(curEntity.numModels == 1)
        {
            [nextModel setEnabled:NO];
            [previousModel setEnabled:NO];
//            [nextModel setTitle:@""];
//            [previousModel setTitle:@""];
        }
        else if(curEntity.numModels ==2)
        {
            if(curModelNo == 1)
            {
                [nextModel setEnabled:YES];
                [previousModel setEnabled:NO];
//                [nextModel setTitle:@"->"];
//                [previousModel setTitle:@""];
            }
            else
            {
                [nextModel setEnabled:NO];
                [previousModel setEnabled:YES];
//                [nextModel setTitle:@""];
//                [previousModel setTitle:@"<-"];
            }
        }
        else
        {
            if(curModelNo == 1)
            {
                [nextModel setEnabled:YES];
                [previousModel setEnabled:NO];
//                [nextModel setTitle:@"->"];
//                [previousModel setTitle:@""];
            }
            else if (curModelNo == curEntity.numModels-1)
            {
                [nextModel setEnabled:NO];
                [previousModel setEnabled:YES];
//                [nextModel setTitle:@""];
//                [previousModel setTitle:@"<-"];
            }
            else
            {
                [nextModel setEnabled:YES];
                [previousModel setEnabled:NO];
//                [nextModel setTitle:@"->"];
//                [previousModel setTitle:@"<-"];
            }
        }
//        [((UIButton*) modelTitle.customView) setTitle:entity.title_en forState:UIControlStateNormal];

        //method 1
        [self.navigationController setToolbarHidden:NO animated:YES];
    }
    else
    {
        [self.navigationController setToolbarHidden:YES animated:YES];
    }
}
- (void) startIBS
{
    self.segmentCtrl3d.hidden = YES;
    self.ibsRectView.hidden = NO;
    self.ibsRectView.layer.borderColor = [UIColor colorWithWhite:50.0/255.0f alpha:50.0/255.0f].CGColor;
    self.ibsRectView.layer.borderWidth = 10.0f;
    self.ibsRectView.layer.shadowColor = [UIColor blackColor].CGColor;
    self.ibsRectView.layer.shadowOffset = CGSizeMake(1.0, 1.0);
    self.ibsRectView.layer.shadowOpacity = 1.0f;
    m_radar->setVisible(false);
//    DataHandler *dHandler = [[DataHandler alloc] init];
    NSString *ibsPath = [dHandler getIBS];
    if(ibsPath){
        arModeBtn = [[UIBarButtonItem alloc] initWithTitle:[DataHandler getLocalizedString:@"Nearby"] style:UIBarButtonItemStyleDone target:self action:@selector(startLBS)];
        m_metaioSDK->setTrackingConfiguration([ibsPath UTF8String]);
        [self.navigationController setToolbarHidden:YES animated:YES];
        self.navigationItem.rightBarButtonItem = arModeBtn;
        metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
        NSInteger systemID = 1;
        for(int i = 0; i < geometries.size(); i++)
        {
            if(geometries[i]->getType()==metaio::EGEOMETRY_TYPE_3D){
                NSString *geometryIdentifier = [NSString stringWithUTF8String:geometries[i]->getName().c_str()];
                NSArray *chunks = [geometryIdentifier componentsSeparatedByString:@"_"];
                NSString *objectID = [chunks objectAtIndex:0];
                NSLog(@"objectID = %@", objectID);
                AppDelegate *del = [[UIApplication sharedApplication] delegate];
                AREntity *entity = [del.entityData getEntityByID:objectID];
                if(entity.shouldTrack){
                    //                geometries[i]->setScale(10);
                    NSLog(@"entityScale = %f", entity.scale);
                    geometries[i]->setScale(entity.scale);
                    //                geometries[i]->setScale(1000.0);
                    if(geometries[i]->isVisible())
                        NSLog(@"geom is visible");
                    else
                        NSLog(@"geom is not visible");
                    if(geometries[i]->getIsRendered())
                        NSLog(@"geom is rendered");
                    else
                        NSLog(@"geom is not rendered");
                    metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(0,entity.rotation / 180.0f * M_PI, 0));
                    NSLog(@"rot y = %f", entity.rotation / 180.0f * M_PI);
                    geometries[i]->setRotation(rot);
                    geometries[i]->setCoordinateSystemID(systemID);
                    geometries[i]->setVisible(true);
                    systemID++;
                }
                else{
                    geometries[i]->setVisible(false);
                }
            }
            else{
                geometries[i]->setVisible(false);
            }
        }
    }
    else
    {
        //error
    }
}

- (void) startLBS
{
    self.segmentCtrl3d.hidden = NO;
    self.ibsRectView.hidden = YES;
    m_radar->setVisible(true);
    arModeBtn = [[UIBarButtonItem alloc] initWithTitle:[DataHandler getLocalizedString:@"Scan"] style:UIBarButtonItemStyleDone target:self action:@selector(startIBS)];
    m_metaioSDK->setTrackingConfiguration("GPS");
    metaio::stlcompat::Vector<metaio::IGeometry*> geometries = m_metaioSDK->getLoadedGeometries();
//    NSLog(@"geometries size = %lu", geometries.size());
    bool show3d;
    if(self.segmentCtrl3d.selectedSegmentIndex == 0)
        show3d = true;
    else
        show3d = false;
    for(int i = 0; i < geometries.size(); i++)
    {
        if(geometries[i]->getType()==metaio::EGEOMETRY_TYPE_3D){
            geometries[i]->setScale(1000.0);
            metaio::Rotation rot = *new metaio::Rotation(*new metaio::Vector3d(M_PI_2,0, 0));
            geometries[i]->setRotation(rot);
            geometries[i]->setVisible(show3d);
        }
        else{
            geometries[i]->setScale(1.0);
            geometries[i]->setVisible(!show3d);
        }
        geometries[i]->setCoordinateSystemID(1);
    }
    [self.navigationController setToolbarHidden:YES animated:YES];
    self.navigationItem.rightBarButtonItem = arModeBtn;
}
- (BOOL)prefersStatusBarHidden
{
    return YES;
}
- (IBAction)onCloseBtnPressed:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Utilities

- (UIImage*) getBillboardImageForTitle: (NSString*) title Thumbnail: (UIImage*) thumb Distance: (CGFloat) distance
{
    UIImage *bgImage = [UIImage imageNamed:@"poi.bundle/POI_bg.png"];
    NSString *distanceStr;
//    NSLog(@"distance = %f", distance);
    if(distance < 1000)
    {
        distanceStr = [NSString stringWithFormat:@"%d m", (int)floor(distance)];
    }
    else
    {
        distanceStr = [NSString stringWithFormat:@"%.1f km", distance/1000.0f];
    }
    UIFont *font = [UIFont boldSystemFontOfSize:8];
    UIFont *distanceFont = [UIFont boldSystemFontOfSize:11];
    UIGraphicsBeginImageContext(bgImage.size);
    [bgImage drawInRect:CGRectMake(0,0, bgImage.size.width , bgImage.size.height)];
    CGRect rect = CGRectMake(40.0, 3.0, bgImage.size.width - 41.0, bgImage.size.height);
    [[UIColor whiteColor] set];
    [title drawInRect:CGRectIntegral(rect) withFont:font];
//    NSLog(@"thumb size = %f %f", thumb.size.width, thumb.size.height);
    [thumb drawInRect:CGRectMake(3.0, 3.0, 36.0, 36.0)];
    CGRect distanceDrawRect = CGRectMake(50.0, 35.0, bgImage.size.width, bgImage.size.height);
    [distanceStr drawInRect:CGRectIntegral(distanceDrawRect) withFont:distanceFont];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}
//draw a billboard with title and thumbnail
- (UIImage*) getBillboardImageForTitle: (NSString*) title Thumbnail: (UIImage*) thumb
{
    UIImage *bgImage = [UIImage imageNamed:@"poi.bundle/POI_bg.png"];
    UIFont *font = [UIFont boldSystemFontOfSize:8];
    UIGraphicsBeginImageContext(bgImage.size);
    [bgImage drawInRect:CGRectMake(0,0, bgImage.size.width, bgImage.size.height)];
    CGRect rect = CGRectMake(40.0, 3.0, bgImage.size.width, bgImage.size.height);
    [[UIColor whiteColor] set];
    [title drawInRect:CGRectIntegral(rect) withFont:font];
//    NSLog(@"thumb size = %f %f", thumb.size.width, thumb.size.height);
    [thumb drawInRect:CGRectMake(3.0, 3.0, 36.0, 36.0)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}
//draw a billboard with title
- (UIImage*) getBillboardImageForTitle: (NSString*) title
{
    // first lets find out if we're drawing retina resolution or not
    
    //    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)
    //        scaleFactor = 2;        // draw in high-res for iPad
    
    // then lets draw
    UIImage* bgImage = [UIImage imageNamed:@"poi.bundle/POI_bg.png"];
    //    NSString* imagePath;
    //    if( scaleFactor == 1 )	// potentially this is not necessary anyway, because iOS automatically picks 2x version for iPhone4
    //    {
    //        imagePath = [[NSBundle mainBundle] pathForResource:@"POI_bg" ofType:@"png" inDirectory:@"tutorialContent_crossplatform/Tutorial5/Assets5"];
    //    }
    //    else
    //    {
    //        imagePath = [[NSBundle mainBundle] pathForResource:@"POI_bg@2x" ofType:@"png" inDirectory:@"tutorialContent_crossplatform/Tutorial5/Assets5"];
    //    }
    //    bgImage = [UIImage imageNamed:@"poi.bundle/POI_bg.png"];
    UIFont *font = [UIFont boldSystemFontOfSize:8];
    UIGraphicsBeginImageContext(bgImage.size);
    [bgImage drawInRect:CGRectMake(0,0,bgImage.size.width,bgImage.size.height)];
    CGRect rect = CGRectMake(1.0, 1.0, bgImage.size.width, bgImage.size.height);
    [[UIColor whiteColor] set];
    [title drawInRect:CGRectIntegral(rect) withFont:font];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}
- (NSArray*) customData
{
    //example on how to create a location based AR View with your own data e.g. fetched from your own server - you can also add e.g. thumbnail images or address of issue
    NSDictionary *entity1 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:@"Broken tile", @"40.5344", @"23.01", nil] forKeys:[NSArray arrayWithObjects:@"title", @"latitude", @"longitude", nil]];
    NSDictionary *entity2 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:@"Big pothole", @"40.5343", @"23.02", nil] forKeys:[NSArray arrayWithObjects:@"title", @"latitude", @"longitude", nil]];
    return [NSArray arrayWithObjects:entity1, entity2, nil];
}

+ (NSString *) applicationDocumentsDirectory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
    return basePath;
}


#pragma mark - etc.
- (void)drawFrame
{
	[glView setFramebuffer];
    if( m_ArelInterpreter )
    {
		m_ArelInterpreter->update();
    }
    
    [glView presentFramebuffer];
}


- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration
{
    
    m_metaioSDK->setScreenRotation( metaio::getScreenRotationForInterfaceOrientation(interfaceOrientation) );
    
    // on ios5, we handle this in didLayoutSubView
	float version = [[[UIDevice currentDevice] systemVersion] floatValue];
	if( version < 5.0)
	{
		float scale = [UIScreen mainScreen].scale;
		m_metaioSDK->resizeRenderer(self.glView.bounds.size.width*scale, self.glView.bounds.size.height*scale);
	}
    
}
- (void)dealloc
{
    if ([EAGLContext currentContext] == context)
        [EAGLContext setCurrentContext:nil];
    delete m_ArelInterpreter;
    [arelFile release];
//    NSLog(@"lb dealloced");
    [_resultField release];
    [_resetButton release];
    [_switch3d release];
    [super dealloc];
}


- (void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    id tracker = [[GAI sharedInstance] defaultTracker];
    
    // This screen name value will remain set on the tracker and sent with
    // hits until it is set to a new value or to nil.
    [tracker set:kGAIScreenName
           value:@"Questionnaire View"];
    
    // manual screen tracking
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}


- (void) viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
    [self startAnimation];
    [locationManager startUpdatingHeading];
    [locationManager startUpdatingLocation];
}


- (BOOL) shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
    return YES;
}


- (void) viewWillDisappear:(BOOL)animated
{
    [locationManager stopUpdatingHeading];
    [locationManager stopUpdatingLocation];
    [super viewWillDisappear:animated];
    [self.navigationController setToolbarHidden:YES animated:NO];
    
}



- (void)didReceiveMemoryWarning
{
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload
{
	[super viewDidUnload];
}

- (void) viewDidLayoutSubviews
{
	float scale = [UIScreen mainScreen].scale;
	m_metaioSDK->resizeRenderer(self.glView.bounds.size.width*scale, self.glView.bounds.size.height*scale);
}

//draw a billboard with distance, title and thumbnail

@end



