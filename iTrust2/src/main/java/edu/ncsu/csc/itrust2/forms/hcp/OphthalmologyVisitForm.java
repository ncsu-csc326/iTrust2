package edu.ncsu.csc.itrust2.forms.hcp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import edu.ncsu.csc.itrust2.models.persistent.OphthalmologyVisit;

/**
 * The abstraction of the form a doctor fills out for any ophthalmology appointment
 * 
 * @author Jack MacDonald
 */
public abstract class OphthalmologyVisitForm extends OfficeVisitForm {

    @Min(20)
    @Max(200)
    private Integer  visualAcuityOD;

    @Min(20)
    @Max(200)
    private Integer  visualAcuityOS;

    private Double  sphereOD;

    private Double  sphereOS;

    private Double  cylinderOD;

    private Double  cylinderOS;

    private Integer axisOD;

    private Integer axisOS;

    /** For Hibernate */
    public OphthalmologyVisitForm () {
    }

    /**
     * Creates the form from the given office visit
     *
     * @param ov
     *            office visit to create form from
     */
    public OphthalmologyVisitForm ( OphthalmologyVisit ov ) {
        super( ov );
        setVisualAcuityOD( ov.getVisualAcuityOD() );
        setVisualAcuityOS( ov.getVisualAcuityOS() );
        setSphereOD( ov.getSphereOD() );
        setSphereOS( ov.getSphereOS() );
        setCylinderOD( ov.getCylinderOD() );
        setCylinderOS( ov.getCylinderOS() );
        setAxisOD( ov.getAxisOD() );
        setAxisOS( ov.getAxisOS() );
    }

    /**
     * Gets the visual acuity for the left eye
     *
     * @return visual acuity for the left eye
     */
    public Integer getVisualAcuityOD () {
        return visualAcuityOD;
    }

    /**
     * Sets the visual acuity for the left eye
     *
     * @param visualAcuityOD
     *            visual acuity for the left eye
     */
    public void setVisualAcuityOD ( Integer visualAcuityOD ) {
        this.visualAcuityOD = visualAcuityOD;
    }

    /**
     * Gets the visual acuity for the right eye
     *
     * @return visual acuity for the right eye
     */
    public Integer getVisualAcuityOS () {
        return visualAcuityOS;
    }

    /**
     * Sets the visual acuity for the right eye
     *
     * @param visualAcuityOS
     *            visual acuity for the right eye
     */
    public void setVisualAcuityOS ( Integer visualAcuityOS ) {
        this.visualAcuityOS = visualAcuityOS;
    }

    /**
     * Gets the sphere of the left eye
     *
     * @return sphere of the left eye
     */
    public Double getSphereOD () {
        return sphereOD;
    }

    /**
     * Sets the sphere for the left eye
     *
     * @param sphereOD
     *            the sphere for the left eye
     */
    public void setSphereOD ( Double sphereOD ) {
        this.sphereOD = sphereOD;
    }

    /**
     * Gets the sphere of the right eye
     *
     * @return sphere of the right eye
     */
    public Double getSphereOS () {
        return sphereOS;
    }

    /**
     * Sets the sphere for the right eye
     *
     * @param sphereOS
     *            the sphere for the right eye
     */
    public void setSphereOS ( Double sphereOS ) {
        this.sphereOS = sphereOS;
    }

    /**
     * Gets the cylinder of the left eye
     *
     * @return cylinder of the left eye
     */
    public Double getCylinderOD () {
        return cylinderOD;
    }

    /**
     * Sets the cylinder of the left eye
     *
     * @param cylinderOD
     *            cylinder of the left eye
     */
    public void setCylinderOD ( Double cylinderOD ) {
        this.cylinderOD = cylinderOD;
    }

    /**
     * Gets the cylinder of the right eye
     *
     * @return the cylinder of the right eye
     */
    public Double getCylinderOS () {
        return cylinderOS;
    }

    /**
     * Sets the cylinder of the right eye
     *
     * @param cylinderOS
     *            the cylinder of the right eye
     */
    public void setCylinderOS ( Double cylinderOS ) {
        this.cylinderOS = cylinderOS;
    }

    /**
     * Gets the axis of the left eye
     *
     * @return the axis of the left eye
     */
    public Integer getAxisOD () {
        return axisOD;
    }

    /**
     * Sets the axis of the left eye
     *
     * @param axisOD
     *            axis of the left eye
     */
    public void setAxisOD ( Integer axisOD ) {
        this.axisOD = axisOD;
    }

    /**
     * Gets the axis of the right eye
     *
     * @return the axis of the right eye
     */
    public Integer getAxisOS () {
        return axisOS;
    }

    /**
     * Sets the axis of the right eye
     *
     * @param axisOS
     *            axis of the right eye
     */
    public void setAxisOS ( Integer axisOS ) {
        this.axisOS = axisOS;
    }
}
