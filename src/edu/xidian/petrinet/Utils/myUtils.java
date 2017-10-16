package edu.xidian.petrinet.Utils;
/*
* @author :wunan
* Time    :2017年5月28日 上午10:43:42
* Email   :2215225782@qq.com
*/

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.traversal.PNTraversalUtils;

public class myUtils {
	public static Map<String, StringBuffer> R = new HashMap<String,StringBuffer>();
	//打印关联矩阵：
	public static void print(int [][] IncidenceMatrix)
	{
		//System.out.println("Current Net IncidenceMatrix :");
		for(int i = 0; i < IncidenceMatrix.length;i++)
		{
			StringBuffer stringBuffer = new StringBuffer();
			System.out.print("[   ");
			stringBuffer.append(" [");
			for(int j = 0 ; j < IncidenceMatrix[0].length;j++)
			{
				if(IncidenceMatrix[i][j] == -1)
				{
					System.out.print(IncidenceMatrix[i][j]+ "   ");
					stringBuffer.append(IncidenceMatrix[i][j]+ "   ");
				}else{
					System.out.print(IncidenceMatrix[i][j]+ "    ");
					stringBuffer.append(IncidenceMatrix[i][j]+ "    ");
				}
			}
			System.out.print("]");
			stringBuffer.append("]");
			
			System.out.println();
			R.put(i+"", stringBuffer);
		}
		System.out.println();
	}
	private static final String toStringFormat = "Current Net IncidenceMatrix: %s %n ";
	public static  String toString1() {
		StringBuilder relationBuilder = new StringBuilder();
		relationBuilder.append("\n");
		for(StringBuffer relation: R.values()){
			relationBuilder.append("");
			relationBuilder.append(relation);
			relationBuilder.append('\n');
		}
		return String.format(toStringFormat, relationBuilder.toString());
	}
	
	//变迁序列生成网：
	public static String Traversal(PTNet ptNet)
	{
		String sequence = null;
		Collection<List<String>> testTraces = PNTraversalUtils.testTraces(ptNet, 10, 100, true,true,true);
		StringBuffer stringBuffer = new StringBuffer("Complete Sequence(to the fianl State):"+"\n");
		for (List<String> list : testTraces) {
			stringBuffer.append("    "+list);
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}
	
	public static String MatrixToFormatString(String desc,int[][] a)
	{
		Map<String, StringBuffer> R = new HashMap<String,StringBuffer>();
		for(int i = 0; i < a.length;i++)
		{
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(" [");
			for(int j = 0 ; j < a[0].length;j++)
			{
				if(a[i][j] == -1)
				{
					stringBuffer.append(a[i][j]+ "   ");
				}else{
					stringBuffer.append(a[i][j]+ "    ");
				}
			}
			stringBuffer.append("]");
			R.put(i+"", stringBuffer);
		}
		String toStringFormat = " "+desc+": %s %n ";
		StringBuilder relationBuilder = new StringBuilder();
		relationBuilder.append("\n");
		for(StringBuffer relation: R.values()){
			relationBuilder.append("");
			relationBuilder.append(relation);
			relationBuilder.append('\n');
		}
		return String.format(toStringFormat, relationBuilder.toString());
	}
	public static void MySystemout(int [][] a)
	{
		for(int i = 0 ; i < a.length; i++)
		{
			for(int j = 0; j < a[0].length;j++)
			{
				System.out.print(a[i][j]+" ");
			}
			System.out.println();
		}
	}
	
}
