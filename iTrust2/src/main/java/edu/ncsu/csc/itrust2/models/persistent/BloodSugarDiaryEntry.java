/**
 *
 */
package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.hibernate.criterion.Criterion;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;

import com.google.gson.annotations.JsonAdapter;
import com.sun.istack.NotNull;

import edu.ncsu.csc.itrust2.adapters.LocalDateAdapter;
import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDiaryForm;

/**
 * Diary object for a patient to store blood sugar levels for a particular date
 *
 * @author Thomas Landsberg
 *
 */
@Entity
@Table ( name = "BloodSugarDiaryEntries" )
public class BloodSugarDiaryEntry extends DomainObject<BloodSugarDiaryEntry> implements Serializable {

    /**
     * Randomly generated ID
     */
    private static final long serialVersionUID = -8945113482211292567L;

    /**
     * The date as milliseconds since epoch of this DiaryEntry
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate         date;

    /**
     * The id of this DiaryEntry
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long              id;

    /**
     * The patient for this DiaryEntry
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private Patient           patient;

    /**
     * Blood sugar level when first waking up
     */
    @Min ( 0 )
    private Integer           fastingLevel;

    /**
     * Blood sugar level after first meal
     */
    @Min ( 0 )
    private Integer           firstLevel;

    /**
     * Blood sugar level after second meal
     */
    @Min ( 0 )
    private Integer           secondLevel;

    /**
     * Blood sugar level after third meal
     */
    @Min ( 0 )
    private Integer           thirdLevel;

    /**
     * Default constructor for making a DiaryEntry that will have its values set
     * without a form.
     */
    public BloodSugarDiaryEntry () {
    }

    /**
     * Constructor for a blood sugar diary entry given a form from the front
     * end.
     *
     * @param form
     *            to use data from.
     */
    public BloodSugarDiaryEntry ( final BloodSugarDiaryForm form ) {
        if ( form.getDate() == null ) {
            setDate( LocalDate.now() );
        }
        else {
            setDate( LocalDate.parse( form.getDate().toString() ) );
        }
        setFirstLevel( form.getFirstLevel() );
        setFastingLevel( form.getFastingLevel() );
        setSecondLevel( form.getSecondLevel() );
        setThirdLevel( form.getThirdLevel() );
    }

    /**
     * Get a list of DiaryEntries by patient.
     *
     * @param patient
     *            the patient whose entries are being searched for
     * @return a list of Blood Sugar Diaries for the given patient
     */
    @SuppressWarnings ( "unchecked" )
    public static List<BloodSugarDiaryEntry> getByPatient ( final Patient patient ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( eq( "patient", patient ) );

        return (List<BloodSugarDiaryEntry>) getWhere( BloodSugarDiaryEntry.class, criteria );
    }

    /**
     * Get a specific diary entry by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific diary entry with the desired ID
     */
    public static BloodSugarDiaryEntry getById ( final Long id ) {
        try {
            return (BloodSugarDiaryEntry) getWhere( BloodSugarDiaryEntry.class, eqList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get a specific diary entry by the date and patient
     *
     * @param date
     *            the date of the entry to look for
     * @param patient
     *            the name of the patient associated with the entry
     * @return the specific diary entry
     */
    public static BloodSugarDiaryEntry getByDateAndPatient ( final String date, final Patient patient ) {
        final List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add( eq( "date", LocalDate.parse( date ) ) );
        criteria.add( eq( "patient", patient ) );
        final List<BloodSugarDiaryEntry> entries = getWhere( criteria );

        if ( entries.size() == 0 ) {
            return null;
        }

        return entries.get( 0 );
    }

    /**
     * Retrieve a List of blood sugar diary entries that meets the given where
     * clause. Clause is expected to be valid SQL.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The matching list
     */
    @SuppressWarnings ( "unchecked" )
    private static List<BloodSugarDiaryEntry> getWhere ( final List<Criterion> where ) {
        return (List<BloodSugarDiaryEntry>) getWhere( BloodSugarDiaryEntry.class, where );
    }

    /**
     * Getter for the date of the entry
     *
     * @return the date
     */
    public LocalDate getDate () {
        return date;
    }

    /**
     * Sets the date of the blood sugar diary entry
     *
     * @param date
     *            the date to set
     */
    public void setDate ( final LocalDate date ) {
        this.date = date;
    }

    /**
     * Gets the ID of the blood sugar diary entry
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the blood sugar diary entry
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the patient of the blood sugar diary entry
     *
     * @return the patient
     */
    public Patient getPatient () {
        return patient;
    }

    /**
     * Sets the patient of the blood sugar diary entry
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( final Patient patient ) {
        this.patient = patient;
    }

    /**
     * Gets the fasting level of the blood sugar diary entry
     *
     * @return the fastingLevel
     */
    public Integer getFastingLevel () {
        return fastingLevel;
    }

    /**
     * Sets the fasting level of the blood sugar diary entry
     *
     * @param fastingLevel
     *            the fastingLevel to set
     */
    public void setFastingLevel ( final Integer fastingLevel ) {
        this.fastingLevel = fastingLevel;
    }

    /**
     * Gets the first meal blood sugar level
     *
     * @return the firstLevel
     */
    public Integer getFirstLevel () {
        return firstLevel;
    }

    /**
     * Sets the first meal blood sugar level
     *
     * @param firstLevel
     *            the firstLevel to set
     */
    public void setFirstLevel ( final Integer firstLevel ) {
        this.firstLevel = firstLevel;
    }

    /**
     * Gets the second meal blood sugar level
     *
     * @return the secondLevel
     */
    public Integer getSecondLevel () {
        return secondLevel;
    }

    /**
     * Sets the second meal blood sugar level
     *
     * @param secondLevel
     *            the secondLevel to set
     */
    public void setSecondLevel ( final Integer secondLevel ) {
        this.secondLevel = secondLevel;
    }

    /**
     * Gets the third meal blood sugar level
     *
     * @return the thirdLevel
     */
    public Integer getThirdLevel () {
        return thirdLevel;
    }

    /**
     * Sets the third meal blood sugar level
     *
     * @param thirdLevel
     *            the thirdLevel to set
     */
    public void setThirdLevel ( final Integer thirdLevel ) {
        this.thirdLevel = thirdLevel;
    }
}
