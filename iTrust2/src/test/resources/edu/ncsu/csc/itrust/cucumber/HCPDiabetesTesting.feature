Feature: HCP Diabetes Testing
  As an HCP
  I want to add a diabetes lab procedure to an office visit
  So that when a lab tech carries out the procedure I can confirm the diagnosis

  Scenario Outline: Add Oral Glucose Tolerance Test to office visit
    Given I am logged in as a general HCP
    And I have documented an Office Visit that includes an Oral Glucose Tolerance Test
    Then a lab tech carries out the procedure and documents the results of <result>
    When I navigate to the editOfficeVisit page and select the correct office visit
    Then I can see and confirm the suggested diagnosis of <diagnosis>

    Examples: 
      | result | diagnosis |
      |     80 | ""        |
      |    139 | ""        |
      |    140 | "R73.03"  |
      |    150 | "R73.03"  |
      |    199 | "R73.03"  |
      |    200 | "E11.9"   |
      |    500 | "E11.9"   |
