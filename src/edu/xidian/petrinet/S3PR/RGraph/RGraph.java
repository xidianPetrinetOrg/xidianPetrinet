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
	 * 强联通分量也是一个RGraph，因此该函数被废弃，替代函数：getStronglyConnectedComponentGraphs()
	 * @param verbose 是否打印输出
	 * @return 资源有向图中的强连通块（含平行边）集合
	 */
	@Deprecated
	public Collection<Component> getStronglyConnectedComponents(boolean verbose) {	
		
		Collection<Component> Components = new HashSet<>();
		
		Set<Set<Vertex<PTPlace>>> coms = null;
		SCCTarjan<Vertex<PTPlace>> tarjan = new SCCTarjan<>();
		coms = tarjan.execute(this); // 强连通分量
		
		// 构造this.Components成员
		// 顶点集
		for (Set<Vertex<PTPlace>> vs: coms) {
			Component com = new Component();
			for (Vertex<PTPlace> v: vs) {
				com.vertexes.add(v.getElement());
			}
			Components.add(com);
		}
		// 边集
		for (REdge edge: this.getEdges()) {
			PTPlace s = edge.getSource().getElement();
			PTPlace t = edge.getTarget().getElement();
			for(Component com: Components) {
				if (com.vertexes.contains(s) && com.vertexes.contains(t)) {
					com.edges.add(edge.getName());
				}
			}
		}
		// 平行边
		for (REdge edge: parallelEdges) { 
			PTPlace s = edge.getSource().getElement();
			PTPlace t = edge.getTarget().getElement();
			for(Component com: Components) {
				if (com.vertexes.contains(s) && com.vertexes.contains(t)) {
					com.edges.add(edge.getName());
				}
			}
		}
		
		if (verbose) {
			System.out.println("Strongly Connected Components:");
			int i = 1;
			for(Component com: Components) {
				System.out.println("Component[" + i + "]: " + com);
				i++;
			}
		}
		return Components;
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
	
	/**
	 * 每个强连通分量的顶点集和边集（包含平行边）
	 * 强联通分量也是一个RGraph，因此该类被废弃
	 * @author Jiangtao Duan
	 *
	 */
	@Deprecated
	public class Component {
		/** 边的名字集合(含平行边) */
		public Collection<String> edges = new HashSet<>();
		/** 顶点集合  */
		public Collection<PTPlace> vertexes = new HashSet<>();
		
		@Override
		public String toString() {
			List<PTPlace> ps = new ArrayList<>(vertexes);
			List<String> es = new ArrayList<>(edges);
			Collections.sort(ps,new PNNodeComparator());
			Collections.sort(es,new StringComparator());
			return "[vertexes=" + vertexes + "]\n" + 
			       "[edges=" + edges + "]";
		}
	}

}
