/*

 Copyright (c) 2013 Joan Lluch <joan.lluch@sweetwilliamsl.com>
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is furnished
 to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 
 Original code:
 Copyright (c) 2011, Philip Kluz (Philip.Kluz@zuui.org)
*/

#import "FrontViewController.h"
#import "SWRevealViewController.h"

@interface FrontViewController()

// Private Methods:
- (IBAction)pushExample:(id)sender;

@end

@implementation FrontViewController

#pragma mark - View lifecycle


- (void)viewDidLoad
{
	[super viewDidLoad];
    dHandler = [[DataHandler alloc] init];
	AppDelegate *del = [UIApplication sharedApplication].delegate;
    self.entityData = del.entityData;
//	self.title = NSLocalizedString(@"List", nil);
    self.title = [DataHandler getLocalizedString:@"List"];
    SWRevealViewController *revealController = [self revealViewController];
    NSLog(@"rotation = %f", M_PI_2);
    dHandler.delegate = self;
    if(!del.userID)
        [dHandler performSelectorInBackground:@selector(createAnonymousUser) withObject:nil];
    if(!del.permissions)
        [dHandler performSelectorInBackground:@selector(getPermissions) withObject:nil];
    if(self.entityData){
        if([self.entityData.entities count] == 0)
        {
            [[UIApplication sharedApplication] beginIgnoringInteractionEvents];
            loadingView = [[UIView alloc] initWithFrame:CGRectMake(0.0, -50.0, 320.0, 50.0)];
            loadingView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.4];
            loadingLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 5.0, 320.0, 40.0)];
            loadingLabel.textAlignment = NSTextAlignmentCenter;
//            loadingLabel.text = NSLocalizedString(@"Downloading ...", @"Downloading ...");
            loadingLabel.text = [DataHandler getLocalizedString:@"Downloading ..."];
            loadingLabel.font = [UIFont boldSystemFontOfSize:14.0];
            loadingLabel.textColor = [UIColor whiteColor];
            loadingLabel.shadowColor = [UIColor grayColor];
            loadingLabel.shadowOffset = CGSizeMake(0.0, 1.0);
            loadingLabel.backgroundColor = [UIColor clearColor];
            CGSize expectedLabelSize = [loadingLabel.text sizeWithFont:loadingLabel.font
                                                     constrainedToSize:CGSizeMake(320.0, 50.0)
                                                         lineBreakMode:loadingLabel.lineBreakMode];
            activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(expectedLabelSize.width /2.0f + 10.0f, 10.0, 30.0, 30.0)];
            [activityIndicator startAnimating];
            [loadingView addSubview:loadingLabel ];
            [loadingView bringSubviewToFront:loadingLabel];
            [loadingView addSubview:activityIndicator];
            [loadingView bringSubviewToFront:activityIndicator];
            [self.listView addSubview:loadingView];
            [self.listView bringSubviewToFront:loadingView];
            [self showLoadingView];
            [dHandler performSelectorInBackground:@selector(getAREntities) withObject:nil];
        }
    }
    else
    {
//        [[UIApplication sharedApplication] beginIgnoringInteractionEvents];
//        loadingView = [[UIView alloc] initWithFrame:CGRectMake(0.0, -50.0, 320.0, 50.0)];
//        loadingView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.4];
//        loadingLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 5.0, 320.0, 40.0)];
//        loadingLabel.textAlignment = NSTextAlignmentCenter;
//        loadingLabel.text = NSLocalizedString(@"Downloading ...", @"Downloading ...");
//        loadingLabel.font = [UIFont boldSystemFontOfSize:14.0];
//        loadingLabel.textColor = [UIColor whiteColor];
//        loadingLabel.shadowColor = [UIColor grayColor];
//        loadingLabel.shadowOffset = CGSizeMake(0.0, 1.0);
//        loadingLabel.backgroundColor = [UIColor clearColor];
//        CGSize expectedLabelSize = [loadingLabel.text sizeWithFont:loadingLabel.font
//                                                 constrainedToSize:CGSizeMake(320.0, 50.0)
//                                                     lineBreakMode:loadingLabel.lineBreakMode];
//        activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(expectedLabelSize.width /2.0f + 10.0f, 10.0, 30.0, 30.0)];
//        [activityIndicator startAnimating];
//        [loadingView addSubview:loadingLabel ];
//        [loadingView bringSubviewToFront:loadingLabel];
//        [loadingView addSubview:activityIndicator];
//        [loadingView bringSubviewToFront:activityIndicator];
//        [self.listView addSubview:loadingView];
//        [self.listView bringSubviewToFront:loadingView];
//        [self showLoadingView];
//        [dHandler performSelectorInBackground:@selector(getAREntities) withObject:nil];
        [self refreshData];
    }
    if(![defaults objectForKey:DEFAULTS_ANONYMOUSUSERID])
        [dHandler createAnonymousUser];
//    [dHandler getPermissions];
//    [dHandler getAREntities];
//    [dHandler getQuestionaireWithOptions:[NSArray arrayWithObjects:@"en",@"41", nil]];
//    [dHandler getQuestionaireWithOptions:nil];
    [revealController panGestureRecognizer];
    [revealController tapGestureRecognizer];
    
    UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
        style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
    
    self.navigationItem.leftBarButtonItem = revealButtonItem;
    
//    UIBarButtonItem *refreshButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Refresh", @"Refresh") style:UIBarButtonItemStyleDone target:self action:@selector(refreshData)];
    UIBarButtonItem *refreshButtonItem = [[UIBarButtonItem alloc] initWithTitle:[DataHandler getLocalizedString:@"Refresh"] style:UIBarButtonItemStyleDone target:self action:@selector(refreshData)];
    self.navigationItem.rightBarButtonItem = refreshButtonItem;
//    UIBarButtonItem *rightRevealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
//        style:UIBarButtonItemStyleBordered target:revealController action:@selector(rightRevealToggle:)];
//    self.navigationItem.rightBarButtonItem = rightRevealButtonItem;

}

- (void) showLoadingView
{
    [activityIndicator startAnimating];
    [UIView animateWithDuration:0.3f
                          delay:0.0f
                        options:UIViewAnimationOptionBeginFromCurrentState
                     animations:^{
                         [loadingView setAlpha:1.0];
                         [loadingView setFrame:CGRectMake(0.0, 0.0, 320.0, 50.0)];
                     }
                     completion:nil];
}
//dismiss the loading view when table finished loading data
- (void) dismissLoadingView
{
    [activityIndicator stopAnimating];
    [UIView animateWithDuration:0.3f
                          delay:0.0f
                        options:UIViewAnimationOptionBeginFromCurrentState
                     animations:^{
                         [loadingView setFrame:CGRectMake(0.0, -50.0, 320.0, 50.0)];
                         [loadingView setAlpha:0.0];
                     }
                     completion:nil];
//    [self.listView reloadData];
    [self.listView reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    [[UIApplication sharedApplication] endIgnoringInteractionEvents];
}

//- (void) dismissLoadingViewWithError:(NSString *) error
//{
//    [activityIndicator stopAnimating];
//    [activityIndicator setHidden:YES];
//    loadingLabel.text = error;
//    [UIView animateWithDuration:1.3f
//                          delay:0.0f
//                        options:UIViewAnimationOptionBeginFromCurrentState
//                     animations:^{
//                         [loadingView setFrame:CGRectMake(0.0, -50.0, 320.0f, 50.0f)];
//                         [loadingView setAlpha:0.0f];
//                     }completion:^(BOOL success){
//                         loadingLabel.text = NSLocalizedString(@"Downloading ...", @"Downloading ...");
//                         [activityIndicator setHidden:NO];
//                     }];
////    [[UIApplication sharedApplication] endIgnoringInteractionEvents];
//}

-(void)refreshData
{
    dHandler.delegate = self;
    [dHandler performSelectorInBackground:@selector(getAREntities) withObject:nil];
    [[UIApplication sharedApplication] beginIgnoringInteractionEvents];
    loadingView = [[UIView alloc] initWithFrame:CGRectMake(0.0, -50.0, 320.0, 50.0)];
    loadingView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.4];
    loadingLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 5.0, 320.0, 40.0)];
    loadingLabel.textAlignment = NSTextAlignmentCenter;
//    loadingLabel.text = NSLocalizedString(@"Downloading ...", @"Downloading ...");
    loadingLabel.text = [DataHandler getLocalizedString:@"Downloading ..."];
    loadingLabel.font = [UIFont boldSystemFontOfSize:14.0];
    loadingLabel.textColor = [UIColor whiteColor];
    loadingLabel.shadowColor = [UIColor grayColor];
    loadingLabel.shadowOffset = CGSizeMake(0.0, 1.0);
    loadingLabel.backgroundColor = [UIColor clearColor];
    CGSize expectedLabelSize = [loadingLabel.text sizeWithFont:loadingLabel.font
                                             constrainedToSize:CGSizeMake(320.0, 50.0)
                                                 lineBreakMode:loadingLabel.lineBreakMode];
    activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(expectedLabelSize.width /2.0f + 10.0f, 10.0, 30.0, 30.0)];
    [activityIndicator startAnimating];
    [loadingView addSubview:loadingLabel ];
    [loadingView bringSubviewToFront:loadingLabel];
    [loadingView addSubview:activityIndicator];
    [loadingView bringSubviewToFront:activityIndicator];
    [self.listView addSubview:loadingView];
    [self.listView bringSubviewToFront:loadingView];
    [self showLoadingView];
//    [dHandler performSelectorInBackground:@selector(getAREntities) withObject:nil];
}

-(void)gotAREntities:(BOOL)success FromSettings:(BOOL)fromSettings
{
    if(success)
    {
//        self.listView.separatorStyle = UITableViewCellSelectionStyleGray;
        AppDelegate *del = [UIApplication sharedApplication].delegate;
        self.entityData = del.entityData;
//        NSLog(@"entities count = %d", [del.entityData.entities count]);
        [self performSelectorOnMainThread:@selector(dismissLoadingView) withObject:nil waitUntilDone:YES];
        //    [self.listView performSelectorOnMainThread:@selector(setSeparatorStyle:) withObject:UITableViewCellSelectionStyleGray waitUntilDone:YES];
    }
    else
    {
        [self performSelectorOnMainThread:@selector(dismissLoadingView) withObject:nil waitUntilDone:YES];
    }
//    NSLog(@"reloading data");
}

- (void)gotAnonymousUser:(BOOL)success FromSettings:(BOOL)fromSettings
{
    [dHandler getUserQuestionnaire:NO];
}

- (void)gotPermissions:(BOOL)success FromSettings:(BOOL)fromSettings
{
    
}
#pragma mark - Example Code

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(self.entityData)
    {
//        NSLog(@"returning entities count = %d", [self.entityData.entities count]);
//        if([self.entityData.entities count] > 0)
//            self.listView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
//        else
//            self.listView.separatorStyle = UITableViewCellSeparatorStyleNone;
        return [self.entityData.entities count];
    }
    else
    {
//        self.listView.separatorStyle = UITableViewCellSeparatorStyleNone;
        return 1;
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [UIView new];
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 137.0f;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    ListTableCell *cell = [self.listView dequeueReusableCellWithIdentifier:@"listTableCell"];
//    if(cell == nil)
//    {
//        cell = [[ListTableCell alloc] init];
//    }
//    cell.entityTitle.text = @"213091";
//    cell.entityImage.image = [UIImage imageNamed:@"reveal-icon.png"];
//    cell.entityDescription.text = @"mpla mpla";
//    return cell;
    static NSString *simpleTableIdentifier = @"listTableCell";
    
    ListTableCell *cell = (ListTableCell *)[self.listView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"ListTableCell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    if(!self.entityData.entities)
    {
        cell.entityTitle.text = @"";
        cell.entityTitle.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        cell.entityImageView.image = nil;
        cell.entityDescription.text = @"";
        cell.entityDescription.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    }
    else
    {
        AREntity *entity = [self.entityData.entities objectAtIndex:[indexPath row]];
//        cell.entityTitle.text = entity.title_en;
        cell.entityTitle.text = [entity getLocalizedTitle];
        cell.entityTitle.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        cell.entityImageView.image = entity.dImage;
//        cell.entityDescription.text = entity.description_en;
        cell.entityDescription.text = [entity getLocalizedDescription];
        cell.entityDescription.autoresizingMask = UIViewAutoresizingFlexibleWidth;
        cell.entityDescription.numberOfLines = 4;
        [cell.entityDescription sizeToFit];
    }
    cell.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSLog(@"did select row = %d", [indexPath row]);
    DetailedViewController *detailedViewController = [[DetailedViewController alloc] initWithNibName:@"DetailedViewController" bundle:nil entity:[self.entityData.entities objectAtIndex:[indexPath row]]];
    [self.navigationController pushViewController:detailedViewController animated:YES];
}
- (IBAction)pushExample:(id)sender
{
	UIViewController *stubController = [[UIViewController alloc] init];
	stubController.view.backgroundColor = [UIColor redColor];
	[self.navigationController pushViewController:stubController animated:YES];
}
- (void)viewWillAppear:(BOOL)animated
{
    self.screenName = @"List View";
}

//- (void)viewWillAppear:(BOOL)animated
//{
//    [super viewWillAppear:animated];
//    NSLog( @"%@: FRONT", NSStringFromSelector(_cmd));
//}
//
//- (void)viewWillDisappear:(BOOL)animated
//{
//    [super viewWillDisappear:animated];
//    NSLog( @"%@: FRONT", NSStringFromSelector(_cmd));
//}
//
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];

//    [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
//    self.navigationController.navigationBar.shadowImage = [UIImage new];
//    self.navigationController.navigationBar.translucent = YES;
}
//
//- (void)viewDidDisappear:(BOOL)animated
//{
//    [super viewDidDisappear:animated];
//    NSLog( @"%@: FRONT", NSStringFromSelector(_cmd));
//}

@end