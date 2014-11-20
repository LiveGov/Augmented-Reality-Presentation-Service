//
//  MapInfoView.m
//  ImproveMyCity
//
//  Created by George Liaros on 9/30/13.
//  Copyright (c) 2013 George Liaros. All rights reserved.
//

#import "MapInfoView.h"

@implementation MapInfoView
@synthesize addressLabel;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)drawRect:(CGRect)rect
{
//    CGContextRef context = UIGraphicsGetCurrentContext();
//    
//    CGContextAddArc(context, 50, 50, 50, 0, 30, 0);
//    CGContextFillPath(context);
//    
//    //set the fill or stroke color
//    CGContextSetRGBFillColor(context, 1, 0.5, 0.5, 1.0);
//    CGContextSetRGBStrokeColor(context, 0.5, 1, 0.5, 1.0);
//    
//    //fill or draw the path
//    CGContextDrawPath(context, kCGPathStroke);
//    CGContextDrawPath(context, kCGPathFill);
//    
//    CGContextAddArc(context, 100, 100, 20, 0, 30, 0);
//    
//    //set the fill or stroke color
//    CGContextSetRGBFillColor(context, 1, 0.5, 0.5, 1.0);
//    CGContextSetRGBStrokeColor(context, 0.5, 1, 0.5, 1.0);
//    
//    //fill or draw the path
//    CGContextDrawPath(context, kCGPathStroke);
//    CGContextDrawPath(context, kCGPathFill);
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    
//    CGContextBeginPath(ctx);
//    CGContextMoveToPoint   (ctx, CGRectGetMinX(rect), CGRectGetMinY(rect));  // top left
//    CGContextAddLineToPoint(ctx, CGRectGetMaxX(rect), CGRectGetMidY(rect));  // mid right
//    CGContextAddLineToPoint(ctx, CGRectGetMinX(rect), CGRectGetMaxY(rect));  // bottom left
//    CGContextClosePath(ctx);
    CGContextSetRGBStrokeColor(ctx, 0.0, 0.0, 0.0, 1.0);
    // Drawing with a blue fill color
    CGContextSetRGBFillColor(ctx, 1.0, 1.0, 1.0, 1.0);
    // Draw them with a 2.0 stroke width so they are a bit more visible.
    CGContextSetLineWidth(ctx, 1.0);
    
//    CGPoint center;
    
    // Add a triangle to the current path
//    center = CGPointMake(90.0, 90.0);
    CGContextMoveToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0 - 9.0, self.containerView.frame.origin.y -1.0+ self.containerView.frame.size.height);
    CGContextAddLineToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0, self.containerView.frame.origin.y + self.containerView.frame.size.height + 20.0);
    CGContextAddLineToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0 + 9.0, self.containerView.frame.origin.y -1.0 + self.containerView.frame.size.height);
//    CGContextMoveToPoint(ctx, center.x, center.y + 60.0);
//    for(int i = 1; i < 3; ++i)
//    {
//        CGFloat x = 60.0 * sinf(i * 4.0 * M_PI / 3.0);
//        CGFloat y = 60.0 * cosf(i * 4.0 * M_PI / 3.0);
//        CGContextAddLineToPoint(ctx, center.x + x, center.y + y);
//    }
    // And close the subpath.
    CGContextClosePath(ctx);
    
    // Now draw the triangle with the current drawing mode.
    CGContextDrawPath(ctx, kCGPathFillStroke);
//    CGContextDrawPath(ctx, kCGPathFillStroke);
//    CGContextBeginPath(ctx);
//    CGContextMoveToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0 - 9.0, self.containerView.frame.origin.y + self.containerView.frame.size.height);
//    CGContextAddLineToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0, self.containerView.frame.origin.y + self.containerView.frame.size.height + 21.0);
//    CGContextAddLineToPoint(ctx, self.containerView.frame.origin.x + self.containerView.frame.size.width/2.0 + 9.0, self.containerView.frame.origin.y + self.containerView.frame.size.height);
//    CGContextSetRGBFillColor(ctx, 1, 1, 1, 1);
//    CGContextSetRGBStrokeColor(ctx, 0, 0, 0, 1);
//    CGContextFillPath(ctx);
}//21
- (id) initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        // CUSTOM INITIALIZATION HERE
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
