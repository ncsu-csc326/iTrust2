package edu.ncsu.csc.itrust2.forms.personnel;

import java.io.Serializable;

import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

/**
 * LabProcedureForm used to document a LabProcedure. This will be validated and
 * converted to a LabProcedure to be stored in the database.
 *
 * @author Alex Phelps
 *
 */
public class LabProcedureForm implements Serializable {
    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an LabProcedureForm for the user
     * to fill out
     */
    public LabProcedureForm () {

    }

    /**
     * LOINC id assigned to the LabProcedure
     */
    private Long   loinc;

    /**
     * Priority of the LabProcedure
     */
    private String priority;

    /**
     * Comment associated with the LabProcedure
     */
    private String comments;

    /**
     * Status of the LabProcedure
     */
    private String status;

    /**
     * ID of the LabProcedure
     */
    private Long   id;

    /**
     * Username of the Lab Tech this procedure is assigned to.
     */
    private String labtech;

    /**
     * Username of the patient this procedure is assigned to.
     */
    private String patient;

    /**
     * ID of the OfficeVisit associated with this LabProcedure.
     */
    private Long   visit;

    /**
     * Creates a LabProcedureForm from the LabProcedure provided
     *
     * @param lp
     *            LabProcedure to turn into a LabProcedureForm
     */
    public LabProcedureForm ( final LabProcedure lp ) {
        setLoincId( lp.getLoinc().getId() );
        setPatient( lp.getPatient().getUsername() );
        setPriority( Integer.toString( lp.getPriority().getCode() ) );
        setComments( lp.getComments() );
        setStatus( Integer.toString( lp.getStatus().getCode() ) );
        setId( lp.getId() );
        setAssignedTech( lp.getAssignedTech().getUsername() );
        setVisitId( lp.getVisit().getId() );
    }

    /**
     * Gets the LOINC code associated with the LabProcedure
     *
     * @return The LOINC code's id
     */
    public Long getLoincId () {
        return loinc;
    }

    /**
     * Sets the LOINC code associated with the LabProcedure
     *
     * @param loinc
     *            The LOINC object
     */
    public void setLoincId ( final Long loinc ) {
        this.loinc = loinc;
    }

    /**
     * Get the patient in the LabProcedure
     *
     * @return The patient's username
     */
    public String getPatient () {
        return this.patient;
    }

    /**
     * Sets a patient on the LabProcedureForm
     *
     * @param patient
     *            The patient's username
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Returns the priority of the LabProcedureForm
     *
     * @return The priority of the LabProcedureForm
     */
    public String getPriority () {
        return priority;
    }

    /**
     * Sets the priority of the LabProcedureForm
     *
     * @param priority
     *            The priority to be set for the LabProcedureForm
     */
    public void setPriority ( final String priority ) {
        this.priority = priority;
    }

    /**
     * Gets the comments attached to the LabProcedureForm
     *
     * @return The comments attached to the LabProcedureForm
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the comments for the LabProcedureForm
     *
     * @param comments
     *            The comments to set for the LabProcedureForm
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the status of the LabProcedureForm
     *
     * @return The status of the LabProcedureForm
     */
    public String getStatus () {
        return status;
    }

    /**
     * Sets the status of the LabProcedureForm
     *
     * @param status
     *            The status to set for the LabProcedureForm
     */
    public void setStatus ( final String status ) {
        this.status = status;
    }

    /**
     * Gets the ID of the LabProcedure
     *
     * @return The ID of the LabProcedure
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the LabProcedure
     *
     * @param id
     *            The ID of the LabProcedure
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the assigned Lab Tech of the LabProcedureForm
     *
     * @return The assigned Lab Tech of the LabProcedureForm
     */
    public String getAssignedTech () {
        return labtech;
    }

    /**
     * Assigns a Lab Tech to the LabProcedureForm
     *
     * @param assignedTech
     *            The tech to be assigned to this LabProcedureForm
     */
    public void setAssignedTech ( final String assignedTech ) {
        this.labtech = assignedTech;
    }

    /**
     * Gets the id of the OfficeVisit associated with the LabProcedureForm
     *
     * @return The id of the OfficeVisit associated with the LabProcedureForm
     */
    public Long getVisitId () {
        return visit;
    }

    /**
     * Sets the id of the OfficeVisit associated with the LabProcedureForm
     *
     * @param visit
     *            The id of the OfficeVisit associated with the LabProcedureForm
     */
    public void setVisitId ( final Long visit ) {
        this.visit = visit;
    }
}
