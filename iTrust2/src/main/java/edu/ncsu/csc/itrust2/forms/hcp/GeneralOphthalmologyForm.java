package edu.ncsu.csc.itrust2.forms.hcp;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.ncsu.csc.itrust2.models.persistent.GeneralOphthalmology;

/**
 * The form a doctor fills out for a GeneralOphthalmology appointment
 * @author Jack MacDonald
 *
 */
public class GeneralOphthalmologyForm extends OphthalmologyVisitForm {

    private String diagnosis;

    /**
     * For Hibernate/Thymeleaf
     */
    public GeneralOphthalmologyForm () {
    }

    /**
     * Creates a form object out of the given appointment
     *
     * @param ov
     *            OfficeVisit to create form out of
     */
    public GeneralOphthalmologyForm ( GeneralOphthalmology ov ) {
        super( ov );
        setDiagnosis( ov.getDiagnosis() );
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
    public void setDiagnosis ( List<String> diagnosis ) {
        this.diagnosis = StringUtils.join( diagnosis, ',' );
    }
}
