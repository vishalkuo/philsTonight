//
//  ToastView.m
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-24.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import "ToastView.h"

@implementation ToastView

float const ToastHeight = 50.0f;
float const ToastGap = 10.0f;


/**
 * TODO: CHECK IF THIS METHOD IS NECESSARY
 **/
-(id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    return self;
}

-(UILabel *)toastLabel{
    if (!_toastLabel){
        _toastLabel = [[UILabel alloc] initWithFrame:CGRectMake(5.0, 5.0, self.frame.size.width - 10, self.frame.size.height - 10)];
        _toastLabel.backgroundColor = [UIColor clearColor];
        _toastLabel.textAlignment = NSTextAlignmentCenter;
        _toastLabel.textColor = [UIColor whiteColor];
        _toastLabel.numberOfLines = 2;
        _toastLabel.font= [UIFont systemFontOfSize:17.0];
        _toastLabel.lineBreakMode = NSLineBreakByCharWrapping;
        [self addSubview:_toastLabel];
    }
    return _toastLabel;
}

-(void)setText:(NSString *)text{
    _text = text;
    self.toastLabel.text = text;
}

+(void)showToast:(UIView *)parentView withText:(NSString *)text withDuration:(float)length{
    int toastsVisible = 0;
    for (UIView *subView in [parentView subviews]){
        if ([subView isKindOfClass:[ToastView class]]){
            toastsVisible++;
        }
    }
    

}

@end
