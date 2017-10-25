package edu.xidian.petrinet.createnet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *  pnt文件和资源关系流文件txt之间相互转换
 */
public class txtAndPntTraverseEachOther {
	//由网生成资源流关系文件
	public static void createRelationResource(int[][] incMatObjectNet,int resourceStart) throws IOException{
		
		File file = new File("D:\\upload\\relationresource.txt");
		PrintStream fos= new PrintStream(file);
		for(int i=resourceStart-1;i<incMatObjectNet.length;i++){
			fos.print(i-resourceStart+2);
			fos.print(",");
			for(int j=0;j<incMatObjectNet[i].length;j++){
				if(incMatObjectNet[i][j]==-1){	
					for(int k=resourceStart-1;k<incMatObjectNet.length;k++){
						if(incMatObjectNet[k][j]==1){							
							fos.print(k-resourceStart+2);
							fos.print(" ");
						}
					}

				}	
			}
			fos.println();
		}
		fos.close();
	}
	// 由资源关系流文件生成pnt文件
	public void createPNT(String txtPath,String pntPath){
		int [][]rRMat = readResourceRelationFile(txtPath);
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
		int[][]incMatObjectNet=new int[row][column];
		//idle库所
		int []idle=new int[count1];
		//token向量
		int[]initialTokenVector=new int[row];
		//p不变式
		int[][] I=new int[count1+rRMat.length][row];
		int resourceStart=3*count1+1;
		
		for(int i=1;i<=count1;i++){
			idle[i-1]=(i-1)*3+1;
			
			initialTokenVector[3*(i-1)]=2;
			incMatObjectNet[(i-1)*3][i*3-1]=1;
			incMatObjectNet[(i-1)*3][(i-1)*3]=-1;//idle库所
			
			incMatObjectNet[(i-1)*3+1][(i-1)*3]=1;
			incMatObjectNet[(i-1)*3+1][(i-1)*3+1]=-1;//第一个状态库所

			incMatObjectNet[(i-1)*3+2][(i-1)*3+1]=1;
			incMatObjectNet[(i-1)*3+2][(i-1)*3+2]=-1;//第二个状态库所
			I[i-1][3*(i-1)]=1;
			I[i-1][3*(i-1)+1]=1;
			I[i-1][3*(i-1)+2]=1;
		}
		//资源对应的token
		for(int i=0;i<Math.max(rRMat.length, rRMat[0].length);i++){
			initialTokenVector[i+column]=1;
		}
		//资源对应的p不变式
		for(int i=0;i<Math.max(rRMat.length, rRMat[0].length);i++){
			I[i+count1][resourceStart-1+i]=1;
		}
		//资源行的关联矩阵。和p不变式
		for(int i=0;i<nRow.length;i++){
			incMatObjectNet[resourceStart-2+nRow[i]][3*i+2]=1;
			incMatObjectNet[resourceStart-2+nRow[i]][3*i+1]=-1;
			incMatObjectNet[resourceStart-2+nColumn[i]][3*i+1]=1;
			incMatObjectNet[resourceStart-2+nColumn[i]][3*i]=-1;
			// 非分支弧段的资源P-inv包含的状态库所
			I[count1+nRow[i]-1][3*i+2]=1;
			I[count1+nColumn[i]-1][3*i+1]=1;
		}
		//生成net.pnt文件
		File file = new File(pntPath);
		PrintStream fos=null;
		try {
			fos = new PrintStream(file);
			fos.print("P	M	 Pre	Post");
			fos.println();
			for(int i=0;i<incMatObjectNet.length;i++){
				List<Integer> list1 = new ArrayList<Integer>();
				fos.print(i+1);
				fos.print(" ");
				fos.print(initialTokenVector[i]);
				fos.print(" ");
				for(int j=0;j<incMatObjectNet[i].length;j++){
					if(incMatObjectNet[i][j]==1){							
						fos.print(j+1);
						fos.print(" ");
					}else if(incMatObjectNet[i][j]==-1){
						list1.add(j+1);
					}	
				}
				fos.print(",");
				for(int j1=0;j1<list1.size();j1++){
					
					fos.print(list1.get(j1));
					fos.print(" ");
				}
				fos.println();
			}
			fos.print("@");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			fos.close();
		}
	}

	//读取资源关系文件，文件以/*为结束标志
	
	public int[][] readResourceRelationFile(String path) {
		File file = new File(path);
		BufferedReader buf = null;		
		//记录行数
		int m=0;
		if(file.exists() && file.isFile()){
			try {
				buf = new BufferedReader(new InputStreamReader
									(new FileInputStream(file), "GBK"));
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			}
			String line = null;
				try {
					while((line = buf.readLine())!=null){
						if(line.startsWith("/*")){
							break;
						}else{
							m++;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						buf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}	
		int SS[][] = new int[m][m];
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
							//按逗号分割
							String[] line0 = line.split(",");
							String line1 = line0[0];
							int p = Integer.parseInt(line1);
							//删除空格
							String line20 = line0[1];
							String line21 = line20.replaceAll(" ", ",");
							String[] line2 = line21.split(",");
							for(int i=0;i<line2.length;i++){
								SS[p-1][Integer.parseInt(line2[i])-1] = 1;
							}
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
		return SS;
	}

}
