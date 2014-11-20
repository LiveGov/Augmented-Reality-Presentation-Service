//
//  ThirdViewController.m
//  ImproveMyCity
//
//  Created by George Liaros on 8/20/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import "ThirdViewController.h"

@interface ThirdViewController ()

@end

@implementation CategoryTableCell



@end

@implementation ThirdViewController
@synthesize tableView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    AppDelegate *del = [[UIApplication sharedApplication] delegate];
    categoryData = del.categoryData;
    categoriesArr = [categoryData getSortedCategories];
    if(del.currentUserInfo)
        NSLog(@"user is logged in");
    else
        NSLog(@"user is not logged in");
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CategoryTableCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"categoryCell"];
    if(!cell)
    {
        NSLog(@"cell = nil");
    }
    Category *category = [categoriesArr objectAtIndex:[indexPath row]];
    NSLog(@"category id = %d", category.idParent);
    [cell.imageView setImage:category.image];
    cell.categoryTitle.text = category.title;
    if(category.isParent)
    {
        [cell.categoryTitle setFont:[UIFont boldSystemFontOfSize:17.0]];
    }
    else
    {
        [cell.categoryTitle setFont:[UIFont systemFontOfSize:17.0]];
    }
    return cell;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [categoriesArr count];
}

@end
