package edu.xidian.petrinet.RGraph;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.Vertex;
import de.uni.freiburg.iig.telematik.jagal.graph.abstr.AbstractGraph;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;

public class RGraph extends AbstractGraph<Vertex<PTPlace>, REdge, PTPlace> {
	
	public RGraph(){
		super();
	}
	
	public RGraph(String name) throws ParameterException{
		super(name);
	}
	
	@Override
	protected Vertex<PTPlace> createNewVertex(String name, PTPlace element) {
		return new Vertex<>(name, element);
	}

	/**
	 * 不能使用此函数创建资源有向图中的边！保留此函数，仅是为了兼容抽象的父类，因为父类中此函数的抽象函数，必须在子类中实现
	 */
	@Override
	protected REdge createNewEdge(Vertex<PTPlace> sourceVertex, Vertex<PTPlace> targetVertex) {
		try {
			throw new Exception("createNewEdge(),Donot use this fuction create the edge, using createNewREdge()!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new REdge("",sourceVertex, targetVertex);
	}
	
	protected REdge createNewREdge(String edgeName, Vertex<PTPlace> sourceVertex, Vertex<PTPlace> targetVertex) {
		return new REdge(edgeName,sourceVertex, targetVertex);
	}
	
	/**
	 * Adds a new edge between two vertexes with the given names to the
	 * graph.
	 *
	 * @param sourceVertexName The name of the source vertex.
	 * @param targetVertexName The name of the target vertex.
	 * @return The newly created edge or<br>
	 * <code>null</code> if the graph already contains an edge between the
	 * given source and target vertexes.
	 * @throws VertexNotFoundException If the graph does not contain
	 * vertexes that are equal to the given source and target vertexes.
	 */
	public REdge addREdge(String edgeName, String sourceVertexName, String targetVertexName) throws VertexNotFoundException {
		if (!containsVertex(sourceVertexName)) {
			throw new VertexNotFoundException(sourceVertexName, this);
		}
		if (!containsVertex(targetVertexName)) {
			throw new VertexNotFoundException(targetVertexName, this);
		}

		if (containsEdge(sourceVertexName, targetVertexName)) {
			return null;
		}

		REdge newEdge = createNewREdge(edgeName, getVertex(sourceVertexName), getVertex(targetVertexName));
		edgeList.add(newEdge);
		getEdgeContainer(sourceVertexName).addOutgoingEdge(newEdge);
		getEdgeContainer(targetVertexName).addIncomingEdge(newEdge);
		return newEdge;
	}

	
//	/**
//	 * Adds a new edge between two vertexes with the given names to the
//	 * graph.
//	 *
//	 * @param sourceVertexName The name of the edge.
//	 * @param sourceVertexName The name of the source vertex.
//	 * @param targetVertexName The name of the target vertex.
//	 * @return The newly created edge or<br>
//	 * <code>null</code> if the graph already contains an edge between the
//	 * given source and target vertexes.
//	 * @throws VertexNotFoundException If the graph does not contain
//	 * vertexes that are equal to the given source and target vertexes.
//	 */
//	public Edge<Vertex<PTPlace>> addEdge(String edgeName, String sourceVertexName, String targetVertexName) throws VertexNotFoundException {
//		Edge<Vertex<PTPlace>> e = super.addEdge(sourceVertexName, targetVertexName);
//		//mapEdges.put(edgeName, e); 
//		return super.addEdge(sourceVertexName, targetVertexName);
//	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Graph: " + name + "\n");
		//str.append("Vertex: " + vertexMap.keySet());
		System.out.println(vertexMap.values());
		str.append("Vertex: " + vertexMap.values());
		str.append("\nEdge:\n");
		for (REdge e: getEdges()) {
			str.append(e + "\n");
		}
		return str.toString();
	}

}
