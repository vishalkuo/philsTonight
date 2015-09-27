//
//  ViewController.m
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-23.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import "ViewController.h"
#import "ToastView.h"

@interface ViewController ()

-(void)loadContacts;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [_addContactBtn addTarget:self action:@selector(loadContacts) forControlEvents:UIControlEventTouchUpInside];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)loadContacts{
    ABAddressBookRef addressBookRef =  ABAddressBookCreateWithOptions(NULL, NULL);
    if (ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined){
        ABAddressBookRequestAccessWithCompletion(addressBookRef, ^(bool granted, CFErrorRef error) {
            if (granted){
                NSLog(@"GRANTED");
            }else{
                [ToastView showToast:self.view withText:@"REJECTED" withDuration:1.0f];
            }
        });
    } else if (ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized){
        NSLog(@"GUCCI");
    } else{
        [self showAlert];
    }
}

-(void)showAlert{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"No Permission!" message:@"Please allow Phil's Tonight to access your contacts by opening Settings -> Privacy -> Contacts" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil];
//    UIAlertAction *openSettings = [UIAlertAction actionWithTitle:@"Open Settings" style:UIAlertActionStyleDefault
//                                                         handler:^(UIAlertAction * _Nonnull action) {
//                                                             [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
//                                                         }];
    [alertController addAction:ok];
//    [alertController addAction:openSettings];
    
    [self presentViewController:alertController animated:YES completion:nil];
}


@end
