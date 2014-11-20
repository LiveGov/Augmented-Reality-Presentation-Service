//
//  MapInfoView.h
//  ImproveMyCity
//
//  Created by George Liaros on 9/30/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

/* A view for the marker info window
 */

#import <UIKit/UIKit.h>

@interface MapInfoView : UIView
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *detailImageView;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UILabel *idLabel;

@end
