package edu.ncsu.csc.itrust2.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

import edu.ncsu.csc.itrust2.models.persistent.DomainObject;

/**
 * This DomainObjectCache is used to cache DomainObjects to avoid expensive
 * trips to the database
 *
 * @author Kai Presler-Marshall
 *
 * @param <K>
 *            Key of the cache. Likely to be a String but not necessarily.
 * @param <D>
 *            Subclass of DomainObject that is being stored in the cache.
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class DomainObjectCache <K extends Object, D extends DomainObject> {

    /**
     * How long items can live in the cache before being up for eviction
     */
    private long                                 ttl;

    /**
     * Map that stores the elements of the cache. When full, will evict the
     * least recently used.
     */
    private LRUMap                               cache;

    /** Name of this cache. Useful for debugging */
    @SuppressWarnings ( "unused" )
    private final String                         name;

    /**
     * How frequently to poll the map for elements that should be evicted
     */
    static private long                          timerInterval = 2;

    /**
     * Stores a mapping of all of the DomainObjectCaches that have been created.
     * This is used so we can have a single cleanup thread shared among all of
     * them and so that the DomainObjects can retrieve the cache and look up an
     * entry in it first.
     */
    static private Map<Class, DomainObjectCache> cacheByClass  = new LinkedHashMap<Class, DomainObjectCache>();

    /**
     * Retrieves the DomainObjectCache by the DomainObject class specified
     *
     * @param cls
     *            Class of the DomainObject to find a cache for
     * @return The Cache found
     */
    static public DomainObjectCache getCacheByClass ( final Class cls ) {
        return cacheByClass.get( cls );
    }

    /**
     * A CleanUp thread that is used to automatically clean out expired entries
     */
    static private Thread cleanupThread = new Thread( new Runnable() {
        @Override
        public void run () {
            for ( ;; ) {
                try {
                    Thread.sleep( timerInterval
                            * 1000 ); /* Convert to milliseconds */
                }
                catch ( final InterruptedException e ) {
                    // Exception ignored
                }
                for ( final DomainObjectCache c : cacheByClass.values() ) {
                    c.cleanup();
                }
            }
        }
    } );

    static {
        cleanupThread.setName( "DomainObjectCache_Cleanup" );
        cleanupThread.setPriority( Thread.MIN_PRIORITY );
        cleanupThread.setDaemon( true );
        cleanupThread.start();
    }

    /**
     * CacheObject stored in the cache. This is essentially just a wrapper that
     * preserves the DomainObject and the time that it was created to know when
     * to purge it
     *
     * @author Kai Presler-Marshall
     *
     */
    protected class CacheObject {
        /**
         * When this CacheObject was created
         */
        public long created;
        /**
         * The DomainObject being stored
         */
        public D    value;

        /**
         * Creates a CacheObject from the DomainObject provided
         *
         * @param value
         *            The DomainObject to make a CacheObject from
         */
        protected CacheObject ( final D value ) {
            this.created = System.currentTimeMillis();
            this.value = value;
        }
    }

    /**
     * Construct a DomainObjectCache with the default values. In a
     * non-distributed environment like this, we can afford a relatively lengthy
     * (1hr) time-to-live for cache entries Change this as appropriate if the
     * environment changes.
     *
     * @param cls
     *            class to create a cache of
     */
    public DomainObjectCache ( final Class cls ) {
        this( cls, 60 * 60, 50 );
    }

    /**
     * Constructor for the DomainObjectCache. Builds the map and starts up a
     * daemon thread to automatically clean out the cache at the specified
     * interval.
     *
     * @param cls
     *            Creates DomainObjectCache for class
     * @param ttl
     *            Time-to-live; how long each object should be allowed to remain
     *            in the cache
     * @param maxCapacity
     *            How many elements should be stored in the cache
     */
    public DomainObjectCache ( final Class cls, final long ttl, final int maxCapacity ) {
        this.ttl = 1000 * ttl; /* Convert to milliseconds */
        this.cache = new LRUMap( maxCapacity );
        this.name = "cache for [" + cls.getSimpleName() + "]";

        cacheByClass.put( cls, this );

    }

    /**
     * Empty the cache (remove all elements)
     */
    public void clear () {
        cache.clear();
    }

    /**
     * Puts the key-value pair in the cache.
     *
     * @param key
     *            lookup value
     * @param value
     *            value associated with the key
     */
    public void put ( final K key, final D value ) {
        synchronized ( cache ) {
            cache.put( key, new CacheObject( value ) );
        }
    }

    /**
     * Retrieves the DomainObject mapped by the key in question. Updates the
     * time-last-accessed to keep the object active in the cache
     *
     * @param key
     *            lookup value
     * @return value associated with key
     */
    public D get ( final K key ) {
        if ( null == key ) {
            return null;
        }
        D result = null;
        synchronized ( cache ) {
            final CacheObject co = (CacheObject) cache.get( key );
            if ( null == co ) {
                result = null;
            }
            else {
                result = co.value;
            }
        }
        return result;
    }

    /**
     * Remove the DomainObject mapped by the key in question
     *
     * @param key
     *            lookup value
     */
    public void remove ( final K key ) {
        synchronized ( cache ) {
            cache.remove( key );
        }
    }

    /**
     * Cleans the cache at the specified interval. Will remove all objects that
     * have not been accessed within the pre-specified time period in order to
     * keep the cache up-to-date.
     */
    private void cleanup () {
        final long now = System.currentTimeMillis();
        List<K> toRemove = null;
        synchronized ( cache ) {
            final MapIterator itr = cache.mapIterator();
            toRemove = new ArrayList<K>();
            CacheObject co = null;
            while ( itr.hasNext() ) {
                final K key = (K) itr.next();
                co = (CacheObject) itr.getValue();
                if ( null != co && ( now > ( ttl + co.created ) ) ) {
                    toRemove.add( key );
                }
            }
            for ( final K key : toRemove ) {
                cache.remove( key );
            }
        }

    }

}
