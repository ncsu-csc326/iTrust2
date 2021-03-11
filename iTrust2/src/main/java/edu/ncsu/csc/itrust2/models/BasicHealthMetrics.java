package edu.ncsu.csc.iTrust2.models;

import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.PatientSmokingStatus;

/**
 * Object persisted in the database that represents the BasicHealthMetrics of a
 * patient's office visit.
 *
 * @author Matthew Gray
 * @author Kai Presler-Marshall
 */

@Entity
public class BasicHealthMetrics extends DomainObject {

    /**
     * Used so that Hibernate can construct and load objects
     */
    public BasicHealthMetrics () {
    }

    /**
     * ID of the AppointmentRequest
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Retrieves the ID of the BasicHealthMetrics
     * 
     * @return The ID of this BasicHealthMetrics
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the BasicHealthMetrics
     *
     * @param id
     *            The new ID of the BasicHealthMetrics. For Hibernate.
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

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
     * The Patient who is associated with this AppointmentRequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User                   patient;

    /**
     * The HCP who is associated with this AppointmentRequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User                   hcp;

    /**
     * Retrieves the User object for the Patient for the AppointmentRequest
     *
     * @return The associated Patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the Patient for the AppointmentRequest
     *
     * @param patient
     *            The User object for the Patient on the Request
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Gets the User object for the HCP on the request
     *
     * @return The User object for the HCP on the request
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Sets the User object for the HCP on the AppointmentRequest
     *
     * @param hcp
     *            User object for the HCP on the Request
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
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
        if ( height == null ) {
            return;
        }

        if ( !Pattern.matches( "^[0-9]{1,3}(\\.[0-9]?)?$", String.valueOf( height ) ) ) {
            throw new IllegalArgumentException( "Height cannot be less than .1 or greater than 999.9" );
        }
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
     *            the weight to set, min .1, max 999.9
     */
    public void setWeight ( final Float weight ) {
        if ( weight == null ) {
            return;
        }
        if ( !Pattern.matches( "^[0-9]{1,3}(\\.[0-9]?)?$", String.valueOf( weight ) ) ) {
            throw new IllegalArgumentException( "Weight cannot be less than .1 or greater than 999.9" );
        }
        this.weight = weight;
    }

    /**
     * Gets the head circumference
     *
     * @return the head circumference
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
        if ( headCircumference == null ) {
            return;
        }
        if ( !Pattern.matches( "^[0-9]{1,3}(\\.[0-9]?)?$", String.valueOf( headCircumference ) ) ) {
            throw new IllegalArgumentException( "Head circumference cannot be less than .1 or greater than 999.9" );
        }
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
        if ( diastolic == null ) {
            return;
        }
        if ( diastolic < 0 || diastolic > 999 ) {
            throw new IllegalArgumentException( "Diastolic must be a 3 digit positive number." );
        }
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
        if ( systolic == null ) {
            return;
        }
        if ( systolic < 0 || systolic > 999 ) {
            throw new IllegalArgumentException( "Systolic must be a 3 digit positive number." );
        }
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
        if ( hdl == null ) {
            return;
        }
        if ( hdl < 0 || hdl > 90 ) {
            throw new IllegalArgumentException( "HDL must be between 0 and 90 inclusive." );
        }
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
        if ( ldl == null ) {
            return;
        }
        if ( ldl < 0 || ldl > 600 ) {
            throw new IllegalArgumentException( "LDL must be between 0 and 600 inclusive." );
        }
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
        if ( tri == null ) {
            return;
        }
        if ( tri < 100 || tri > 600 ) {
            throw new IllegalArgumentException( "Triglycerides must be between 100 and 600 inclusive." );
        }
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

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( diastolic == null ) ? 0 : diastolic.hashCode() );
        result = prime * result + ( ( hcp == null ) ? 0 : hcp.hashCode() );
        result = prime * result + ( ( hdl == null ) ? 0 : hdl.hashCode() );
        result = prime * result + ( ( headCircumference == null ) ? 0 : headCircumference.hashCode() );
        result = prime * result + ( ( height == null ) ? 0 : height.hashCode() );
        result = prime * result + ( ( houseSmokingStatus == null ) ? 0 : houseSmokingStatus.hashCode() );
        result = prime * result + ( ( ldl == null ) ? 0 : ldl.hashCode() );
        result = prime * result + ( ( patient == null ) ? 0 : patient.hashCode() );
        result = prime * result + ( ( patientSmokingStatus == null ) ? 0 : patientSmokingStatus.hashCode() );
        result = prime * result + ( ( systolic == null ) ? 0 : systolic.hashCode() );
        result = prime * result + ( ( tri == null ) ? 0 : tri.hashCode() );
        result = prime * result + ( ( weight == null ) ? 0 : weight.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final BasicHealthMetrics other = (BasicHealthMetrics) obj;
        if ( diastolic == null ) {
            if ( other.diastolic != null ) {
                return false;
            }
        }
        else if ( !diastolic.equals( other.diastolic ) ) {
            return false;
        }
        if ( hcp == null ) {
            if ( other.hcp != null ) {
                return false;
            }
        }
        else if ( !hcp.equals( other.hcp ) ) {
            return false;
        }
        if ( hdl == null ) {
            if ( other.hdl != null ) {
                return false;
            }
        }
        else if ( !hdl.equals( other.hdl ) ) {
            return false;
        }
        if ( headCircumference == null ) {
            if ( other.headCircumference != null ) {
                return false;
            }
        }
        else if ( !headCircumference.equals( other.headCircumference ) ) {
            return false;
        }
        if ( height == null ) {
            if ( other.height != null ) {
                return false;
            }
        }
        else if ( !height.equals( other.height ) ) {
            return false;
        }
        if ( houseSmokingStatus != other.houseSmokingStatus ) {
            return false;
        }
        if ( ldl == null ) {
            if ( other.ldl != null ) {
                return false;
            }
        }
        else if ( !ldl.equals( other.ldl ) ) {
            return false;
        }
        if ( patient == null ) {
            if ( other.patient != null ) {
                return false;
            }
        }
        else if ( !patient.equals( other.patient ) ) {
            return false;
        }
        if ( patientSmokingStatus != other.patientSmokingStatus ) {
            return false;
        }
        if ( systolic == null ) {
            if ( other.systolic != null ) {
                return false;
            }
        }
        else if ( !systolic.equals( other.systolic ) ) {
            return false;
        }
        if ( tri == null ) {
            if ( other.tri != null ) {
                return false;
            }
        }
        else if ( !tri.equals( other.tri ) ) {
            return false;
        }
        if ( weight == null ) {
            if ( other.weight != null ) {
                return false;
            }
        }
        else if ( !weight.equals( other.weight ) ) {
            return false;
        }
        return true;
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

}
