#author ejwoodho

Feature: Patient Statistics
	As a virologist
	I want to find the page for Patient Statistics
	So that I can view disease spread within the system

Scenario Outline: No Passenger Data
	Given there is no passenger data in the system
	When the user navigates to Patient Statistics
	Then the missing data <message> appears

Examples:
   | message 																			  | 
   | Please upload passenger data before attempting to load statistics: Upload Patient Data |
   
Scenario Outline: Passenger Data in System
	Given there is some passenger data from <file> in the system
	When the user navigates to Patient Statistics
	Then the total infected patients are plotted
	And the new infected patients are plotted
	And the patients by severity are plotted

Examples:
   | file              		  |
   | duplicate-data.csv 	  |
   | passenger-data-short.csv |
   | passenger-data.csv 	  |
   
