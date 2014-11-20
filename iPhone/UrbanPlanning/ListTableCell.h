//
//  ListTableCell.h
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ListTableCell : UITableViewCell


@property (weak,nonatomic) IBOutlet UIImageView *entityImageView;
@property (weak, nonatomic) IBOutlet UILabel *entityTitle;
@property (weak, nonatomic) IBOutlet UILabel *entityDescription;
@end
