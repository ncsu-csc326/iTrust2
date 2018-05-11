package edu.ncsu.csc.itrust2.controllers.api;

/**
 * Base class for all of the API controllers for manipulating DomainObjects. Add
 * in any fields or functionality that ought to be shared throughout.
 *
 * PLEASE NOTE: For (almost) all of the REST API endpoints that deal with the
 * creation or modification of DomainObject entities, we have opted to have the
 * API endpoint receive the appropriate *Form* object for each type instead of
 * the object itself. This is done because the entirely text-based forms are
 * easier for a client to generate rather than the complex object structure. For
 * instance, otherwise, when creating a new OfficeVisit, the JSON sent in by the
 * user would need to contain the _entire_ objects for the Patient and HCP
 * (including the username, hashed password, and status) as nested JSON. This
 * would be _very_ ugly and would make it much harder to use the API. Thus,
 * we're using _Forms_ for the API, where the user only has to provide the
 * _name_ (or other primary key) of a system resource that is presumed to
 * already exist. The JSON for the Form objects is automatically de-serialized
 * to a proper Java object by Spring, and then after that we manually convert
 * the Forms to proper entities before they can be saved in the database. This
 * allows proper validation of input and gets it into datastructures that use
 * proper, strong typing so that it is abundantly clear what is what.
 *
 * @author Kai Presler-Marshall
 *
 */
public abstract class APIController {
    /** Base path of API */
    static final protected String BASE_PATH = "/api/v1/";
}
