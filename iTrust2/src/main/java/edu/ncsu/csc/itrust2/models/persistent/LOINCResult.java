package edu.ncsu.csc.itrust2.models.persistent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * An abstract representation of a result for a LOINC. Each LOINC can specify
 * what the results for that procedure should look like. Results can be either
 * Qualitative or Quantitative. LOINCResult should never be instantiated
 * directly, you should always use one of the inheriting classes.
 *
 * @author Sam Fields
 *
 */
@Entity ( name = "LOINCResult" )
@Inheritance ( strategy = InheritanceType.SINGLE_TABLE )
public class LOINCResult extends DomainObject<LOINCResult> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Empty constructor for thymeleaf. LOINCResult should never be
     * instantiated, you should always use one of the child implementations.
     */
    public LOINCResult () {
    }

    /**
     * Returns the diagnosis ICD-10 that corresponds with the provided result.
     *
     * @param result
     *            the result for which a diagnosis is provided
     * @return the suggested diagnosis for the provided result
     */
    public ICDCode getDiagnosisForResult ( String result ) {
        return null;
    }

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the LOINCResult
     *
     * @param id
     *            the id to set
     */
    public void setId ( Long id ) {
        this.id = id;
    }
}
