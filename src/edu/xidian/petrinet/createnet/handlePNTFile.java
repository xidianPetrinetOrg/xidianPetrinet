package edu.xidian.petrinet.createnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*
 * p1 
   3 
   t2 p1 1 t4 p1 1  
   p1 t1 1 p1 t3 1 
 * */
import java.util.Set;

import javax.swing.plaf.metal.MetalIconFactory.PaletteCloseIcon;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.traversal.PNTraversalUtils;
import edu.xidian.petrinet.CreatePetriNet;
import edu.xidian.petrinet.S3PR.S2PR;
import edu.xidian.petrinet.graph.PTNetGraphComponent;

public class handlePNTFile extends S2PR{
	// 抓取文件中的库所，存入place
	private static ArrayList<String> place = new ArrayList<String>();
	// 抓取文件中的变迁，存入transition
	private static Set<String> transition = new HashSet<String>();
	// 抓取文件中的初始token,存入Marking
	private static ArrayList<Integer> Marking = new ArrayList<Integer>();
	// 抓取文件中的T-->P 存入TP
	private static ArrayList<String> TP = new ArrayList<String>();
	// 抓取文件中的P-->T 存入PT
	private static ArrayList<String> PT = new ArrayList<String>();

	static PTNet ptNet = null;

	private static final String toStringFormat = "Current Net IncidenceMatrix: %s %n ";
	
	// j 用来记录行数的辅助数字
	private static int j = 0;
	private static int m = 0;
	// 用str_2和最后一行读取的字符串进行比较,此处默认的处理机制是：最后一行的只有一个字符@ 且位于第一个位置。
	private static String str_2 = "@";
	private static final String StringBuffer = null;

	// 计算关联矩阵：
	// 1 的辅助数组tpArray：
	public ArrayList<Integer> tpArray = new ArrayList<>();
	// 2的辅助数组ptArray：
	public ArrayList<Integer> ptArray = new ArrayList<>();

	protected static int[][] IncidenceMatrix = null;

	// private static RandomTraversalTest3 randomTraversalTest3 = new
	// RandomTraversalTest3();
	private ArrayList<PTNetTraversalStepInfo> aList;
	private static DisplayFrame DPF;

	public DisplayFrame getDP() {

		return DPF;
	}

	public ArrayList<PTNetTraversalStepInfo> getaList() {
		return aList;
	}

	public void readTxtFile(String filePath) {
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				while ((lineText = bufferedReader.readLine()) != null) {
					if (m == 0 || lineText.equals(str_2)) {
						// Do nothing! 对第一行和最后一行不进行处理！
					} else {
						// 抓取中间行的数据！
						// System.out.println("file exist");
						String str1 = lineText;
						System.out.println(str1);
						String[] string2 = str1.split(",");
						String string3 = string2[0].replaceAll(" +", ",");
						String string4 = string2[1].replaceAll(" +", ",");

						String[] sq = string3.split(",");
						String[] sq1 = string4.split(",");

						// 1 逗号的前半部分的数据分析
						for (int i = 0; i < sq.length; i++) {
							if (i == 0) {
								
								place.add(getP_prefix()+Integer.valueOf(sq[i]));
								//place.add(Place[Integer.valueOf(sq[i]) - 1]);
							} else if (i == 1) {
								Marking.add(Integer.valueOf(sq[i]));
							} else {
								TP.add(getT_prefix()+sq[i]);
								//TP.add(Transition[Integer.valueOf(sq[i]) - 1]);
								// tpArray是为了获取数字比如 ：t4 获取 4
								tpArray.add(Integer.valueOf(sq[i]));
								transition.add(getT_prefix()+sq[i]);
								//transition.add(Transition[Integer.valueOf(sq[i]) - 1]);
								TP.add(place.get(j));
								// tpArray是为了获取数字比如 ：p1 获取 1
								tpArray.add(j + 1);
								TP.add(String.valueOf(1));
							}
						}
						// 2 逗号的后半部分的分析
						for (int i = 0; i < sq1.length; i++) {
							PT.add(place.get(j));
							// tpArray是为了获取数字比如 ：p1 获取 1
							ptArray.add(j + 1);
							TP.add(getT_prefix()+sq[i]);
							//PT.add(Transition[Integer.valueOf(sq1[i]) - 1]);
							// ptArray是为了获取数字比如 ：t4 获取 4
							ptArray.add(Integer.valueOf(sq[i]));
							transition.add(getT_prefix()+sq1[i]);
							//transition.add(Transition[Integer.valueOf(sq1[i]) - 1]);
							PT.add(String.valueOf(1));
						}
						j++;
					} // if语句结束
					m++;
				}
				// 关闭读入流
				read.close();

				// 测试抓取的数据准确性：
				// System.out.println("遍历库所：");
				// bianli(place);
				equipmentPlaceTool(place);

				// System.out.println("遍历变迁：");
				// bianliTransition(transition);
				equipmentTransitionTool(transition);

				// System.out.println("遍历TP：");
				// bianli(TP);
				equipmenTransitionToPlace(TP);

				// System.out.println("遍历PT：");
				// bianli(PT);
				equipmentPlaceToTransition(PT);

				System.out.println("遍历初始marking");
				bianliMarking(Marking);
				PTMarking marking = new PTMarking();
				equipmentInitMarkingTools(marking, place, Marking);

				ptNet.setInitialMarking(marking);

				// 随机遍历一个Petri网
				RandomTraversal.RandomPTTraverserTest(ptNet);
				// 获取遍历后的信息
				aList = RandomTraversal.getPtNetTraversalStepInfos();

				System.out.println(aList.size());

				// 此处为了传递DPF到主线程方便，不采用多线程进行值的传递，具体的线程间的对象或者值的传递机制
				// 知识还有欠缺，刚开始用常规的方法进行多线程间的值DPF传递，一直报空指针异常，调试了好久，发现是
				// 多线程之间的值或者对象的传递不是那么简单。所有去掉了多线程的机制。
				PTNetGraphComponent component = new PTNetGraphComponent(ptNet);
				try {
					component.initialize(); // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
				} catch (Exception e) {
					e.printStackTrace();
				}
				DPF = new DisplayFrame(component, true); // 显示图形元素
				// DP.dispose();

				/*
				 * javax.swing.SwingUtilities.invokeLater(new Runnable() {
				 * public void run() { PTNetGraphComponent component = new
				 * PTNetGraphComponent(ptNet); try { component.initialize(); //
				 * 初始化，由petriNet信息，装配visualGraph,图形元素中心布局 } catch (Exception e)
				 * { e.printStackTrace(); } DPF = new
				 * DisplayFrame(component,true); // 显示图形元素 //DP.dispose(); } });
				 */
			} else {
				// ActionHandle3 actionHandle3 = new ActionHandle3();
				ActionHandle.info_Lable1.setText("<html>您输入的路径有误或者不存在！<br/>请点击Clear后重新输入！</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public void readTxtFile1(String filePath) {
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				while ((lineText = bufferedReader.readLine()) != null) {
					if (m == 0 || lineText.equals(str_2)) {
						// Do nothing! 对第一行和最后一行不进行处理！
					} else {
						// 抓取中间行的数据！
						// System.out.println("file exist");
						String str1 = lineText;
						System.out.println(str1);
						String[] string2 = str1.split(",");
						String string3 = string2[0].replaceAll(" +", ",");
						String string4 = string2[1].replaceAll(" +", ",");

						String[] sq = string3.split(",");
						String[] sq1 = string4.split(",");

						// 1 逗号的前半部分的数据分析
						for (int i = 0; i < sq.length; i++) {
							if (i == 0) {
								place.add(getP_prefix()+sq[i]);
								//place.add(Place[Integer.valueOf(sq[i]) - 1]);
							} else if (i == 1) {
								Marking.add(Integer.valueOf(sq[i]));
							} else {
								TP.add(getT_prefix()+sq[i]);
								//TP.add(Transition[Integer.valueOf(sq[i]) - 1]);
								// tpArray是为了获取数字比如 ：t4 获取 4
								tpArray.add(Integer.valueOf(sq[i]));
								transition.add(getT_prefix()+sq[i]);
								//transition.add(Transition[Integer.valueOf(sq[i]) - 1]);
								TP.add(place.get(j));
								// tpArray是为了获取数字比如 ：p1 获取 1
								tpArray.add(j + 1);
								TP.add(String.valueOf(1));
							}
						}
						// 2 逗号的后半部分的分析
						for (int i = 0; i < sq1.length; i++) {
							PT.add(place.get(j));
							// tpArray是为了获取数字比如 ：p1 获取 1
							ptArray.add(j + 1);
							PT.add(getT_prefix()+sq1[i]);
							//PT.add(Transition[Integer.valueOf(sq1[i]) - 1]);
							// ptArray是为了获取数字比如 ：t4 获取 4
							ptArray.add(Integer.valueOf(sq1[i]));
							transition.add(getT_prefix()+sq1[i]);
							//transition.add(Transition[Integer.valueOf(sq1[i]) - 1]);
							PT.add(String.valueOf(1));
						}
						j++;
					} // if语句结束
					m++;
				}
				// 关闭读入流
				read.close();

				// 装配完成以后，进行计算关联矩阵：
				System.out.println("库所个数：" + place.size());
				System.out.println("变迁个数：" + transition.size());
				IncidenceMatrix = new int[place.size()][transition.size()];
				calculateIncidenceMatrix();
				print();

				// 测试抓取的数据准确性：
				// System.out.println("遍历库所：");
				// bianli(place);
				equipmentPlaceTool(place);

				// System.out.println("遍历变迁：");
				// bianliTransition(transition);
				equipmentTransitionTool(transition);

				System.out.println("遍历TP：");
				bianli(TP);
				System.out.println("遍历tpArray：");
				bianli2(tpArray);
				equipmenTransitionToPlace(TP);

				System.out.println("遍历PT：");
				bianli(PT);
				System.out.println("遍历ptArray：");
				bianli2(ptArray);
				equipmentPlaceToTransition(PT);

				System.out.println("遍历初始marking");
				bianliMarking(Marking);
				PTMarking marking = new PTMarking();
				equipmentInitMarkingTools(marking, place, Marking);

				ptNet.setInitialMarking(marking);

				
				//此时应该是替换原先的框图中的图形。。。
				
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// createAndShowGUI(ptnetGraphComponent); //
						// 显示PTNet对应的图形
						PTNetGraph.createAndShowGUI(ptNet); // 显示PTNet
					}
				});
			} else {
				// ActionHandle3 actionHandle3 = new ActionHandle3();
				ActionHandle.info_Lable1.setText("<html>您输入的路径有误或者不存在！<br/>请点击Clear后重新输入！</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public static void bianli(ArrayList<String> str) {
		System.out.println(str.size());
		for (int i = 0; i < str.size(); i++) {
			System.out.print(str.get(i) + " ");
		}
		System.out.println("");
	}
/*
	public static void bianli2(ArrayList<Integer> str) {
		System.out.println(str.size());
		for (int i = 0; i < str.size(); i++) {
			System.out.print(str.get(i) + " ");
		}
		System.out.println("");
	}*/

	public static void bianliTransition(Set<String> transition) {
		List<String> t = new ArrayList<String>(transition);
		for (int i = 0; i < t.size(); i++) {
			System.out.print(t.get(i) + " ");
		}
		System.out.println("");
	}

	public static void bianliMarking(ArrayList<Integer> str) {
		for (int i = 0; i < str.size(); i++) {
			System.out.print(str.get(i) + " ");
		}
		System.out.println("");
	}

	// 将Place存入到PTNET中:
	public void equipmentPlaceTool(List<String> place) {
		if (ptNet == null) {
			ptNet = new PTNet();
		}
		Iterator<String> it = place.iterator();
		while (it.hasNext()) {
			ptNet.addPlace(it.next());
		}
	}

	// 将transition 存入到PTNET中:
	public void equipmentTransitionTool(Set<String> transition) {
		if (ptNet == null) {
			ptNet = new PTNet();
		}
		Iterator<String> it = transition.iterator();
		while (it.hasNext()) {
			ptNet.addTransition(it.next());
		}
	}

	public void equipmenTransitionToPlace(ArrayList<String> TP) {
		int len1 = TP.size() / 3;
		String tp_array[][] = new String[len1][3];
		int k1 = 0;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < 3; j++) {
				tp_array[i][j] = TP.get(k1);
				k1++;
			}
		}
		for (int i = 0; i < tp_array.length; i++) {
			ptNet.addFlowRelationTP(tp_array[i][0], tp_array[i][1], Integer.valueOf(tp_array[i][2]));
		}
	}

	public void equipmentPlaceToTransition(ArrayList<String> PT) {
		int len = PT.size() / 3;
		String pt_array[][] = new String[len][3];
		int k = 0;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < 3; j++) {
				pt_array[i][j] = PT.get(k);
				k++;
			}
		}
		for (int i = 0; i < pt_array.length; i++) {
			ptNet.addFlowRelationPT(pt_array[i][0], pt_array[i][1], Integer.valueOf(pt_array[i][2]));
		}
	}

	public void equipmentInitMarkingTools(PTMarking marking, ArrayList<String> place, ArrayList<Integer> Marking) {
		for (int i = 0; i < place.size(); i++) {
			marking.set(place.get(i), Marking.get(i));
		}
	}

	public int[][] getIncidenceMatrix() {
		return IncidenceMatrix;
	}

	public void setIncidenceMatrix(int[][] incidenceMatrix) {
		IncidenceMatrix = incidenceMatrix;
	}

	public void calculateIncidenceMatrix() {
		for (int i = 0; i < tpArray.size(); i++) {
			if (((i + 1) % 2) == 0) {
				int col = tpArray.get(i - 1) - 1;
				int row = tpArray.get(i) - 1;
				IncidenceMatrix[row][col] = 1;
			}
		}
		for (int i = 0; i < ptArray.size(); i++) {
			if (((i + 1) % 2) == 0) {
				int col = ptArray.get(i) - 1;
				int row = ptArray.get(i - 1) - 1;
				IncidenceMatrix[row][col] = -1;
			}
		}

	}

	public static Map<String, StringBuffer> R = new HashMap<String, StringBuffer>();

	// 打印关联矩阵：
	public static void print() {
		System.out.println("Current Net IncidenceMatrix :");
		// 如果矩阵为0，说明矩阵不是从pnt文件生成的网；则，是特殊的网生成的结构；此时，需要从特殊的网
		// 的结构处获取生成的关联矩阵。
		if (IncidenceMatrix == null) {
			int row = CreatePetriNet.IncMatObjectNet.length;
			int col = CreatePetriNet.IncMatObjectNet[0].length;
			System.out.println("row = " + row + "\n" + "col = " + col);
			IncidenceMatrix = new int[row][col];
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					IncidenceMatrix[i][j] = CreatePetriNet.IncMatObjectNet[i][j];
				}
			}

		}

		for (int i = 0; i < IncidenceMatrix.length; i++) {
			StringBuffer stringBuffer = new StringBuffer();
			System.out.print("[   ");
			stringBuffer.append(" [");
			for (int j = 0; j < IncidenceMatrix[0].length; j++) {
				if (IncidenceMatrix[i][j] == -1) {
					System.out.print(IncidenceMatrix[i][j] + "   ");
					stringBuffer.append(IncidenceMatrix[i][j] + "   ");
				} else {
					System.out.print(IncidenceMatrix[i][j] + "    ");
					stringBuffer.append(IncidenceMatrix[i][j] + "    ");
				}
			}
			System.out.print("]");
			stringBuffer.append("]");

			System.out.println();
			R.put(i + "", stringBuffer);
		}
		System.out.println();
	}

	public String toString1() {
		StringBuilder relationBuilder = new StringBuilder();
		relationBuilder.append("\n");
		for (StringBuffer relation : R.values()) {
			relationBuilder.append("");
			relationBuilder.append(relation);
			relationBuilder.append('\n');
		}
		return String.format(toStringFormat, relationBuilder.toString());
	}

	// 变迁序列生成网：
	public static String Traversal() {
		String sequence = null;
		if (ptNet == null) {
			ptNet = CreatePetriNet.ptnet;
		}
		Collection<List<String>> testTraces = PNTraversalUtils.testTraces(ptNet, 10, 100, true, true, true);
		StringBuffer stringBuffer = new StringBuffer("Complete Sequence(to the fianl State):" + "\n");
		for (List<String> list : testTraces) {
			stringBuffer.append("    " + list);
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}

	public PTNet readPntFile(String path) {
		
		try {
			String encoding = "GBK";
			File file = new File(path);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				while ((lineText = bufferedReader.readLine()) != null) {
					if (m == 0 || lineText.equals(str_2)) {
						// Do nothing! 对第一行和最后一行不进行处理！
					} else {
						// 抓取中间行的数据！
						// System.out.println("file exist");
						String str1 = lineText;
						System.out.println(str1);
						String[] string2 = str1.split(",");
						String string3 = string2[0].replaceAll(" +", ",");
						String string4 = string2[1].replaceAll(" +", ",");

						String[] sq = string3.split(",");
						String[] sq1 = string4.split(",");

						// 1 逗号的前半部分的数据分析
						for (int i = 0; i < sq.length; i++) {
							if (i == 0) {
								place.add(getP_prefix()+sq[i]);
							} else if (i == 1) {
								Marking.add(Integer.valueOf(sq[i]));
							} else {
								TP.add(getT_prefix()+sq[i]);
								// tpArray是为了获取数字比如 ：t4 获取 4
								tpArray.add(Integer.valueOf(sq[i]));
								transition.add(getT_prefix()+sq[i]);
								TP.add(place.get(j));
								// tpArray是为了获取数字比如 ：p1 获取 1
								tpArray.add(j + 1);
								TP.add(String.valueOf(1));
							}
						}
						// 2 逗号的后半部分的分析
						for (int i = 0; i < sq1.length; i++) {
							PT.add(place.get(j));
							// tpArray是为了获取数字比如 ：p1 获取 1
							ptArray.add(j + 1);
							PT.add(getT_prefix()+sq1[i]);
							// ptArray是为了获取数字比如 ：t4 获取 4
							ptArray.add(Integer.valueOf(sq1[i]));
							transition.add(getT_prefix()+sq1[i]);
							PT.add(String.valueOf(1));
						}
						j++;
					} // if语句结束
					m++;
				}
				// 关闭读入流
				read.close();

				// 装配完成以后，进行计算关联矩阵：
				System.out.println("库所个数：" + place.size());
				System.out.println("变迁个数：" + transition.size());
				IncidenceMatrix = new int[place.size()][transition.size()];
				calculateIncidenceMatrix();
				print();

				// 测试抓取的数据准确性：
				// System.out.println("遍历库所：");
				// bianli(place);
				equipmentPlaceTool(place);

				// System.out.println("遍历变迁：");
				// bianliTransition(transition);
				equipmentTransitionTool(transition);

				System.out.println("遍历TP：");
				bianli(TP);
				System.out.println("遍历tpArray：");
				//bianli2(tpArray);
				equipmenTransitionToPlace(TP);

				System.out.println("遍历PT：");
				bianli(PT);
				System.out.println("遍历ptArray：");
				//bianli2(ptArray);
				equipmentPlaceToTransition(PT);

				System.out.println("遍历初始marking");
				bianliMarking(Marking);
				PTMarking marking = new PTMarking();
				equipmentInitMarkingTools(marking, place, Marking);

				ptNet.setInitialMarking(marking);

				
				//此时应该是替换原先的框图中的图形。。。
				
				/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// createAndShowGUI(ptnetGraphComponent); //
						// 显示PTNet对应的图形
						PTNetGraph.createAndShowGUI(ptNet); // 显示PTNet
					}
				});*/
			} else {
				// ActionHandle3 actionHandle3 = new ActionHandle3();
				ActionHandle.info_Lable1.setText("<html>您输入的路径有误或者不存在！<br/>请点击Clear后重新输入！</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			}
		return ptNet;
	}

	public static void main(String[] args) {
		
	}
}
