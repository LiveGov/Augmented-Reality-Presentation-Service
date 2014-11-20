//
//  ARELViewController.h
//  Metaio
//
//  Created by George Liaros on 6/26/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import "MetaioSDKViewController.h"
#import <metaioSDK/GestureHandlerIOS.h>
#import <metaioSDK/IARELInterpreterIOS.h>
#import <metaioSDK/ISensorsComponent.h>
#import <metaioSDK/IMetaioSDKCallback.h>
#import <AVFoundation/AVFoundation.h>
#import <ASIHTTPRequest/ASIHTTPRequest.h>
#import <CoreLocation/CoreLocation.h>
#import <ASIHTTPRequest/ASIFormDataRequest.h>
#import <QuartzCore/QuartzCore.h>
#import "TestViewController.h"
#import "XMLReader.h"
#import "SWRevealViewController.h"
#import "AppDelegate.h"
#import "DetailedViewController.h"
#import "DataHandler.h"
#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"
#import <AudioToolbox/AudioToolbox.h>

@protocol ARELViewControllerDelegate;

@class EAGLView;

@interface ARELViewController : MetaioSDKViewController <UIGestureRecognizerDelegate, IARELInterpreterIOSDelegate, AVCaptureVideoDataOutputSampleBufferDelegate, CLLocationManagerDelegate, DataHandlerDelegate>
{
    
    metaio::IARELInterpreterIOS* m_ArelInterpreter;
    metaio::IBillboardGroup *billboardGroup;
    NSString* arelFile;
    UIWebView* arelWebview;
    BOOL timerRunning;
    BOOL busy;
    BOOL lockObject;
    BOOL recognitionReady;
    NSTimer *timer;
    UIImage *redButtonImg;
    UIImage *greenButtonImg;
    metaio::LLACoordinate previousLLA;
    metaio::Rotation previousRotation;
    metaio::Vector3d previousScale;
    metaio::IGeometry* currentGeometry;
    metaio::IRadar* m_radar;
    int previousIdx;
    float scaleFactor;
    float prevRecScale;
    int counter;
    float savedRecognitionState;
    UIActivityIndicatorView *activityIndicator;
    CLLocationManager *locationManager;
    CLLocation *lastLocation;
    
    UIBarButtonItem *previousModel;
    UIBarButtonItem *nextModel;
    UIBarButtonItem *modelTitle;
    AREntityData *entityData;
    
    AREntity *curEntity;
    NSInteger curModelNo;
    metaio::IGeometry *curGeometry;
//    UILabel *gpsAccuracy;
    UIBarButtonItem *arModeBtn;
    DataHandler *dHandler;
    UIView *shakeToReload;
    double updateTime;
    SystemSoundID mBeep;
    BOOL initialUpdate;
    
}

@property (retain, nonatomic) NSArray *objects;
@property (nonatomic) BOOL visualRecognition;
@property (nonatomic, retain) IBOutlet UIWebView* arelWebView;
@property (nonatomic, retain) NSString* arelFile;
@property (retain, nonatomic) IBOutlet UIButton *resetButton;
@property (retain, nonatomic) id<ARELViewControllerDelegate> delegate;
@property (nonatomic) BOOL filterBillboards;
@property (nonatomic) NSInteger idApp;
@property (retain, nonatomic) IBOutlet UILabel *resultField;
@property (retain, nonatomic) IBOutlet UISwitch *switch3d;
@property (nonatomic) CGFloat previousScaleF;
@property (retain,nonatomic) NSString *arMode;
@property (retain, nonatomic) IBOutlet UISegmentedControl *segmentCtrl3d;
@property (retain, nonatomic) IBOutlet UIView *ibsRectView;

- (id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil ARMode: (NSString*) mode arelInstructionsOrNil: (NSString*) arelConfigOrXmlStr;
- (IBAction)onCloseBtnPressed:(id)sender;
- (IBAction)onResetBtnPressed:(id)sender;
- (IBAction)pinchDetected:(UIPinchGestureRecognizer*)sender;
- (IBAction)draggingAction:(UIPanGestureRecognizer*)sender;
- (IBAction)onSwitchValueChanged:(id)sender;
- (IBAction)onSegmentCtrlChange:(id)sender;

- (void) filterAllBillboards;
- (void) filter3dModels;
- (void) triggerSearch;

- (void) onSDKReady;
- (void) onSceneReady;
@end

@protocol ARELViewControllerDelegate <NSObject>

- (void) sdkFinishedLoading;

@end