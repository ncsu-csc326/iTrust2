#Author tvbarnet
Feature: Change or Reset password
	As an iTrust2 user
	I want to change my password
	I want to forget my password and reset it without logging in

Scenario Outline: Change password correctly
Given I can log in to iTrust as <username> with password <password>
When I navigate to the change password page
When I fill out the form with current password <password> and new password <newPassword>
Then My password is updated sucessfully
Examples:
	|username   |password|newPassword|
	|pwtestuser1|123456  |654321     |	

Scenario Outline: Change password incorrectly
Given I can log in to iTrust as <username> with password <password>
When I navigate to the change password page
When I fill out the form with current password <currentPassword>, new password <newPassword>, and re-entry <newPassword2>
Then My password is not updated because <message>
Examples:
	|username   |password|currentPassword|newPassword|newPassword2|message|
	|pwtestuser2|123456  |123456         |654321     |different   |Confirmed new password must match     |
	|pwtestuser2|123456  |wrong          |654321     |654321      |Incorrect password                    |


