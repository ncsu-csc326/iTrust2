package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Office Visit form used to document an Office Visit by the HCP. This will be
 * validated and converted to a OfficeVisit to be stored in the database.
 *
 * @author Kai Presler-Marshall
 * @author Elizabeth Gilbert
 * @author Jack MacDonald
 * @author Matt Dzwonczyk
 */
public class GeneralCheckupForm extends OfficeVisitForm implements Serializable {

    private static final long      serialVersionUID = 1L;

    /**
     * Diagnoses associated with this visit
     */
    private List<Diagnosis>        diagnoses;
    /**
     * Lab Procedures associated with this visit
     */
    private List<LabProcedure>     labProcedures;

    private List<PrescriptionForm> prescriptions;

    /**
     * Empty constructor so that we can create an 
     * office visit form for the user to fill out.
     */
    public GeneralCheckupForm () { }

    /**
     * Creates an OfficeVisitForm from the OfficeVisit provided
     *
     * @param officeVisit
     *            OfficeVisit to turn into an OfficeVisitForm
     */
    public GeneralCheckupForm ( final GeneralCheckup officeVisit ) {
        super( officeVisit );
        setDiagnoses( officeVisit.getDiagnoses() );
        setLabProcedures( officeVisit.getLabProcedures() );
        setPrescriptions( officeVisit.getPrescriptions().stream().map( ( final Prescription p ) -> new PrescriptionForm( p ) )
                .collect( Collectors.toList() ) );
        setType( "GENERAL_CHECKUP" );
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
     * Sets the Lab Procedure list for this visit.
     *
     * @param list
     *            The list of Lab Procedures.
     */
    public void setLabProcedures ( final List<LabProcedure> list ) {
        labProcedures = list;
    }

    /**
     * Returns the list of lab procedures associated with this office visit.
     *
     * @return The list of Lab Procedures
     */
    public List<LabProcedure> getLabProcedures () {
        return labProcedures;
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
