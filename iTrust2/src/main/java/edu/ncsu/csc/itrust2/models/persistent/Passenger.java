package edu.ncsu.csc.itrust2.models.persistent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import edu.ncsu.csc.itrust2.forms.passenger.PassengerForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;

/**
 * Class representing a passenger in the COVID-19 outbreak scenario. Passengers
 * are a type of Patient with additional functionality extended.
 */
@Entity ( name = "Passenger" )
public class Passenger extends Patient {

    /**
     * Randomly generated id
     */
    private static final long serialVersionUID = 7361166996562783259L;

    /**
     * The Passenger's unique ID
     */
    @Column
    private String            passengerId;

    /**
     * The date that the Passenger first showed symptoms. If no symptoms, this
     * will be null
     */
    @Column
    @Convert ( converter = LocalDateTimeAttributeConverter.class )
    private LocalDateTime     initialSymptomArrival;

    /**
     * Severity level of the Passenger's symptoms
     */
    @Column
    @Enumerated ( EnumType.STRING )
    private SymptomSeverity   symptomSeverity;

    /**
     * List of IDs of Passengers this Passenger instance has come in direct
     * contact with
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    private List<String>      contacts;

    /**
     * Constructs the Passenger
     *
     * @param passengerId
     *            the Passenger's ID
     * @param name
     *            the name of the passenger
     * @param symptomSeverity
     *            symptom level of the Passenger
     * @param initialSymptomArrival
     *            date when the Passenger first showed symptoms
     * @param self
     *            user associated with passenger
     */
    public Passenger ( final String passengerId, final String name, final SymptomSeverity symptomSeverity,
            final LocalDateTime initialSymptomArrival, final User self ) {

        // create patient user first
        super( self );

        // check parameters are valid
        if ( passengerId == null || !passengerId.matches( "[a-zA-Z0-9]+" )
                || Passenger.getById( passengerId ) != null ) {
            throw new IllegalArgumentException( "Invalid Id." );
        }
        if ( name == null ) {
            throw new IllegalArgumentException( "Invalid First/Last name." );
        }
        if ( symptomSeverity == null ) {
            throw new IllegalArgumentException( "Invalid severity." );
        }
        if ( symptomSeverity == SymptomSeverity.NOT_INFECTED && initialSymptomArrival != null ) {
            throw new IllegalArgumentException( "A Not Infected Passenger should not have an Initial Symptom Date" );
        }
        if ( symptomSeverity != SymptomSeverity.NOT_INFECTED && initialSymptomArrival == null ) {
            throw new IllegalArgumentException( "Must specify initial Symptom Date" );
        }

        final String[] nameSplit = name.replace( ",", "" ).split( " " );
        final String lastName = nameSplit[0];

        String firstName = "";
        if ( nameSplit.length > 1 ) {
            for ( int n = 1; n < nameSplit.length; n++ ) {
                firstName = nameSplit[n] + " " + firstName;
            }
            firstName = firstName.trim();
        }
        this.setPassengerId( passengerId );
        if ( !firstName.equals( "" ) ) {
            this.setFirstName( firstName );
        }
        this.setLastName( lastName );
        this.setInitialSymptomArrival( initialSymptomArrival );
        this.setSymptomSeverity( symptomSeverity );

    }

    /**
     * Empty constructor necessary for Hibernate.
     */
    public Passenger () {
    }

    /**
     * Retrieves all the Passengers currently in the database
     *
     * @return list of all Passengers
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Passenger> getPassengers () {
        return (List<Passenger>) DomainObject.getAll( Passenger.class );

    }

    /**
     * Gets a map of passenger ID strings to their Passenger instances
     *
     * @return passenger id map
     */
    public static Map<String, Passenger> getPassengerIds () {
        final Map<String, Passenger> passengerIDs = new HashMap<String, Passenger>();
        final List<Passenger> passengers = getPassengers();
        for ( final Passenger p : passengers ) {
            passengerIDs.put( p.getPassengerId(), p );
        }

        return passengerIDs;
    }

    /**
     * Fetches the Passenger corresponding to the given passenger ID
     *
     * @param passengerId
     *            passenger's ID
     * @return the Passenger
     */
    @SuppressWarnings ( { "rawtypes" } )
    public static Passenger getById ( final String passengerId ) {
        final List<Criterion> criterionList = new ArrayList<Criterion>();
        final Criterion c = Restrictions.eq( "passengerId", passengerId );
        criterionList.add( c );

        final List< ? extends DomainObject> p = DomainObject.getWhere( Patient.class, criterionList );
        if ( p == null || p.size() != 1 ) {
            return null;
        }
        else {
            return (Passenger) p.get( 0 );
        }

    }

    /**
     * Parses Passengers from the given file.
     *
     * @param info
     *            string representation of CSV file containing Passenger data
     * @return integer array of [#successfullyAdded, #skipped]
     */
    public static int[] parse ( final String info ) {
        final int[] status = { 0, 0 };
        final int[] error = { 0, 0 };

        // check if string is valid
        if ( info == null || info.equals( "" ) || !info.contains( "," ) ) {
            return error; // (our way of saying "error")
        }

        final String linesplit = "\n";
        final String quotesplit = "\"";
        final String[] lines = info.split( linesplit );
        final List<User> newUsers = new ArrayList<User>();
        final List<Passenger> newPassengers = new ArrayList<Passenger>();
        final List<String> passengerids = new ArrayList<String>();
        for ( int i = 0; i < lines.length; i++ ) {
            final String[] dataLine = lines[i].split( quotesplit );
            if ( dataLine.length != 3 ) {
                return error;
            }

            // Get the Name
            final String name = dataLine[1];

            // Process Id
            dataLine[0] = dataLine[0].trim();
            if ( dataLine[0].equals( "" ) ) {
                return error;
            }
            // remove comma
            final String id = dataLine[0].substring( 0, dataLine[0].length() - 1 );

            // Check for Duplicates
            if ( passengerids.contains( id ) || Passenger.getById( id ) != null ) {
                status[1] += 1; // add to duplicate count
                continue;
            }
            // keep track of ids to make sure there are no duplicates
            passengerids.add( id );

            // Process Severity and Date
            final String[] severityAndDate = dataLine[2].split( "," );
            if ( severityAndDate.length < 2 || severityAndDate.length > 3 ) {
                return error;
            }
            final SymptomSeverity severity = SymptomSeverity.parse( severityAndDate[1].trim() ); // get
                                                                                                 // severity
                                                                                                 // enum

            LocalDateTime date = null;
            // For Those infected, load date
            if ( severity != SymptomSeverity.NOT_INFECTED ) {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy/MM/dd HH:mm:ss" );

                try {
                    date = LocalDateTime.parse( severityAndDate[2].trim(), formatter );
                }
                catch ( final Exception e ) {
                    return error;
                }

            }

            // Create User
            final User user = new User( id, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            newUsers.add( user );

            // Create Passenger
            Passenger p = null;
            try {
                p = new Passenger( id, name, severity, date, user );
                status[0] += 1; // increment added status
                newPassengers.add( p );
            }
            catch ( final IllegalArgumentException e ) {
                return error;
            }
        }

        // save the users
        User.saveAll( newUsers );

        // save the passengers

        Passenger.saveAll( newPassengers );

        return status;
    }

    /**
     * Parses Passenger contact lists from the given file.
     *
     * @param info
     *            string representation of a CSV file containing Passenger
     *            contact data. Leftmost column is the source passengerId and
     *            everything else on the row is the contact list
     * @return if successful or not
     */
    public static boolean parseContacts ( final String info ) {
        if ( info == null || info.equals( "" ) || !info.contains( "," ) ) {
            return false;
        }
        // store the original string and put it into a usable from from JSON
        String infoContacts = info;
        infoContacts = infoContacts.replaceAll( "\"", "" );
        infoContacts = infoContacts.replaceAll( "\\\\r\\\\n", "\n" );

        // split on newlines
        final String[] lines = infoContacts.split( "\n" );

        // check if string is valid

        // have a reference to all of the valid ids and their passengers
        final Map<String, Passenger> allPassengers = Passenger.getPassengerIds();
        final ArrayList<Passenger> trackedPassengers = new ArrayList<Passenger>();

        final String idsplit = ",";
        // iterate through each passenger
        for ( int i = 0; i < lines.length; i++ ) {
            // stores this particular passenger's contacts
            final List<Passenger> contacts = new ArrayList<Passenger>();

            // comma separated ids in the line
            String[] dataLine = lines[i].split( idsplit );

            dataLine = StringUtils.stripAll( dataLine );
            // no contact being tracked
            if ( dataLine.length == 0 ) {
                continue;
            }

            // check if the passenger being tracked is valid
            final Passenger rootPassenger = allPassengers.get( dataLine[0] );
            if ( rootPassenger == null ) {
                return false;
            }

            // iterates through the initial id's contacts
            for ( int p = 1; p < dataLine.length; p++ ) {

                // validate every contact ID
                if ( !allPassengers.containsKey( dataLine[p] ) ) {
                    return false;
                }

                if ( contacts.contains( allPassengers.get( dataLine[p] ) ) ) {
                    return false;
                }

                // build list of Passenger contacts
                contacts.add( allPassengers.get( dataLine[p] ) );
            }
            final List<String> contactsAsStrings = new ArrayList<>();
            for ( final Passenger c : contacts ) {
                contactsAsStrings.add( c.getPassengerId() );
            }
            rootPassenger.setContacts( contactsAsStrings );
            trackedPassengers.add( rootPassenger );
        }

        // saved contact lists
        Passenger.saveAll( trackedPassengers );
        return true;
    }

    /**
     * Performs an n-depth search of Passengers that have come in contact with a
     * specified source Passenger
     *
     * @param passengerId
     *            ID of the source Passenger
     * @param depth
     *            Depth of the search to perform
     * @return Map of levels (integers) to the list of Passengers corresponding
     *         to that level's contact list results
     */
    public static Map<Integer, List<PassengerForm>> getContactsByDepth ( final String passengerId, final int depth ) {
        // Test that the date is valid
        // Test that the depth is greater than or equal to one
        // Test that the passengerId is not null.
        if ( depth <= 0 || passengerId == null || passengerId.trim().length() == 0 ) {
            return null;
        }

        final Map<Integer, List<Passenger>> contactMap = new TreeMap<Integer, List<Passenger>>();
        // map of PassengerForms, only maintains data that is safe to return to
        // frontend
        final Map<Integer, List<PassengerForm>> contactFormMap = new TreeMap<>();
        final Set<Passenger> masterSet = new HashSet<Passenger>();

        final Map<String, Passenger> passengerIds = getPassengerIds();

        final Passenger root = passengerIds.get( passengerId.trim() );
        // See if the passenger does not exist - return null
        if ( root == null ) {
            return null;
        }

        // Now that it is valid, create our level zero.
        contactMap.put( 0, new ArrayList<Passenger>() );
        contactMap.get( 0 ).add( root );
        masterSet.add( root );

        for ( int i = 0; i < depth; i++ ) {
            final List<Passenger> iList = new ArrayList<Passenger>();
            // same as above with map of PassengerForm lists, only maintain data
            // safe to send to frontend
            final List<PassengerForm> iFormList = new ArrayList<>();

            for ( final Passenger p : contactMap.get( i ) ) {
                final List<String> contactStringList = p.getContacts();

                if ( contactStringList == null || contactStringList.size() == 0 ) {
                    continue;
                }

                for ( final String nextId : contactStringList ) {
                    final Passenger contact = passengerIds.get( nextId );
                    if ( masterSet.add( contact ) ) {
                        iList.add( contact );
                        iFormList.add( new PassengerForm( contact ) );
                    }
                }
            }

            contactMap.put( i + 1, iList );
            contactFormMap.put( i + 1, iFormList );
        }

        return contactFormMap;
    }

    /**
     * Retrieves the Passenger's ID
     *
     * @return the ID
     */
    public String getPassengerId () {
        return passengerId;
    }

    /**
     * Sets the Passenger's ID
     *
     * @param passengerId
     *            the ID
     */
    public void setPassengerId ( final String passengerId ) {
        this.passengerId = passengerId;
    }

    /**
     * Retrieves the Passenger's initial symptom date and time
     *
     * @return initial symptom date and time
     */
    public LocalDateTime getInitialSymptomArrival () {
        return initialSymptomArrival;
    }

    /**
     * Sets the date and time the Passenger first showed symptoms
     *
     * @param initialSymptomArrival
     *            the date and time
     */
    public void setInitialSymptomArrival ( final LocalDateTime initialSymptomArrival ) {
        this.initialSymptomArrival = initialSymptomArrival;
    }

    /**
     * Retrieves the Passenger's symptom severity level
     *
     * @return symptom severity level
     */
    public SymptomSeverity getSymptomSeverity () {
        return symptomSeverity;
    }

    /**
     * Sets the Passenger's symptom severity level
     *
     * @param symptomSeverity
     *            the symptom severity level
     */
    public void setSymptomSeverity ( final SymptomSeverity symptomSeverity ) {
        this.symptomSeverity = symptomSeverity;
    }

    /**
     * Retrieves a list of IDs for Passengers this Passenger instance has come
     * in direct contact with
     *
     * @return list of Strings
     */
    public List<String> getContacts () {
        return contacts;
    }

    /**
     * Sets the list of IDs for Passenger's this Passenger instance has come in
     * direct contact with
     *
     * @param contacts
     *            the list of Strings
     */
    public void setContacts ( final List<String> contacts ) {
        this.contacts = contacts;
    }

    /**
     * To String method
     *
     * @return String
     */
    @Override
    public String toString () {
        final LocalDateTime date = this.getInitialSymptomArrival();
        final String dateTime = date != null ? date.toString() : "";
        final String firstName = this.getFirstName() != null ? this.getFirstName() : "";
        final String lastName = this.getLastName() != null ? this.getLastName() : "";
        final String severity = this.getSymptomSeverity() != null ? this.getSymptomSeverity().getName() : "";
        final String id = this.passengerId != null ? this.passengerId : "";
        return id + " " + firstName + " " + lastName + " " + severity + " " + dateTime;
    }

    /**
     * Deletes all Passengers in the database.
     */
    public static void deleteAll () {
        DomainObject.deleteAll( Passenger.class );

    }

}
