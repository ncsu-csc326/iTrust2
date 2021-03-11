package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Email extends DomainObject {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    @NotNull
    private String sender;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "receiver", columnDefinition = "varchar(100)" )
    private User   receiver;

    @NotNull
    private String subject;

    @NotNull
    private String messageBody;

    public Email ( final String sender, final User receiver, final String subject, final String messageBody ) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.messageBody = messageBody;
    }

    public Email () {

    }

    public Long getId () {
        return id;
    }

    public String getSender () {
        return sender;
    }

    public void setSender ( final String sender ) {
        this.sender = sender;
    }

    public User getReceiver () {
        return receiver;
    }

    public void setReceiver ( final User receiver ) {
        this.receiver = receiver;
    }

    public String getSubject () {
        return subject;
    }

    public void setSubject ( final String subject ) {
        this.subject = subject;
    }

    public String getMessageBody () {
        return messageBody;
    }

    public void setMessageBody ( final String messageBody ) {
        this.messageBody = messageBody;
    }

}
