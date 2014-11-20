//
//  TestViewController.m
//  Metaio
//
//  Created by George Liaros on 7/4/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import "TestViewController.h"

@interface TestViewController ()

@end

@implementation TestViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil modelId: (NSInteger) id_model
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    self.modelID = id_model;
    thumbsDownPresed = NO;
    thumbsUpPressed = NO;
    self.red = [UIImage imageNamed:@"thumbsdown.png"];
    self.redPressed = [UIImage imageNamed:@"thumbsdownpressed.png"];
    self.green = [UIImage imageNamed:@"thumbsup.png"];
    self.greenPressed = [UIImage imageNamed:@"thumbsuppressed.png"];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.titleField.text = [NSString stringWithFormat:@"Model : %d", self.modelID];
    self.textView.text = [NSString stringWithFormat:@""];
    self.textView.layer.borderWidth = 5.0f;
    self.textView.layer.borderColor = [[UIColor blackColor] CGColor];
    //http://augreal.mklab.iti.gr/Models3D_DB/1/1/AR_1_1.jpg
    NSString *path = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/Models3D_DB/%d/1/AR_%d_1.jpg",self.modelID, self.modelID];
    NSURL *url = [NSURL URLWithString:path];
    NSData *data = [NSData dataWithContentsOfURL:url];
    UIImage *img = [UIImage imageWithData:data];
    [self.imageView setImage:img];
    NSString *requestStr = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/VisRec/descriptions.php?id=%d",self.modelID];
    url = [NSURL URLWithString:requestStr];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *error = [request error];
    if(!error) {
        NSString *response = [request responseString];
        self.textView.text = response;
    }
    requestStr = [NSString stringWithFormat:@"http://augreal.mklab.iti.gr/VisRec/descriptions.php?name=%d",self.modelID];
    url = [NSURL URLWithString:requestStr];
    request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    error = [request error];
    if(!error){
        NSString *response = [request responseString];
        self.titleField.text = response;
    }

    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [_titleField release];
    [_imageView release];
    [_textView release];
    [_thumbsDownButton release];
    [_thumbsUpButton release];
    [super dealloc];
}
- (IBAction)onCloseButtonPressed:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)thumbsDownPressed:(id)sender {
    if(!thumbsDownPresed)
    {
        thumbsDownPresed = YES;
        [self.thumbsDownButton setImage:self.redPressed forState:UIControlStateNormal];
        if(thumbsUpPressed)
        {
            [self.thumbsUpButton setImage:self.green forState:UIControlStateNormal];
            thumbsUpPressed = NO;
        }
    }
}

- (IBAction)thumbsUpPressed:(id)sender {
    if(!thumbsUpPressed)
    {
        thumbsUpPressed = YES;
        [self.thumbsUpButton setImage:self.greenPressed forState:UIControlStateNormal];
        if(thumbsDownPresed)
        {
            [self.thumbsDownButton setImage:self.red forState:UIControlStateNormal];
            thumbsDownPresed = NO;
        }
    }
}
@end
