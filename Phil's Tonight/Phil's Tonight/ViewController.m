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
    _defaults = [NSUserDefaults standardUserDefaults];
    
    _peoplePickerController = [[CNContactPickerViewController alloc] init];
    _peoplePickerController.delegate = self;
    _peopleList = [[NSMutableArray alloc] init];
    
    [_addContactBtn addTarget:self action:@selector(loadContacts) forControlEvents:UIControlEventTouchUpInside];
    [_philsBtn addTarget:self action:@selector(alertSquad) forControlEvents:UIControlEventTouchUpInside];
    NSDictionary *dict = [_defaults dictionaryRepresentation];
    
    if (dict != nil){
        for(NSString *key in dict){
            NSLog(key);
            if([[key substringToIndex:2]  isEqual: @"++"]){
                [_peopleList addObject:[[Person alloc] initWithParams:[key substringFromIndex:2] phoneNumber:[dict valueForKey:key]]];
            }
        }
        [_tableView reloadData];
    }
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
    
    NSString *completeName = [NSString stringWithFormat:@"%@ %@", [contact givenName], [contact familyName]];
    CNLabeledValue<CNPhoneNumber *> *firstPhoneNum = [contact phoneNumbers][0];
    CNPhoneNumber *phoneNum = [firstPhoneNum value];
    if ([[phoneNum stringValue] length] == 0){
        NSLog(@"BAD");
    }
    Person *person = [[Person alloc] initWithParams:completeName phoneNumber:[phoneNum stringValue]];
    [_peopleList addObject:person];
    [_defaults setObject:[person phoneNumber] forKey:[NSString stringWithFormat:@"++%@", [person fullName]]];
    
    [_defaults synchronize];
    [_tableView reloadData];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *tableId = @"TableCellId";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:tableId];
    if (cell == nil){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:tableId];
    }
    cell.textLabel.text = [[_peopleList objectAtIndex:indexPath.row] fullName];
    return cell;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [_peopleList count];
}

-(void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result{
    [self dismissViewControllerAnimated:YES completion:nil];
    [ToastView showToast:self.view withText:@"Squad alerted." withDuration:0.8f];
    
}

-(void)alertSquad{
    if(![MFMessageComposeViewController canSendText]){
        [ToastView showToast:self.view withText:@"You cannot send texts!" withDuration:1.0f];
        return;
    }
    NSMutableArray *recipients = [[NSMutableArray alloc] init];
    for (int i = 0; i < [_peopleList count]; i++){
        [recipients addObject:[_peopleList[i] phoneNumber]];
    }
    NSString *message = @"Phil's Tonight?";
    MFMessageComposeViewController *viewController = [[MFMessageComposeViewController alloc] init];
    viewController.messageComposeDelegate = self;
    [viewController setRecipients:recipients];
    [viewController setBody:message];
    
    [self presentViewController:viewController animated:YES completion:nil];
}

@end
