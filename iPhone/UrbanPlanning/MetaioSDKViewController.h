//
//  MetaioSDKViewController.h
//  Metaio
//
//  Created by George Liaros on 6/26/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <metaioSDK/IMetaioSDKIOS.h>
#import <metaioSDK/MobileStructs.h>

namespace metaio
{
    class IMetaioSDKIOS;
    class IGeometry;
    class ISensorsComponent;
//    class ImageStruct;
}
@class EAGLView;
@class EAGLContext;
@class CADisplayLink;

@interface MetaioSDKViewController : UIViewController <MetaioSDKDelegate>
{
    metaio::IMetaioSDKIOS* m_metaioSDK;
    metaio::ISensorsComponent* m_sensors;
    EAGLContext *context;
    BOOL animating;
    NSInteger animationFrameInterval;
    CADisplayLink *displayLink;
    EAGLView *glView;
}

@property (nonatomic, retain) IBOutlet EAGLView *glView;
@property (readonly, nonatomic, getter=isAnimating) BOOL animating;
@property (nonatomic) NSInteger animationFrameInterval;
@property (nonatomic, retain) IBOutlet UIButton *closeButton;

- (IBAction)onBtnClosePushed:(id)sender;
- (void)startAnimation;
- (void)stopAnimation;
- (void)drawFrame;

@end
