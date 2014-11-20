//
//  UserInfoTableViewController.h
//  Urban Planning
//
//  Created by George Liaros on 3/4/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SWRevealViewController.h"
#import "FrontViewController.h"
#import "RearViewController.h"
#import "RightViewController.h"
#import "RadioTableViewController.h"
#import "AppDelegate.h"

#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

@interface UserInfoTableViewController : UITableViewController<RadioTableViewControllerDelegate, UITextFieldDelegate>
{
    NSArray *tableData;
    NSArray *values;
    NSInteger value1;
    NSInteger value2;
    NSInteger value3;
    NSInteger value4;
    NSInteger value5;
    NSString *name;
    BOOL doneEnabled;
    DataHandler *dHandler;
}

@property (nonatomic) BOOL getFromDefaults;

@end
