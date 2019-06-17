package edu.ncsu.csc.itrust2.models.enums;

/**
 * A TransactionType represents an event that took place in the system and that
 * is to be logged. This is used to provide a code that can easily be saved in
 * the database and a longer description of it that can be displayed to the
 * user. Also stores whether the event is patient-visible.
 *
 * As new functionality is added to iTrust2, add in new TransactionType codes
 * representing the event.
 *
 * @author Kai Presler-Marshall
 * @author Jack MacDonald
 *
 */
public enum TransactionType {

    /**
     * Failed login
     */
    LOGIN_FAILURE ( 1, "Failed login", true ),
    /**
     * Successful login
     */
    LOGIN_SUCCESS ( 2, "Successful login", true ),
    /**
     * User logged out
     */
    LOGOUT ( 3, "Logged Out", true ),
    /**
     * User locked out of system (temporary)
     */
    USER_LOCKOUT ( 4, "User Locked Out", true ),
    /**
     * IP locked out of system (temporary)
     */
    IP_LOCKOUT ( 5, "IP Locked Out", true ),
    /**
     * User banned
     */
    USER_BANNED ( 6, "User Banned", true ),
    /**
     * IP Banned
     */
    IP_BANNED ( 7, "IP Banned", true ),
    /**
     * New User created
     */
    CREATE_USER ( 100, "New user created", true ),
    /**
     * User was viewed
     */
    VIEW_USER ( 101, "Single user viewed", false ),
    /**
     * Multiple users viewed
     */
    VIEW_USERS ( 102, "List of users viewed", false ),
    /**
     * User deleted
     */
    DELETE_USER ( 103, "User deleted", false ),
    /**
     * User changed/updated
     */
    UPDATE_USER ( 104, "User updated", false ),

    /**
     * User viewed their demographics
     */
    VIEW_DEMOGRAPHICS ( 400, "Demographics viewed by user", true ),
    /**
     * User updated their demographics
     */
    EDIT_DEMOGRAPHICS ( 410, "Demographics edited by user", true ),

    /**
     * User creates their demographics
     */
    CREATE_DEMOGRAPHICS ( 411, "Demographics created by user", true ),

    /**
     * Hospital created
     */
    CREATE_HOSPITAL ( 500, "New hospital created", false ),
    /**
     * Hospital viewed by user
     */
    VIEW_HOSPITAL ( 501, "Hospital viewed", false ),
    /**
     * Hospital modified by user
     */
    EDIT_HOSPITAL ( 502, "Hospital edited", false ),
    /**
     * Hospital deleted
     */
    DELETE_HOSPITAL ( 503, "Hospital deleted", false ),

    /**
     * Upcoming appointment viewed by Patient or HCP
     */
    VIEW_SCHEDULED_APPOINTMENT( 611, "Upcoming general checkup viewed", true ),
    /**
     * AppointmentRequest submitted by patient
     */
    APPOINTMENT_REQUEST_SUBMITTED( 640, "General checkup requested by patient", true ),
    /**
     * AppointmentRequest viewed
     */
    APPOINTMENT_REQUEST_VIEWED( 641, "Appointment request(s) viewed", true ),
    /**
     * AppointmentRequest canceled/deleted by patient
     */
    APPOINTMENT_REQUEST_DELETED( 642, "General checkup request deleted by patient", true ),
    /**
     * AppointmentRequest approved by HCP
     */
    APPOINTMENT_REQUEST_APPROVED( 650, "General checkup request approved by HCP", true ),
    /**
     * AppointmentRequest denied by HCP
     */
    APPOINTMENT_REQUEST_DENIED( 651, "General checkup request denied by HCP", true ),
    /**
     * AppointmentRequest otherwise updated
     */
    APPOINTMENT_REQUEST_UPDATED( 652, "General checkup request was updated", true ),

    /**
     * Create basic health metrics
     */
    GENERAL_CHECKUP_CREATE( 800, "Create office visit for patient", true ),
    /**
     * HCP views basic health metrics
     */
    GENERAL_CHECKUP_HCP_VIEW( 801, "View office visit by HCP", true ),
    /**
     * HCP edits basic health metrics
     */
    GENERAL_CHECKUP_EDIT( 802, "HCP edits basic health metrics", true ),
    /**
     * Patient views basic health metrics for an office visit
     */
    GENERAL_CHECKUP_PATIENT_VIEW( 810, "View office visit by Patient", true ),
    /**
     * Patient deleted all office visits
     */
    DELETE_ALL_OFFICE_VISITS( 899, "Patient deleted all office visits", false),
    /**
     * Patient view all office visits
     */
    VIEW_ALL_OFFICE_VISITS( 898, "Patient viewed all office visits", false),

    /**
     * Office visit is deleted
     */
    GENERAL_CHECKUP_DELETE( 811, "Office visit deleted", true ),

    /**
     * Admin adds an ICD-10 code
     */
    ICD_CREATE ( 1001, "Admin adds ICD-10 code", false ),
    /**
     * Admin deletes an ICD10 code
     */
    ICD_DELETE ( 1002, "Admin deletes ICD-10 code", false ),
    /**
     * Admin edits ICD-10 code
     */
    ICD_EDIT ( 1003, "Admin edits ICD-10 code", false ),
    /**
     * Admin views ICD-10 code
     */
    ICD_VIEW ( 1004, "Administrator views ICD-10 codes", false ),
    /**
     * Admin views all ICD-10 code
     */
    ICD_VIEW_ALL ( 1005, "Administrator views all ICD-10 codes", false ),
    /**
     * User gets diagnosis by id
     */
    DIAGNOSIS_VIEW_BY_ID ( 1006, "Diagnoses retrieved by id", true ),
    /**
     * User gets diagnoses for an office visit
     */
    DIAGNOSIS_VIEW_BY_OFFICE_VISIT ( 1007, "Diagnoses retrieved by office visit", true ),
    /**
     * Patient views diagnoses
     */
    DIAGNOSIS_PATIENT_VIEW_ALL ( 1008, "Patient views diagnoses", true ),
    /**
     * HCP creates diagnosis
     */
    DIAGNOSIS_CREATE ( 1009, "HCP creates a diagnosis within and office visit", true ),
    /**
     * HCP edits diagnosis
     */
    DIAGNOSIS_EDIT ( 1010, "HCP edits diagnosis", true ),
    /**
     * HCP deletes diagnosis
     */
    DIAGNOSIS_DELETE ( 1011, "HCP deletes diagnosis", true ),

    /**
     * Admin created a new drug
     */
    DRUG_CREATE ( 900, "Admin created a new drug", true ),
    /**
     * Admin edited an existing drug
     */
    DRUG_EDIT ( 901, "Admin edited an existing drug", true ),
    /**
     * Admin deleted an existing drug
     */
    DRUG_DELETE ( 902, "Admin deleted an existing drug", true ),
    /**
     * Admin views all drugs in the system
     */
    DRUG_VIEW ( 903, "Admin views all drugs in the system", true ),

    /**
     * HCP created a new prescription
     */
    PRESCRIPTION_CREATE ( 910, "HCP created a new prescription", true ),
    /**
     * HCP edited an existing prescription
     */
    PRESCRIPTION_EDIT ( 911, "HCP edited an existing prescription", true ),
    /**
     * HCP deleted an existing prescription
     */
    PRESCRIPTION_DELETE ( 912, "HCP deleted an existing prescription", true ),
    /**
     * User viewed an existing prescription
     */
    PRESCRIPTION_VIEW ( 913, "User viewed an existing prescription", true ),
    /**
     * Patient viewed their list of prescriptions
     */
    PATIENT_PRESCRIPTION_VIEW ( 914, "Patient viewed their list of prescriptions", true ),
    /**
     * Attempt to update password fails
     */
    PASSWORD_UPDATE_FAILURE ( 1100, "Failed password update", true ),

    /**
     * Attempt to update password is successful
     */
    PASSWORD_UPDATE_SUCCESS ( 1101, "Successful password update", true ),
    /**
     * Reset request email sent successfully
     */
    PASSWORD_RESET_EMAIL_SENT ( 1102, "Reset request email sent", true ),

    /**
     * HCP views patient's demographics
     */
    PATIENT_DEMOGRAPHICS_VIEW ( 1200, "HCP views patient's demographics", true ),
    /**
     * HCP edits patient's demographics
     */
    PATIENT_DEMOGRAPHICS_EDIT ( 1201, "HCP edits patient's demographics", true ),

    /**
     * User views their log entries
     */
    VIEW_USER_LOG ( 1301, "Log events viewed", true ),
    /**
     * An email is sent to the user on password change
     */
    CREATE_PW_CHANGE_EMAIL ( 1401, "PW Change Email notification sent", true ),
    /**
     * An email is sent to the user on appointment request change
     */
    CREATE_APPOINTMENT_REQUEST_EMAIL ( 1402, "AppointmentRequest Email notification sent", true ),
    /**
     * An email is sent to the user on lockout
     */
    CREATE_LOCKOUT_EMAIL ( 1403, "Account Lockout Email notification sent", true ),
    /**
     * An email would be sent but email address is missing.
     */
    CREATE_MISSING_EMAIL_LOG ( 1404, "Email notification could not be sent due to missing email address", true ),
    /**
     * HCP pulls up a patient's emergency record.
     */
    HCP_VIEW_ER ( 1501, "HCP Viewed An Emergency Health Record", true ),
    /**
     * Emergency Responder pulls up a patient's emergency record.
     */
    ER_VIEW_ER ( 1502, "ER Viewed An Emergency Health Record", true ),
    /**
     * Declare a user as a personal representative.
     */
    DECLARE_PR ( 1601, "Declare a personal representative", true ),
    /**
     * HCP declares a user as a personal representative.
     */
    HCP_DECLARE_PR ( 1602, "HCP declares a personal representative", true ),
    /**
     * Remove a user as a personal representative.
     */
    REMOVE_PR ( 1603, "Undeclare a personal representative", true ),
    /**
     * Remove a user as a personal representative.
     */
    REMOVE_SELF_AS_PR ( 1604, "Undeclare self as personal representative", true ),
    /**
     * Admin creates new LOINC code.
     */
    LOINC_CREATE ( 1701, "Administrator adds LOINC code", false ),
    /**
     * Admin deletes existing LOINC code.
     */
    LOINC_DELETE ( 1702, "Administrator deletes LOINC code", false ),
    /**
     * Admin updates existing LOINC code.
     */
    LOINC_EDIT ( 1703, "Administrator edits LOINC code", false ),
    /**
     * LabTech Views Procedures.
     */
    LABTECH_VIEW_PROCS ( 1704, "LabTech Views Procedures", false ),
    /**
     * LabTech Edits Procedure.
     */
    LABTECH_EDIT_PROC ( 1705, "LabTech Edits Procedure", false ),
    /**
     * LabTech Reassigns Procedure.
     */
    LABTECH_REASSIGN_PROC ( 1706, "LabTech Reassigns Procedure", false ),
    /**
     * HCP creates lab procedure.
     */
    HCP_CREATE_PROC ( 1707, "HCP creates Lab Procedure", false ),
    /**
     * HCP edits lab procedure.
     */
    HCP_EDIT_PROC ( 1708, "HCP edits Lab Procedure", false ),
    /**
     * HCP deletes lab procedure.
     */
    HCP_DELETE_PROC ( 1709, "HCP deletes Lab Procedure", false ),
    /**
     * HCP views lab procedures.
     */
    HCP_VIEW_PROCS ( 1710, "HCP Views Procedures", false ),
    /**
     * Patient views lab procedures.
     */
    PATIENT_VIEW_PROCS ( 1711, "Patient Views Procedures", false ),
    /**
     * Food Diary Entry is created
     */
    CREATE_FOOD_DIARY_ENTRY (1901, "Create a Food Diary Entry", true),
    /**
     * Patient views a food diary entry
     */
    PATIENT_VIEW_FOOD_DIARY_ENTRY (1902, "Patient Views Food Diary Entry", true),
    /**
     * HCP views a food diary entry
     */
    HCP_VIEW_FOOD_DIARY_ENTRY (1903, "HCP Views Food Diary Entry", true),
    /**
     * OPH viewed upcoming appointment
     */
    OPH_VIEW_UPCOMING_APPOINTMENT(2001, "OPH viewed upcoming appointment", true),
    /**
     * Patient requests ophthalmology appointment
     */
    PATIENT_REQ_OPH_APPT(2010, "Patient requests ophthalmology appointment", false),
    /**
     * OPH viewed upcoming appointment
     */
    OPH_VIEWS_APPT_REQ(2011, "OPH viewed upcoming appointment", true),
    /**
     * Patient deletes ophthalmology appointment request
     */
    PATIENT_DELETES_OPH_APPT_REQUEST(2012, "Patient deletes ophthalmology appointment request", false),
    /**
     * Patient requests ophthalmology appointment
     */
    PATIENT_REQ_OPH_SURG(2013, "Patient requests ophthalmology surgery", false),
    /**
     * Patient deletes ophthalmology surgery
     */
    PATIENT_DELETES_OPH_SURG(2013, "Patient deletes ophthalmology surgery", false),
    /**
     * OPH approves appointment request
     */
    OPH_APPT_REQ_APPROVED(2020, "OPH approves appointment request", true),
    /**
     * OPH denies appointment request
     */
    OPH_APPT_REQ_DENIED(2021, "OPH denies appointment request", true),
    /**
     * OPH approves surgery request
     */
    OPH_SURG_REQ_APPROVED(2020, "OPH approves surgery request", true),
    /**
     * OPH denies surgery request
     */
    OPH_SURG_REQ_DENIED(2020, "OPH denies surgery request", true),
    /**
     * OPH updates appointment request
     */
    OPH_APPT_REQ_UPDATED(2025, "OPH updates appointment request", true),

    /**
     * Create basic health metrics
     */
    GENERAL_OPHTHALMOLOGY_CREATE( 2100, "Create general ophthalmology visit for patient", true ),
    /**
     * HCP views basic health metrics
     */
    GENERAL_OPHTHALMOLOGY_HCP_VIEW( 2101, "View general ophthalmology visit by HCP", true ),
    /**
     * HCP edits basic health metrics
     */
    GENERAL_OPHTHALMOLOGY_EDIT( 2102, "HCP edits basic health metrics", true ),
    /**
     * Patient views basic health metrics for an general ophthalmology visit
     */
    GENERAL_OPHTHALMOLOGY_PATIENT_VIEW( 2110, "View general ophthalmology visit by Patient", true ),

    /**
     * general ophthalmology visit is deleted
     */
    GENERAL_OPHTHALMOLOGY_DELETE( 2111, "general ophthalmology visit deleted", true ),

    /**
     * Create basic health metrics
     */
    OPHTHALMOLOGY_SURGERY_CREATE( 2200, "Create ophthalmology surgery for patient", true ),
    /**
     * HCP views basic health metrics
     */
    OPHTHALMOLOGY_SURGERY_HCP_VIEW( 2201, "View ophthalmology surgery by HCP", true ),
    /**
     * HCP edits basic health metrics
     */
    OPHTHALMOLOGY_SURGERY_EDIT( 2202, "HCP edits basic health metrics", true ),
    /**
     * Patient views basic health metrics for an ophthalmology surgery
     */
    OPHTHALMOLOGY_SURGERY_PATIENT_VIEW( 2210, "View ophthalmology surgery by Patient", true ),

    /**
     * ophthalmology surgery is deleted
     */
    OPHTHALMOLOGY_SURGERY_DELETE( 2211, "ophthalmology surgery deleted", true );
    
    /**
     * Creates a TransactionType for logging events
     *
     * @param code
     *            Code of the event
     * @param description
     *            Description of the event that occurred
     * @param patientViewable
     *            Whether this logged event can be viewed by the patient
     *            involved
     */
    private TransactionType ( final int code, final String description, final boolean patientViewable ) {
        this.code = code;
        this.description = description;
        this.patientView = patientViewable;
    }

    /**
     * Code of the TransactionType, from the iTrust2 wiki.
     */
    private int     code;
    /**
     * Description of the event
     */
    private String  description;
    /**
     * Whether the patient can view the event
     */
    private boolean patientView;

    /**
     * Retrieves the code of this TransactionType
     *
     * @return Code of the event
     */
    public int getCode () {
        return code;
    }

    /**
     * Description of this TransactionType event
     *
     * @return Description of the event
     */
    public String getDescription () {
        return description;
    }

    /**
     * Retrieves if the Patient can view this event
     *
     * @return Patient viewable or not
     */
    public boolean isPatientViewable () {
        return patientView;
    }

}
