package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;

import edu.ncsu.csc.itrust2.utils.DomainObjectCache;
import edu.ncsu.csc.itrust2.utils.HibernateUtil;

/**
 * The common super-class for all database entities. This is done to centralize
 * the logic for database saves and retrieval. When using this class, you should
 * create an overridden getAll() method in the subclasses that passes in their
 * class type to the DomainObject.getAll(Class cls) method so that it can
 * perform the database retrieval.
 *
 * The DomainObject (this class) itself isn't persisted to the database, but it
 * is included here as it is the root class for all persistent classes.
 *
 * @author Kai Presler-Marshall
 *
 * @param <D>
 *            Subtype of DomainObject in question
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public abstract class DomainObject <D extends DomainObject<D>> {

    /**
     * Performs a getAll on the subtype of DomainObject in question. The
     * resulting list can then be streamed and filtered on any parameters
     * desired
     *
     * @param cls
     *            class to find DomainObjects for
     * @return A List of all records for the selected type.
     */
    protected static List< ? extends DomainObject> getAll ( final Class cls ) {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        final List< ? extends DomainObject< ? >> requests = session.createCriteria( cls ).list();
        session.getTransaction().commit();
        session.close();

        return requests;
    }

    /**
     * Retrieves a list of all matching DomainObject elements from the class
     * provided meeting the where clause provided.
     *
     * @param table
     *            The subclass of DomainObject to retrieve from
     * @param whereClause
     *            The valid (SQL) where clause to use for filtering results
     * @return A list of all matching elements
     */
    public static List< ? extends DomainObject> getWhere ( final Class table, final String whereClause ) {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        final List< ? extends DomainObject> requests = session
                .createQuery( "FROM " + table.getSimpleName() + " WHERE " + whereClause ).list();
        session.getTransaction().commit();
        session.close();

        return requests;
    }

    /**
     * Provides the ability to quickly delete all instances of the current
     * class. Useful for clearing out data for testing or regeneration.
     * Visibility is set to protected to force subclasses of DomainObject to
     * override this.
     *
     * @param cls
     *            class to delete instances of
     */
    public static void deleteAll ( final Class cls ) {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        final List<DomainObject> instances = session.createCriteria( cls ).list();
        for ( final DomainObject d : instances ) {
            session.delete( d );
        }
        session.getTransaction().commit();
        session.close();
        getCache( cls ).clear();
    }

    /**
     * Saves the DomainObject into the database. If the object instance does not
     * exist a new record will be created in the database. If the object already
     * exists in the DB, then the existing record will be updated.
     */
    public void save () {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate( this );
        session.getTransaction().commit();
        session.close();

        getCache( this.getClass() ).put( this.getId(), this );
    }

    /**
     * Deletes the selected DomainObject from the database. This is operation
     * cannot be reversed.
     */
    public void delete () {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete( this );
        session.getTransaction().commit();
        session.close();
        getCache( this.getClass() ).remove( this.getId() );
    }

    /**
     * Retrieves a specific instance of the DomainObject subtype by its
     * persistent ID
     *
     * @param cls
     *            class to retrieve instance of by id
     * @param id
     *            id of object
     * @return object with given id
     */
    public static DomainObject getById ( final Class cls, final Object id ) {
        DomainObject obj;
        try {
            obj = (DomainObject) cls.newInstance();
        }
        catch ( final Exception e ) {
            return null;
        }
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.load( obj, (Serializable) id );
        session.getTransaction().commit();
        session.close();
        getCache( cls ).put( obj.getId(), obj );
        return obj;
    }

    /**
     * Ability to get a DomainObject of any type by any field/value provided.
     * Will return null if the field is not valid for the DomainObject type or
     * no record exists with the value provided.
     *
     * @param cls
     *            class of DomainObject
     * @param field
     *            The field of the object (ie, name, id, etc) requested
     * @param value
     *            The value for the field in question
     * @return object associated with class and field
     */
    public static DomainObject getBy ( final Class cls, final String field, final String value ) {
        final List<Field> fields = Arrays.asList( cls.getDeclaredFields() );
        for ( final DomainObject d : getAll( cls ) ) {
            for ( final Field f : fields ) {
                f.setAccessible( true );
                try {
                    if ( f.get( d ).equals( value ) ) {
                        getCache( cls ).put( d.getId(), d );
                        return d;
                    }
                }
                catch ( final Exception e ) {
                    // Ignore exception
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the instance of the DomainObjectCache associated with the
     * subclass of DomainObject specified
     *
     * @param cls
     *            The subclass of DomainObject to retrieve a cache for
     * @return The cache found, or null if it does not exist.
     */
    protected static DomainObjectCache getCache ( final Class cls ) {
        return DomainObjectCache.getCacheByClass( cls );
    }

    /**
     * Retrieves the ID of the DomainObject. May be a numeric ID assigned by the
     * database or another primary key that is user-assigned
     *
     * @return ID of the DomainObject.
     */
    abstract public Serializable getId ();

}
