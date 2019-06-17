package edu.ncsu.csc.itrust2.forms.hcp;

import edu.ncsu.csc.itrust2.models.enums.EyeSurgeryType;
import edu.ncsu.csc.itrust2.models.persistent.OphthalmologySurgery;

/**
 * Represents an Ophthalmology Surgery
 *
 * @author Jack MacDonald
 */
public class OphthalmologySurgeryForm extends OphthalmologyVisitForm {

    private EyeSurgeryType surgeryType;

    /**
     * Default constructor for Hibernate/Thymeleaf
     */
    public OphthalmologySurgeryForm () {
    }

    /**
     * Creates an ophthalmology surgery form from the given office visit
     *
     * @param ov
     *            office visit to create form from
     */
    public OphthalmologySurgeryForm ( final OphthalmologySurgery ov ) {
        super( ov );
        setSurgeryType( ov.getSurgeryType() );
    }

    /**
     * Gets the surgery type
     *
     * @return the surgery type
     */
    public EyeSurgeryType getSurgeryType () {
        return surgeryType;
    }

    /**
     * Sets the surgery type
     *
     * @param surgeryType
     *            the surgery type to set
     */
    public void setSurgeryType ( final EyeSurgeryType surgeryType ) {
        this.surgeryType = surgeryType;
    }
}
