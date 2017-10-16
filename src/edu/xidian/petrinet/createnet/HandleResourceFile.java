package edu.xidian.petrinet.createnet;
/*
* Time    :2017年7月14日 上午8:22:29
* Email   :2215225782@qq.com
* 作用：处理资源流文件
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.S3PR.S3PR;
import edu.xidian.petrinet.Utils.PNNodeComparator;
import edu.xidian.petrinet.Utils.myUtils;
import sun.util.resources.cldr.es.TimeZoneNames_es_419;

public class HandleResourceFile extends S3PR{
	
	private PTNet ptnet;
	private int[][] resourceRelationMatrix = null;
	private int[][] IncidenceMatrix ;
	private int[] idles;
	
	public int[][] getResourceRelationMatrix() {
		return resourceRelationMatrix;
	}
	public void setResourceRelationMatrix(int[][] resourceRelationMatrix) {
		this.resourceRelationMatrix = resourceRelationMatrix;
	}
	public int[][] getIncidenceMatrix() {
		return IncidenceMatrix;
	}
	public void setIncidenceMatrix(int[][] incidenceMatrix) {
		IncidenceMatrix = incidenceMatrix;
	}

	private int resourceStart;

	public PTNet ReadResRelFile(String path) {
		resourceRelationMatrix = ReadResourceRelationFile(path);
		List createS3PR = createS3PR(resourceRelationMatrix);
		
		IncidenceMatrix = (int[][] )createS3PR.get(0);
		idles=(int[]) createS3PR.get(2);
		resourceStart=(Integer)createS3PR.get(4);
		
		
		if(ptnet == null){
			ptnet = new PTNet();
		}
		 for(int i = 0; i < IncidenceMatrix.length; i++)
		 {
			 String pi = getP_prefix()+(i+1);
			 //String pi = Place[i];
			 ptnet.addPlace(pi);
			 for(int j = 0;j < IncidenceMatrix[0].length;j++)
			 {
				 String tj = getT_prefix()+(j+1);
				 //String tj = Transition[j];
				 ptnet.addTransition(tj);
				 
				 if(IncidenceMatrix[i][j] == 1)
				 {
					 String pI = getP_prefix()+(i+1);
					 //String pI = Place[i];
					 String tJ = getT_prefix()+(j+1);
					 //String tJ = Transition[j];
					 ptnet.addFlowRelationTP(tJ, pI,1);
				 }
				 if(IncidenceMatrix[i][j] == -1)
				 {
					 String pI = getP_prefix()+(i+1);
					 //String pI = Place[i];
					 String tJ = getT_prefix()+(j+1);
					 //String tJ = Transition[j];
					 ptnet.addFlowRelationPT(pI, tJ,1);
				 }
			 }
		 }

		 PTMarking marking = new PTMarking();
		 for(int i = resourceStart-1;i<IncidenceMatrix.length;i++)
		 {
			 //资源库所的marking是1
			 String pi = getP_prefix()+(i+1);
			 marking.set(pi, 1); 
		 }
		 for(int i = 0; i <idles.length;i++)
		 {
			 //闲置库所的初始marking是2
			 String Pi = getP_prefix()+idles[i];
			 marking.set(Pi, 2);
			 //marking.set(Place[idles[i]-1], 2);
		 }
		 ptnet.setInitialMarking(marking);
		 
		 System.out.println("--------ptNet-----------");
		 System.out.println(ptnet);
		 System.out.println("--------ptNet-----------");
		 
		 
		 
		 return ptnet; 
	}
	
	
	
	public static int[][] ReadResourceRelationFile(String path) {
		File file = new File(path);
		BufferedReader buf = null;
		int SS[][] = new int[50][50];
		Integer hang = 0;
		Integer lie  = 0;
		if(file.exists() && file.isFile()){
			try {
				buf = new BufferedReader(new InputStreamReader
									(new FileInputStream(file), "GBK"));
				String line = null;
				
				try {
					while((line = buf.readLine())!=null){
						if(line.startsWith("/*")){
							break;
						}else{
							String[] line0 = line.split(",");
							String line1 = line0[0];
							int p = Integer.parseInt(line1);
							//删除空格
							String line20 = line0[1];
							String line21 = line20.replaceAll(" +", ",");
							String[] line2 = line21.split(",");
							for(int i=0;i<line2.length;i++){
								SS[p-1][Integer.parseInt(line2[i])-1] = 1;
							}
							hang ++;
							lie ++;
						}
						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			finally{
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		int  [][] ss1 = new int [hang][lie];
		for(int i = 0; i <ss1.length;i++)
		{
			for(int j = 0; j <ss1[0].length;j++)
			{
				ss1[i][j] = SS[i][j];
			}
		}
		return ss1;
	}
	//生成LS3PR网
	public static List createS3PR(int [][]rRMat){
		int count1=0;
		for(int i=0;i<rRMat.length;i++){
			for(int j=0;j<rRMat[i].length;j++){
				if(rRMat[i][j]==1){
					count1++;
				}
			}
		}
		int []nColumn=new int[count1];
		int k=0;
		for(int i=0;i<rRMat.length;i++){
			for(int j=0;j<rRMat[i].length;j++){
				if(rRMat[i][j]==1){
					nColumn[k]=i+1;
					k++;
				}
			}
		}
		
		int []nRow=new int[count1];
		int m=0;
		for(int i=0;i<rRMat[0].length;i++){
			for(int j=0;j<rRMat.length;j++){
				if(rRMat[j][i]==1){
					nRow[m]=j+1;
					m++;
				}
			}
		}
		//System.out.println(count1);
		//关联矩阵的行
		int row=3*count1+rRMat.length;
		//System.out.println(row);
		//关联矩阵的列
		int column=3*count1;
		//关联矩阵
		int[][]IncMatObjectNet=new int[row][column];
		//idle库所
		int []idle=new int[count1];
		//token向量
		int[]InitialTokenVector=new int[row];
		//p不变
		int[][] I=new int[count1+rRMat.length][row];
		int resourceStart=3*count1+1;
		
		for(int i=1;i<=count1;i++){
			idle[i-1]=(i-1)*3+1;
			
			InitialTokenVector[3*(i-1)]=2;
			IncMatObjectNet[(i-1)*3][i*3-1]=1;
			IncMatObjectNet[(i-1)*3][(i-1)*3]=-1;//idle库所
			
			IncMatObjectNet[(i-1)*3+1][(i-1)*3]=1;
			IncMatObjectNet[(i-1)*3+1][(i-1)*3+1]=-1;//第一个状态库

			IncMatObjectNet[(i-1)*3+2][(i-1)*3+1]=1;
			IncMatObjectNet[(i-1)*3+2][(i-1)*3+2]=-1;//第二个状态库
			I[i-1][3*(i-1)]=1;
			I[i-1][3*(i-1)+1]=1;
			I[i-1][3*(i-1)+2]=1;
		}
		//资源对应的token
		for(int i=0;i<Math.max(rRMat.length, rRMat[0].length);i++){
			InitialTokenVector[i+column]=1;
		}
		//资源对应的p不变
		for(int i=0;i<Math.max(rRMat.length, rRMat[0].length);i++){
			I[i+count1][resourceStart-1+i]=1;
		}
		//资源行的关联矩阵。和p不变
		for(int i=0;i<nRow.length;i++){
			IncMatObjectNet[resourceStart-2+nRow[i]][3*i+2]=1;
			IncMatObjectNet[resourceStart-2+nRow[i]][3*i+1]=-1;
			IncMatObjectNet[resourceStart-2+nColumn[i]][3*i+1]=1;
			IncMatObjectNet[resourceStart-2+nColumn[i]][3*i]=-1;
			// 非分支弧段的资源P-inv包含的状态库
			I[count1+nRow[i]-1][3*i+2]=1;
			I[count1+nColumn[i]-1][3*i+1]=1;
		}
		List list = new ArrayList();
		list.add(IncMatObjectNet);
		list.add(InitialTokenVector);
		list.add(idle);
		list.add(I);
		list.add(resourceStart);
		return list;
	}



	public void equipmenet(Collection<String> p0, Collection<String> pA, Collection<String> pR) {
		for(int i = resourceStart-1;i<IncidenceMatrix.length;i++)
		 {
			pR.add(getP_prefix()+(i+1));
			//pR.add(Place[i]);
		 }
		 for(int i = 0; i <idles.length;i++)
		 {
			 p0.add(getP_prefix()+idles[i]);
			 //p0.add(Place[idles[i]-1]);
		 }
		
		for(int i = 0; i < resourceStart-1;i++){
			int flag = -1;
			String t = getP_prefix()+(i+1);
			//String t = Place[i];
			boolean contains = p0.contains(t);
			if(contains == false){
				pA.add(t);
			}
		}
		
	}

	public static void main(String[] args) {
		
	}

	
	
}
