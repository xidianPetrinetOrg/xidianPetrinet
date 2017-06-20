package edu.xidian.petrinet.S3PR.RGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.Vertex;
import de.uni.freiburg.iig.telematik.jagal.graph.abstr.AbstractGraph;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.Utils.PNNodeComparator;
import edu.xidian.petrinet.Utils.StringComparator;

/**
 * 资源有向图，本身不含平行边.<br>
 * 可以使用函数{@link #getParallelEdges()}获取平行边
 * @author Jiangtao Duan
 *
 */
public class RGraph extends AbstractGraph<Vertex<PTPlace>, REdge, PTPlace> {
	
	/**
	 * 记录平行边
	 */
	protected final List<REdge> parallelEdges = new ArrayList<>();
	
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
	
	/**
	 * 内部调用，生成RGraph的edge
	 * @param edgeName
	 * @param sourceVertex
	 * @param targetVertex
	 * @return
	 */
	protected REdge createNewREdge(String edgeName, Vertex<PTPlace> sourceVertex, Vertex<PTPlace> targetVertex) {
		return new REdge(edgeName,sourceVertex, targetVertex);
	}
	
	/**
	 * Adds a new edge between two vertexes with the given names to the
	 * graph.
	 *
	 * @param edgeName The name of the edge.
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
			REdge pEdge = createNewREdge(edgeName, getVertex(sourceVertexName), getVertex(targetVertexName));
			parallelEdges.add(pEdge);
			return null;
		}

		REdge newEdge = createNewREdge(edgeName, getVertex(sourceVertexName), getVertex(targetVertexName));
		edgeList.add(newEdge);
		getEdgeContainer(sourceVertexName).addOutgoingEdge(newEdge);
		getEdgeContainer(targetVertexName).addIncomingEdge(newEdge);
		return newEdge;
	}
	
	/**
	 * @return the parallelEdges
	 */
	public List<REdge> getParallelEdges() {
		return parallelEdges;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Graph: " + name + "\n");
		
		str.append("Vertices(" + vertexMap.size() + "):\n");
		List<PTPlace> places = new ArrayList<>();
		for (Vertex<PTPlace> v: getVertices()) {
			places.add(v.getElement());
		}
		Collections.sort(places,new PNNodeComparator());
		str.append(places);
		
		// 不含平行边的集合
		str.append("\nEdges(" + edgeList.size() + "):\n");
		List<String> list = new ArrayList<>();
		for (REdge e: getEdges()) {
			list.add(e.toString());
		}
		Collections.sort(list,new StringComparator());
		for (String s: list) {
			str.append(s+"\n");
		}
		
		// 平行边集合
		list.clear();
		str.append("Parallel edges(" + parallelEdges.size() + "):\n");
		for (REdge e: parallelEdges) {
			list.add(e.toString());
		}
		Collections.sort(list,new StringComparator());
		for (String s: list) {
			str.append(s+"\n");
		}
		return str.toString();
	}

}
