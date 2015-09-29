//
//  Person.h
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-29.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Person : NSObject

@property NSString *fullName;
@property NSString *phoneNumber;

-(id)initWithParams:(NSString *)fullName phoneNumber:(NSString *)number;

@end
