/*
 *  This file is part of Golddigger.
 *
 *	[2010] Octahedron - All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Octahedron and its suppliers, if any.
 */

package br.octahedron.cotopaxi.cloudservice.common;

import br.octahedron.cotopaxi.cloudservice.DisabledMemcacheException;
import br.octahedron.cotopaxi.cloudservice.MemcacheFacade;
import br.octahedron.cotopaxi.inject.Inject;

/**
 * A decorator for {@link MemcacheFacade} that fails silently, it means, if some error occurs
 * accessing memcache it simple ignores.
 * 
 * @author Danilo Penna Queiroz - daniloqueiroz@octahedron.com.br
 * 
 */
public class QuietMemcacheFacade implements MemcacheFacade {

	@Inject
	private MemcacheFacade memcacheFacade;

	/**
	 * @param memcacheFacade the memcacheFacade to set
	 */
	public void setMemcacheFacade(MemcacheFacade memcacheFacade) {
		this.memcacheFacade = memcacheFacade;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.octahedron.cotopaxi.cloudservice.MemcacheFacade#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return this.memcacheFacade.contains(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.octahedron.cotopaxi.cloudservice.MemcacheFacade#get(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public <T> T get(Class<T> klass, String key) {
		try {
			return this.memcacheFacade.get(klass, key);
		} catch (DisabledMemcacheException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.octahedron.cotopaxi.cloudservice.MemcacheFacade#put(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public <T> void put(String key, T value) {
		try {
			this.memcacheFacade.put(key, value);
		} catch (DisabledMemcacheException e) {
			// nothing to do
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.octahedron.cotopaxi.cloudservice.MemcacheFacade#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		this.memcacheFacade.remove(key);
	}

}
