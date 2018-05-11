package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Office Visit form used to document an Office Visit by the HCP. This will be
 * validated and converted to a OfficeVisit to be stored in the database.
 *
 * @author Kai Presler-Marshall
 * @author Elizabeth Gilbert
 *
 */
public class OfficeVisitForm implements Serializable {
    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an Office Visit form for the user
     * to fill out
     */
    public OfficeVisitForm () {
    }

    /**
     * Name of the Patient involved in the OfficeVisit
     */
    @NotEmpty
    private String                 patient;

    /**
     * Name of the HCP involved in the OfficeVisit
     */
    @NotEmpty
    private String                 hcp;

    /**
     * Date at which the OfficeVisit occurred
     */
    @NotEmpty
    private String                 date;

    /**
     * ID of the OfficeVisit
     */
    private String                 id;

    /**
     * Time at which the OfficeVisit occurred
     */
    @NotEmpty
    private String                 time;

    /**
     * Type of the OfficeVisit.
     */
    @NotEmpty
    private String                 type;

    /**
     * Hospital where the OfficeVisit occurred
     */
    @NotEmpty
    private String                 hospital;

    /**
     * Doctor's Notes on the OfficeVisit
     */
    @Length ( max = 255 )
    private String                 notes;

    /**
     * Whether the OfficeVisit was prescheduled or not
     */
    public String                  preScheduled;

    /**
     * Height or length of the person. Up to a 3-digit number and potentially
     * one digit of decimal precision. > 0
     */
    private Float                  height;

    /**
     * Weight of the person. Up to a 3-digit number and potentially one digit of
     * decimal precision. > 0
     */
    private Float                  weight;

    /**
     * Head circumference of the person. Up to a 3-digit number and potentially
     * one digit of decimal precision. > 0
     */
    private Float                  headCircumference;

    /**
     * Systolic blood pressure. 3-digit positive number.
     */
    private Integer                systolic;

    /**
     * Diastolic blood pressure. 3-digit positive number.
     */
    private Integer                diastolic;

    /**
     * HDL cholesterol. Between 0 and 90 inclusive.
     */
    private Integer                hdl;

    /**
     * LDL cholesterol. Between 0 and 600 inclusive.
     */
    private Integer                ldl;

    /**
     * Triglycerides cholesterol. Between 100 and 600 inclusive.
     */
    private Integer                tri;

    /**
     * Smoking status of the patient's household.
     */
    private HouseholdSmokingStatus houseSmokingStatus;

    /**
     * Smoking status of the patient.
     */
    private PatientSmokingStatus   patientSmokingStatus;

    /**
     * Diagnoses associated with this visit
     */
    private List<Diagnosis>        diagnoses;

    private List<PrescriptionForm> prescriptions;

    /**
     * Creates an OfficeVisitForm from the OfficeVisit provided
     *
     * @param ov
     *            OfficeVisit to turn into an OfficeVisitForm
     */
    public OfficeVisitForm ( final OfficeVisit ov ) {
        setPatient( ov.getPatient().getUsername() );
        setHcp( ov.getHcp().getUsername() );
        final SimpleDateFormat tempDate = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        setDate( tempDate.format( ov.getDate().getTime() ) );
        final SimpleDateFormat tempTime = new SimpleDateFormat( "hh:mm aaa", Locale.ENGLISH );
        setTime( tempTime.format( ov.getDate().getTime() ) );
        setNotes( ov.getNotes() );
        setId( ov.getId().toString() );
        setPreScheduled( ( (Boolean) ( ov.getAppointment() != null ) ).toString() );
        setDiagnoses( new ArrayList<Diagnosis>() );
        setPrescriptions( ov.getPrescriptions().stream().map( ( Prescription p ) -> new PrescriptionForm( p ) )
                .collect( Collectors.toList() ) );
    }

    /**
     * Get the patient in the OfficeVisit
     *
     * @return The patient's username
     */
    public String getPatient () {
        return this.patient;
    }

    /**
     * Sets a patient on the OfficeVisitForm
     *
     * @param patient
     *            The patient's username
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Retrieves the HCP on the OfficeVisit
     *
     * @return Username of the HCP on the OfficeVisit
     */
    public String getHcp () {
        return this.hcp;
    }

    /**
     * Set a HCP on the OfficeVisitForm
     *
     * @param hcp
     *            The HCP's username
     */
    public void setHcp ( final String hcp ) {
        this.hcp = hcp;
    }

    /**
     * Retrieves the date that the OfficeVisit occurred at
     *
     * @return Date of the OfficeVisit
     */
    public String getDate () {
        return this.date;
    }

    /**
     * Sets the date that the OfficeVisit occurred at
     *
     * @param date
     *            The date of the office visit
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Gets the ID of the OfficeVisit
     *
     * @return ID of the Visit
     */
    public String getId () {
        return this.id;
    }

    /**
     * Sets the ID of the OfficeVisit
     *
     * @param id
     *            The ID of the OfficeVisit
     */
    public void setId ( final String id ) {
        this.id = id;
    }

    /**
     * Gets the Time of the OfficeVisit
     *
     * @return Time of the Visit
     */
    public String getTime () {
        return this.time;
    }

    /**
     * Sets the time of the OfficeVisit
     *
     * @param time
     *            New time to set
     */
    public void setTime ( final String time ) {
        this.time = time;
    }

    /**
     * Gets the Type of the OfficeVisit
     *
     * @return Type of the visit
     */
    public String getType () {
        return this.type;
    }

    /**
     * Sets the Type of the OfficeVisit
     *
     * @param type
     *            New Type to set
     */
    public void setType ( final String type ) {
        this.type = type;
    }

    /**
     * Gets the Hospital of the OfficeVisit
     *
     * @return Hospital of the Visit
     */
    public String getHospital () {
        return this.hospital;
    }

    /**
     * Sets the Hospital on the OfficeVisit
     *
     * @param hospital
     *            Hospital to set on the visit
     */
    public void setHospital ( final String hospital ) {
        this.hospital = hospital;
    }

    /**
     * Get the Notes on the OfficeVisit
     *
     * @return The notes of the Visit
     */
    public String getNotes () {
        return this.notes;
    }

    /**
     * Set the notes on the OfficeVisit
     *
     * @param notes
     *            New notes
     */
    public void setNotes ( final String notes ) {
        this.notes = notes;
    }

    /**
     * Sets whether the visit was prescheduled
     *
     * @param prescheduled
     *            Whether the Visit is prescheduled or not
     */
    public void setPreScheduled ( final String prescheduled ) {
        this.preScheduled = prescheduled;
    }

    /**
     * Gets whether the visit was prescheduled or not
     *
     * @return Whether the visit was prescheduled
     */
    public String getPreScheduled () {
        return this.preScheduled;
    }

    /**
     * Gets the height
     *
     * @return the height
     */
    public Float getHeight () {
        return height;
    }

    /**
     * Sets the height
     *
     * @param height
     *            the height to set
     */
    public void setHeight ( final Float height ) {
        this.height = height;
    }

    /**
     * Gets the weight
     *
     * @return the weight
     */
    public Float getWeight () {
        return weight;
    }

    /**
     * Sets the weight
     *
     * @param weight
     *            the weight to set
     */
    public void setWeight ( final Float weight ) {
        this.weight = weight;
    }

    /**
     * Gets the head circumference
     *
     * @return the weight
     */
    public Float getHeadCircumference () {
        return headCircumference;
    }

    /**
     * Sets the headCircumference
     *
     * @param headCircumference
     *            the headCircumference to set
     */
    public void setHeadCircumference ( final Float headCircumference ) {
        this.headCircumference = headCircumference;
    }

    /**
     * Gets the diastolic blood pressure
     *
     * @return the diastolic
     */
    public Integer getDiastolic () {
        return diastolic;
    }

    /**
     * Sets the diastolic blood pressure
     *
     * @param diastolic
     *            the diastolic to set
     */
    public void setDiastolic ( final Integer diastolic ) {
        this.diastolic = diastolic;
    }

    /**
     * Gets the systolic blood pressure
     *
     * @return the systolic
     */
    public Integer getSystolic () {
        return systolic;
    }

    /**
     * Sets the systolic blood pressure
     *
     * @param systolic
     *            the systolic to set
     */
    public void setSystolic ( final Integer systolic ) {
        this.systolic = systolic;
    }

    /**
     * Gets HDL cholesterol.
     *
     * @return the hdl
     */
    public Integer getHdl () {
        return hdl;
    }

    /**
     * Sets HDL cholesterol. Between 0 and 90 inclusive.
     *
     * @param hdl
     *            the hdl to set
     */
    public void setHdl ( final Integer hdl ) {
        this.hdl = hdl;
    }

    /**
     * Gets the LDL cholesterol.
     *
     * @return the ldl
     */
    public Integer getLdl () {
        return ldl;
    }

    /**
     * Sets LDL cholesterol. Between 0 and 600 inclusive.
     *
     * @param ldl
     *            the ldl to set
     */
    public void setLdl ( final Integer ldl ) {
        this.ldl = ldl;
    }

    /**
     * Gets triglycerides level.
     *
     * @return the tri
     */
    public Integer getTri () {
        return tri;
    }

    /**
     * Sets triglycerides cholesterol. Between 100 and 600 inclusive.
     *
     * @param tri
     *            the tri to set
     */
    public void setTri ( final Integer tri ) {
        this.tri = tri;
    }

    /**
     * Gets the smoking status of the patient's household.
     *
     * @return the houseSmokingStatus
     */
    public HouseholdSmokingStatus getHouseSmokingStatus () {
        return houseSmokingStatus;
    }

    /**
     * Sets the smoking status of the patient's household.
     *
     * @param houseSmokingStatus
     *            the houseSmokingStatus to set
     */
    public void setHouseSmokingStatus ( final HouseholdSmokingStatus houseSmokingStatus ) {
        this.houseSmokingStatus = houseSmokingStatus;
    }

    /**
     * Gets the smoking status of the patient.
     *
     * @return the patientSmokingStatus
     */
    public PatientSmokingStatus getPatientSmokingStatus () {
        return patientSmokingStatus;
    }

    /**
     * Sets the smoking status of the patient.
     *
     * @param patientSmokingStatus
     *            the patientSmokingStatus to set
     */
    public void setPatientSmokingStatus ( final PatientSmokingStatus patientSmokingStatus ) {
        this.patientSmokingStatus = patientSmokingStatus;
    }

    /**
     * Sets the Diagnosis list for this visit.
     *
     * @param list
     *            The list of Diagnoses.
     */
    public void setDiagnoses ( final List<Diagnosis> list ) {
        diagnoses = list;
    }

    /**
     * Returns the list of diagnoses associated with this office visit.
     *
     * @return The list of Diagnoses
     */
    public List<Diagnosis> getDiagnoses () {
        return diagnoses;
    }

    /**
     * Sets the list of prescriptions for this visit.
     *
     * @param prescriptions
     *            the list of prescriptions
     */
    public void setPrescriptions ( final List<PrescriptionForm> prescriptions ) {
        this.prescriptions = prescriptions;
    }

    /**
     * Returns the list of prescriptions associated with this office visit.
     *
     * @return prescriptions the list prescriptions
     */
    public List<PrescriptionForm> getPrescriptions () {
        return prescriptions;
    }
}
