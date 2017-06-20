/**
 * 
 */
package edu.xidian.petrinet.S3PR.RGraph;

import de.uni.freiburg.iig.telematik.jagal.graph.Edge;
import de.uni.freiburg.iig.telematik.jagal.graph.Vertex;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;

/**
 * @author Administrator
 * @param <V>
 *
 */
public class REdge extends Edge<Vertex<PTPlace>> {
	/** this edge's name */
	protected String name = null;
	
	public REdge(String name) {
		super();
		this.name = name;
	}

	public REdge(String name, Vertex<PTPlace> source, Vertex<PTPlace> target) {
		super(source, target);
		this.name = name;
	}

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/**
	 * @see de.uni.freiburg.iig.telematik.jagal.graph.Edge#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		REdge other = (REdge) obj;
		if (source.getName() == null) {
			if (other.source.getName() != null)
				return false;
		} else if (!source.getName().equals(other.source.getName()))
			return false;
		if (target.getName() == null) {
			if (other.target.getName() != null)
				return false;
		} else if (!target.getName().equals(other.target.getName()))
			return false;
		return super.equals(obj);
	}

	/**
	 * @see de.uni.freiburg.iig.telematik.jagal.graph.Edge#clone()
	 */
	@Override
	public REdge clone() {
		return new REdge(name,source, target);
	}

	/* (non-Javadoc)
	 * @see de.uni.freiburg.iig.telematik.jagal.graph.Edge#toString()
	 */
	@Override
	public String toString() {
		PTPlace s = source.getElement();
		PTPlace t = target.getElement();
		try {
			if (s == null || t == null) {
				throw new Exception("Add vertex, plase use function add(name,source,target)");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name + "(" + s + " -> " + t + ")";
	}

}
