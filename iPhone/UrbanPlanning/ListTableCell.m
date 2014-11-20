//
//  ListTableCell.m
//  Urban Planning
//
//  Created by George Liaros on 2/25/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "ListTableCell.h"

@implementation ListTableCell
@synthesize entityDescription,entityTitle,entityImageView;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

//- (void)layoutSubviews{
//    if ( UIDeviceOrientationIsPortrait([[UIDevice currentDevice]orientation]) ) {
//        // for portrait
//        self.entityDescription.frame = CGRectMake(184.0f, 40.0f, <#CGFloat width#>, <#CGFloat height#>)
//    } else {
//        //For landscape modes
//    }
//}

@end
