package edu.ncsu.csc.itrust2.controllers.api;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * This class provides GET endpoints for all of the Enums, so that they can be
 * used for creating proper DomainObjects
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
public class APIEnumController extends APIController {

    /**
     * Gets appointment types
     *
     * @return appointment types
     */
    @GetMapping ( BASE_PATH + "/appointmenttype" )
    public List<AppointmentType> getAppointmentTypes () {
        return Arrays.asList( AppointmentType.values() );
    }

    /**
     * Get the blood types
     *
     * @return blood types
     */
    @GetMapping ( BASE_PATH + "/bloodtype" )
    public List<BloodType> getBloodTypes () {
        return Arrays.asList( BloodType.values() );
    }

    /**
     * Get ethnicity
     *
     * @return ethnicity
     */
    @GetMapping ( BASE_PATH + "/ethnicity" )
    public List<Ethnicity> getEthnicity () {
        return Arrays.asList( Ethnicity.values() );
    }

    /**
     * Get genders
     *
     * @return genders
     */
    @GetMapping ( BASE_PATH + "/gender" )
    public List<Gender> getGenders () {
        return Arrays.asList( Gender.values() );
    }

    /**
     * Get states
     *
     * @return states
     */
    @GetMapping ( BASE_PATH + "/state" )
    public List<State> getStates () {
        return Arrays.asList( State.values() );
    }

    /**
     * Get house smoking statuses
     *
     * @return house smoking statuses
     */
    @GetMapping ( BASE_PATH + "/housesmoking" )
    public List<HouseholdSmokingStatus> getHouseSmokingStatuses () {
        final List<HouseholdSmokingStatus> ret = Arrays.asList( HouseholdSmokingStatus.values() ).subList( 1,
                HouseholdSmokingStatus.values().length );
        return ret;
    }

    /**
     * Get patient smoking statuses
     *
     * @return patient smoking statuses
     */
    @GetMapping ( BASE_PATH + "/patientsmoking" )
    public List<PatientSmokingStatus> getPatientSmokingStatuses () {
        final List<PatientSmokingStatus> ret = Arrays.asList( PatientSmokingStatus.values() ).subList( 1,
                PatientSmokingStatus.values().length );
        return ret;
    }

}
