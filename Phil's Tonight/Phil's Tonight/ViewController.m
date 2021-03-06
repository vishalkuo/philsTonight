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
#import "CustomTableViewCell.h"

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _defaults = [NSUserDefaults standardUserDefaults];
    
    if ([_tableView respondsToSelector:@selector(setSeparatorInset:)]) {  // Safety check for below
        _tableView.allowsMultipleSelectionDuringEditing = NO;
        [_tableView setSeparatorInset:UIEdgeInsetsZero];
    }

    
    _peoplePickerController = [[CNContactPickerViewController alloc] init];
    _peoplePickerController.delegate = self;
    _peopleList = [[NSMutableArray alloc] init];
    
    [_addContactBtn addTarget:self action:@selector(loadContacts) forControlEvents:UIControlEventTouchUpInside];
    [_philsBtn addTarget:self action:@selector(alertSquad) forControlEvents:UIControlEventTouchUpInside];
    NSDictionary *dict = [_defaults dictionaryRepresentation];
    
    if (dict != nil){
        for(NSString *key in dict){
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
                [ToastView showToast:self.view withText:@"No Contacts Permission!" withDuration:1.0f];
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
    [ToastView showToast:self.view withText:@"Denied from Squad" withDuration:1.0f];
}

-(void)contactPicker:(CNContactPickerViewController *)picker didSelectContact:(CNContact *)contact{
    
    NSString *completeName = [NSString stringWithFormat:@"%@ %@", [contact givenName], [contact familyName]];
    CNLabeledValue<CNPhoneNumber *> *firstPhoneNum = [contact phoneNumbers][0];
    CNPhoneNumber *phoneNum = [firstPhoneNum value];
    if ([[phoneNum stringValue] length] == 0){
        [ToastView showToast:self.view withText:@"No phone number!" withDuration:1.0f];
        return;
    }
    Person *person = [[Person alloc] initWithParams:completeName phoneNumber:[phoneNum stringValue]];
    [_peopleList addObject:person];
    [_defaults setObject:[person phoneNumber] forKey:[NSString stringWithFormat:@"++%@", [person fullName]]];
    
    [_defaults synchronize];
    [_tableView reloadData];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *tableId = @"TableCell";
    CustomTableViewCell *cell = (CustomTableViewCell *)[tableView dequeueReusableCellWithIdentifier:tableId];
    if (cell == nil){
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:tableId owner:self options:nil];
        cell = [nib objectAtIndex:0];
    }
    cell.nameLabel.text = [[_peopleList objectAtIndex:indexPath.row] fullName];
    cell.separatorInset = UIEdgeInsetsZero;
    return cell;
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [_peopleList count];
}

-(void)messageComposeViewController:(MFMessageComposeViewController *)controller didFinishWithResult:(MessageComposeResult)result{
    [self dismissViewControllerAnimated:YES completion:nil];
    if (result == 1){
        [ToastView showToast:self.view withText:@"Squad alerted." withDuration:0.8f];
    }
    
    
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

-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        NSString *delString = [NSString stringWithFormat:@"++%@", [[_peopleList objectAtIndex:indexPath.row] fullName]];
        [_peopleList removeObjectAtIndex:indexPath.row];
        [_defaults removeObjectForKey:delString];
        [_defaults synchronize];
        [_tableView reloadData];
        
        [ToastView showToast:self.view withText:@"Removed from Squad" withDuration:1.0f];
    }
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    return UIStatusBarStyleLightContent;
}

@end
