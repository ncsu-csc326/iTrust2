package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Table;

import edu.ncsu.csc.itrust2.forms.hcp.OphthalmologySurgeryForm;
import edu.ncsu.csc.itrust2.models.enums.EyeSurgeryType;

/**
 * Represents a Ophthalmology Surgery
 *
 * @author Jack MacDonald
 */
@Entity
@Table ( name = "OphthalmologySurgery" )
public class OphthalmologySurgery extends OphthalmologyVisit {

    private EyeSurgeryType surgeryType;

    /**
     * Default constructor for hibernate
     */
    public OphthalmologySurgery () {
        // Placeholder
    }

    /**
     * Creates an ophthalmology surgery from the given form.
     *
     * @param visitF
     *            Visit form to create surgery out of
     * @throws ParseException
     *             problem creating the surgery
     */
    public OphthalmologySurgery ( final OphthalmologySurgeryForm visitF ) throws ParseException {
        super( visitF );
        setSurgeryType( visitF.getSurgeryType() );
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

    /**
     * Get a specific office visit by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific office visit with the desired ID
     */
    public static OphthalmologySurgery getById ( final Long id ) {
        return (OphthalmologySurgery) OfficeVisit.getById( id );
    }
}
