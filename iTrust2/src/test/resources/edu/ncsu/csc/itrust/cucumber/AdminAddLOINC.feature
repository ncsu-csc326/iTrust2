Feature: Admin add LOINC
  As the admin
  I want to be able to add a LOINC
  and specify the format of the result for that LOINC
  so that lab techs can enter specific results

  Scenario Outline: Add a LOINC with no results
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and None.
    And I click Add code
    And the code is added

    Examples: 
      | code     | common name | component | property    |
      | "1234-5" | "CName"     | "Nothing" | "Something" |

  Scenario Outline: Add a Qualitative LOINC
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and QUALITATIVE.
    Then I add a new result value with name: <name1> and ICD: <icd1>
    And I add a new result value with name: <name2> and ICD: <icd2>
    And I click Add code
    And the code is added

    Examples: 
      | code     | common name | component | property | name1   | icd1   | name2 | icd2    |
      | "5778-6" | "Color Ur"  | "Color"   | "Type"   | "Clear" | "None" | "Red" | "R31.9" |

  Scenario Outline: Add a Quantiative LOINC
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and QUANTITATIVE.
    Then I add a new range with min: <min1>, max: <max1>, and ICD: <icd1>
    Then I add a new range with min: <min2>, max: <max2>, and ICD: <icd2>
    Then I add a new range with min: <min3>, max: <max3>, and ICD: <icd3>
    And I click Add code
    And the code is added

    Examples: 
      | code      | common name                         | component                      | property | min1 | max1  | icd1   | min2  | max2  | icd2     | min3  | max3   | icd3    |
      | "20436-2" | "Glucose 2 Hr After Glucose, Blood" | "Glucose^2H post dose glucose" | "MCnc"   | "0"  | "139" | "None" | "140" | "199" | "R73.03" | "200" | "5000" | "E11.9" |

  Scenario Outline: Add a Quantiative LOINC with overlapping range
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and QUANTITATIVE.
    Then I add a new range with min: <min1>, max: <max1>, and ICD: <icd1>
    Then I add a new range with min: <min2>, max: <max2>, and ICD: <icd2>
    And I get an overlapping error message

    Examples: 
      | code      | common name                         | component                      | property | min1 | max1  | icd1   | min2  | max2  | icd2     |
      | "20436-2" | "Glucose 2 Hr After Glucose, Blood" | "Glucose^2H post dose glucose" | "MCnc"   | "0"  | "199" | "None" | "199" | "250" | "R73.03" |
      | "20436-2" | "Glucose 2 Hr After Glucose, Blood" | "Glucose^2H post dose glucose" | "MCnc"   | "0"  | "100" | "None" | "50"  | "75"  | "R73.03" |
      | "20436-2" | "Glucose 2 Hr After Glucose, Blood" | "Glucose^2H post dose glucose" | "MCnc"   | "0"  | "199" | "None" | "180" | "250" | "R73.03" |

  Scenario Outline: Add a Qualitative LOINC with duplicate name
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and QUALITATIVE.
    Then I add a new result value with name: <name> and ICD: <icd1>
    Then I add a new result value with name: <name> and ICD: <icd2> 
		And I get a duplicate name error

    Examples: 
      | code     | common name | component | property | name    | icd1   | icd2    |
      | "5778-6" | "Color Ur"  | "Color"   | "Type"   | "Clear" | "None" | "R31.9" |

  Scenario Outline: Delete a Quantiative LOINC Range
    Given the required ICD codes exist
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I add a LOINC with the values: <code>, <common name>, <component>, <property>, and QUANTITATIVE.
    Then I add a new range with min: <min1>, max: <max1>, and ICD: <icd1>
    Then I delete the code with ICD: <icd1> and the table is empty

    Examples: 
      | code      | common name                         | component                      | property | min1 | max1  | icd1     |
      | "20436-2" | "Glucose 2 Hr After Glucose, Blood" | "Glucose^2H post dose glucose" | "MCnc"   | "0"  | "139" | "R73.03" |

  Scenario Outline: Edit a LOINC
    Given there exists a quantitative LOINC
    And I login as the Admin
    When I navigate to the Add LOINC page
    And I select the LOINC and change the scale to qualitative
    Then I add a new result value with name: <name> and ICD: <icd1>
    And I select edit code
    Then code is edited

    Examples: 
      | name    | icd1    |
      | "name"  | "R31.9" |
      | "name2" | "None"  |
