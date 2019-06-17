package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import edu.ncsu.csc.itrust2.forms.hcp.GeneralOphthalmologyForm;

/**
 * Represents a GeneralOphthalmology appointment
 *
 * @author Jack MacDonald
 */
@Entity
@Table ( name = "GeneralOphthalmology" )
public class GeneralOphthalmology extends OphthalmologyVisit {

    private String diagnosis;

    /**
     * Empty constructor for Hibernate/Thymeleaf
     */
    public GeneralOphthalmology () {
    }

    /**
     * Creates a GeneralOphthalmology from the given form.
     *
     * @param visitF
     *            Form to create appointment from
     * @throws ParseException
     *             If there is a problem with the form
     */
    public GeneralOphthalmology ( final GeneralOphthalmologyForm visitF ) throws ParseException {
        super( visitF );
        setDiagnosis( visitF.getDiagnosis() );
    }

    /**
     * Gets the diagnosis
     *
     * @return The diagnosis as a list of strings
     */
    public List<String> getDiagnosis () {
        if ( diagnosis != null ) {
            return Arrays.asList( diagnosis.split( "," ) );
        }
        return null;
    }

    /**
     * Sets the diagnosis
     *
     * @param diagnosis
     *            the list of diagnosis to store.
     */
    public void setDiagnosis ( final List<String> diagnosis ) {
        this.diagnosis = StringUtils.join( diagnosis, ',' );
    }

    /**
     * Get a specific office visit by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific office visit with the desired ID
     */
    public static GeneralOphthalmology getById ( final Long id ) {
        return (GeneralOphthalmology) OfficeVisit.getById( id );
    }

}
