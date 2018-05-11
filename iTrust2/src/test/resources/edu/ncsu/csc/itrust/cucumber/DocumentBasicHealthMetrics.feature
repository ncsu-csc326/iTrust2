#Author jshore and mrgray4
Feature: Document office visit with basic health metrics
	As an iTrust2 HCP
	I want to document an office visit with basic health metrics
	So that a record exits of a Patient visiting the doctor

Scenario Outline: Document an Office Visit with Basic Health Metrics for Infant
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for an infant with date: <date>, weight: <weight>, length: <length>, head circumference: <head>, household smoking status: <smoking>, and note: <note>
Then The office visit is documented successfully
And The basic health metrics for the infant are correct

Examples:
	| first    | last    | birthday   | date       | weight | length | head | smoking | note                                                                                      |
	| Brynn    | McClain | 05/01/2017 | 10/01/2017 | 16.5   | 22.3   | 16.1 | 1       | Brynn can start eating rice cereal mixed with breast milk or formula once a day.          |
	| Caldwell | Hudson  | 09/29/2015 | 10/28/2017 | 30.2   | 34.7   | 19.4 | 3       | Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics. |
	| Caldwell | Hudson  | 09/29/2015 | 10/28/2017 | 15.2   | 30     | 19   | 2       | Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics. |
	| Brynn    | McClain | 05/01/2017 | 10/28/2017 | 8.2    | 24.7   | 15.4 | 1       | Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics. |
	
Scenario Outline: Document an Office Visit with invalid Basic Health Metrics
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for an infant with date: <date>, weight: <weight>, length: <length>, head circumference: <head>, household smoking status: <smoking>, and note: <note>
Then The office visit is not documented

Examples:
	| first    | last    | birthday   | date       | weight  | length | head   | smoking | note                                                                                      |
	| Brynn    | McClain | 05/01/2017 | 10/01/2017 | 16.55   | 22.3   | 16.1   | 1       | Brynn can start eating rice cereal mixed with breast milk or formula once a day.          |
	| Caldwell | Hudson  | 09/29/2015 | 10/28/2017 | 30.2    | -1     | 19.4   | 3       | Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics. |
	| Caldwell | Hudson  | 09/29/2015 | 10/28/2017 | 30.2    | 22.3   | 1999.4 | 3       | Healthy                                                                                   |
	| Brynn    | McClain | 05/01/2017 | 10/01/2017 | 16.55   | 22.3   | 16.1   | 4       | Healthy                                                                                   |

#12yrs and over valid
Scenario Outline: Document an Office Visit with Basic Health Metrics for people 12 and over
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for people 12 and over with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, patient smoking status: <patientSmoking>, HDL cholesterol: <HDL>, LDL cholesterol: <LDL>, triglycerides: <triglycerides>, and note: <note>
Then The office visit is documented successfully
And The basic health metrics for the adult are correct

Examples:
	| first    | last    | birthday   | date       | weight | height | sys | dia | houseSmoking | patientSmoking | HDL | LDL | triglycerides | note                                                                            |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3  | 62.3   | 110 | 175 | 1            | 3              | 65  | 102 | 147           | Patient is healthy                                                              |
	| Thane    | Ross    | 01/03/1993 | 10/25/2017 | 210.1  | 73.1   | 160 | 100 | 1            | 4              | 37  | 141 | 162           | Thane should consider modifying diet and exercise to avoid future heart disease |
	
#12yrs and over invalid
Scenario Outline: Document an Office Visit with Basic Health Metrics for people 12 and over
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for people 12 and over with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, patient smoking status: <patientSmoking>, HDL cholesterol: <HDL>, LDL cholesterol: <LDL>, triglycerides: <triglycerides>, and note: <note>
Then The office visit is not documented

Examples:
	| first    | last    | birthday   | date       | weight  | height  | sys  | dia  | houseSmoking | patientSmoking | HDL | LDL | triglycerides | note                |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | -124.3  | 62.3    | 110  | 175  | 1            | 3              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | -62.3   | 110  | 175  | 1            | 3              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 1110 | 175  | 1            | 3              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 1750 | 1            | 3              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 175  | 0            | 3              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 175  | 1            | 0              | 65  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 175  | 1            | 3              | -5  | 102 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 175  | 1            | 3              | 65  | 999 | 147           | Patient is healthy  |
	| Daria    | Griffin | 10/25/1997 | 10/25/2017 | 124.3   | 62.3    | 110  | 175  | 1            | 3              | 65  | 102 | 999           | Patient is healthy  |
	
#3yrs to 12yrs valid
Scenario Outline: Document an Office Visit with Basic Health Metrics for Child
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for patients of age 3 to 12 with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, and note: <note>
Then The office visit is documented successfully
And The basic health metrics for the child are correct

Examples:
	| first    | last    | birthday   | date       | weight | height | sys | dia | houseSmoking | note                                                                            |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Timothy  | Dorsett | 02/29/2012 | 11/16/2017 | 46.9   | 47.7   | 110 | 75  | 1            | Timothy is healthy.                                                             |
	| Kat      | Shuler  | 04/06/2008 | 04/06/2017 | 87.2   | 55.4   | 90  | 70  | 3            | Patient is healty at 9 year old check up                                        |
	| Doris    | Belmont | 12/31/2000 | 12/30/2012 | 100.3  | 57.3   | 105 | 88  | 1            | Patient is healty at 9 year old check up                                        |
	
#3yrs to 12yrs invalid
Scenario Outline: Document an Office Visit with Basic Health Metrics for Child
Given The required facilities exist
And A patient exists with name: <first> <last> and date of birth: <birthday>
When I log in to iTrust2 as a HCP
When I navigate to the Document Office Visit page
When I fill in information on the office visit for patients of age 3 to 12 with date: <date>, weight: <weight>, height: <height>, systolic blood pressure: <sys>, diastolic blood pressure: <dia>, household smoking status: <houseSmoking>, and note: <note>
Then The office visit is not documented

Examples:
	| first    | last    | birthday   | date       | weight | height | sys | dia | houseSmoking | note                                                                            |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | -37.9  | 42.9   | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | -42.9  | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | -95 | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | 95  | -65 | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | 95  | 65  | -2           | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | 95  | 65  | 4            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.9   | 9544| 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42.95  | 95  | 6555| 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.91  | 42.9   | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 12345  | 42.9   | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 1234   | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
	| Fulton   | Gray    | 10/10/2012 | 10/13/2017 | 37.9   | 42..9  | 95  | 65  | 2            | Fulton has all required immunizations to start kindergarten next year.          |
