//
//  DetailedViewController.m
//  Urban Planning
//
//  Created by George Liaros on 2/26/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "DetailedViewController.h"

@interface DetailedViewController ()

@end

@implementation DetailedViewController

@synthesize arEntity;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil entity:(AREntity *)entity
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if(self) {
        self.arEntity = entity;
    }
    return self;
}

- (void)viewDidLoad
{
    dHandler = [[DataHandler alloc] init];
    [super viewDidLoad];
//    self.title = self.arEntity.title_en;
//    self.title = @"Restoration of old pedestrian trail with ";
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 480, 44)];
    label.backgroundColor = [UIColor clearColor];
    label.numberOfLines = 1;
    label.font = [UIFont boldSystemFontOfSize: 20.0f];
    label.adjustsFontSizeToFitWidth = YES;
    label.shadowColor = [UIColor colorWithWhite:0.5 alpha:0.5];
    label.textAlignment = NSTextAlignmentLeft;
    label.textColor = [UIColor blackColor];
//    label.text = self.arEntity.title_en;
    label.text = [self.arEntity getLocalizedTitle];
    
    self.navigationItem.titleView = label;
    
    self.entityImage.image = self.arEntity.dImage;
//    self.entityDescription.text = self.arEntity.description_en;
    self.entityDescription.text = [self.arEntity getLocalizedDescription];
    [self.entityDescription sizeToFit];
    CGFloat descriptionHeight = self.entityDescription.frame.size.height;
    hr = [[UIView alloc] initWithFrame:CGRectMake(0.0f, self.entityDescription.frame.origin.y + self.entityDescription.frame.size.height + 10.0f, 320.0, 1.0f)];
    hr.backgroundColor = [UIColor lightGrayColor];
    [self.scrollView addSubview:hr];
    [provideOpinionBtn removeFromSuperview];
    provideOpinionBtn = [[UIButton alloc] initWithFrame:CGRectMake(10.0, hr.frame.origin.y +10.0, 300.0, 50.0)];
    provideOpinionBtn.layer.borderColor = [UIColor blackColor].CGColor;
    provideOpinionBtn.layer.borderWidth = 1.0f;
    //    [provideOpionionBtn addTarget:self action:@selector(onProvideOpinionBtnPressed) forControlEvents:UIControlEventTouchUpInside];
    provideOpinionBtn.backgroundColor = [UIColor colorWithWhite:0.92f alpha:1.0f];
    //    [provideOpionionBtn setTintColor:[UIColor colorWithWhite:0.0 alpha:0.4]];
    //    provideOpionionBtn.tintColor = [UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f];
    [provideOpinionBtn setTitleColor:[UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f] forState:UIControlStateNormal];
    [provideOpinionBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
    //    [provideOpionionBtn setTitle:NSLocalizedString(@"Provide opinion", @"Provide opinion button") forState:UIControlStateNormal];
    provideOpinionBtn.alpha =0.0f;

    //    UIActivityIndicatorView *activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(130.0f, 5.0, 40.0, 40.0)];
    //    [activityIndicator startAnimating];
    //    [provideOpinionBtn addSubview:activityIndicator];
//    NSLog(@"subview count = %d", provideOpinionBtn.subviews.count);
    [self.scrollView setContentSize:CGSizeMake(320.0, descriptionHeight*2)];
    [self.scrollView addSubview:provideOpinionBtn];
    //
    //    DataHandler *dHandler = [[DataHandler alloc] init];
    //    [dHandler performSelectorInBackground:@selector(getQuestionaireWithOptions:) withObject:@[@"EN",self.arEntity.identifier]];
    //    dHandler.delegate = self;
//    float sizeOfContent = 0;
//    NSInteger wd = hr.frame.origin.y;
//    NSInteger ht = hr.frame.size.height;
//    NSInteger wd = resultsBtn.frame.origin.y;
//    NSInteger ht = resultsBtn.frame.size.height;
//    sizeOfContent = wd+ht+50.0f;
//    sizeOfContent = 800.0f;
    //    sizeOfContent = 1000.0f;
//    NSLog(@"size of content = %f", sizeOfContent);
//    self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, sizeOfContent);
//    NSLog(@"scrollview contentSize = %f %f", self.scrollView.frame.size.width, sizeOfContent);
    self.scrollView.scrollEnabled = YES;

//    self.navigationItem.titleView=tlabel;
    self.navigationController.navigationItem.titleView = label;
//    self.entityTitle.text = self.arEntity.title_en;
//    [self.entityTitle sizeToFit];
    
}

- (void)viewWillAppear:(BOOL)animated
{
//    [self.navigationController.navigationBar setBackgroundColor:[UIColor colorWithRed:(247/255.0) green:(247/255.0) blue:(247/255.0) alpha:1]];
//    [self.navigationController setNavigationBarHidden:NO animated:YES];
//    [self.navigationController.navigationBar setTranslucent:YES];
    [self.navigationController.navigationBar setBarTintColor:[UIColor colorWithRed:247.0/255.0 green:247.0/255.0 blue:247.0/255.0 alpha:1.0]];
    self.navigationController.navigationBar.barStyle = UIBarStyleDefault;
    [provideOpinionBtn removeFromSuperview];
    [resultsBtn removeFromSuperview];
//    NSString *language = @"ES";
//    if([[[NSLocale preferredLanguages] objectAtIndex:0] isEqualToString:@"es"])
//        language = @"ES";
//    else
//        language = @"EN";
    [dHandler performSelectorInBackground:@selector(getQuestionaireWithOptions:) withObject:@[@"ES",self.arEntity.identifier]];
    dHandler.delegate = self;
    self.screenName = @"Detailed View";
}
-(void)gotQuestionnaire:(NSMutableDictionary *)questionnaire
{
    theQuestionnaire = questionnaire;
    [self performSelectorOnMainThread:@selector(updateButtons) withObject:nil waitUntilDone:NO];
//    if(theQuestionnaire)
//    {
//        if([[theQuestionnaire objectForKey:@"status"] integerValue] != 0)
//        {
//            resultsBtn = [[UIButton alloc] initWithFrame:CGRectMake(10.0, provideOpinionBtn.frame.origin.y + provideOpinionBtn.frame.size.height + 10.0, 300.0, 50.0)];
//            resultsBtn.layer.borderColor = [UIColor blackColor].CGColor;
//            resultsBtn.layer.borderWidth = 1.0f;
//            resultsBtn.backgroundColor = [UIColor colorWithWhite:0.92f alpha:1.0f];
//            [resultsBtn setTitleColor:[UIColor colorWithRed:0.0 green:122.0f/255.0f blue:1.0f alpha:1.0] forState:  UIControlStateNormal];
//            [resultsBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
//            [self.scrollView addSubview:resultsBtn];
//            float sizeOfContent = resultsBtn.frame.origin.y + resultsBtn.frame.size.height + 50.0f;
//            self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, sizeOfContent);
//            [provideOpinionBtn setTitle:NSLocalizedString(@"Your Opinion", @"Your opinion") forState:UIControlStateNormal];
//        }
//        else
//        {
//            //not answered
//            for(UIView *view in provideOpinionBtn.subviews)
//            {
//            [view removeFromSuperview];
//            }
//            [provideOpinionBtn setTitle:NSLocalizedString(@"Provide Opinion", @"Provide Opinion") forState:UIControlStateNormal];
//        }
//    }
//    else
//    {
//        [provideOpinionBtn removeFromSuperview];
//    }
}

- (void) updateButtons
{
    if(theQuestionnaire)
    {
        if([[theQuestionnaire objectForKey:@"status"] integerValue] != 0)
        {
//            provideOpinionBtn = [[UIButton alloc] initWithFrame:CGRectMake(10.0, hr.frame.origin.y +10.0, 300.0, 50.0)];
//            provideOpinionBtn.layer.borderColor = [UIColor blackColor].CGColor;
//            provideOpinionBtn.layer.borderWidth = 1.0f;
//            //    [provideOpionionBtn addTarget:self action:@selector(onProvideOpinionBtnPressed) forControlEvents:UIControlEventTouchUpInside];
//            provideOpinionBtn.backgroundColor = [UIColor colorWithWhite:0.92f alpha:1.0f];
//            //    [provideOpionionBtn setTintColor:[UIColor colorWithWhite:0.0 alpha:0.4]];
//            //    provideOpionionBtn.tintColor = [UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f];
//            [provideOpinionBtn setTitleColor:[UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f] forState:UIControlStateNormal];
//            [provideOpinionBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
//            //    [provideOpionionBtn setTitle:NSLocalizedString(@"Provide opinion", @"Provide opinion button") forState:UIControlStateNormal];
//            provideOpinionBtn.alpha =0.0f;
//            [provideOpinionBtn setTitle:NSLocalizedString(@"Your Opinion", @"Your opinion") forState:UIControlStateNormal];
//            [provideOpinionBtn addTarget:self action:@selector(onProvideOpinionBtnPressed) forControlEvents:UIControlEventTouchUpInside];
//            provideOpinionBtn.layer.shadowOpacity = 0.3f;
//            provideOpinionBtn.layer.shadowColor = [UIColor blackColor].CGColor;
//            provideOpinionBtn.layer.shadowOffset =CGSizeMake(0.0, 1.0);
//            provideOpinionBtn.layer.masksToBounds = NO;
//            [self.scrollView addSubview:provideOpinionBtn];
            
            resultsBtn = [[UIButton alloc] initWithFrame:CGRectMake(10.0, hr.frame.origin.y+ 10.0, 300.0, 50.0)];
            resultsBtn.layer.borderColor = [UIColor blackColor].CGColor;
            resultsBtn.layer.borderWidth = 1.0f;
            resultsBtn.backgroundColor = [UIColor colorWithWhite:0.92f alpha:1.0f];
            resultsBtn.layer.shadowColor = [UIColor blackColor].CGColor;
            resultsBtn.layer.shadowOffset = CGSizeMake(0.0, 1.0);
            resultsBtn.layer.shadowOpacity = 0.3f;
            resultsBtn.layer.masksToBounds = NO;
            [resultsBtn setTitleColor:[UIColor colorWithRed:0.0 green:122.0f/255.0f blue:1.0f alpha:1.0] forState:  UIControlStateNormal];
            [resultsBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
            [resultsBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
//            [resultsBtn setTitle:NSLocalizedString(@"Results", @"Results") forState:UIControlStateNormal];
            [resultsBtn setTitle:[DataHandler getLocalizedString:@"Results"] forState:UIControlStateNormal];
            [resultsBtn setAlpha:0.0f];
            [resultsBtn addTarget:self action:@selector(onResultsBtnPressed) forControlEvents:UIControlEventTouchUpInside];
            [self.scrollView addSubview:resultsBtn];
            float sizeOfContent = resultsBtn.frame.origin.y + resultsBtn.frame.size.height + 100.0f;
            NSLog(@"sizeOfContent = %f", sizeOfContent);
            self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, sizeOfContent);
        }
        else
        {
            //not answered
//            for(UIView *view in provideOpinionBtn.subviews)
//            {
//                [view removeFromSuperview];
//            }
            provideOpinionBtn = [[UIButton alloc] initWithFrame:CGRectMake(10.0, hr.frame.origin.y +10.0, 300.0, 50.0)];
            provideOpinionBtn.layer.borderColor = [UIColor blackColor].CGColor;
            provideOpinionBtn.layer.borderWidth = 1.0f;
            //    [provideOpionionBtn addTarget:self action:@selector(onProvideOpinionBtnPressed) forControlEvents:UIControlEventTouchUpInside];
            provideOpinionBtn.backgroundColor = [UIColor colorWithWhite:0.92f alpha:1.0f];
            //    [provideOpionionBtn setTintColor:[UIColor colorWithWhite:0.0 alpha:0.4]];
            //    provideOpionionBtn.tintColor = [UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f];
            [provideOpinionBtn setTitleColor:[UIColor colorWithRed:0.0f green:122.0f/255.0f blue:1.0f alpha:1.0f] forState:UIControlStateNormal];
            [provideOpinionBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
            //    [provideOpionionBtn setTitle:NSLocalizedString(@"Provide opinion", @"Provide opinion button") forState:UIControlStateNormal];
            provideOpinionBtn.alpha =0.0f;
            [provideOpinionBtn addTarget:self action:@selector(onProvideOpinionBtnPressed) forControlEvents:UIControlEventTouchUpInside];
            provideOpinionBtn.layer.shadowOpacity = 0.3f;
            provideOpinionBtn.layer.shadowColor = [UIColor blackColor].CGColor;
            provideOpinionBtn.layer.shadowOffset =CGSizeMake(0.0, 1.0);
            provideOpinionBtn.layer.masksToBounds = NO;
//            [provideOpinionBtn setTitle:NSLocalizedString(@"Provide Opinion", @"Provide Opinion") forState:UIControlStateNormal];
            [provideOpinionBtn setTitle:[DataHandler getLocalizedString:@"Provide Opinion"] forState:UIControlStateNormal];
            [self.scrollView addSubview:provideOpinionBtn];
            float sizeOfContent = provideOpinionBtn.frame.origin.y + provideOpinionBtn.frame.size.height + 100.0f;
            NSLog(@"sizeOfContent = %f", sizeOfContent);
            self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, sizeOfContent);
        }
        [UIView animateWithDuration:0.7f
                              delay:0.0f
                            options:UIViewAnimationOptionBeginFromCurrentState
                         animations:^{
                             [provideOpinionBtn setAlpha:1.0f];
                             [resultsBtn setAlpha:1.0f];
                         }
                         completion:nil];
    }
    else
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", @"Error") message:NSLocalizedString(@"No internet connection", @"No internet connection") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Error"] message:[DataHandler getLocalizedString:@"Error loading questionnaire"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
        float sizeOfContent = self.entityDescription.frame.origin.y + self.entityDescription.frame.size.height + 100.0f;
        self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, sizeOfContent);
        [alert show];
        [provideOpinionBtn removeFromSuperview];
    }
}

- (void) onResultsBtnPressed
{
    ResultsTableViewController *resultsController = [[ResultsTableViewController alloc] initWithNibName:@"ResultsTableViewController" bundle:nil];
    resultsController.theQuestionnaire = theQuestionnaire;
    [self.navigationController pushViewController:resultsController animated:YES];
}
- (void) onProvideOpinionBtnPressed
{
    NSString *language = @"ES";
//    if([[[NSLocale preferredLanguages] objectAtIndex:0] isEqualToString:@"es"])
//        language = @"ES";
//    else
//        language = @"EN";
    NSMutableDictionary *questionnaire = [dHandler getQuestionaireWithOptions:@[language, self.arEntity.identifier]];
    if(questionnaire)
    {
        QuestionnaireTableViewController *questionnaireViewController = [[QuestionnaireTableViewController alloc] initWithNibName:@"QuestionnaireTableViewController" bundle:nil];
        questionnaireViewController.questionnaire = questionnaire;
//        questionnaireViewController.theTitle = self.arEntity.title_en;
        questionnaireViewController.theTitle = [self.arEntity getLocalizedTitle];
        [self.navigationController pushViewController:questionnaireViewController animated:YES];
    }
    else
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", @"Error") message:NSLocalizedString(@"Cannot connect to server.\nPlease check your internet connection", @"Cannot connect to server.\nPlease check your internet connection") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Error"] message:[DataHandler getLocalizedString:@"Cannot connect to server.\nPlease check your internet connection"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
        [alert show];
    }
}

- (void) viewDidAppear:(BOOL)animated
{
//    self.navigationController.navigationBar.backgroundColor = [UIColor lightGrayColor];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
