#Author tadicke3
#Author nseamon
#Author dmbangol
Feature: Lab procedures
  As an HCP
  I want to add lab procedures to an office visit
  So that I can keep track of what lab procedures a patient needs

  Scenario: Add lab procedure to office visit
    Given I log in to iTrust2 as an HCP
    When I create a new Office Visit
    And I add a lab procedure to that visit
    Then I recieve a message that office visit details were changed successfully

  Scenario: View assigned lab procedures
    Given I log in to iTrust2 as a Lab Tech
    When I navigate to Assigned Procedures
    And I change my newest procedures status to In-Progress
    Then I recieve a message that lab procedure details were changed successfully

  Scenario: Add new lab procedure
    Given I log in to iTrust2 as an Admin
    When I navigate to Admin Procedures
    And I add a new lab procedure
    Then I recieve a message that the procedure list was changed successfully

  Scenario Outline: Fill out procedure with Oral Glucose Tolerance Test result
    Given I log in to iTrust2 as a LabTech
    And I have documented an Office Visit that includes an Oral Glucose Tolerance Test
    When I navigate to Assigned Procedures
    Then I can submit a <result>

    Examples: 
      | result |
      | "80"   |
      | "139"  |
      | "140"  |
      | "150"  |
      | "199"  |
      | "200"  |
      | "500"  |

  Scenario Outline: Fill out procedure with quantitative result, invalid range
    Given I log in to iTrust2 as a LabTech
    And I have documented an Office Visit that includes an Oral Glucose Tolerance Test
    When I navigate to Assigned Procedures
    And I submit result <invalid>
    Then I recieve an error message for the invalid result

    Examples: 
      | invalid   |
      | "abc"     |
      | "9999999" |
      | "-100"    |
