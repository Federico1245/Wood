/**
 * 
 */
package design1;

/**
 * @author Fede
 *
 */
public class Line {
	public Integer id;
	public Integer tableId;
	public Integer section;
	public Integer thinckness;
	public Integer length;
	public Boolean dark;
	
	public Line(Integer id, Integer thinckness, Integer length, Boolean dark) {
		this.id = id;
		this.thinckness = thinckness;
		this.length = length;
		this.dark = dark;
	}
	
}
