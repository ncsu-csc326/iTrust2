package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Class to represent a Diagnosis made by an HCP as part of an Office Visit
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Diagnoses" )
public class Diagnosis extends DomainObject {

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "visit_id", nullable = false )
    @JsonBackReference
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

}
