//
//  DetailedViewController.h
//  Urban Planning
//
//  Created by George Liaros on 2/26/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "AppDelegate.h"
#import "DataHandler.h"
#import "QuestionnaireTableViewController.h"
#import "ResultsViewController.h"
#import "ResultsTableViewController.h"
#import "GAITrackedViewController.h"

@interface DetailedViewController : GAITrackedViewController<DataHandlerDelegate>
{
    UIButton *provideOpinionBtn;
    UIButton *resultsBtn;
    UIView *hr;
    NSMutableDictionary *theQuestionnaire;
    DataHandler *dHandler;
}
//@property (weak, nonatomic) IBOutlet UILabel *entityTitle;
@property (weak, nonatomic) IBOutlet UIImageView *entityImage;
@property (weak, nonatomic) IBOutlet UILabel *entityDescription;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) AREntity *arEntity;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil entity: (AREntity*) entity;
@end
