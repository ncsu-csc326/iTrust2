#Author tvbarnet
Feature: Locking and Banning Users and IPs
	I want the system to lockout users or IPs after too many invalid login attempts

Scenario Outline: User locked out after 3 failed attempts
Given The user <username> with password <correct> and the current machine have no failed login attempts
When I try to login as <username> with password <password>
Then My credentials are incorrect
When I try to login as <username> with password <password>
Then My credentials are incorrect
When I try to login as <username> with password <password>
Then My account is locked for one hour
When I wait one hour
And I try to login as <username> with password <correct>
Then I login successfully
	Examples:
	|username   |password|correct|
	|lockoutUser|111111  |123456 |

#Need to correctly login as different user occasionally to prevent IP lockout
Scenario Outline: User banned after 3 lockouts
Given The user <username> with password <correct> and the current machine have no failed login attempts
When I try to login <n> times as <username> with password <password>
Then My account is locked for one hour
When I wait one hour
And I try to login as <username2> with password <password2>
Then I login successfully
When I logout
And I try to login <n> times as <username> with password <password>
Then My account is locked for one hour
When I wait one hour
And I try to login as <username2> with password <password2>
Then I login successfully
When I logout
And I try to login <n> times as <username> with password <password>
Then My account is banned
	Examples:
	|username|password|correct|username2|password2|n|
	|lockoutUser|111111|123456|hcp|123456|3|
	
Scenario Outline: IP locked out after 6 attempts
Given The user <username> with password <correct> and the current machine have no failed login attempts
Given The user <username2> with password <correct2> and the current machine have no failed login attempts
When I try to login <n> times as <username> with password <password>
Then My account is locked for one hour
When I try to login <n> times as <username2> with password <password>
Then My IP is locked for one hour
When I wait one hour
And I try to login as <username> with password <correct>
Then I login successfully
	Examples:
	|username|password|correct|username2|password2|correct2|n|
	|lockoutUser|111111|123456|lockoutUser2|111111|123456|3|
	
Scenario Outline: IP banned after 3 lockouts
When I try to login <n> times as <username> with password <password>
Then My IP is locked for one hour
When I wait one hour
When I try to login <n> times as <username> with password <password>
Then My IP is locked for one hour
When I wait one hour
When I try to login <n> times as <username> with password <password>
Then My IP is banned
	Examples:
	|username|password|n|
	|fakeName|111111|6|

