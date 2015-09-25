//
//  ToastView.h
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-24.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ToastView : UIView

@property(strong, nonatomic)NSString *text;
@property(strong, nonatomic) UILabel *toastLabel;


+ (void)showToast: (UIView *)parentView withText:(NSString *)text withDuration:(float)length;

@end
