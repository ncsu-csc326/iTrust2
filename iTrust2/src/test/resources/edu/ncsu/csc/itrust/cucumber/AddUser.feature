#Author kpresle

Feature: Add a User
	As an Admin
	I want to add a new user
	So that someone new can use iTrust

Scenario: Add new user
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Add User page
When I fill in the values in the Add User form
Then The user is created successfully
And The new user can login

Scenario: Add new user
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Add User page
When I fill in the values in the Add User form with two valid roles
Then The user is created successfully
And The new user can login
And The new user has both roles

Scenario: Invalid Add User: Extra Roles
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Add User page
When I fill in invalid roles in the Add User form
Then The user is not created successfully

Scenario: Invalid Add User: Missing Roles
Given An Admin exists in iTrust2
When I log in as admin
When I navigate to the Add User page
When I fill in invalid roles in the Add User form
Then The user is not created successfully
