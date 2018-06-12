/**
 * 
 */
package design1;

import design1.shapeFunc.ShapeFunction;

/**
 * @author Fede
 *
 */
public class DesignerProperties {
	public Float lineWidth = 22.2f;
	
	public Integer highLineMinThickness = 27;
	public Integer highLineMaxThickness = 45;
	public Integer lowLineMinThickness = 10;
	public Integer lowLineMaxThickness = 23;
	
	private ShapeFunction shapeFunction;

	/**
	 * @return the highLineMinLength
	 */
	public Integer getHighLineMinLength(Integer index) {
		return shapeFunction.getMinForIndex(index);
	}

	/**
	 * @return the highLineMaxLength
	 */
	public Integer getHighLineMaxLength(Integer index) {
		return shapeFunction.getMaxForIndex(index);
	}

	/**
	 * @return the shapeFunction
	 */
	public ShapeFunction getShapeFunction() {
		return shapeFunction;
	}

	/**
	 * @param shapeFunction the shapeFunction to set
	 */
	public void setShapeFunction(ShapeFunction shapeFunction) {
		this.shapeFunction = shapeFunction;
	}
	
}
