//
//  QuestionnaireTableViewController.h
//  Urban Planning
//
//  Created by George Liaros on 3/5/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RadioTableViewController.h"
#import "DataHandler.h"
#import <CoreLocation/CoreLocation.h>

#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

@interface QuestionnaireTableViewController : UITableViewController<UITextFieldDelegate, RadioTableViewControllerDelegate, UIAlertViewDelegate, CLLocationManagerDelegate>
{
    NSMutableArray *questions;
    CLLocation *currentLocation;
    CLLocationManager *locationManager;
    DataHandler *dHandler;
}

@property(retain,nonatomic) NSString *theTitle;
@property(retain, nonatomic) NSMutableDictionary *questionnaire;
@end
