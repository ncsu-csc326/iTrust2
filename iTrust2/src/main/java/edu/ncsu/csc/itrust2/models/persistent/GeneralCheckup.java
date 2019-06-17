package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.ncsu.csc.itrust2.forms.hcp.GeneralCheckupForm;
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Model class for a general checkup.
 * 
 * @author Jack MacDonald
 */
@Entity
@Table ( name = "GeneralCheckups" )
public class GeneralCheckup extends OfficeVisit {

    /**
     * Construsts a new GeneralCheckup instance for Hibernate.
     */
    public GeneralCheckup () {
        // For Hibernate
    }

    /**
     * Construsts a new GeneralCheckup instance with the specified
     * GeneralCheckupForm for Hibernate.
     *
     * @param ovf
     *            The GeneralCheckupForm instance.
     */
    public GeneralCheckup ( GeneralCheckupForm ovf ) throws NumberFormatException, ParseException {
        super( ovf );
        // associate all diagnoses with this visit
        if ( ovf.getDiagnoses() != null ) {
            setDiagnoses( ovf.getDiagnoses() );
            for ( final Diagnosis d : diagnoses ) {
                d.setVisit( this );
            }
        }

        // associate all lab procedures with this visit
        if ( ovf.getLabProcedures() != null ) {
            setLabProcedures( ovf.getLabProcedures() );
            for ( final LabProcedure d : labProcedures ) {
                d.setVisit( this );
            }
        }

        validateDiagnoses();

        final List<PrescriptionForm> ps = ovf.getPrescriptions();
        if ( ps != null ) {
            setPrescriptions( ps.stream().map( ( final PrescriptionForm pf ) -> new Prescription( pf ) )
                    .collect( Collectors.toList() ) );
        }
    }

    private void validateDiagnoses () {
        if ( diagnoses == null ) {
            return;
        }
        for ( final Diagnosis d : diagnoses ) {
            if ( d.getNote().length() > 500 ) {
                throw new IllegalArgumentException( "Dagnosis note too long (500 character max) : " + d.getNote() );
            }
            if ( d.getCode() == null ) {
                throw new IllegalArgumentException( "Diagnosis Code missing!" );
            }
        }
    }

    /**
     * Get a specific office visit by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific office visit with the desired ID
     */
    public static GeneralCheckup getById ( final Long id ) {
        return (GeneralCheckup) OfficeVisit.getById( id );
    }

    /**
     * Sets the list of Diagnoses associated with this visit
     *
     * @param list
     *            The List of Diagnoses
     */
    public void setDiagnoses ( final List<Diagnosis> list ) {
        diagnoses = list;
    }

    /**
     * Returns the list of diagnoses for this visit
     *
     * @return The list of diagnoses
     */
    public List<Diagnosis> getDiagnoses () {
        return diagnoses;
    }

    /**
     * Sets the list of Lab Procedures associated with this visit
     *
     * @param list
     *            The List of Lab Procedures
     */
    public void setLabProcedures ( final List<LabProcedure> list ) {
        labProcedures = list;
    }

    /**
     * Returns the list of lab procedures for this visit
     *
     * @return The list of lab procedures
     */
    public List<LabProcedure> getLabProcedures () {
        return labProcedures;
    }

    /**
     * Sets the list of prescriptions associated with this visit
     *
     * @param prescriptions
     *            The list of prescriptions
     */
    public void setPrescriptions ( final List<Prescription> prescriptions ) {
        this.prescriptions = prescriptions;
    }

    /**
     * Returns the list of prescriptions for this visit
     *
     * @return The list of prescriptions
     */
    public List<Prescription> getPrescriptions () {
        return prescriptions;
    }

    /**
     * The set of diagnoses associated with this visits Marked transient so not
     * serialized or saved in DB If removed, serializer gets into an infinite
     * loop
     */
    @OneToMany ( mappedBy = "visit" )
    public transient List<Diagnosis>     diagnoses;

    /**
     * The list of Lab Procedures associated with this Office Visit
     */
    @OneToMany ( mappedBy = "visit" )
    private transient List<LabProcedure> labProcedures;

    @OneToMany ( fetch = FetchType.EAGER )
    @JoinColumn ( name = "prescriptions_id" )
    private List<Prescription>           prescriptions = Collections.emptyList();

    @Override
    public void save () {
        //// SAVE PRESCRIPTIONS ////

        // Get saved visit
        final GeneralCheckup oldVisit = (GeneralCheckup) OfficeVisit.getById( this.getId() );

        // Get prescription ids included in this office visit
        final Set<Long> currentIds = this.getPrescriptions().stream().map( Prescription::getId )
                .collect( Collectors.toSet() );

        // Get prescription ids saved previously
        final Set<Long> savedIds = oldVisit == null ? Collections.emptySet()
                : oldVisit.getPrescriptions().stream().map( Prescription::getId ).collect( Collectors.toSet() );

        // Save each of the prescriptions
        this.getPrescriptions().forEach( p -> {
            final boolean isSaved = savedIds.contains( p.getId() );
            if ( isSaved ) {
                LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(), getPatient().getUsername(),
                        "Editing prescription with id " + p.getId() );
            }
            else {
                LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(),
                        getPatient().getUsername(), "Creating prescription with id " + p.getId() );
            }
            p.save();
        } );

        // Remove prescriptions no longer included
        if ( !savedIds.isEmpty() ) {
            savedIds.forEach( id -> {
                final boolean isMissing = currentIds.contains( id );
                if ( isMissing ) {
                    LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(),
                            getPatient().getUsername(), "Deleting prescription with id " + id );
                    Prescription.getById( id ).delete();
                }
            } );
        }

        //// END PRESCRIPTIONS ////

        try {
            super.save();

            // get list of ids associated with this visit if this visit already
            // exists
            final Set<Long> previous = Diagnosis.getByVisit( this.getId() ).stream().map( Diagnosis::getId )
                    .collect( Collectors.toSet() );
            if ( getDiagnoses() != null ) {
                for ( final Diagnosis d : getDiagnoses() ) {
                    if ( d == null ) {
                        continue;
                    }

                    final boolean had = previous.remove( d.getId() );
                    try {
                        if ( !had ) {
                            // new Diagnosis
                            LoggerUtil.log( TransactionType.DIAGNOSIS_CREATE, getHcp().getUsername(),
                                    getPatient().getUsername(), getHcp() + " created a diagnosis for " + getPatient() );
                        }
                        else {
                            // already had - check if edited
                            final Diagnosis old = Diagnosis.getById( d.getId() );
                            if ( !old.getCode().getCode().equals( d.getCode().getCode() )
                                    || !old.getNote().equals( d.getNote() ) ) {
                                // was edited:
                                LoggerUtil.log( TransactionType.DIAGNOSIS_EDIT, getHcp().getUsername(),
                                        getPatient().getUsername(),
                                        getHcp() + " edit a diagnosis for " + getPatient() );

                            }
                        }
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                    d.save();

                }
            }
            // delete any previous associations - they were deleted by user.
            for ( final Long oldId : previous ) {
                final Diagnosis dDie = Diagnosis.getById( oldId );
                if ( dDie != null ) {
                    dDie.delete();
                    try {
                        LoggerUtil.log( TransactionType.DIAGNOSIS_DELETE, getHcp().getUsername(),
                                getPatient().getUsername(),
                                getHcp().getUsername() + " deleted a diagnosis for " + getPatient().getUsername() );
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                }
            }

            // get list of ids associated with this visit if this visit already
            // exists
            final Set<Long> prev = LabProcedure.getByVisit( this.getId() ).stream().map( LabProcedure::getId )
                    .collect( Collectors.toSet() );
            if ( getLabProcedures() != null ) {
                for ( final LabProcedure d : getLabProcedures() ) {
                    if ( d == null ) {
                        continue;
                    }

                    final boolean had = prev.remove( d.getId() );
                    try {
                        if ( !had ) {
                            // new Lab Procedure
                            LoggerUtil.log( TransactionType.HCP_CREATE_PROC, getHcp().getUsername(),
                                    d.getAssignedTech().getUsername(),
                                    getHcp() + " created a Lab Procedure for " + getPatient() );
                        }
                        else {
                            // already had - check if edited
                            final LabProcedure old = LabProcedure.getById( d.getId() );
                            if ( !old.getLoinc().getCode().equals( d.getLoinc().getCode() )
                                    || !old.getComments().equals( d.getComments() )
                                    || !old.getAssignedTech().equals( d.getAssignedTech() )
                                    || !old.getPriority().equals( d.getPriority() )
                                    || !old.getStatus().equals( d.getStatus() ) ) {
                                // was edited:
                                LoggerUtil.log( TransactionType.HCP_EDIT_PROC, getHcp().getUsername(),
                                        d.getAssignedTech().getUsername(),
                                        getHcp() + " edited a Lab Procedure for " + getPatient() );

                            }
                        }
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                    d.save();

                }
            }
            // delete any previous associations - they were deleted by user.
            for ( final Long oldId : previous ) {
                final LabProcedure dDie = LabProcedure.getById( oldId );
                if ( dDie != null ) {
                    dDie.delete();
                    try {
                        LoggerUtil.log( TransactionType.HCP_DELETE_PROC, getHcp().getUsername(),
                                dDie.getAssignedTech().getUsername(),
                                getHcp().getUsername() + " deleted a Lab Procedure for " + getPatient().getUsername() );
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch ( final Exception e ) {
            // Do nothing
        }
    }

    /**
     * Deletes any diagnoses associated with this office visit, then deletes the
     * visit entry
     */
    @Override
    public void delete () {
        if ( diagnoses != null ) {
            for ( final Diagnosis d : diagnoses ) {
                d.delete();
                try {
                    LoggerUtil.log( TransactionType.DIAGNOSIS_DELETE, getHcp().getUsername(),
                            getPatient().getUsername(), getHcp() + " deleted a diagnosis for " + getPatient() );
                }
                catch ( final Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        if ( labProcedures != null ) {
            for ( final LabProcedure d : labProcedures ) {
                d.delete();
                try {
                    LoggerUtil.log( TransactionType.HCP_DELETE_PROC, getHcp().getUsername(),
                            d.getAssignedTech().getUsername(),
                            getHcp().getUsername() + " deleted a Lab Procedure for " + getPatient().getUsername() );
                }
                catch ( final Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        super.delete();
    }

    /**
     * Deletes all Office visits, and all Diagnoses and Lab Procedure (No
     * diagnoses without an office visit)
     */
    public static void deleteAll () {
        DomainObject.deleteAll( Diagnosis.class );
        DomainObject.deleteAll( LabProcedure.class );
        DomainObject.deleteAll( GeneralCheckup.class );
    }
}
