//
//  DataHandlerDelegate.h
//  ImproveMyCity
//
//  Created by George Liaros on 9/16/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

/* Delegate functions used by most view controllers of this application
 */

#import <Foundation/Foundation.h>
#import "AREntity.h"

@protocol DataHandlerDelegate <NSObject>

@optional

- (void) gotAnonymousUser: (BOOL) success FromSettings: (BOOL) fromSettings;
- (void) gotPermissions: (BOOL) success FromSettings: (BOOL) fromSettings;
- (void) gotAREntities: (BOOL) success FromSettings: (BOOL) fromSettings;
- (void) gotModelForEntity: (AREntity*) entity withPath: (NSString*) path;
- (void) gotQuestionnaire: (NSMutableDictionary*) questionnaire;
@end
