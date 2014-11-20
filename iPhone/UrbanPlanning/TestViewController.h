//
//  TestViewController.h
//  Metaio
//
//  Created by George Liaros on 7/4/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <ASIHTTPRequest/ASIHTTPRequest.h>
#import <QuartzCore/QuartzCore.h>

@interface TestViewController : UIViewController
{
    BOOL thumbsUpPressed,thumbsDownPresed;
//    UIImage *redPressed, *red, *greenPressed, *green;
}


@property (nonatomic) NSInteger modelID;

@property (retain, nonatomic) UIImage *redPressed, *red, *greenPressed, *green;
@property (retain, nonatomic) IBOutlet UILabel *titleField;
@property (retain, nonatomic) IBOutlet UIImageView *imageView;
@property (retain, nonatomic) IBOutlet UITextView *textView;
@property (retain, nonatomic) IBOutlet UIButton *thumbsDownButton;
@property (retain, nonatomic) IBOutlet UIButton *thumbsUpButton;

- (IBAction)onCloseButtonPressed:(id)sender;
- (IBAction)thumbsDownPressed:(id)sender;
- (IBAction)thumbsUpPressed:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil modelId: (NSInteger) id_model;


@end
