
//
//  Person.m
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-29.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import "Person.h"

@implementation Person

-(id)initWithParams:(NSString *)fullName phoneNumber:(NSString *)number{
    self = [super init];
    if (self){
        _fullName = fullName;
        _phoneNumber = number;
    }
    return self;
}

@end
