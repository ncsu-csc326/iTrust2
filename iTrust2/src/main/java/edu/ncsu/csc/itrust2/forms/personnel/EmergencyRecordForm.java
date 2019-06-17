package edu.ncsu.csc.itrust2.forms.personnel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * EmergencyRecordForm used to quickly pull up important patient demographics as
 * well as recent prescriptions and diagnoses associated with the patient. Can
 * be created/called as either an HCP or an ER.
 *
 * @author Alexander Phelps
 *
 */
public class EmergencyRecordForm implements Serializable {
    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long  serialVersionUID = 1L;

    /**
     * Same patient but their user object.
     */
    @NotEmpty
    private User               user;

    /**
     * Patient used for the EmergencyRecordForm
     */
    @NotEmpty
    private Patient            patient;

    /**
     * First Name of the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             firstName;

    /**
     * Last Name of the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             lastName;

    /**
     * BloodType of the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             bloodType;

    /**
     * Date of birth for the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             dateOfBirth;

    /**
     * Gender of the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             gender;

    /**
     * Age of the patient being used for the EmergencyRecordForm
     */
    @NotEmpty
    private String             age;

    /**
     * Diagnoses associated with this patient
     */
    private List<Diagnosis>    diagnoses;

    /**
     * Prescriptions associated with this patient.
     */
    private List<Prescription> prescriptions;

    /**
     * Creates an EmergencyRecordForm from the patient provided
     *
     * @param user
     *            User to use to create an EmergencyRecordForm
     */
    public EmergencyRecordForm ( final String user ) {
        setPatient( user );
        setFirstName();
        setLastName();
        setAge();
        setDateOfBirth();
        setGender();
        setBloodType();
        setDiagnoses();
        setPrescriptions();
    }

    /**
     * Get the patient in the EmergencyRecordForm
     *
     * @return The EmergencyRecordForm's patient.
     */
    public Patient getPatient () {
        return this.patient;
    }

    /**
     * Sets a patient on the EmergencyRecordForm.
     *
     * @param user
     *            The username of the patient to grab.
     */
    public void setPatient ( final String user ) {
        this.patient = Patient.getByName( user );
    }

    /**
     * Sets the first name of the patient on the EmergencyRecordForm
     */
    void setFirstName () {
        try {
            this.firstName = this.patient.getFirstName();
        }
        catch ( final NullPointerException e ) {
            this.firstName = "NA";
        }
    }

    /**
     * Returns the patient's first name.
     *
     * Used for directly testing EmergencyRecordForm.
     *
     * @return firstName The patient's first name.
     */
    public String getFirstName () {
        return this.firstName;
    }

    /**
     * Sets the last name of the patient on the EmergencyRecordForm
     */
    void setLastName () {
        try {
            this.lastName = this.patient.getLastName();
        }
        catch ( final NullPointerException e ) {
            this.lastName = "NA";
        }
    }

    /**
     * Returns the patient's last name.
     *
     * Used for directly testing EmergencyRecordForm.
     *
     * @return lastName The patient's last name
     */
    public String getLastName () {
        return this.lastName;
    }

    /**
     * Sets the age of the patient on the EmergencyRecordForm
     */
    void setAge () {
        try {
            final LocalDate dob = this.patient.getDateOfBirth();
            this.age = Integer.toString( Period.between( dob, LocalDate.now() ).getYears() );
        } catch ( final NullPointerException e ) {
            this.age = "NA";
        }
    }

    /**
     * Returns the patient's age.
     *
     * Used for directly testing EmergencyRecordForm.
     *
     * @return age The patient's age
     */
    public String getAge () {
        return this.age;
    }

    /**
     * Sets the date of birth of the patient on the EmergencyRecordForm
     */
    void setDateOfBirth () {
        try {
            this.dateOfBirth = this.patient.getDateOfBirth().toString();
        } catch ( final NullPointerException e ) {
            this.dateOfBirth = "NA";
        }
    }

    /**
     * Returns the patient's date of birth.
     *
     * Used for directly testing EmergencyRecordForm
     *
     * @return dateOfBirth The patient's date of birth
     */
    public String getDateOfBirth () {
        return this.dateOfBirth;
    }

    /**
     * Sets the gender of the patient on the EmergencyRecordForm
     */
    void setGender () {
        try {
            this.gender = this.patient.getGender().toString();
        }
        catch ( final NullPointerException e ) {
            this.gender = "NA";
        }
    }

    /**
     * Returns the gender of the patient on the EmergencyRecordForm.
     *
     * Used for directly testing EmergencyRecordForm
     *
     * @return gender The gender of the patient
     */
    public String getGender () {
        return this.gender;
    }

    /**
     * Sets the BloodType of the patient on the EmergencyRecordForm
     */
    void setBloodType () {
        try {
            this.bloodType = this.patient.getBloodType().toString();
        }
        catch ( final NullPointerException e ) {
            this.bloodType = "NA";
        }
    }

    /**
     * Sets the BloodType of the patient on the EmergencyRecordForm
     *
     * Used for directly testing EmergencyRecordForm
     *
     * @return bloodType The bloodType of the patient
     */
    public String getBloodType () {
        return this.bloodType;
    }

    /**
     * Fetches the patients most recent Diagnoses (within the past 60 days) and
     * sets them as the diagnosis list.
     */
    public void setDiagnoses () {
        try {
            final List<Diagnosis> allDiagnoses = Diagnosis
                    .getForPatient( User.getByName( this.patient.getSelf().getUsername() ) );
            final List<Diagnosis> recentDiagnoses = new ArrayList<Diagnosis>();

            final Date date = new Date();
            final LocalDate now = date.toInstant().atZone( ZoneId.systemDefault() ).toLocalDate();
            for ( final Diagnosis diag : allDiagnoses ) {
                final ZonedDateTime officeVisitDate = diag.getVisit().getDate();
                final LocalDate diagnosisDate = officeVisitDate.toInstant().atZone( ZoneId.systemDefault() )
                        .toLocalDate();
                if ( Period.between( diagnosisDate, now ).getDays() <= 60 ) {
                    recentDiagnoses.add( diag );
                }
            }
            this.diagnoses = recentDiagnoses;
        }
        catch ( final NullPointerException e ) {
            this.diagnoses = new ArrayList<Diagnosis>();
        }
    }

    /**
     * Returns the patients most recent Diagnoses (within the past 60 days).
     *
     * @return The list of Diagnoses
     */
    public List<Diagnosis> getDiagnoses () {
        return this.diagnoses;
    }

    /**
     * Fetches the patients most recent Prescriptions (within the past 90 days)
     * and sets them as the Prescription list.
     */
    public void setPrescriptions () {
        try {
            final List<Prescription> allPrescriptions = Prescription
                    .getForPatient( this.patient.getSelf().getUsername() );
            final List<Prescription> recentPrescriptions = new ArrayList<Prescription>();

            final Date date = new Date();
            final LocalDate now = date.toInstant().atZone( ZoneId.systemDefault() ).toLocalDate();
            for ( final Prescription drug : allPrescriptions ) {
                final LocalDate endDate = drug.getEndDate();
                if ( Period.between( endDate, now ).getDays() <= 90 || now.isBefore( endDate ) ) {
                    recentPrescriptions.add( drug );
                }
            }
            this.prescriptions = recentPrescriptions;
        }
        catch ( final NullPointerException e ) {
            this.prescriptions = new ArrayList<Prescription>();
        }
    }

    /**
     * Returns the patients most recent Prescriptions (within the past 90 days).
     *
     * @return prescriptions the list prescriptions
     */
    public List<Prescription> getPrescriptions () {
        return this.prescriptions;
    }
}
