//
//  QuestionnaireTableViewController.m
//  Urban Planning
//
//  Created by George Liaros on 3/5/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "QuestionnaireTableViewController.h"

@interface QuestionnaireTableViewController ()

@end

@implementation QuestionnaireTableViewController
@synthesize questionnaire, theTitle;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)viewDidLoad
{
//    questionnaireMutableCopy = [self mutableDeepCopy:questionnaire];
//    questionnaireMutableCopy = [questionnaire mutableCopy];
    dHandler = [[DataHandler alloc] init];
    self.automaticallyAdjustsScrollViewInsets = YES;
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 480, 44)];
    label.backgroundColor = [UIColor clearColor];
    label.numberOfLines = 1;
    label.font = [UIFont boldSystemFontOfSize: 20.0f];
    label.adjustsFontSizeToFitWidth = YES;
    label.shadowColor = [UIColor colorWithWhite:0.5 alpha:0.5];
    label.textAlignment = NSTextAlignmentLeft;
    label.textColor = [UIColor blackColor];
    label.text = self.theTitle;
    
    self.navigationItem.titleView = label;
    
    //    self.navigationItem.titleView=tlabel;
    self.navigationController.navigationItem.titleView = label;
    questions = [[[[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
//    NSLog(@"questions count = %d", [questions count]);
//    UIBarButtonItem *rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Submit", @"Submit") style:UIBarButtonItemStyleDone target:self action:@selector(onSubmitBtnPressed)];
    UIBarButtonItem *rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:[DataHandler getLocalizedString:@"Submit"] style:UIBarButtonItemStyleDone target:self action:@selector(onSubmitBtnPressed)];
    self.navigationItem.rightBarButtonItem = rightBarButtonItem;
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    [locationManager startUpdatingLocation];
    if([[questionnaire objectForKey:@"status"] integerValue] == 2)
    {
        //already answered
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Questionnaire" message:@"You have already submitted this questionnaire\nYou may edit your answers if you want" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
//        [alert show];
    }
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    currentLocation = (CLLocation *) [locations objectAtIndex:0];
    [locationManager stopUpdatingLocation];
}

- (void)viewWillAppear:(BOOL)animated
{
    id tracker = [[GAI sharedInstance] defaultTracker];
    
    // This screen name value will remain set on the tracker and sent with
    // hits until it is set to a new value or to nil.
    [tracker set:kGAIScreenName
           value:@"Questionnaire View"];
    
    // manual screen tracking
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void) onSubmitBtnPressed
{
    NSLog(@"current lat = %f", currentLocation.coordinate.latitude);
    NSLog(@"current lon = %f", currentLocation.coordinate.longitude);
    NSUserDefaults *def = defaults;
    NSLog(@"def = %@", [[def dictionaryRepresentation] description]);
    BOOL userQSent = [(NSNumber*) [defaults objectForKey:DEFAULTS_QUESTIONNAIRE_SENT] boolValue];
    if(!userQSent){
        //error senting userQ
        if(![dHandler getUserQuestionnaire:NO]){
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Error"] message:[DataHandler getLocalizedString:@"Something went wrong while submitting the questionnaire"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
            [alert show];
        }
        return;
    }
    if(currentLocation)
    {
        [[questionnaire objectForKey:@"questionnaire"] setValue:[NSString stringWithFormat:@"%f",currentLocation.coordinate.latitude] forKey:@"lat"];
        [[questionnaire objectForKey:@"questionnaire"] setValue:[NSString stringWithFormat:@"%f",currentLocation.coordinate.longitude] forKey:@"lon"];
    }
    if(!currentLocation)
        currentLocation = [[CLLocation alloc] initWithLatitude:0.0 longitude:0.0];
//    NSError *error;
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:questionnaire
//                                                       options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
//                                                         error:&error];
//    
//    if (! jsonData) {
//        NSLog(@"Got an error: %@", error);
//    } else {
//        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//        NSLog(@"json = %@", jsonString);
//    }
    if([dHandler validateQuestionnaire:questionnaire])
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Nice" message:@"Questionnaire is valid and ready to be submitted" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
//        [alert show];
        if([dHandler submitQuestionnaire:questionnaire WithLat:currentLocation.coordinate.latitude AndLon:currentLocation.coordinate.longitude])
        {
            NSString *usermessage = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"usermessage"];
            if(![usermessage isEqual:[NSNull null]])
            {
//                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Success", @"Success") message:usermessage delegate:self cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Success"] message:usermessage delegate:self cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
                [alert show];
            }
            else
            {
//                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Success", @"Success") message:NSLocalizedString(@"Questionnaire updated.", @"Questionnaire updated.") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Success"] message:[DataHandler getLocalizedString:@"Questionnaire updated."] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
                [alert show];
            }
        }
        else
        {
//            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", @"Error") message:NSLocalizedString(@"Something went wrong while submitting the questionnaire", @"Something went wrong while sumbitting the questionnaire") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Error"] message:[DataHandler getLocalizedString:@"Something went wrong while submitting the questionnaire"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
            [alert show];
        }
    }
    else
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", @"Error") message:NSLocalizedString(@"Questionnaire is not valid", @"Questionnaire is not valid") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Error"] message:[DataHandler getLocalizedString:@"Questionnaire is not valid"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
        [alert show];
    }
    
}

-(void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [questions count] +1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    if(section == 0)
    {
        return 0;
    }
    return 1;
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    NSDictionary *question = [questions objectAtIndex:[indexPath section] -1];
    NSNumber *displayType = [question objectForKey:@"displaytype"];
//    NSLog(@"displaytype = %d", [displayType integerValue]);
    if([displayType integerValue] == 0)
    {
        //Radio
        for(UIView *view in cell.contentView.subviews)
        {
            [view removeFromSuperview];
        }
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        cell.tag = [[question objectForKey:@"id"] integerValue];//radio tag
        NSArray *answerOptions = [question objectForKey:@"answeroptions"];
//        NSMutableArray *answerValues = [[NSMutableArray alloc] initWithCapacity:[answerOptions count]];
//        NSInteger selectedIdx = -1;
//        NSInteger counter = 0;
        NSString *answer;
        for(NSDictionary *answerOption in answerOptions)
        {
//            [answerValues addObject:[answerOption objectForKey:@"answeroptiontext"]];
            NSNumber *selected = [answerOption objectForKey:@"selected"];
            if([selected boolValue])
            {
                answer = [answerOption objectForKey:@"answeroptiontext"];
            }
//            counter++;
        }
        if(answer)
        {
            cell.textLabel.text = answer;
            cell.textLabel.textColor = [UIColor blueColor];
        }
        else
        {
//            cell.textLabel.text = NSLocalizedString(@"Select an answer", @"Select an answer");
            cell.textLabel.text = [DataHandler getLocalizedString:@"Select an answer"];
            cell.textLabel.textColor = [UIColor lightGrayColor];
        }
    }
    else if([displayType integerValue] == 2)
    {
        for(UIView *view in cell.contentView.subviews)
        {
            [view removeFromSuperview];
        }
//        [[cell.contentView viewWithTag:[[question objectForKey:@"id"] integerValue]] removeFromSuperview];
        cell.tag = -1;//textfield tag
        cell.accessoryType = UITableViewCellAccessoryNone;
        cell.textLabel.text = nil;
        UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(12.0, 0.0, cell.contentView.frame.size.width, cell.contentView.frame.size.height)];
        id comment = [[question objectForKey:@"comment"] copy];
        if(![comment isEqual:[NSNull null]])
        {
//            NSLog(@"comment not null");
            textField.text = comment;
        }
//        textField.text = [question objectForKey:@"comment"];
        textField.textColor = [UIColor blueColor];
        textField.returnKeyType = UIReturnKeyDone;
//        textField.placeholder = NSLocalizedString(@"Write here..", @"Write here..");
        textField.placeholder = [DataHandler getLocalizedString:@"Write here.."];
        textField.delegate = self;
        textField.tag = [[question objectForKey:@"id"] integerValue];
        [cell.contentView addSubview:textField];
    }
    return cell;
}

//-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
//{
//    if(section == 0)
//    {
//        return [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"description"];
//    }
//    else
//    {
//        return [[questions objectAtIndex:section -1] objectForKey:@"questiontext"];
//    }
//}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
//fontName: Helvetica-Bold
//pointSize: 17.000000
//textColor: UIDeviceRGBColorSpace 0.298039 0.337255 0.423529 1
//shadowColor: UIDeviceWhiteColorSpace 1 1
//shadowOffset: CGSize 0 1
    NSString *headerText;
    UIFont *font;
    if(section == 0)
    {
        headerText = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"description"];
        font = [UIFont systemFontOfSize:17.0];
    }
    else
    {
        headerText = [[questions objectAtIndex:section -1] objectForKey:@"questiontext"];
        headerText = [NSString stringWithFormat:@"%d.\n%@",section,headerText];
        font = [UIFont boldSystemFontOfSize:18.0];
    }
    CGSize maxSize = CGSizeMake(300.0, 999999.0);
    int height = 0;
    NSDictionary *attributesDictionary = [NSDictionary dictionaryWithObjectsAndKeys:
                                          font, NSFontAttributeName,
                                          nil];
//    CGRect paragraphRect =
//    [headerText boundingRectWithSize:CGSizeMake(300.f, CGFLOAT_MAX)
//                                 options:(NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading)
//                                 context:nil];
    CGRect frame = [headerText boundingRectWithSize:maxSize
                                          options:(NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading)
                                       attributes:attributesDictionary
                                          context:nil];
    height = frame.size.height;
    return height+5;
}

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *headerView = [[UIView alloc] init];
    UILabel *label = [[UILabel alloc] init];
    
    label.numberOfLines = 0;
    if(section == 0)
    {
        label.text = [[questionnaire objectForKey:@"questionnaire"] objectForKey:@"description"];
        label.textColor = [UIColor colorWithWhite:0.1 alpha:1.0];
//        label.textColor = [UIColor grayColor];
//        label.shadowColor = [UIColor lightGrayColor];
//        label.shadowOffset = CGSizeMake(0.0, 1.0);
        label.font = [UIFont systemFontOfSize:17.0];
    }
    else
    {
        label.text = [[questions objectAtIndex:section-1] objectForKey:@"questiontext"];
        label.text = [NSString stringWithFormat:@"%d. %@",section,label.text];
        label.shadowColor = [UIColor lightGrayColor];
        label.shadowOffset = CGSizeMake(0.0, 1.0);
        label.font = [UIFont boldSystemFontOfSize:18.0f];
    }
    CGSize maxSize = CGSizeMake(300.0, 999999.0);
    NSDictionary *attributesDictionary = [NSDictionary dictionaryWithObjectsAndKeys:label.font,NSFontAttributeName, nil];
    CGRect frame = [label.text boundingRectWithSize:maxSize
                                          options:(NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading)
                                         attributes:attributesDictionary
                                             context:nil];
//    NSLog(@"frame x y w h = %f %f %f %f", frame.origin.x, frame.origin.y, frame.size.width, frame.size.height);
    frame.origin.x +=10.0;
    label.frame = frame;
    headerView.frame = CGRectMake(0.0, 0.0, 320.0, frame.size.height);
    [headerView addSubview:label];
    return headerView;
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}

- (BOOL) textFieldShouldBeginEditing:(UITextField *)textField
{
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 220.0, 0);
    UITableViewCell *parent = (UITableViewCell*) textField.superview.superview;
    NSIndexPath *indexPath = [self.tableView indexPathForCell:parent];
    [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    return YES;
}

//- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
//{
//    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
//    return YES;
//}

- (BOOL)textFieldShouldEndEditing:(UITextField *)textField
{
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
    return YES;
}

-(void)textFieldDidEndEditing:(UITextField *)textField
{
//    NSInteger tag = textField.tag;
//    NSMutableDictionary *questionnaireDict = [questionnaire objectForKey:@"questionnaire"];
//    NSMutableArray *categories = [questionnaireDict objectForKey:@"categories"];
//    NSMutableArray *questions = [[[questionnaireDict objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
//    for(NSMutableDictionary *question in questions)
//    {
//        if([[[question objectForKey:@"id"] stringValue] isEqualToString:[NSString stringWithFormat:@"%d", tag]])
//        {
//            [question setObject:textField.text forKey:@"comment"];
//        }
//    }
    NSInteger tag = textField.tag;
    NSMutableArray *questions = [[[[questionnaire objectForKey:@"questionnaire"]objectForKey:@"categories" ] objectAtIndex:0] objectForKey:@"questions"];
    for(NSMutableDictionary *question in questions)
    {
        if([[[question objectForKey:@"id"] stringValue] isEqualToString:[NSString stringWithFormat:@"%d", tag]])
        {
            [question setObject:textField.text forKey:@"comment"];
        }
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    NSInteger tag = cell.tag;
//    NSLog(@"selected tag = %d",tag);
    if(tag != -1)
    {
        //Radio
        //get the values
//        NSDictionary *question = [questions objectAtIndex:[indexPath section] -1];
        NSMutableArray *questions = [[[[questionnaire objectForKey:@"questionnaire"]objectForKey:@"categories" ] objectAtIndex:0] objectForKey:@"questions"];
        for(NSMutableDictionary *question in questions)
        {
//            NSLog(@"questions count = %d", [questions count]);
            if([[[question objectForKey:@"id"] stringValue] isEqualToString:[NSString stringWithFormat:@"%d", tag]])
            {
                RadioTableViewController *radioController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
                radioController.theID = tag;
                NSMutableArray *answeroptions = [question objectForKey:@"answeroptions"];
                NSMutableArray *values = [[NSMutableArray alloc] initWithCapacity:[answeroptions count]];
                NSInteger selectedIdx = -1;
                NSInteger count = 0;
                for(NSMutableDictionary *answeroption in answeroptions)
                {
                    [values addObject:[answeroption objectForKey:@"answeroptiontext"]];
                    if([[answeroption objectForKey:@"selected"] boolValue])
                    {
                        selectedIdx =count;
                    }
                    count++;
//                    NSLog(@"text = %@", [answeroption objectForKey:@"answeroptiontext"]);
                }
                radioController.selectedIndex = selectedIdx;
                radioController.values = [values copy];
                radioController.questionTitle = [question objectForKey:@"questiontext"];
                radioController.delegate = self;
                [self.navigationController pushViewController:radioController animated:YES];
            }
        }
    }
    else
    {
        [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}

- (void)selectedElementAtIndex:(NSInteger)index andID:(NSInteger)_id
{
    NSMutableArray *questions = [[[[questionnaire objectForKey:@"questionnaire"] objectForKey:@"categories"] objectAtIndex:0] objectForKey:@"questions"];
    for(NSMutableDictionary *question in questions)
    {
        if([[question objectForKey:@"id"] integerValue] == _id)
        {
            NSMutableArray *answeroptions = [question objectForKey:@"answeroptions"];
            NSInteger count = 0;
            for(NSMutableDictionary *answeroption in answeroptions)
            {
                if(count == index)
                {
                    [answeroption setObject:@YES forKey:@"selected"];
                }
                else
                {
                    [answeroption setObject:@NO forKey:@"selected"];
                }
                count++;
            }
        }
    }
    [self.tableView reloadData];
}

- (void)selectedElementAtIndex:(NSInteger)index andTitle:(NSString *)title
{
    //pass
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Table view delegate

// In a xib-based application, navigation from a table can be handled in -tableView:didSelectRowAtIndexPath:
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here, for example:
    // Create the next view controller.
    <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];

    // Pass the selected object to the new view controller.
    
    // Push the view controller.
    [self.navigationController pushViewController:detailViewController animated:YES];
}
 
 */

@end
