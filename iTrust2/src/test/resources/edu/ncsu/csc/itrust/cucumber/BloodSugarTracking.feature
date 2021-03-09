Feature: Blood Sugar Tracking
  As a patient
  I want to log and view my blood sugar for each day for different times during the day
  So that I can keep track of my blood sugar

  Scenario Outline: Patient creates a blood sugar entry
    Given I log in to iTrust2 as a Patient
    And I click the link to enter a new blood sugar diary entry
    When I enter <fastingSugar>, <firstMealSugar>, <secondMealSugar>, and <thirdMealSugar>
    And I submit the form successfully

    Examples: 
      | fastingSugar | firstMealSugar | secondMealSugar | thirdMealSugar |
      |           80 |            120 |             130 |            100 |

  Scenario Outline: Patient edits the log created for that day
    Given I log in to iTrust2 as a Patient
    And I click the link to enter a new blood sugar diary entry
    And The previous values are displayed: <fastingSugar>, <firstMealSugar>, <secondMealSugar>, and <thirdMealSugar>
    When I enter <newFastingSugar>, <newFirstMealSugar>, <newSecondMealSugar>, and <newThirdMealSugar>
    And I submit the form successfully

    Examples: 
      | fastingSugar | firstMealSugar | secondMealSugar | thirdMealSugar | newFastingSugar | newFirstMealSugar | newSecondMealSugar | newThirdMealSugar |
      |           80 |            120 |             130 |            100 |              10 |                10 |                 10 |                10 |
      |           10 |             10 |              10 |             10 |              25 |                 0 |                  0 |                 0 |
      |           25 |              0 |               0 |              0 |               0 |               200 |                200 |                 0 |
      |            0 |            200 |             200 |              0 |             100 |               100 |                100 |               100 |

  Scenario Outline: Patient enters an invalid log
    Given I log in to iTrust2 as a Patient
    And I click the link to enter a new blood sugar diary entry
    And The previous values are displayed: <fastingSugar>, <firstMealSugar>, <secondMealSugar>, and <thirdMealSugar>
    When I enter invalid data: <newFastingSugar>, <newFirstMealSugar>, <newSecondMealSugar>, and <newThirdMealSugar>
    Then I  attempt to submit the form, but the submit button is disabled

    Examples: 
      | fastingSugar | firstMealSugar | secondMealSugar | thirdMealSugar | newFastingSugar | newFirstMealSugar | newSecondMealSugar | newThirdMealSugar |
      |          100 |            100 |             100 |            100 |             -10 |               100 |                100 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               -10 |                100 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               100 |                -10 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               100 |                100 |               -10 |
      |          100 |            100 |             100 |            100 |             1.5 |               100 |                100 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               1.5 |                100 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               100 |                1.5 |               100 |
      |          100 |            100 |             100 |            100 |             100 |               100 |                100 |               1.5 |
