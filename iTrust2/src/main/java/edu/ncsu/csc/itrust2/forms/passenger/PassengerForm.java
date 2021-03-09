package edu.ncsu.csc.itrust2.forms.passenger;

import java.time.LocalDateTime;

import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;

/**
 * Bare-bones representation of a Passenger, safe to pass to the front end.
 *
 * @author caproven
 * @author lli34
 */
public class PassengerForm {
    /**
     * The Passenger's unique ID
     */
    private String          passengerId;
    /**
     * The Passenger's full name
     */
    private String          name;
    /**
     * The symptom severity of the Passenger
     */
    private SymptomSeverity symptomSeverity;
    /**
     * Date of first symptoms for the Passenger
     */
    private LocalDateTime   initialSymptomDate;

    /**
     * Empty constructor
     */
    public PassengerForm () {
    }

    /**
     * Constructs the PassengerForm from a Passenger instance
     *
     * @param p
     *            the Passenger
     */
    public PassengerForm ( final Passenger p ) {
        setPassengerId( p.getPassengerId() );
        setName( p.getFirstName() + " " + p.getLastName() );
        setSymptomSeverity( p.getSymptomSeverity() );
        setInitialSymptomDate( p.getInitialSymptomArrival() );
    }

    /**
     * Retrieves the Passenger's ID
     *
     * @return the ID
     */
    public String getPassengerId () {
        return passengerId;
    }

    /**
     * Sets the Passenger's ID
     *
     * @param passengerId
     *            the ID
     */
    public void setPassengerId ( final String passengerId ) {
        this.passengerId = passengerId;
    }

    /**
     * Retrieves the Passenger's full name
     *
     * @return full name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the Passenger's full name
     *
     * @param name
     *            full name
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieves the Passenger's symptom severity
     *
     * @return symptom severity
     */
    public SymptomSeverity getSymptomSeverity () {
        return symptomSeverity;
    }

    /**
     * Sets the Passenger's symptom severity
     *
     * @param symptomSeverity
     *            symptom severity
     */
    public void setSymptomSeverity ( final SymptomSeverity symptomSeverity ) {
        this.symptomSeverity = symptomSeverity;
    }

    /**
     * Retrieves the date of first symptoms for the Passenger
     *
     * @return the date
     */
    public LocalDateTime getInitialSymptomDate () {
        return initialSymptomDate;
    }

    /**
     * Sets the date of first symptoms for the Passenger
     *
     * @param initialSymptomDate
     *            the date
     */
    public void setInitialSymptomDate ( final LocalDateTime initialSymptomDate ) {
        this.initialSymptomDate = initialSymptomDate;
    }

}
