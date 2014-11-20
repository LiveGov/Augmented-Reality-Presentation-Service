//
//  ResultsTableViewController.h
//  Urban Planning
//
//  Created by George Liaros on 3/6/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CorePlot-CocoaTouch.h"
#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

@interface ResultsTableViewController : UITableViewController<CPTPlotDelegate,CPTPlotDataSource>
{
    NSArray *questions;
    NSMutableArray *headerViews;
    NSMutableArray *footerViews;
}

@property (retain,nonatomic) NSMutableDictionary *theQuestionnaire;

@end
