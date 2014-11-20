//
//  AboutViewController.h
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SWRevealViewController.h"
#import "GAITrackedViewController.h"
#import "DataHandler.h"

@interface AboutViewController : GAITrackedViewController<UIWebViewDelegate>
{
    DataHandler *dHandler;
}
@property (weak, nonatomic) IBOutlet UIWebView *aboutWebView;

@end
