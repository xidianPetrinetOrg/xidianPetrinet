package edu.xidian.petrinet.createnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace;
import edu.xidian.petrinet.S3PR.S3PR;
/**
 * 根据pnt文件生成S3PR网对象 
 *
 */
public class CreateS3prByPNT extends S3PR{
	
	private Collection<String> P0 = new ArrayList<String>();
	private Collection<String> PA = new ArrayList<String>();
	private Collection<String> PR = new ArrayList<String>();
	
	private static Map<String,Integer> Marking = new HashMap<String,Integer>();
	private String p_prefix = "p";
	private String t_prefix = "t";
	
	//j 用来记录行数的辅助数字
	private static int j = 1;
	private static int m = 0;
	//用str_2和最后一行读取的字符串进行比较
	private static String  str_2 = "@";
	/**
	 * 生成时S3PR网对象
	 * @return
	 */
	public S3PR getS3PR(String pathPnt){
		System.out.println(pathPnt);
		PTNet net = readTxtFile(pathPnt);
		Collection<PTPlace> places = net.getPlaces();
		Map<String, Integer> marking =this.Marking;
		for(PTPlace p: places){	
			int token = marking.get(p.getName())	;	
			if(token==0){
				PA.add(p.getName());
			}else if(token>0){
				Collection<PTPlace> prepre = getPreAndPre(p);
				Collection<PTPlace> postpost = getPostPost(p);
				prepre.retainAll(postpost);
				if(!prepre.isEmpty()){
					PR.add(p.getName());
				}else{
					P0.add(p.getName());
				}
			}
		}
		S3PR s3pr = new S3PR("S3PR", net, P0, PA, PR);
		return s3pr;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<PTPlace> getPreAndPre(PTPlace p){
		Collection<PTPlace> prePre = new HashSet<>();
		Collection<AbstractPNNode<PTFlowRelation>> nodes = p.getParents();
		for (AbstractPNNode node: nodes) {
			prePre.addAll(node.getParents()); // H(r)必然是places，不是transitions，因此Hr的类型说明正确
		}
		return prePre;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection<PTPlace> getPostPost(PTPlace p){
		Collection<PTPlace> postPost = new HashSet<>();
		Collection<AbstractPNNode<PTFlowRelation>> nodes = p.getChildren();
		for (AbstractPNNode node: nodes) {
			postPost.addAll(node.getChildren()); // H(r)必然是places，不是transitions，因此Hr的类型说明正确
		}
		return postPost;
	}
	/**
	 * 读取pnt文件生成PTNet对象
	 * @param filePath
	 * @return
	 */
	public  PTNet readTxtFile(String filePath)
	 { 
		 PTNet ptNet = new PTNet();
		 PTMarking ptMarking = new PTMarking();
			try { 
			 String encoding="GBK"; 
			 File file=new File(filePath); 
		     	if(file.isFile() && file.exists()){ //判断文件是否存在 
				     InputStreamReader read = new InputStreamReader( 
				     new FileInputStream(file),encoding);//考虑到编码格式 
				     BufferedReader bufferedReader = new BufferedReader(read); 
				     String lineText = null; 
				     //int  total_Line = 0;
				     while((lineText = bufferedReader.readLine(	)) != null)
				     { 
				    	 if(m == 0 || lineText.equals(str_2)){
				    		 //Do nothing!  对第一行和最后一行不进行处理！
				    	 }else				    		 
				    	 {
				    		 //抓取中间行的数据！
					    	 //System.out.println("file exist");
					    	 String str1=lineText; 
					    	// System.out.println(str1);
					    	 String[] string2 = str1.split(",");
					    	 String string3_ = string2[0].trim();
					    	 String string4_ = string2[1].trim();
					 		 String string3 = string3_.replaceAll(" +",",");
					 		 String string4 = string4_.replaceAll(" +", ",");
					 		 
					 		 String[] sq  = string3.split(",");
					 		 String[] sq1 = string4.split(",");
					 		//System.out.println(sq[2].length());
					 		 //1 逗号的前半部分的数据分析
					 		 for(int i = 0; i <sq.length;i++)
					 		 {
					 			  if(i == 0)
					 			  {
					 				  ptNet.addPlace(p_prefix+sq[i].trim());
					 			  }else if(i == 1)
					 			  {
					 				
					 				ptMarking.set(p_prefix+sq[0], Integer.valueOf(sq[i]));
					 				Marking.put(p_prefix+sq[0],Integer.valueOf(sq[i]));
					 				ptNet.setInitialMarking(ptMarking);
					 				 // Marking.put(Place[Integer.valueOf(sq[0])-1],Integer.valueOf(sq[i]));
					 			  }else
					 			  {	
					 				 ptNet.addTransition(t_prefix+sq[i]);
					 				 ptNet.addFlowRelationTP(t_prefix+sq[i], p_prefix+sq[0]);					 				
					 			  }
					 		  }
					 		  //2 逗号的后半部分的分析
					 		  for(int i = 0; i < sq1.length;i++)
					 		  {
					 			  ptNet.addTransition(t_prefix+sq1[i].trim());
					 			  ptNet.addFlowRelationPT(p_prefix+String.valueOf(j), t_prefix+sq1[i]);
					 		  }
					 		  j++;
				    	 }//if语句结束
				    	 m++;
				     }
				     //关闭读入流 
				     read.close();
	         	}else{
	         	}
	      }catch (Exception e) { 
	    	 //System.out.println("读取文件内容出错"); 
	         e.printStackTrace(); 
	      } 
			return ptNet;
	   }	
}
