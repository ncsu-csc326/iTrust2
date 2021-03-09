#author ejwoodho

Feature: Calculating R-Naught Value 
    As a virologist
    I want to find the page for Patient Statistics 
    So that I can view the R-Naught value of the disease within the system 

Scenario Outline: No Passenger Data 
    Given there is no passenger data in the system 
    When the user navigates to Patient Statistics 
    Then the R-Naught tab displays the missing data <message> 

Examples: 
   | message                                                                              | 
   | Please upload patient data before attempting to load statistics: Upload Patient Data |

Scenario Outline: Passenger Data in System 
    Given there is some passenger data from <file> in the system 
    When the user navigates to Patient Statistics 
    Then R-Naught <value> is displayed and its corresponding <description>

Examples: 
   | file           | value | description |
   | rnaught_greaterthan_one.csv   | 2.00   | Spreading   |
   | rnaught_equalto_one.csv | 1.00   | Stable      |
   | rnaught_invalid.csv | Error: Not Enough Data | Not Available |
   | abcpassengers.csv | 1.00 | Stable |
   

