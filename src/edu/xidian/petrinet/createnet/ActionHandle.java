package edu.xidian.petrinet.createnet;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.graph.PTNetGraphComponent;

public class ActionHandle {
	
	//
	
	//用于存放从界面中筛选后的Place：
	private ArrayList<String> p_List  = new ArrayList<String>();
	//用于存放从界面中删选后的Transition
	private ArrayList<String> t_List  = new ArrayList<String>();
	//存放从变迁到库所的权值信息
	private ArrayList<String> pt_temp = new ArrayList<String>();
	//存放从库所到变迁的权值信息
	private ArrayList<String> tp_temp = new ArrayList<String>();
	//存放初始marking
	private ArrayList<String> Marking = new ArrayList<String>(); 
	static PTNet ptNet = null;
	

	private JFrame frame = new JFrame("PetrinetModel");
	private JButton Submit = new JButton("start");
	private JButton Clear = new JButton("clear");
	
	
	private JButton StepBefore = new JButton("上一步");
	private JButton StepAfter = new JButton("下一步");

	
	//pnt 文件的处理按钮
	private JButton pnt_Submit = new JButton("start");
	private JButton pnt_Clear = new JButton("clear");
	
	private JLabel name_Lable1 = new JLabel("Place:");  //库所框
	private JLabel name_Lable2 = new JLabel("Transition:"); //变迁框
	private JLabel name_Lable3 = new JLabel("PT:");  //库所到变迁关系
	private JLabel name_Lable4 = new JLabel("TP:");  //变迁到库所关系
	private JLabel name_Lable5 = new JLabel("InitMark:");
	
	private JLabel name_Pnt    = new JLabel("请输入pnt文件路径：");
	
	private JLabel info_Lable = new JLabel("输入Petri网的信息");
	
	private JLabel name_Step    = new JLabel("单步实现Petri网的token变化：");
	
	public  static JLabel info_Lable1 = new JLabel("");
	
	private JTextField nameText_place = new JTextField();
	private JTextField nameText_transition = new JTextField();
	private JTextField nameText_PT = new JTextField();
	private JTextField nameText_TP = new JTextField();
	private JTextField nameText_InitMarking = new JTextField();
	
	private JTextField nameText_Pnt = new JTextField();
	
	//用来标识List中存储的网的信息的下标
	static int i = 0 ;
	ArrayList<PTNetTraversalStepInfo>  aList;
	DisplayFrame DP;
	DisplayFrame DP1;
	
	
	//构造函数
	public ActionHandle()
	{
		Font fnt = new Font("Serief", Font.BOLD,15);
		info_Lable.setFont(fnt);
		Submit.addActionListener(new MyActionListener());
		Clear.addActionListener(new MyActionListener());
		
		pnt_Submit.addActionListener(new pntActionHandle3());
		pnt_Clear.addActionListener(new pntActionHandle3());
		
		StepBefore.addActionListener(new StepActionHandle());
		StepAfter.addActionListener(new StepActionHandle());
	
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg)
			{
				System.exit(1);
			}
		});
		
		
		frame.setLayout(null);
		name_Lable1.setBounds(20, 30, 60, 30);
		name_Lable2.setBounds(20, 60, 60, 30);
		name_Lable3.setBounds(20, 90, 60, 30);
		name_Lable4.setBounds(20, 120,60, 30);
		name_Lable5.setBounds(20, 150,60, 30);
		name_Pnt.setBounds(350, 30, 200, 30);
		
		nameText_place.setBounds(80, 30, 200, 30);
		nameText_transition.setBounds(80, 60, 200, 30);
		nameText_PT.setBounds(80, 90, 200, 30);
		nameText_TP.setBounds(80, 120, 200, 30);
		nameText_InitMarking.setBounds(80, 150, 200, 30);
		nameText_Pnt.setBounds(350, 60, 200, 30);
		
		
		Submit.setBounds(80, 190, 200, 30);
		Clear.setBounds(80, 220, 200, 30);
		
		pnt_Submit.setBounds(410, 90, 70, 30);
		pnt_Clear.setBounds(480, 90, 70, 30);
		
		name_Step.setBounds(350, 180, 200, 30);
		StepBefore.setBounds(350, 210, 100, 30);
		StepAfter.setBounds(450, 210, 100, 30);
		
		
		info_Lable.setBounds(130, 270, 200, 30);
		info_Lable1.setBounds(350, 120, 200, 60);
		
		
		frame.add(Submit);
		frame.add(Clear);
		frame.add(pnt_Submit);
		frame.add(pnt_Clear);
		
		frame.add(name_Step);
		frame.add(StepBefore);
		frame.add(StepAfter);
		
		frame.add(info_Lable);
		frame.add(info_Lable1);
		frame.add(nameText_PT);
		frame.add(nameText_TP);
		frame.add(nameText_place);
		frame.add(nameText_transition);
		frame.add(nameText_InitMarking);
		frame.add(nameText_Pnt); 
		
		frame.add(name_Lable1);
		frame.add(name_Lable2);
		frame.add(name_Lable3);
		frame.add(name_Lable4);
		frame.add(name_Lable5);
		frame.add(info_Lable);
		frame.add(name_Pnt);
		//frame.setSize(350, 350);
		frame.setSize(700, 350);
		frame.setVisible(true);
		
	}
	class StepActionHandle implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == StepAfter )
			{
				if(i < aList.size())
				{
					i = i+1;
				}
				
				System.out.println("++++++"+ aList.size());
				if(i > 0 && i < aList.size())
				{
					//0 如果刚开始是非初始状态，则首先关闭上一次打开的框图，然后在创建框图
					if(DP1 != null)
					{
						DP1.dispose();
					}
					
					
					
					//1 创建Petri网
					PTNet ptNet = new PTNet();
					
					//2获取网的信息
					ArrayList<String>  P = aList.get(i).getKuSuo();
					ArrayList<String>  T = aList.get(i).getBianQian();
					ArrayList<String>  M = aList.get(i).getChuShiKuSuo();
					String []       str4 = aList.get(i).getLiuGuanXi();
					
					
					
					//3 给网中装配获取的信息
					  //3.1 装配Place
					RandomTraversal.equipmentPlaceTool(ptNet,P);
					  //3.2装配Transition
					RandomTraversal.equipmentTransitionTool(ptNet,T);
					
					  //3.3装配初始的Marking
					PTMarking marking = new PTMarking();
					  int len2 = M.size()/2;
						String temp_Marking[][] = new String[len2][2];
						int k2 = 0;
						//把一维数组转换为二维数组n行2列，方便后面的对号入座：p1,1
						for(int w = 0 ; w < len2; w++)
						{
							for(int j = 0; j < 2;j++)
							{
								temp_Marking[w][j] = M.get(k2);
								k2++;
							}
						}
					RandomTraversal.equipmentMarkingTools(marking,temp_Marking);
				    ptNet.setInitialMarking(marking);
				       //3.4装配流关系
					for(int x= 0; x < str4.length;x = x+3)
					{
						if(P.contains(str4[x]))
						{
							ptNet.addFlowRelationPT(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
						}
						if(T.contains(str4[x]))
						{
							ptNet.addFlowRelationTP(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
						}
					}
					//2 再配置要显示的图形元素
					 PTNetGraphComponent component = new PTNetGraphComponent(ptNet);
			        	try {
			        		component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
			        	} catch (Exception b) {
			        		b.printStackTrace();
			        	}	
			        	DP1 = new DisplayFrame(component,true);  // 显示图形元素	

						//2.1 如果刚开始是初始状态的框图，在构建完成新的框图以后，然后在关闭初始状态的框图
						if(DP != null)
						{
							DP.dispose();
						}
				}
			}
			if(e.getSource() == StepBefore)
			{
				//如果到达第一个显示图之后，如果一直点击
				if(i > 0)
				{
					i= i - 1;
				}
				if(i > 0)
				{
					//1 如果刚开始是非初始状态，则首先关闭上一次打开的框图，然后在创建框图
					if(DP1 != null)
					{
						DP1.dispose();
					}
					
					//1 创建Petri网
					PTNet ptNet = new PTNet();
					
					//2获取网的信息
					ArrayList<String>  P = aList.get(i).getKuSuo();
					ArrayList<String>  T = aList.get(i).getBianQian();
					ArrayList<String>  M = aList.get(i).getChuShiKuSuo();
					String []       str4 = aList.get(i).getLiuGuanXi();
					
					//3 给网中装配获取的信息
					  //3.1 装配Place
					RandomTraversal.equipmentPlaceTool(ptNet,P);
					  //3.2装配Transition
					RandomTraversal.equipmentTransitionTool(ptNet,T);
					
					  //3.3装配初始的Marking
					PTMarking marking = new PTMarking();
					  int len2 = M.size()/2;
						String temp_Marking[][] = new String[len2][2];
						int k2 = 0;
						//把一维数组转换为二维数组n行2列，方便后面的对号入座：p1,1
						for(int w = 0 ; w < len2; w++)
						{
							for(int j = 0; j < 2;j++)
							{
								temp_Marking[w][j] = M.get(k2);
								k2++;
							}
						}
					RandomTraversal.equipmentMarkingTools(marking,temp_Marking);
				    ptNet.setInitialMarking(marking);
				       //3.4装配流关系
					for(int x= 0; x < str4.length;x = x+3)
					{
						if(P.contains(str4[x]))
						{
							ptNet.addFlowRelationPT(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
						}
						if(T.contains(str4[x]))
						{
							ptNet.addFlowRelationTP(str4[x],str4[x+2],Integer.valueOf(str4[x+1]));
						}
					}
					//4 通过上面的装配关系来进行图形装配
					 PTNetGraphComponent component = new PTNetGraphComponent(ptNet);
			        	try {
			        		component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
			        	} catch (Exception b) {
			        		b.printStackTrace();
			        	}	
			        	DP1 = new DisplayFrame(component,true);  // 显示图形元素
				}
				 //如果是一直点击上一步，则最终会到达初始的框图，如果到达初始的框图，则
				if(i == 0)
				{
					//do nothing  当然也可以进行在主界面上进行提醒， 最好的方式是进行主界面的显示每一步的变化
					//中时网的结构信息。
   				}
			}
		}
	}

	class MyActionListener implements ActionListener{
	
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == Submit)
			{
				//从框图的输入中获取输入的信息
				String place = nameText_place.getText();
				String transition = nameText_transition.getText();
				String pt = nameText_PT.getText();
				String tp = nameText_TP.getText();
				String initMarking = nameText_InitMarking.getText();
				
				//对获取的信息进行处理：
				//（1）先判断获取的信息是否有效；
				/*   if(有效)
				   {
					//处理方法	
				   }else{
					   //处理方法
				   }*/
				//先将其转换为字符数组：
				char place_Array[] = place.toCharArray();
				String temp ="";
				for(int i = 0; i < place_Array.length; i++)
				{
					if(place_Array[i] != ',')
					{
						temp = temp + String.valueOf(place_Array[i]);
					}else if(place_Array[i] == ','){
						p_List.add(temp);
						temp="";
					}
				}
				if(temp != "")
				{
					p_List.add(temp);
				}
				equipmentPlaceTool(p_List);

				//解析库所
				char transition_Array[] = transition.toCharArray();
				String  temp1 = "";
				for(int i = 0; i < transition_Array.length; i++)
				{
					if(transition_Array[i] != ',')
					{
						temp1 = temp1 + String.valueOf(transition_Array[i]);
					}else{
						t_List.add(temp1);
						temp1 = "";
					}
				}
				if(temp1 != "")
				{
					t_List.add(temp1);
				}
				equipmentTransitionTool();
				
				//解析Place-->transition 的权值
				char pt_Array[] = pt.toCharArray();
				
				String temp2 = ""; 
				for(int i = 0; i <pt_Array.length; i++)
				{
					if((pt_Array[i] != ',')&&(pt_Array[i] != ';'))
					{
						temp2 = temp2 + String.valueOf(pt_Array[i]);
					}else{
						pt_temp.add(temp2);
						temp2 = "";
					}
				}
				if(temp2!= "")
				{
					pt_temp.add(temp2);
				}
				//测试：上面的解析p-->t权值代码段ok！
				//test();				
				int len = pt_temp.size()/3;
				String pt_array[][] = new String[len][3];
				int k = 0;
				//把一维数组转换为二维数组n行3列，方便后面的对号入座：p1,t1,1
				for(int i = 0 ; i < len; i++)
				{
					for(int j = 0; j < 3;j++)
					{
						pt_array[i][j] = pt_temp.get(k);
						k++;
					}
				}
			    equipmentPlaceToTransitionTools(pt_array);
			  //解析transition-->Place 的权值
				char tp_Array[] = tp.toCharArray();
				
				String temp3 = ""; 
				for(int i = 0; i <tp_Array.length; i++)
				{
					if((tp_Array[i] != ',')&&(tp_Array[i] != ';'))
					{
						temp3 = temp3 + String.valueOf(tp_Array[i]);
					}else{
						tp_temp.add(temp3);
						temp3 = "";
					}
				}
				if(temp3!= "")
				{
					tp_temp.add(temp3);
				}
				//测试：上面的解析t-->p权值代码段ok！
				//test();
				int len1 = tp_temp.size()/3;
				String tp_array[][] = new String[len1][3];
				int k1 = 0;
				//把一维数组转换为二维数组n行3列，方便后面的对号入座：p1,t1,1
				for(int i = 0 ; i < len1; i++)
				{
					for(int j = 0; j < 3;j++)
					{
						tp_array[i][j] = tp_temp.get(k1);
						k1++;
					}
				}
			    equipmentTransitionToPlaceTools(tp_array);
			    
			  
			    //从文本中获取InitMarking文本框中的数据，填充至命令框中；
			    char Marking_temp[] = initMarking.toCharArray();				
				String temp4 = ""; 
				for(int i = 0; i <Marking_temp.length; i++)
				{
					if((Marking_temp[i] != ',')&&(Marking_temp[i] != ';'))
					{
						temp4 = temp4 + String.valueOf(Marking_temp[i]);
					}else{
						Marking.add(temp4);
						temp4 = "";
					}
				}
				if(temp4!= "")
				{
					Marking.add(temp4);
				}
				//测试：解析上面的代码段ok！
				//test1();
				
				int len2 = Marking.size()/2;
				String temp_Marking[][] = new String[len2][2];
				int k2 = 0;
				//把一维数组转换为二维数组n行2列，方便后面的对号入座：p1,1
				for(int i = 0 ; i < len2; i++)
				{
					for(int j = 0; j < 2;j++)
					{
						temp_Marking[i][j] = Marking.get(k2);
						k2++;
					}
				}
				
				//初始的marking数：
			    PTMarking marking = new PTMarking();
			    equipmentMarkingTools(marking,temp_Marking);
			    ptNet.setInitialMarking(marking);
			    
				
			    
			    javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	PTNetGraphComponent component = new PTNetGraphComponent(ptNet);
		        		try {
		        			component.initialize();  // 初始化，由petriNet信息，装配visualGraph,图形元素中心布局
		        		} catch (Exception e) {
		        			e.printStackTrace();
		        		}	
		        	    new DisplayFrame(component,true);  // 显示图形元素
		        	    info_Lable.setText("建模ok...");
		        	}       
		        }); 
			}
			if(e.getSource() == Clear)
			{
				nameText_place.setText("");
				nameText_transition.setText("");
				nameText_PT.setText("");
				nameText_TP.setText("");
				nameText_InitMarking.setText("");
				
				info_Lable.setText("建模中...");
			}	
		}
	}
	public void equipmentPlaceTool(List<String> p_List)
	{
		if(ptNet == null)
		{
			ptNet = new PTNet();
		}
	    Iterator<String> it=p_List.iterator();
		while(it.hasNext())
		{
			ptNet.addPlace(it.next());
	    }
	}
	public void equipmentTransitionTool()
	{
		if(ptNet == null)
		{
			ptNet = new PTNet();
		}
		Iterator<String> it = t_List.iterator();
		while(it.hasNext())
		{
			ptNet.addTransition(it.next());
		}
	}
	public void test()
	{
		Iterator<String> it = pt_temp.iterator();
		while(it.hasNext())
		{
			System.out.println(it.next());
		}
	}
	public void test1()
	{
		for(int i = 0; i < Marking.size(); i++)
		{
			System.out.println(Marking.get(i));
		}
	}
	 public void equipmentPlaceToTransitionTools(String [][]pt_array)
	 {
		 for(int i = 0; i < pt_array.length; i++)
		 {
			 ptNet.addFlowRelationPT(pt_array[i][0],pt_array[i][1],Integer.valueOf(pt_array[i][2]));
		 }
	 }
	 public void equipmentTransitionToPlaceTools(String [][]tp_array)
	 {
		 for(int i = 0; i < tp_array.length; i++)
		 {
			ptNet.addFlowRelationTP(tp_array[i][0],tp_array[i][1],Integer.valueOf(tp_array[i][2]));
		 }
	 }
	 public void equipmentMarkingTools( PTMarking marking,String [][] temp_Marking)
	 {
		 for(int i = 0; i < temp_Marking.length;i++)
		 {
			 marking.set(temp_Marking[i][0], Integer.valueOf(temp_Marking[i][1])); //散列在hash Map中
		 }
	 }
	 
	 class pntActionHandle3 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == pnt_Submit)
			{
				//1  如果选择提交，首先，先获取文件路径:必须是绝对路径
				//2  读取：接收，IO读取
				//3  分析：装配
				//4  生成图像
				String pnt_add = nameText_Pnt.getText();
				//文件路径读取正确！
				//System.out.println(pnt_add);
				handlePNTFile handleFile = new handlePNTFile();
				
				
				handleFile.readTxtFile(pnt_add);
				aList = handleFile.getaList();
				DP = handleFile.getDP();
				//System.out.println("aList = " + aList.size());
			}
			if(e.getSource() == pnt_Clear)
			{
				nameText_Pnt.setText("");
				info_Lable1.setText("");
			}	
		}
	}
	public static void main(String[] args) 
	{
		//ActionHandle3 actionHandle3 = new ActionHandle3();
		
	}
}



