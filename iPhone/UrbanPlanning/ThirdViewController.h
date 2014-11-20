//
//  ThirdViewController.h
//  ImproveMyCity
//
//  Created by George Liaros on 8/20/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"
#import "CategoryData.h"

@interface ThirdViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>
{
    CategoryData *categoryData;
    NSArray *categoriesArr;
}
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end

@interface CategoryTableCell : UITableViewCell
{
    
}
@property (weak, nonatomic) IBOutlet UIImageView *categoryIcon;
@property (weak, nonatomic) IBOutlet UILabel *categoryTitle;

@property (nonatomic) NSInteger identifier;

@end