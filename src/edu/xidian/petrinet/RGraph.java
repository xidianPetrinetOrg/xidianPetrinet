package edu.xidian.petrinet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.Edge;
import de.uni.freiburg.iig.telematik.jagal.graph.Graph;
import de.uni.freiburg.iig.telematik.jagal.graph.Vertex;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;

//public class RGraph<T extends Object> extends AbstractGraph<Vertex<T>, Edge<Vertex<T>>, T> {
public class RGraph<T extends Object> extends Graph<T> {
	/**
	 * 以edge的名字索引"边"
	 */
	protected final Map<String, Edge<Vertex<T>>> mapEdges = new HashMap<>();
	public RGraph(){
		super();
	}
	
	public RGraph(String name) throws ParameterException{
		super(name);
	}
	
	public RGraph(Collection<String> vertexNames) throws ParameterException{
		super(vertexNames);
	}
	
	public RGraph(String name, Collection<String> vertexNames) throws ParameterException{
		super(name, vertexNames);
	}
	
	/**
	 * Adds a new edge between two vertexes with the given names to the
	 * graph.
	 *
	 * @param sourceVertexName The name of the edge.
	 * @param sourceVertexName The name of the source vertex.
	 * @param targetVertexName The name of the target vertex.
	 * @return The newly created edge or<br>
	 * <code>null</code> if the graph already contains an edge between the
	 * given source and target vertexes.
	 * @throws VertexNotFoundException If the graph does not contain
	 * vertexes that are equal to the given source and target vertexes.
	 */
	public Edge<Vertex<T>> addEdge(String edgeName, String sourceVertexName, String targetVertexName) throws VertexNotFoundException {
		Edge<Vertex<T>> e = super.addEdge(sourceVertexName, targetVertexName);
		mapEdges.put(edgeName, e); 
		return super.addEdge(sourceVertexName, targetVertexName);
	}
	
	@Override
	public String toString() {
		return String.format(toStringFormat, name,
			Arrays.toString(vertexMap.keySet().toArray()),"") + 
				mapEdges;
			//Arrays.toString(getEdges().toArray()));
		
	}
}
