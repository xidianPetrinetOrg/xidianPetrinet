package edu.xidian.petrinet.S3PR.RGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.invation.code.toval.validate.ParameterException;
import de.uni.freiburg.iig.telematik.jagal.graph.Vertex;
import de.uni.freiburg.iig.telematik.jagal.graph.abstr.AbstractGraph;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.EdgeNotFoundException;
import de.uni.freiburg.iig.telematik.jagal.graph.exception.VertexNotFoundException;
import de.uni.freiburg.iig.telematik.jagal.traverse.algorithms.SCCTarjan;
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
	protected final Collection<REdge> parallelEdges = new HashSet<>();
	//protected final List<REdge> parallelEdges = new HashList<>();
	
	public RGraph(){
		super();
	}
	
	public RGraph(String name) throws ParameterException{
		super(name);
	}
	
	@Override
	public RGraph clone(){
		RGraph cloneGraph = new RGraph();
		// clone Vertexes
		for (Vertex<PTPlace> v: getVertices()) {
			cloneGraph.addVertex(v.getName(), v.getElement());	
		}
		
		// clone edges, 包含平行边
		Collection<REdge> edges = new HashSet<>(getEdges());
		edges.addAll(getParallelEdges());
		for (REdge edge: edges) {
			try {
				cloneGraph.addREdge(edge.getName(), edge.getSource().getName(), edge.getTarget().getName());
			} catch (VertexNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return cloneGraph;
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
	 * Adds a new edge between two vertexes with the given names to the graph.
	 * 平行边被记录
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
	public Collection<REdge> getParallelEdges() {
		return parallelEdges;
	}
	
	/**
	 * 计算强连通块
	 * @param verbose 是否打印输出
	 * @return  资源有向图中的强连通块（含平行边）集合
	 */
	public Collection<RGraph> getStronglyConnectedComponentGraphs(boolean verbose) {
		Collection<RGraph> ComGraphs = new HashSet<>();
		
		Set<Set<Vertex<PTPlace>>> coms = null;
		SCCTarjan<Vertex<PTPlace>> tarjan = new SCCTarjan<>();
		coms = tarjan.execute(this); // 强连通分量
		
		// 构造连通分量的RGraph元素
		// 顶点集
		int i = 1;
		for (Set<Vertex<PTPlace>> vs: coms) {
			RGraph com = new RGraph("ComponentGraph(" + i + ")");
			for (Vertex<PTPlace> v: vs) {
				com.addVertex(v.getName(),v.getElement());
			}
			// 边集
			for (REdge edge: this.getEdges()) {
				Vertex<PTPlace> s = edge.getSource();
				Vertex<PTPlace> t = edge.getTarget();
				if (vs.contains(s) && vs.contains(t)) {
					try {
						com.addREdge(edge.getName(), s.getName(), t.getName());
					} catch (VertexNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// 平行边
			for (REdge edge: parallelEdges) { 
				Vertex<PTPlace> s = edge.getSource();
				Vertex<PTPlace> t = edge.getTarget();
				if (vs.contains(s) && vs.contains(t)) {
					com.parallelEdges.add(edge);
				}
			}
				
			ComGraphs.add(com);
			i++;
		}
		
		if (verbose) {
			System.out.println("Strongly Connected Components:");
			i = 1;
			for(RGraph com: ComGraphs) {
				System.out.println("Component[" + i + "]: " + com);
				i++;
			}
		}
		return ComGraphs;
	}
	
	/**
	 * 删除符合条件的内部边和平行边。
	 * 父类的removeEdge(String sourceVertexName, String targetVertexName)，不会删除平行边
	 * @throws VertexNotFoundException 
	 */
	public void removeEdges(String sourceVertexName, String targetVertexName) throws VertexNotFoundException, EdgeNotFoundException {
		if (!containsVertex(sourceVertexName)) {
			throw new VertexNotFoundException(sourceVertexName, this);
		}
		if (!containsVertex(targetVertexName)) {
			throw new VertexNotFoundException(targetVertexName, this);
		}
		// 平行边中找
		for (REdge edge: getParallelEdges()) {
			if (edge.getSource().getName().equals(sourceVertexName) &&
			    edge.getTarget().getName().equals(targetVertexName)) {
				parallelEdges.remove(edge);
			}
		}
		// 内部边
		super.removeEdge(sourceVertexName, targetVertexName);
	}
	
	/**
	 * 按名字删除边名字在整个边集合中（包括内部边和平行边）是唯一的。
	 * 父类的removeEdge(String sourceVertexName, String targetVertexName)，不会删除平行边
	 * @throws VertexNotFoundException, EdgeNotFoundException 
	 */
	public void removeEdge(String edgeName) throws VertexNotFoundException, EdgeNotFoundException {
		REdge edge = getEdge(edgeName);
		if (getParallelEdges().contains(edge)) {
			parallelEdges.remove(edge);
			return; // 是平行边，就不会是内部边
		}
		if (edge != null ) {
			removeEdge(edge); // 删除内部边，有可能抛出异常VertexNotFoundException
			return;
		}
		// 没有这样的边
		throw new EdgeNotFoundException(edge,this);
	}
	
	/**
	 * 按名字找边，名字在整个边集合中（包括内部边和平行边）是唯一的。
	 * @return 边对象，如果没有符合条件的边，返回null
	 */
	public REdge getEdge(String edgeName) {
		REdge result = null;
		for (REdge edge: getEdges()) {
			if (edge.getName().equals(edgeName)) {
				return edge; // 在内部边，就不可能在平行边
			}
		}
		// 平行边中找
		for (REdge edge: getParallelEdges()) {
			if (edge.getName().equals(edgeName)) {
				result = edge;
				break;
			}
		}
		return result;
	}
	
	/**
	 * @return 边集合，包含符合条件的内部边和平行边。
	 * @throws EdgeNotFoundException 
	 * @throws VertexNotFoundException 
	 */
	public Collection<REdge> getEdges(String sourceVertexName, String targetVertexName) throws VertexNotFoundException, EdgeNotFoundException {
		Collection<REdge> edges = new HashSet<>();
		edges.add(getEdge(sourceVertexName, targetVertexName));
		// 平行边中找
		for (REdge edge: getParallelEdges()) {
			if (edge.getSource().getName().equals(sourceVertexName) &&
			    edge.getTarget().getName().equals(targetVertexName)) {
				edges.add(edge);
		        // break; // 可能有多个
			}
		}
		return edges;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Graph: " + name + "\n");
		
		str.append("Vertices(" + vertexMap.size() + "):\n");
		List<PTPlace> places = new ArrayList<>();
		for (Vertex<PTPlace> v: getVertices()) {
			// 如果此Graph中无顶点元素，顶点和边按照父类中的描述，按照顶点名称输出
			if (v.getElement() == null) return super.toString();
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
