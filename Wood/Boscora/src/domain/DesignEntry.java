package domain;

import java.util.Date;

/**
 * @author Fede
 *
 */
public class DesignEntry extends PersistenceObject {

	private String name;
	private Long seed;
	private Date addedDate;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the seed
	 */
	public Long getSeed() {
		return seed;
	}
	/**
	 * @param seed the seed to set
	 */
	public void setSeed(Long seed) {
		this.seed = seed;
	}
	/**
	 * @return the addedDate
	 */
	public Date getAddedDate() {
		return addedDate;
	}
	/**
	 * @param addedDate the addedDate to set
	 */
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

}
