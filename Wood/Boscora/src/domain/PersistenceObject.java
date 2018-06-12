/**
 * 
 */
package domain;

import utils.PersistenceManager;

/**
 * @author Fede
 *
 */
public class PersistenceObject {

	private Long id;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 
	 */
	public void save() {
		PersistenceManager.getSingleton().save(this);
	}
}
