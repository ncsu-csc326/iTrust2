package edu.ncsu.csc.iTrust2.models;

import java.io.Serializable;

/**
 * The root class for all of our persistent entities. Defines no fields or
 * methods, but is used to provide a common superclass that the `Service`
 * methods can use.
 *
 * @author Kai Presler-Marshall
 *
 */
abstract public class DomainObject {

    public abstract Serializable getId ();

}
