//
//  ViewController.m
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-23.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import "ViewController.h"
#import "ToastView.h"
#import "Person.h"

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _peoplePickerController = [[CNContactPickerViewController alloc] init];
    _peoplePickerController.delegate = self;
    
    _testArray = [NSArray arrayWithObjects:@"Egg Benedict", @"Mushroom Risotto", @"Full Breakfast", @"Hamburger", @"Ham and Egg Sandwich", @"Creme Brelee", @"White Chocolate Donut", @"Starbucks Coffee", @"Vegetable Curry", @"Instant Noodle with Egg", @"Noodle with BBQ Pork", @"Japanese Noodle with Pork", @"Green Tea", @"Thai Shrimp Cake", @"Angry Birds Cake", @"Ham and Cheese Panini", nil];
    
    [_addContactBtn addTarget:self action:@selector(loadContacts) forControlEvents:UIControlEventTouchUpInside];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)loadContacts{
    CNContactStore *contactStore = [[CNContactStore alloc] init];
    
    if ([CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts] == CNAuthorizationStatusNotDetermined){
        [contactStore requestAccessForEntityType:CNEntityTypeContacts completionHandler:^(BOOL granted, NSError * _Nullable error) {
            if (granted){
                [self presentViewController:_peoplePickerController animated:YES completion:nil];
            }else{
                [ToastView showToast:self.view withText:@"REJECTED" withDuration:1.0f];
            }
        }];
    } else if ([CNContactStore authorizationStatusForEntityType:CNEntityTypeContacts] == CNAuthorizationStatusAuthorized){
        [self presentViewController:_peoplePickerController animated:YES completion:nil];
    } else{
        [self showAlert];
    }
}

-(void)showAlert{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"No Permission!" message:@"Please allow Phil's Tonight to access your contacts by opening Settings -> Privacy -> Contacts" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil];
    
    [alertController addAction:ok];
    
    [self presentViewController:alertController animated:YES completion:nil];
}

-(void)contactViewController:(CNContactViewController *)viewController didCompleteWithContact:(CNContact *)contact{
}

-(void)contactPickerDidCancel:(CNContactPickerViewController *)picker{
    
}

-(void)contactPicker:(CNContactPickerViewController *)picker didSelectContact:(CNContact *)contact{
    
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 0;
}

-(NSInteger)tableView:(UITableView *)tableView indentationLevelForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 0;
}

-(NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    return nil;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    return nil;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [_testArray count];
}

@end
