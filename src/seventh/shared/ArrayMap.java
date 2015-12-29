/*
 * see license.txt 
 */
package seventh.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Tony
 *
 */
public class ArrayMap<K,V>  implements Map<K, V> {
	private static final int      MIN_HASH_CAPACITY = 2;
	
	private static final Object[] EMPTY_ARRAY = new Object[1];
	
	/** the hash keys */
	protected Object[] hashKeys;
	
	/** the hash values */
	protected Object[] hashValues;
	
	/** the number of hash entries */
	protected int hashEntries;
		
	
	/** Construct empty table */
	public ArrayMap() {		
		this(16);
	}
	
	/** 
	 * Construct table with preset capacity.
	 * @param nhash capacity of hash part
	 */
	public ArrayMap(int nhash) {		
		presize(nhash);
	}
	
	public void presize(int nhash) {
		if ( nhash > 0 && nhash < MIN_HASH_CAPACITY )
			nhash = MIN_HASH_CAPACITY;
		
		hashKeys = (nhash>0? new Object[nhash]: EMPTY_ARRAY);
		hashValues = (nhash>0? new Object[nhash]: EMPTY_ARRAY);
		hashEntries = 0;
	}


	
	@SuppressWarnings("unchecked")
	protected V hashget(K key) {
		if ( hashEntries > 0 ) {
			V v = (V)hashValues[hashFindSlot(key)];
			return v;
		}
		return null;
	}
	

	/** caller must ensure key is not nil */
	public void set( K key, V value ) {			
		rawset(key, value);
	}

	
	/** caller must ensure key is not nil */
	public void rawset( K key, V value ) {	
		hashset( key, value );
	}



	public int length() {
		return this.hashEntries;
	}


	private void error(String error) {
		throw new RuntimeException(error);
	}

	public int nexti( K key ) {
		int i = 0;
		do {
			// find current key index
			if ( key != null ) {
				
				if ( hashKeys.length == 0 )
					error( "invalid key to 'next'" );
				i = hashFindSlot(key);
				if ( hashKeys[i] == null )
					error( "invalid key to 'next'" );				
			}
		} while ( false );
		
		// check hash part
		for ( ; i<hashKeys.length; ++i )
			if ( hashKeys[i] != null )
				return i;
		
		// nothing found, push nil, return nil.
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public K getKey(int index) {
		return (K)hashKeys[index];
	}
	
	@SuppressWarnings("unchecked")
	public V getValue(int index) {
		return (V)hashValues[index];
	}
	
	@SuppressWarnings("unchecked")
	public K nextKey(K key) {
		int i = nexti(key);
		return (K)hashKeys[i];
	}
	
	@SuppressWarnings("unchecked")
	public V nextValue(K key) {
		int i = nexti(key);
		return (V) hashValues[i];
	}	
	

	/**
	 * Set a hashtable value
	 * @param key key to set
	 * @param value value to set
	 */
	@SuppressWarnings("unchecked")
	public V hashset(K key, V value) {
		V r = null;
		if ( value == null )
			r = hashRemove(key);
		else {
			if ( hashKeys.length == 0 ) {
				hashKeys = new Object[ MIN_HASH_CAPACITY ];
				hashValues = new Object[ MIN_HASH_CAPACITY ];
			}
			int slot = hashFindSlot( key );
			r = (V) hashValues[slot];
			
			if ( hashFillSlot( slot, value ) )
				return r;
			hashKeys[slot] = key;
			hashValues[slot] = value;
			if ( checkLoadFactor() )
				rehash();
		}
		
		return r;
	}
	
	/** 
	 * Find the hashtable slot to use
	 * @param key key to look for
	 * @return slot to use
	 */
	@SuppressWarnings("unchecked")
	public int hashFindSlot(K key) {		
		int i = ( key.hashCode() & 0x7FFFFFFF ) % hashKeys.length;
		
		// This loop is guaranteed to terminate as long as we never allow the
		// table to get 100% full.
		K k;
		while ( ( k = (K)hashKeys[i] ) != null && !k.equals(key) ) {
			i = ( i + 1 ) % hashKeys.length;
		}
		return i;
	}

	private boolean hashFillSlot( int slot, V value ) {
		hashValues[ slot ] = value;
		if ( hashKeys[ slot ] != null ) {
			return true;
		} else {
			++hashEntries;
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private V hashRemove( K key ) {
		V r = null;
		if ( hashKeys.length > 0 ) {
			int slot = hashFindSlot( key );
			r = (V)hashValues[slot];
			
			hashClearSlot( slot );
		}
		return r;
	}
	
	/**
	 * Clear a particular slot in the table
	 * @param i slot to clear.
	 */
	protected void hashClearSlot( int i ) {
		if ( hashKeys[ i ] != null ) {
			
			int j = i;
			int n = hashKeys.length; 
			while ( hashKeys[ j = ( ( j + 1 ) % n ) ] != null ) {
				final int k = ( ( hashKeys[ j ].hashCode() )& 0x7FFFFFFF ) % n;
				if ( ( j > i && ( k <= i || k > j ) ) ||
					 ( j < i && ( k <= i && k > j ) ) ) {
					hashKeys[ i ] = hashKeys[ j ];
					hashValues[ i ] = hashValues[ j ];
					i = j;
				}
			}
			
			--hashEntries;
			hashKeys[ i ] = null;
			hashValues[ i ] = null;
			
			if ( hashEntries == 0 ) {
				hashKeys = EMPTY_ARRAY;
				hashValues = EMPTY_ARRAY;
			}
		}
	}

	private boolean checkLoadFactor() {
		// Using a load factor of (n+1) >= 7/8 because that is easy to compute without
		// overflow or division.
		final int hashCapacity = hashKeys.length;
		return hashEntries >= (hashCapacity - (hashCapacity>>3));
	}

	@SuppressWarnings("unchecked")
	private void rehash() {
		final int oldCapacity = hashKeys.length;
		final int newCapacity = oldCapacity+(oldCapacity>>2)+MIN_HASH_CAPACITY;
		
		final Object[] oldKeys = hashKeys;
		final Object[] oldValues = hashValues;
		
		hashKeys = new Object[ newCapacity ];
		hashValues = new Object[ newCapacity ];
		
		for ( int i = 0; i < oldCapacity; ++i ) {
			final K k = (K) oldKeys[i];
			if ( k != null ) {
				final V v = (V) oldValues[i];
				final int slot = hashFindSlot( k );
				hashKeys[slot] = k;
				hashValues[slot] = v;
			}
		}
	}

	
	// equality w/ metatable processing	
	@SuppressWarnings("rawtypes")
	public boolean equal( Object val )  {
		if ( val == null ) return false;
		if ( this == val ) return true;
		if ( getClass() != val.getClass() ) return false;
		ArrayMap t = (ArrayMap)val;
		return  t.hashEntries==hashEntries && t.hashKeys.equals(hashKeys) && t.hashValues.equals(hashValues);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.hashEntries;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.hashEntries == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		int slot = hashFindSlot((K)key);		
		return this.hashValues[slot] != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value) {
		V val = (V)value;
		for(int i = 0; i < this.hashKeys.length; i++) {			
			if ( this.hashValues[i] != null) {
				if ( this.hashValues[i].equals(val) )
					return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return hashget((K)key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		return hashset(key, value);		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		return this.hashRemove((K)key);		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K,? extends V> m) {
		for(Map.Entry<? extends K,? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		for(int i = 0; i < this.hashKeys.length; i++) {
			this.hashKeys[i] = null;
			this.hashValues[i] = null;
		}
		this.hashEntries = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		Set<K> r = new HashSet<K>(this.hashEntries);
		for(int i = 0; i < this.hashKeys.length; i++) {
			if ( this.hashKeys[i] != null ) {
				r.add( (K) this.hashKeys[i]);
			}
		}
		return r;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		List<V> r = new ArrayList<V>(this.hashEntries);
		for(int i = 0; i < this.hashValues.length; i++) {
			if ( this.hashValues[i] != null ) {
				r.add( (V)this.hashValues[i]);
			}
		}
		return r;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}
	
}

