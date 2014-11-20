//
//  AREntityData.h
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AREntity.h"

@interface AREntityData : NSObject
@property(retain, nonatomic) NSMutableArray *entities;

- (void)setTheEntitiesFromArr: (NSArray*) arr;
- (AREntity*) getEntityByID: (NSString*) identifier;
@end
