//
//  UserInfoTableViewController.m
//  Urban Planning
//
//  Created by George Liaros on 3/4/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#define LANGUAGE_IN_APP_SWITCH

#import "UserInfoTableViewController.h"

@interface UserInfoTableViewController ()

@end

@implementation UserInfoTableViewController

@synthesize getFromDefaults;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    id tracker = [[GAI sharedInstance] defaultTracker];
    
    // This screen name value will remain set on the tracker and sent with
    // hits until it is set to a new value or to nil.
    [tracker set:kGAIScreenName
           value:@"User Info View"];
    
    // manual screen tracking
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}
- (void)viewDidLoad
{
    dHandler = [[DataHandler alloc] init];
    [super viewDidLoad];
//    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//    self.title = NSLocalizedString(@"Personal Information", @"Personal Information");
    self.title = [DataHandler getLocalizedString:@"Personal Information"];
#ifndef LANGUAGE_IN_APP_SWITCH
    if(getFromDefaults)
    {
//        tableData =@[@"User name:",@"Age",@"Gender", @"Resident in Gordexola", @"Area of residence", @"Save"];
//        tableData = @[NSLocalizedString(@"User name:", @"User name:"), NSLocalizedString(@"Age", @"Age"), NSLocalizedString(@"Gender", @"Gender"), NSLocalizedString(@"Resident in Gordexola", @"Resident in Gordexola"), NSLocalizedString(@"Area of residence", @"Area of residence"), NSLocalizedString(@"Language", @"Language"), NSLocalizedString(@"Save", @"Save")];
        tableData = @[[dHandler getLocalizedString:@"User name:"], [dHandler getLocalizedString:@"Age"], [dHandler getLocalizedString:@"Gender"], [dHandler getLocalizedString:@"Area of residence"], [dHandler getLocalizedString:@"Save"]];
        SWRevealViewController *revealController = [self revealViewController];
        UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"]
                                                                             style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
        
        self.navigationItem.leftBarButtonItem = revealButtonItem;
    }
    else
    {
#endif
#ifdef LANGUAGE_IN_APP_SWITCH
        if(getFromDefaults){
            SWRevealViewController *revealController = [self revealViewController];
            UIBarButtonItem *revealButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"reveal-icon.png"] style:UIBarButtonItemStyleBordered target:revealController action:@selector(revealToggle:)];
            self.navigationItem.leftBarButtonItem = revealButtonItem;
        }
#endif
//        tableData = @[@"User name:",@"Age",@"Gender", @"Resident in Gordexola", @"Area of residence", @"Done"];
//        tableData = @[NSLocalizedString(@"User name:", @"User name:"), NSLocalizedString(@"Age", @"Age"), NSLocalizedString(@"Gender", @"Gender"), NSLocalizedString(@"Resident in Gordexola", @"Resident in Gordexola"), NSLocalizedString(@"Area of residence", @"Area of residence"), NSLocalizedString(@"Language", @"Language"), NSLocalizedString(@"Done", @"Done")];
        tableData = @[[DataHandler getLocalizedString:@"User name:"], [DataHandler getLocalizedString:@"Age"], [DataHandler getLocalizedString:@"Gender"], [DataHandler getLocalizedString:@"Area of residence"], [DataHandler getLocalizedString:@"Language"], [DataHandler getLocalizedString:@"Done"]];
#ifndef LANGUAGE_IN_APP_SWITCH
    }
#endif
//    values = @[@[@"Under the 18", @"18 - 35", @"36 - 65", @"Over the 65"],@[@"Male", @"Female"],@[@"Yes", @"No"],@[@"Zandamendi", @"Zubiete", @"Irazagorria", @"Ponton Urarte", @"Zaldu"]];
//    values = @[
//               @[NSLocalizedString(@"Under the 18", @"Under the 18"), NSLocalizedString(@"18 - 35", @"15 - 35"), NSLocalizedString(@"36 - 65", @"36 - 65"), NSLocalizedString(@"Over the 65", @"Over the 65")],
//               @[NSLocalizedString(@"Male", @"Male"), NSLocalizedString(@"Female", @"Female")],
//               @[NSLocalizedString(@"Yes", @"Yes"), NSLocalizedString(@"No", @"No")],
//               @[NSLocalizedString(@"Zandamendi", @"Zandamendi"),NSLocalizedString(@"Zubiete", @"Zubiete"),NSLocalizedString(@"Irazagorria", @"Irazagorria"),NSLocalizedString(@"Ponton Urarte", @"Ponton Urarte"), NSLocalizedString(@"Zaldu", @"Zaldu")],
//               @[NSLocalizedString(@"English", @"English"), NSLocalizedString(@"Spanish", @"Spanish"), NSLocalizedString(@"Basque", @"Basque")]];
    NSArray *preferredLanguages = [NSLocale preferredLanguages];
    NSInteger espanolIndex = [preferredLanguages indexOfObject:@"es"];
    NSInteger englishIndex = [preferredLanguages indexOfObject:@"en"];
    NSString *deviceLanguageKey;
    if(espanolIndex < englishIndex)
        deviceLanguageKey = @"Spanish";
    else
        deviceLanguageKey = @"English";
    NSLog(@"index of espanol = %d", [preferredLanguages indexOfObject:@"es"]);
    NSLog(@"index of english = %d", [preferredLanguages indexOfObject:@"en"]);
    values = @[
               @[[DataHandler getLocalizedString:@"Under the 18"], [DataHandler getLocalizedString:@"18 - 35"], [DataHandler getLocalizedString:@"36 - 65"], [DataHandler getLocalizedString:@"Over the 65"]],
               @[[DataHandler getLocalizedString:@"Male"], [DataHandler getLocalizedString:@"Female"]],
               @[[DataHandler getLocalizedString:@"Zandamendi"], [DataHandler getLocalizedString:@"Zubiete"], [DataHandler getLocalizedString:@"Irazagorria"], [DataHandler getLocalizedString:@"Ponton Urarte"], [DataHandler getLocalizedString:@"Molinar"], [DataHandler getLocalizedString:@"Zaldu"], [DataHandler getLocalizedString:@"None"]],
               @[[DataHandler getLocalizedString:deviceLanguageKey],[DataHandler getLocalizedString:@"Basque"]]];
#warning return here
    if(getFromDefaults)
    {
        value1 = [[defaults objectForKey:DEFAULTS_VALUE1] integerValue];
        value2 = [[defaults objectForKey:DEFAULTS_VALUE2] integerValue];
        value3 = [[defaults objectForKey:DEFAULTS_VALUE3] integerValue];
        value4 = [[defaults objectForKey:DEFAULTS_VALUE4] integerValue];
        value5 = [[defaults objectForKey:DEFAULTS_VALUE5] integerValue];
        name = [defaults objectForKey:DEFAULTS_NAME];
    }
    else
    {
        value1 = -1;
        value2 = -1;
        value3 = -1;
        value4 = -1;
        value5 = -1;
    }
    doneEnabled = NO;
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    if(section == 0)
    {
//        return NSLocalizedString(@"Please provide your information", @"Please provide your information");
        return [DataHandler getLocalizedString:@"Please provide your information"];
    }
    else
    {
        return nil;
    }
}
- (NSString *) tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
    if(section == 0){
        return [DataHandler getLocalizedString:@"Optional"];
    }
    else
        return nil;
}
//- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
//{
//    if(section == 0)
//    {
//        UILabel *section0Label = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 0.0, 320.0, 44.0)];
//        section0Label.textColor = [UIColor lightGrayColor];
//        section0Label.text = NSLocalizedString(@"Please provide your information", @"Please provide your information");
//        return section0Label;
//    }
//    else
//        return nil;
//}
#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
#ifndef LANGUAGE_IN_APP_SWITCH
    if(getFromDefaults)
        return 5;
    else
#endif
        return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    cell.textLabel.text = [tableData objectAtIndex:[indexPath row]];
    if([indexPath row] == 0)
    {
        [[cell.contentView viewWithTag:5] removeFromSuperview];
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
        cell.accessoryType = UITableViewCellAccessoryNone;
//        cell.textLabel.text = @"";
        UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(188.0, 0.0,120.0, 44.0)];
        textField.delegate = self;
        textField.textColor = [UIColor blueColor];
        textField.tag = 5;
        if(name)
        {
            textField.text = name;
        }
        else
        {
            textField.text = @"";
//            textField.placeholder = NSLocalizedString(@"Your name", @"Your name");
            textField.placeholder = [DataHandler getLocalizedString:@"Your name"];
        }
        textField.textAlignment = NSTextAlignmentRight;
        cell.textLabel.textColor = [UIColor blackColor];
        [cell.contentView addSubview:textField];
    }
    else if([indexPath row] == 1)
    {
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        [[cell.contentView viewWithTag:5] removeFromSuperview];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        UILabel *value = [[UILabel alloc] initWithFrame:CGRectMake(168.0, 0.0, 120.0, 44.0)];
        value.textColor = [UIColor blueColor];
        value.tag = 4;
//        value.text = [values objectAtIndex:[indexPath row]];//change
        value.font = [UIFont systemFontOfSize:16.0f];
        value.text  = (value1 ==-1)?@"":[[values objectAtIndex:0] objectAtIndex:value1];
//        NSLog(@"value.text = %@", value.text);
        value.textAlignment = NSTextAlignmentRight;
        cell.textLabel.textColor = [UIColor blackColor];
        [cell.contentView addSubview:value];
    }
    else if([indexPath row] == 2)
    {
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        [[cell.contentView viewWithTag:5] removeFromSuperview];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        UILabel *value = [[UILabel alloc] initWithFrame:CGRectMake(168.0, 0.0, 120.0, 44.0)];
        value.textColor = [UIColor blueColor];
        value.tag = 4;
        //        value.text = [values objectAtIndex:[indexPath row]];//change
        value.font = [UIFont systemFontOfSize:16.0f];
        value.text = (value2 ==-1)?@"":[[values objectAtIndex:1] objectAtIndex:value2];
        value.textAlignment = NSTextAlignmentRight;
//        NSLog(@"value.text = %@", value.text);
        cell.textLabel.textColor = [UIColor blackColor];
        [cell.contentView addSubview:value];
    }
//    else if([indexPath row] == 3)
//    {
//        [[cell.contentView viewWithTag:4] removeFromSuperview];
//        [[cell.contentView viewWithTag:5] removeFromSuperview];
//        cell.textLabel.textAlignment = NSTextAlignmentLeft;
//        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
//        UILabel *value = [[UILabel alloc] initWithFrame:CGRectMake(168.0, 0.0, 120.0, 44.0)];
//        value.textColor = [UIColor blueColor];
//        value.tag = 4;
//        //        value.text = [values objectAtIndex:[indexPath row]];//change
//        value.font = [UIFont systemFontOfSize:16.0f];
//        value.text = (value3 ==-1)?@"":[[values objectAtIndex:2]
//                                        objectAtIndex:value3];
//        value.textAlignment = NSTextAlignmentRight;
//        value.textAlignment = NSTextAlignmentRight;
////        NSLog(@"value.text = %@", value.text);
//        cell.textLabel.textColor = [UIColor blackColor];
//        [cell.contentView addSubview:value];
//    }
    else if([indexPath row] == 3)
    {
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        [[cell.contentView viewWithTag:5] removeFromSuperview];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        UILabel *value = [[UILabel alloc] initWithFrame:CGRectMake(168.0, 0.0, 120, 44.0)];
        value.textAlignment = NSTextAlignmentRight;
        value.textColor = [UIColor blueColor];
        value.tag = 4;
        //        value.text = [values objectAtIndex:[indexPath row]];//change
        value.font = [UIFont systemFontOfSize:16.0f];
        value.text = (value4 ==-1)?@"":[[values objectAtIndex:2] objectAtIndex:value4];
//        NSLog(@"value.text = %@", value.text);
        cell.textLabel.textColor = [UIColor blackColor];
        [cell.contentView addSubview:value];
    }
    else if ([indexPath row] == 4){
#ifndef LANGUAGE_IN_APP_SWITCH
        if(getFromDefaults){
            if(doneEnabled)
            {
                cell.textLabel.textColor = [UIColor blackColor];
                cell.textLabel.font = [UIFont systemFontOfSize:16.0f];
                cell.textLabel.textAlignment = NSTextAlignmentLeft;
            }
            else
            {
                cell.textLabel.textColor = [UIColor blackColor];
                cell.textLabel.font = [UIFont boldSystemFontOfSize:16.0f];
                cell.textLabel.textAlignment = NSTextAlignmentLeft;
            }
            [[cell.contentView viewWithTag:4] removeFromSuperview];
            [[cell.contentView viewWithTag:5] removeFromSuperview];
        }
        else{
#endif
            [[cell.contentView viewWithTag:4] removeFromSuperview];
            [[cell.contentView viewWithTag:5] removeFromSuperview];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            UILabel *value = [[UILabel alloc] initWithFrame:CGRectMake(168.0, 0.0, 120.0, 44.0)];
            value.textAlignment = NSTextAlignmentRight;
            value.textColor = [UIColor blueColor];
            value.tag = 4;
            value.font = [UIFont systemFontOfSize:16.0f];
            value.text = (value5 == -1) ? @"" : [[values objectAtIndex:3] objectAtIndex:value5];
            cell.textLabel.textColor = [UIColor blackColor];
            [cell.contentView addSubview:value];
#ifndef LANGUAGE_IN_APP_SWITCH
        }
#endif
    }
    else if([indexPath row] == 5)
    {
        cell.accessoryType = UITableViewCellAccessoryNone;
        if(doneEnabled)
        {
            cell.textLabel.textColor = [UIColor blackColor];
            cell.textLabel.font = [UIFont systemFontOfSize:16.0f];
            cell.textLabel.textAlignment = NSTextAlignmentLeft;
        }
        else
        {
            cell.textLabel.textColor = [UIColor blackColor];
            cell.textLabel.font = [UIFont boldSystemFontOfSize:16.0f];
            cell.textLabel.textAlignment = NSTextAlignmentLeft;
        }
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        [[cell.contentView viewWithTag:5] removeFromSuperview];
    }
    else
    {
        [[cell.contentView viewWithTag:4] removeFromSuperview];
        [[cell.contentView viewWithTag:5] removeFromSuperview];
    }
    // Configure the cell...
    
    return cell;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString * proposedNewString = [[textField text] stringByReplacingCharactersInRange:range withString:string];
    name = proposedNewString;
    if([proposedNewString length]==0)
    {
        textField.text = nil;
    }
//    [textField sizeToFit];
    return YES;
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

- (void)selectedElementAtIndex:(NSInteger)index andTitle:(NSString *)title
{
    NSLog(@"title = %@", title);
    NSLog(@"index = %d", index);
//    if([title isEqualToString:NSLocalizedString(@"Age", @"Age")])
    if([title isEqualToString:[DataHandler getLocalizedString:@"Age"]])
    {
        value1 = index;
//        [self.tableView reloadData];
//        [[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]].contentView setNeedsDisplay];
        [self.tableView beginUpdates];
        NSMutableIndexSet* index = [[NSMutableIndexSet alloc]init];
        [index addIndex:0];
        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.tableView endUpdates];
    }
//    else if([title isEqualToString:NSLocalizedString(@"Gender", @"Gender")])
    else if([title isEqualToString:[DataHandler getLocalizedString:@"Gender"]])
    {
        value2 = index;
        [self.tableView beginUpdates];
        NSMutableIndexSet* index = [[NSMutableIndexSet alloc]init];
        [index addIndex:0];
        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.tableView endUpdates];
    }
//    else if([title isEqualToString:NSLocalizedString(@"Resident in Gordexola", @"Resident in Gordexola")])
    else if([title isEqualToString:[DataHandler getLocalizedString:@"Resident in Gordexola"]])
    {
        value3 = index;
        [self.tableView beginUpdates];
        NSMutableIndexSet* index = [[NSMutableIndexSet alloc]init];
        [index addIndex:0];
        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.tableView endUpdates];
    }
//    else if([title isEqualToString:NSLocalizedString(@"Area of residence", @"Area of residence")])
    else if([title isEqualToString:[DataHandler getLocalizedString:@"Area of residence"]])
    {
        value4 = index;
        [self.tableView beginUpdates];
        NSMutableIndexSet* index = [[NSMutableIndexSet alloc]init];
        [index addIndex:0];
        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.tableView endUpdates];
    }
//    else if([title isEqualToString:NSLocalizedString(@"Language", @"Language")])
//    else if([title isEqualToString:[dHandler getLocalizedString:@"Language"]])
//    {
//        value5 = index;
//        [self.tableView beginUpdates];
//        NSMutableIndexSet *index = [[NSMutableIndexSet alloc] init];
//        [index addIndex:0];
//        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
//        [self.tableView endUpdates];
//    }
    else {
        value5 = index;
        [self.tableView beginUpdates];
        NSMutableIndexSet *index = [[NSMutableIndexSet alloc] init];
        [index addIndex:0];
        [self.tableView reloadSections:index withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.tableView endUpdates];
    }
    [self.tableView reloadData];
}

- (void)selectedElementAtIndex:(NSInteger)index andID:(NSInteger)_id
{
    //pass
}
#pragma mark - Table view delegate

// In a xib-based application, navigation from a table can be handled in -tableView:didSelectRowAtIndexPath:
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if([indexPath row] == 0)
    {
        [[[[self.tableView cellForRowAtIndexPath:indexPath] contentView] viewWithTag:5] becomeFirstResponder];
    }
    else if([indexPath row] == 1)
    {
        RadioTableViewController *radioTableViewController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
        radioTableViewController.selectedIndex = value1;
        radioTableViewController.values = [values objectAtIndex:0];
        radioTableViewController.theTitle = [tableData objectAtIndex:[indexPath row]];
        radioTableViewController.delegate = self;
        [self.navigationController pushViewController:radioTableViewController animated:YES];
    }
    else if([indexPath row] == 2)
    {
        RadioTableViewController *radioTableViewController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
        radioTableViewController.selectedIndex = value2;
        radioTableViewController.values = [values objectAtIndex:1];
        radioTableViewController.theTitle = [tableData objectAtIndex:[indexPath row]];
        radioTableViewController.delegate = self;
        [self.navigationController pushViewController:radioTableViewController animated:YES];
    }
//    else if([indexPath row] == 3)
//    {
//        RadioTableViewController *radioTableViewController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
//        radioTableViewController.selectedIndex = value3;
//        radioTableViewController.values = [values objectAtIndex:2];
//        radioTableViewController.theTitle = [tableData objectAtIndex:[indexPath row]];
//        radioTableViewController.delegate = self;
//        [self.navigationController pushViewController:radioTableViewController animated:YES];
//    }
    else if([indexPath row] == 3)
    {
        RadioTableViewController *radioTableViewController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
        radioTableViewController.selectedIndex = value4;
        radioTableViewController.values = [values objectAtIndex:2];
        radioTableViewController.theTitle = [tableData objectAtIndex:[indexPath row]];
        radioTableViewController.delegate = self;
        [self.navigationController pushViewController:radioTableViewController animated:YES];
    }
    else if([indexPath row] == 4)
    {
#ifndef LANGUAGE_IN_APP_SWITCH
        if(!getFromDefaults)
        {
#endif
            RadioTableViewController *radioTableViewController = [[RadioTableViewController alloc] initWithNibName:@"RadioTableViewController" bundle:nil];
            radioTableViewController.selectedIndex = value5;
            NSLog(@"value 5 = %d", value5);
            radioTableViewController.values = [values objectAtIndex:3];
            radioTableViewController.theTitle = [tableData objectAtIndex:[indexPath row]];
            radioTableViewController.delegate = self;
            [self.navigationController pushViewController:radioTableViewController animated:YES];
#ifndef LANGUAGE_IN_APP_SWITCH
        }
        else
        {
            [dHandler setUserInformation:name
                                     Age:values[0][value1]
                                     Sex:values[1][value2]
                       GordexolaResident:values[2][value3]
                           ResidenceZone:values[3][value4]];
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[dHandler getLocalizedString:@"Success"] message:[dHandler getLocalizedString:@"User information stored successfully"] delegate:nil cancelButtonTitle:[dHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
            [alert show];
        }
#endif
    }
    else if([indexPath row] == 5)
    {
//        NSLog(@"name = %@", name);
//        NSLog(@"Age = %@", [[values objectAtIndex:0] objectAtIndex:value1]);
//        NSLog(@"Sex = %@", [[values objectAtIndex:1] objectAtIndex:value2]);
//        NSLog(@"Location = %@", [[values objectAtIndex:2] objectAtIndex:value3]);
//        NSLog(@"Residence = %@", [[values objectAtIndex:3] objectAtIndex:value4]);
        if(name && value4 != -1 && value5 != -1)
        {
//            [dHandler setUserInformation:name
//                                     Age:[[values objectAtIndex:0] objectAtIndex:value1]
//                                     Sex:[[values objectAtIndex:1] objectAtIndex:value2]
//                       GordexolaResident:[[values objectAtIndex:2]objectAtIndex:value3]
//                           ResidenceZone:[[values objectAtIndex:3] objectAtIndex:value4]];
            NSString *age;
            if(value1 == -1)
                age = nil;
            else
                age = [[values objectAtIndex:0] objectAtIndex:value1];
            NSString *gender;
            if(value2 == -1)
                gender = nil;
            else
                gender = [[values objectAtIndex:1] objectAtIndex:value2];
            [dHandler setUserInformation:name
                                     Age:age
                                     Sex:gender
                           ResidenceZone:[[values objectAtIndex:2] objectAtIndex:value4]
                                Language:[[values objectAtIndex:3] objectAtIndex:value5]];
            [dHandler storeUserv1:value1
                               v2:value2
                               v4:value4
                               v5:value5];
//            [dHandler storeUserv1:value1 v2:value2 v3:value3 v4:value4];
            if(getFromDefaults)
            {
//                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Success", @"Success") message:NSLocalizedString(@"User information stored successfully", @"User information stored successfully") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
                [dHandler getUserQuestionnaire:YES];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Success"] message:[DataHandler getLocalizedString:@"User information stored successfully"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
                [alert show];
                UINavigationController *rearNavController = (UINavigationController*) [[self revealViewController] rearViewController];
                RearViewController *rearController = rearNavController.viewControllers[0];
                [[rearController rearTableView] reloadData];
                rearController.title = [DataHandler getLocalizedString:@"Navigation"];
                tableData = @[[DataHandler getLocalizedString:@"User name:"], [DataHandler getLocalizedString:@"Age"], [DataHandler getLocalizedString:@"Gender"], [DataHandler getLocalizedString:@"Resident in Gordexola"], [DataHandler getLocalizedString:@"Area of residence"], [DataHandler getLocalizedString:@"Language"], [DataHandler getLocalizedString:@"Done"]];
                NSArray *preferredLanguages = [NSLocale preferredLanguages];
                NSInteger espanolIndex = [preferredLanguages indexOfObject:@"es"];
                NSInteger englishIndex = [preferredLanguages indexOfObject:@"en"];
                NSString *deviceLanguageKey;
                if(espanolIndex < englishIndex)
                    deviceLanguageKey = @"Spanish";
                else
                    deviceLanguageKey = @"English";
                NSLog(@"index of espanol = %d", [preferredLanguages indexOfObject:@"es"]);
                NSLog(@"index of english = %d", [preferredLanguages indexOfObject:@"en"]);
                values = @[
                           @[[DataHandler getLocalizedString:@"Under the 18"], [DataHandler getLocalizedString:@"18 - 35"], [DataHandler getLocalizedString:@"36 - 65"], [DataHandler getLocalizedString:@"Over the 65"]],
                           @[[DataHandler getLocalizedString:@"Male"], [DataHandler getLocalizedString:@"Female"]],
                           @[[DataHandler getLocalizedString:@"Zandamendi"], [DataHandler getLocalizedString:@"Zubiete"], [DataHandler getLocalizedString:@"Irazagorria"], [DataHandler getLocalizedString:@"Ponton Urarte"], [DataHandler getLocalizedString:@"Molinar"], [DataHandler getLocalizedString:@"Zaldu"], [DataHandler getLocalizedString:@"None"]],
                           @[[DataHandler getLocalizedString:deviceLanguageKey],[DataHandler getLocalizedString:@"Basque"]]];
                self.title = [DataHandler getLocalizedString:@"Personal Information"];
                [tableView reloadData];
            }
            else
            {
                FrontViewController *frontViewController = [[FrontViewController alloc] init];
                RearViewController *rearViewController = [[RearViewController alloc] init];
        
                UINavigationController *frontNavigationController = [[UINavigationController alloc] initWithRootViewController:frontViewController];
                UINavigationController *rearNavigationController = [[UINavigationController alloc] initWithRootViewController:rearViewController];
        
                SWRevealViewController *revealController = [[SWRevealViewController alloc] initWithRearViewController:rearNavigationController frontViewController:frontNavigationController];
//        revealController.delegate = self;
        
        
                RightViewController *rightViewController = rightViewController = [[RightViewController alloc] init];
                rightViewController.view.backgroundColor = [UIColor greenColor];
        
                revealController.rightViewController = rightViewController;
        
        //revealController.bounceBackOnOverdraw=NO;
        //revealController.stableDragOnOverdraw=YES;
                [self presentViewController:revealController animated:YES completion:nil];
            }
        }
        else
        {
//            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Empty Fields", @"Empty Fields") message:NSLocalizedString(@"Please fill all the questions", @"Please fill all the questions") delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", @"OK") otherButtonTitles:nil, nil];
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[DataHandler getLocalizedString:@"Empty Fields"] message:[DataHandler getLocalizedString:@"Please fill all the questions"] delegate:nil cancelButtonTitle:[DataHandler getLocalizedString:@"OK"] otherButtonTitles:nil, nil];
            [alert show];
        }

    }
}
 

@end
