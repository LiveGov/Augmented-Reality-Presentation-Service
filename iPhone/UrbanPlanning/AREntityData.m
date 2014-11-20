//
//  AREntityData.m
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "AREntityData.h"

@implementation AREntityData
@synthesize entities;

-(void)setTheEntitiesFromArr:(NSArray *)arr
{
    self.entities = [[NSMutableArray alloc] init];
    for(NSDictionary *dict in arr)
    {
        AREntity *temp = [AREntity fromDict:dict];
        if(temp)
        {
//            NSLog(@"temp is not null");
        }
        [self.entities addObject:temp];
    }
//    NSLog(@"self entities count = %d", [self.entities count]);
}

- (AREntity *)getEntityByID:(NSString *)identifier
{
    for(AREntity *entity in self.entities)
    {
        if([entity.identifier isEqualToString:identifier])
        {
            return entity;
        }
    }
    return nil;
}

@end
