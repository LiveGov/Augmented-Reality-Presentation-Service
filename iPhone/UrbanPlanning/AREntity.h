//
//  AREntity.h
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ASIHTTPRequest/ASIHTTPRequest.h>
//#import "AREntityData.h"
@class AppDelegate;
@interface AREntity : NSObject
//{
//    NSString *title_en;
//    NSString *title_es;
//    NSString *description_en;
//    NSString *description_es;
//    NSString *identifier;
//    CGFloat latitude;
//    CGFloat longitude;
//}

@property(retain, nonatomic) NSString *title_en,*title_es,*title_eu;
@property(retain, nonatomic) NSString *description_en,*description_es, *description_eu;
@property(retain, nonatomic) NSString *identifier;
@property (nonatomic) BOOL shouldTrack;
@property(nonatomic) CGFloat latitude,longitude,altitude, rotation, scale;
@property(retain, nonatomic) UIImage *dImage;
@property (nonatomic) NSInteger numModels;

+ (AREntity*) fromDict:(NSDictionary*) dictionary;
-(NSString *)get3dPathForModel : (NSInteger) modelIndex;
-(NSString*) getLocalizedTitle;
-(NSString*) getLocalizedDescription;
@end
