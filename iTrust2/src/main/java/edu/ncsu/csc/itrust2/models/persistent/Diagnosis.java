package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

/**
 * Class to represent a Diagnosis made by an HCP as part of an Office Visit
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Diagnoses" )
public class Diagnosis extends DomainObject<Diagnosis> {

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "visit_id", nullable = false )
    private OfficeVisit visit;

    private String      note;

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long        id;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "code_id" )
    private ICDCode     code;

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the Diagnosis
     *
     * @param id
     *            The new ID of the Diagnosis. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Sets the Office Visit this diagnosis is associated with
     *
     * @param visit
     *            The Visit to associate with
     */
    public void setVisit ( final OfficeVisit visit ) {
        this.visit = visit;
    }

    /**
     * Returns the visit this diagnosis is part of
     *
     * @return The Offive Visit
     */
    public OfficeVisit getVisit () {
        return visit;
    }

    /**
     * Sets the note for the diagnosis
     *
     * @param n
     *            The new note
     */
    public void setNote ( final String n ) {
        this.note = n;
    }

    /**
     * Returns the note of the diagnosis
     *
     * @return The note
     */
    public String getNote () {
        return note;
    }

    /**
     * Sets the code for this diagnosis
     *
     * @param code
     *            The new code
     */
    public void setCode ( final ICDCode code ) {
        this.code = code;
    }

    /**
     * Returns the code for this diagnosis
     *
     * @return The code
     */
    public ICDCode getCode () {
        return code;
    }

    /**
     * Returns a List of Diagnosis that are selected by the given WHERE clause
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of diagnoses SELECTed
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Diagnosis> getWhere ( final List<Criterion> where ) {
        return (List<Diagnosis>) getWhere( Diagnosis.class, where );
    }

    /**
     * Returns the Diagnosis with the given ID
     *
     * @param id
     *            The ID of the Diagnosis to retrieve
     * @return The requested diagnosis.
     */
    public static Diagnosis getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Return a list of Diagnosis for the specified visit
     *
     * @param id
     *            The ID of the Office Visit to search for
     * @return The list of Diagnoses
     */
    public static List<Diagnosis> getByVisit ( final Long id ) {
        return getWhere( createCriterionAsList( "visit", OfficeVisit.getById( id ) ) );
    }

    /**
     * Returns a list of diagnoses for the specified Patient
     *
     * @param user
     *            The patient to get diagnoses for
     * @return The list of diagnoses
     */
    public static List<Diagnosis> getForPatient ( final User user ) {
        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();
        OfficeVisit.getForPatient( user.getId() ).stream().map( OfficeVisit::getId )
                .forEach( e -> diagnoses.addAll( getByVisit( e ) ) );
        return diagnoses;

    }
}
