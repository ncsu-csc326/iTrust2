package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;

import edu.ncsu.csc.iTrust2.models.Diagnosis;

public class DiagnosisForm implements Serializable {

    private Long   visit;

    private String note;

    private Long   id;

    private String code;

    public DiagnosisForm () {

    }

    public DiagnosisForm ( final Diagnosis diag ) {
        /* May not be attached to a visit yet */
        if ( null != diag.getVisit() ) {
            visit = diag.getVisit().getId();
        }

        note = diag.getNote();
        id = diag.getId();
        code = diag.getCode().getCode();
    }

    public Long getVisit () {
        return visit;
    }

    public void setVisit ( final Long visit ) {
        this.visit = visit;
    }

    public String getNote () {
        return note;
    }

    public void setNote ( final String note ) {
        this.note = note;
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public String getCode () {
        return code;
    }

    public void setCode ( final String code ) {
        this.code = code;
    }

}
