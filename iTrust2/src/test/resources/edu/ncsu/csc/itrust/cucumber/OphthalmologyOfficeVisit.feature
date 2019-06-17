Feature: Document ophthalmology office visits
	Ophthalmologist HCPs should be able to document ophthalmology office visits
	And Ophthalmologist HCPs and patients should be able to view ophthalmology office visits
	So that patients eye appointment history can be tracked

Scenario Outline: HCP documents an ophthalmology surgery office visit
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
    When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, surgery type <surgeryType>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is documented successfully
	And The log is updated stating that the surgery was documented

Examples:
	| date			| time 		| patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 	| axisOS 	| surgeryType 		| notes 									|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 20				| 30				| 2.5		| -2.5		| 2.0			| -2.0			| 20		| 30		|  CATARACT     	| This is a test of the edit functionality	|

Scenario Outline: HCP incorrectly documents an ophthalmology surgery office visit
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
	When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, surgery type <surgeryType>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is not submitted

Examples:
	| date			| time 	    | patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 	| axisOS 	| surgeryType 			| notes 				|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| -40			    | 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| -30			    | 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 10/40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 10/30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 2000/40			| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 3000			    | 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| bobby				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| bobby				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| sphered	| -1.5		| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| numbre	| 1.0			| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| cylindered	| -1.0			| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| square		| 45		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| -720		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| -600		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 720		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 600		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| angle		| 90		| CATARACT      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| OPHTHALMOLOGY_SURGERY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| angle		| CATARACT      		| Test of invalid visit	|


Scenario Outline: HCP documents an ophthalmology office visit
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
    When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, and diagnoses <diagnosis>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is documented successfully
	And The log is updated stating that the oph office visit was documented

Examples:
	| date			| time 		| patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 	| axisOS 	| diagnosis 		| notes 									|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 20				| 30				| 2.5		| -2.5		| 2.0			| -2.0			| 20		| 30		|  cataracts     	| This is a test of the edit functionality	|



Scenario Outline: HCP documents an ophthalmology office visit with Basic Health Metrics
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
    When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the basic health metrics with height <height>, weight <weight>, systolic <systolic>, diastolic <diastolic>, HDL <HDL>, LDL <LDL>, Triglycerides <tri>, patient smoking status <patientSmoking>, and household smoking status <householdSmokingStatus>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, and diagnoses <diagnosis>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is documented successfully
	And The log is updated stating that the oph office visit was documented

Examples:
	| date			| time 		| patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 	| axisOS 	| diagnosis 		|  height  |  weight  |  systolic  |  diastolic|  HDL  |  LDL  |  tri  | patientSmoking  | householdSmokingStatus  |  notes 									|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 20				| 30				| 2.5		| -2.5		| 2.0			| -2.0			| 20		| 30		|  cataracts     	|    160   |    160   |     50     |     50    |  60   |  105  |  550  |      NEVER      |        NONSMOKING       |This is a test of the edit functionality	|



Scenario Outline: HCP documents an invalid ophthalmology office visit
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
    When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, and diagnoses <diagnosis>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is not submitted
	
Examples:
    | date			| time 	    | patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 	| axisOS 	| diagnosis 			| notes 				|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| -40			    | 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| -30			    | 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 10/40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 10/30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 2000/40			| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 3000			    | 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| bobby				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| bobby				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| sphered	| -1.5		| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| numbre	| 1.0			| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| cylindered	| -1.0			| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| square		| 45		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| -720		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| -600		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 720		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| 600		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| angle		| 90		| cataracts      		| Test of invalid visit	|
	| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 40				| 30				| 1.5		| -1.5		| 1.0			| -1.0			| 45		| angle		| cataracts      		| Test of invalid visit	|

	
Scenario Outline: HCP edits an ophthalmology office visit
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	Then The HCP logs in and navigates to the Document Office Visit page
    When The HCP enters the date <date>, time <time>, patient <patient>, type of visit <type>, hospital <hospital>
	And The HCP enters the basic health metrics with height <height>, weight <weight>, systolic <systolic>, diastolic <diastolic>, HDL <HDL>, LDL <LDL>, Triglycerides <tri>, patient smoking status <patientSmoking>, and household smoking status <householdSmokingStatus>
	And The HCP enters the eye metrics with left eye visual acuity <visualAcuityOS>, right eye visual acuity <visualAcuityOD>, left eye sphere <sphereOS>, right eye sphere <sphereOD>, left eye cylinder <cylinderOS>, right eye cylinder <cylinderOD>, left eye axis <axisOD>, right eye axis <axisOS>, and diagnoses <diagnosis>
	And The HCP enters notes <notes>
	And The HCP submits the ophthalmology office visit
	Then The ophthalmology office visit is documented successfully
	Then The HCP logs in and navigates to the Edit Office Visit page
	When The HCP selects the existing office visit
	And The HCP modifies the date to be <newdate>, height <newheight>, and the left eye visual acuity <newVisualAcuityOS>
	And The HCP saves the office visit
	Then The ophthalmology office visit is updated successfully

Examples:
	| newdate   	| newheight | newVisualAcuityOS	| date			| time 		| patient 		| type 					| hospital 			| visualAcuityOS 	| visualAcuityOD	| sphereOS	| sphereOD	| cylinderOS 	| cylinderOD 	| axisOD 		| axisOS 		| diagnosis 		|  height  	|  weight  	|  systolic 	|  diastolic	|  HDL  	|  LDL  	|  tri  	| patientSmoking 	| householdSmokingStatus 	| notes 									|
	| 10/29/2018	| 150    	| 31				| 10/10/2018	| 10:00 am	| bobby			| GENERAL_OPHTHALMOLOGY	| General Hospital 	| 20				| 30				| 2.5		| -2.5		| 2.0			| -2.0			| 20			| 30			|  cataracts    	|    160   	|    160  	|     50    	|     50    	|  60  		|  105  	|  550  	|      NEVER     	|        NONSMOKING      	|This is a test of the edit functionality	|

Scenario Outline: HCP incorrectly edits an ophthalmology surgery office visit 
	Given There exists a patient in the system
	And There exists an ophthalmologist HCP in the system
	And there are office visits of all types
	Then The HCP logs in and navigates to the Edit Office Visit page
	When The HCP selects the existing office visit
	And The HCP modifies the date to be <date>, height <height>, and the left eye visual acuity <visualAcuityOS>
	And The HCP saves the office visit
	Then The ophthalmology office visit is not updated successfully

Examples:
	| date			| height 		| visualAcuityOS	|
	| 10/29/2018	| 150    		| 4000000       	|
	| 10/29/2018	| 15000000000   | 40     			|
