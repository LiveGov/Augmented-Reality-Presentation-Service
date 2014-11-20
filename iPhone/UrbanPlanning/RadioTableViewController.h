//
//  RadioTableViewController.h
//  Urban Planning
//
//  Created by George Liaros on 3/4/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RadioTableViewControllerDelegate;

@interface RadioTableViewController : UITableViewController


@property(retain, nonatomic) NSArray *values;
@property(retain, nonatomic) NSString *theTitle;
@property(retain, nonatomic) NSString *questionTitle;
@property(nonatomic) NSInteger theID;
@property(nonatomic) NSInteger selectedIndex;
@property(nonatomic, weak) id<RadioTableViewControllerDelegate> delegate;
@end


@protocol RadioTableViewControllerDelegate <NSObject>

@optional
-(void) selectedElementAtIndex:(NSInteger) index andTitle:(NSString*) title;
-(void) selectedElementAtIndex:(NSInteger) index andID:(NSInteger) _id;
@end