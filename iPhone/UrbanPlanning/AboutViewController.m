//
//  AboutViewController.m
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "AboutViewController.h"


@interface AboutViewController ()

@end

@implementation AboutViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    self.screenName = @"About View";
}

- (void)viewDidLoad
{
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    [super viewDidLoad];
//    self.title = NSLocalizedString(@"About", @"About");
    self.title = [DataHandler getLocalizedString:@"About"];
    SWRevealViewController *revealController = [self revealViewController];
    UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
                                                                         style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
    
    self.navigationItem.leftBarButtonItem = revealButtonItem;
    NSString *htmlFile;
    if(del.basque)
        htmlFile = [[NSBundle mainBundle] pathForResource:@"about_eu" ofType:@"html"];
    else{
        NSArray *preferredLanguages = [NSLocale preferredLanguages];
        NSInteger espanolIndex = [preferredLanguages indexOfObject:@"es"];
        NSInteger englishIndex = [preferredLanguages indexOfObject:@"en"];
        if(espanolIndex < englishIndex){
            htmlFile = [[NSBundle mainBundle] pathForResource:@"about_es" ofType:@"html" ];
        }
        else {
            htmlFile = [[NSBundle mainBundle] pathForResource:@"about_en" ofType:@"html" ];
        }
    }
//    NSString *appVersion = [NSString stringWithFormat:@"Version %@ (%@)", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"],kRevisionNumber];
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    NSString *version = [NSString stringWithFormat:@"v %@", appVersion];
//    if([[[NSLocale preferredLanguages] objectAtIndex:0] isEqualToString:@"es"])
//        htmlFile = [[NSBundle mainBundle] pathForResource:@"about_es" ofType:@"html"];
//    else
//        htmlFile = [[NSBundle mainBundle] pathForResource:@"about_en" ofType:@"html"];
    self.aboutWebView.delegate = self;
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlFile encoding:NSUTF8StringEncoding error:nil];
    if(version)
        htmlString = [htmlString stringByReplacingOccurrencesOfString:@"[VERSION]" withString:version];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlFile];
    [self.aboutWebView loadHTMLString:htmlString baseURL:baseURL];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
