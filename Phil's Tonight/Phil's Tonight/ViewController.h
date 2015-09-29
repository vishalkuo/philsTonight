//
//  ViewController.h
//  Phil's Tonight
//
//  Created by Vishal Kuo on 2015-09-23.
//  Copyright © 2015 VishalKuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@import ContactsUI;

@interface ViewController : UIViewController <CNContactViewControllerDelegate, CNContactPickerDelegate, UITableViewDataSource, UITableViewDelegate>

/**
 * UI ELEMENTS
 **/
@property(weak, nonatomic)IBOutlet UIButton *addContactBtn;
@property(weak, nonatomic)IBOutlet UITableView *tableView;
/**
 * MODEL ELEMENTS
 **/
@property (strong, nonatomic)CNContactPickerViewController *peoplePickerController;
@property(strong, nonatomic)NSMutableArray *peopleList;
@property(strong, nonatomic)NSArray *testArray;

-(void)loadContacts;

@end

