
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

#import "RearViewController.h"

#import "SWRevealViewController.h"
#import "FrontViewController.h"
#import "MapViewController.h"

@interface RearViewController()

@end

@implementation RearViewController

@synthesize rearTableView = _rearTableView;


#pragma mark - View lifecycle


- (void)viewDidLoad
{
	[super viewDidLoad];
	dHandler = [[DataHandler alloc] init];
//	self.title = NSLocalizedString(@"Navigation", nil);
    self.title = [DataHandler getLocalizedString:@"Navigation"];
    self.view.backgroundColor = [UIColor colorWithRed:241.0f/255.0f green:241.0f/255.0f blue:241.0f/255.0f alpha:1.0f];
    self.rearTableView.backgroundColor = [UIColor colorWithRed:241.0f/255.0f green:241.0f/255.0f blue:241.0f/255.0f alpha:1.0f];
}


#pragma marl - UITableView Data Source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return 5;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [UIView new];
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 42.0f;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//	static NSString *cellIdentifier = @"sidebarViewCell";
//	SidebarViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
//    NSInteger row = indexPath.row;
//	
//	if (nil == cell)
//	{
//        [[NSBundle mainBundle] loadNibNamed:@"RearViewController" owner:self options:nil];
//        cell = self.sidebarCell;
//        self.sidebarCell = nil;
//	}
//	
//	if (row == 0)
//	{
//        cell.sideCellImageView.image = [UIImage imageNamed:@"IconSidebarList.png"];
//        cell.sideCellLabel.text = NSLocalizedString(@"List", @"List sidebar text");
//	}
//	else if (row == 1)
//	{
//        cell.sideCellLabel.text = NSLocalizedString(@"Map", @"Map sidebar text");
//        cell.sideCellImageView.image = [UIImage imageNamed:@"IconSidebarMap.png"];
//	}
//	else if (row == 2)
//	{
//        cell.sideCellLabel.text = NSLocalizedString(@"AR Layer", @"AR sidebar text");
//        cell.sideCellImageView.image = [UIImage imageNamed:@"IconSidebarAR.png"];
//	}
//	else if (row == 3)
//	{
//        cell.sideCellLabel.text = NSLocalizedString(@"Personal Information", @"Personal information sidebar text");
//        cell.sideCellImageView.image = [UIImage imageNamed:@"IconSidebarPersonal.png"];
//	}
//    else if (row == 4)
//    {
//        cell.sideCellLabel.text = NSLocalizedString(@"About", @"About sidebar text");
//        cell.sideCellImageView.image = [UIImage imageNamed:@"IconSidebarAbout.png"];
//    }
//
    static NSString *cellIdentifier = @"Cell";
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    NSInteger row = indexPath.row;
	
    SWRevealViewController *revealController = self.revealViewController;
    UINavigationController *frontNavigationController = (id)revealController.frontViewController;
	if (nil == cell)
	{
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier];
	}
	
	if (row == 0)
	{
        [[cell viewWithTag:123] removeFromSuperview];
        UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10.0f, 7.0f, 28.0f, 28.0f)];
        cellImageView.image = [UIImage imageNamed:@"IconSidebarList.png"];
        UILabel *cellLabel = [[UILabel alloc] initWithFrame:CGRectMake(45.0f, 7.0f, 200.0f, 28.0f)];
//        cellLabel.text = NSLocalizedString(@"List", @"List sidebar");
        cellLabel.text = [DataHandler getLocalizedString:@"List"];
        cellLabel.font = [UIFont systemFontOfSize:18.0f];
        cellLabel.tag = 123;
        if ( ![frontNavigationController.topViewController isKindOfClass:[FrontViewController class]] )
        {
//            NSLog(@"not frontview controller");
//			cellImageView.alpha = 0.3f;
            cellLabel.textColor = [UIColor colorWithRed:153.0f/255.0f green:153.0f/255.0f blue:153.0f/255.0f alpha:1.0f];
		}
        else
        {
//            cellImageView.alpha = 1.0f;
            cellLabel.textColor = [UIColor blackColor];
        }
        cell.contentView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:cellImageView];
        [cell.contentView addSubview:cellLabel];
	}
	else if (row == 1)
	{
        [[cell viewWithTag:123] removeFromSuperview];
        UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10.0f, 7.0f, 28.0f, 28.0f)];
        cellImageView.image = [UIImage imageNamed:@"IconSidebarMap.png"];
        UILabel *cellLabel = [[UILabel alloc] initWithFrame:CGRectMake(45.0f, 7.0f, 200.0f, 28.0f)];
//        cellLabel.text = NSLocalizedString(@"Map", @"Map sidebar");
        cellLabel.text = [DataHandler getLocalizedString:@"Map"];
        cellLabel.font = [UIFont systemFontOfSize:18.0f];
        cellLabel.tag = 123;
        if ( ![frontNavigationController.topViewController isKindOfClass:[MapViewController class]] )
        {
//            NSLog(@"not mapview controller");
//			cellImageView.alpha = 0.3f;
            cellLabel.textColor = [UIColor colorWithRed:153.0f/255.0f green:153.0f/255.0f blue:153.0f/255.0f alpha:1.0f];
		}
        else
        {
//            cellImageView.alpha = 1.0f;
            cellLabel.textColor = [UIColor blackColor];
        }
        cell.contentView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:cellImageView];
        [cell.contentView addSubview:cellLabel];
        
	}
	else if (row == 2)
	{
        [[cell viewWithTag:123] removeFromSuperview];
		UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10.0f, 7.0f, 28.0f, 28.0f)];
        cellImageView.image = [UIImage imageNamed:@"IconSidebarAR.png"];
        UILabel *cellLabel = [[UILabel alloc] initWithFrame:CGRectMake(45.0f, 7.0f, 200.0f, 28.0f)];
//        cellLabel.text = NSLocalizedString(@"AR Layer", @"AR Layer sidebar");
        cellLabel.text = [DataHandler getLocalizedString:@"AR Layer"];
        cellLabel.font = [UIFont systemFontOfSize:18.0f];
        cellLabel.tag = 123;
        if ( ![frontNavigationController.topViewController isKindOfClass:[ARELViewController class]] )
        {
//			cellImageView.alpha = 0.3f;
            cellLabel.textColor = [UIColor colorWithRed:153.0f/255.0f green:153.0f/255.0f blue:153.0f/255.0f alpha:1.0f];
		}
        else
        {
//            cellImageView.alpha = 1.0f;
            cellLabel.textColor = [UIColor blackColor];
        }
        cell.contentView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:cellImageView];
        [cell.contentView addSubview:cellLabel];
	}
	else if (row == 3)
	{
        [[cell viewWithTag:123] removeFromSuperview];
		UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10.0f, 7.0f, 28.0f, 28.0f)];
        cellImageView.image = [UIImage imageNamed:@"IconSidebarPersonal.png"];
        UILabel *cellLabel = [[UILabel alloc] initWithFrame:CGRectMake(45.0f, 7.0f, 200.0f, 28.0f)];
//        cellLabel.text = NSLocalizedString(@"Personal Information", @"Personal Information sidebar");
        cellLabel.text = [DataHandler getLocalizedString:@"Personal Information"];
        cellLabel.font = [UIFont systemFontOfSize:18.0f];
        cellLabel.tag = 123;
        if ( ![frontNavigationController.topViewController isKindOfClass:[UserInfoTableViewController class]] )
        {
//			cellImageView.alpha = 0.3f;
            cellLabel.textColor = [UIColor colorWithRed:153.0f/255.0f green:153.0f/255.0f blue:153.0f/255.0f alpha:1.0f];
		}
        else
        {
//            cellImageView.alpha = 1.0f;
            cellLabel.textColor = [UIColor blackColor];
        }
        cell.contentView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:cellImageView];
        [cell.contentView addSubview:cellLabel];
	}
    else if (row == 4)
    {
        [[cell viewWithTag:123] removeFromSuperview];
        UIImageView *cellImageView = [[UIImageView alloc] initWithFrame:CGRectMake(10.0f, 7.0f, 28.0f, 28.0f)];
        cellImageView.image = [UIImage imageNamed:@"IconSidebarAbout.png"];
        UILabel *cellLabel = [[UILabel alloc] initWithFrame:CGRectMake(45.0f, 7.0f, 200.0f, 28.0f)];
//        cellLabel.text = NSLocalizedString(@"About", @"About sidebar");
        cellLabel.text = [DataHandler getLocalizedString:@"About"];
        cellLabel.font = [UIFont systemFontOfSize:18.0f];
        cellLabel.tag = 123;
        if ( ![frontNavigationController.topViewController isKindOfClass:[AboutViewController class]] )
        {
//			cellImageView.alpha = 0.3f;
            cellLabel.textColor = [UIColor colorWithRed:153.0f/255.0f green:153.0f/255.0f blue:153.0f/255.0f alpha:1.0f];
		}
        else
        {
//            cellImageView.alpha = 1.0f;
            cellLabel.textColor = [UIColor blackColor];
        }
        cell.contentView.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:cellImageView];
        [cell.contentView addSubview:cellLabel];
    }
	cell.selectionStyle = UITableViewCellSelectionStyleNone;
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	// Grab a handle to the reveal controller, as if you'd do with a navigtion controller via self.navigationController.
    SWRevealViewController *revealController = self.revealViewController;
    
    // We know the frontViewController is a NavigationController
    UINavigationController *frontNavigationController = (id)revealController.frontViewController;  // <-- we know it is a NavigationController
    NSInteger row = indexPath.row;

	// Here you'd implement some of your own logic... I simply take for granted that the first row (=0) corresponds to the "FrontViewController".
	if (row == 0)
	{
		// Now let's see if we're not attempting to swap the current frontViewController for a new instance of ITSELF, which'd be highly redundant.        
        if ( ![frontNavigationController.topViewController isKindOfClass:[FrontViewController class]] )
        {
			FrontViewController *frontViewController = [[FrontViewController alloc] init];
			UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:frontViewController];
			[revealController setFrontViewController:navigationController animated:YES];
        }
		// Seems the user attempts to 'switch' to exactly the same controller he came from!
		else
		{
			[revealController revealToggle:self];
		}
	}
    
	// ... and the second row (=1) corresponds to the "MapViewController".
	else if (row == 1)
	{
		// Now let's see if we're not attempting to swap the current frontViewController for a new instance of ITSELF, which'd be highly redundant.
        if ( ![frontNavigationController.topViewController isKindOfClass:[MapViewController class]] )
        {
            
            
			MapViewController *mapViewController = [[MapViewController alloc] init];
			UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:mapViewController];
			[revealController setFrontViewController:navigationController animated:YES];
            [self.rearTableView reloadData];
		}
        
		// Seems the user attempts to 'switch' to exactly the same controller he came from!
		else
		{
			[revealController revealToggle:self];
		}
	}
	else if (row == 2)
	{
        if ( ![frontNavigationController.topViewController isKindOfClass:[ARELViewController class]] )
        {
            ARELViewController *ARController = [[ARELViewController alloc] initWithNibName:@"ARELViewController" bundle:nil ARMode:@"imc" arelInstructionsOrNil:@""];
			UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:ARController];
			[revealController setFrontViewController:navigationController animated:YES];
		}
        
        else
        {
			[revealController revealToggle:self];
        }
        
	}
	else if (row == 3)
	{
        if( ![frontNavigationController.topViewController isKindOfClass:[UserInfoTableViewController class]])
        {
            UserInfoTableViewController *infoController = [[UserInfoTableViewController alloc] initWithNibName:@"UserInfoTableViewController" bundle:nil];
            infoController.getFromDefaults = YES;
            UINavigationController *navigationController = [[UINavigationController alloc]initWithRootViewController:infoController];
            
            [revealController setFrontViewController:navigationController animated:YES];
        }
        else
        {
            [revealController revealToggle:self];
        }

	}
    else if (row == 4)
    {
//        ARELViewController *ARController = [[ARELViewController alloc] initWithNibName:@"ARELViewController" bundle:nil ARMode:@"lbs" arelInstructionsOrNil:@""];
//        ARController.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
//        [self presentViewController:ARController animated:YES completion:nil];
        if ( ![frontNavigationController.topViewController isKindOfClass:[AboutViewController class]] )
        {
            AboutViewController *aboutViewController = [[AboutViewController alloc] initWithNibName:@"AboutViewController" bundle:nil];
			UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:aboutViewController];
			[revealController setFrontViewController:navigationController animated:YES];
		}
        
		// Seems the user attempts to 'switch' to exactly the same controller he came from!
		else
		{
			[revealController revealToggle:self];
		}
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [cell setBackgroundColor:[UIColor clearColor]];
}

- (void)viewWillAppear:(BOOL)animated
{
    self.screenName = @"Sidebar View";
}


//- (void)viewWillAppear:(BOOL)animated
//{
//    [super viewWillAppear:animated];
//    NSLog( @"%@: REAR", NSStringFromSelector(_cmd));
//}
//
//- (void)viewWillDisappear:(BOOL)animated
//{
//    [super viewWillDisappear:animated];
//    NSLog( @"%@: REAR", NSStringFromSelector(_cmd));
//}
//
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.rearTableView reloadData];
}
//
//- (void)viewDidDisappear:(BOOL)animated
//{
//    [super viewDidDisappear:animated];
//    NSLog( @"%@: REAR", NSStringFromSelector(_cmd));
//}

@end