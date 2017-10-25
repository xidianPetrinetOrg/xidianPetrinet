package edu.xidian.petrinet.createnet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;
import com.sun.javafx.css.CalculatedValue;

import de.uni.freiburg.iig.telematik.sepia.generator.PNGenerator;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;
import edu.xidian.petrinet.S3PR.S3PR;
import edu.xidian.petrinet.Utils.myUtils;
import edu.xidian.petrinet.graph.PTMarkingGraphComponent;
import edu.xidian.petrinet.graph.PTMarkingGraphComponent.IMarkingGraphReady;
import edu.xidian.petrinet.graph.PTNetGraphComponent;

/**
 * 可视化的总图形界面，可进行基本的编辑功能<br>
 */
public class PTNetGraph implements ActionListener, ItemListener {
	/** 输出状态信息 */
    private JTextArea output;
    private JTextArea output1;
    private JTextArea output2;
    
    private static final String newline = "\n";
    private static JFrame frame = null;
    
    /** PTNet */
    private PTNet ptNet = null;
    CreatePetriNetByFile createPetriNetByFile;
    
    
    public static final String ComboStr[] = {"ResourceRelationMatrix","IncidenceMatrix","P-InvariantMatrix","T-InvariantMatrix","Calculate_SMS"};
    /** PTNet graph component */
    private PTNetGraphComponent ptnetGraphComponent = null;
    
    /** PTNet Marking graph component */
    private PTMarkingGraphComponent ptMarkingGraphComponent = null;
    
    /** marking graph is ready */
    private boolean isMarkingGraphReady = false;

    /** 图的朝向,如果改变，请注意在createMenuBar()中，修改快捷键，现在是：N,W,S,E */
    private String[] orientationStr = {"NORTH","WEST","SOUTH","EAST"};
    
    /** 表示PTnet graph的朝向的单选按钮，key: orientationStr */
    private Map<String,JRadioButtonMenuItem> netOrientationRadioBtn = new HashMap<>();
    
    /** 表示Marking graph的朝向的单选按钮，key: orientationStr */
    private Map<String,JRadioButtonMenuItem> markingOrientationRadioBtn = new HashMap<>();
    
    /** 表示选择PTNet graph或Making graph,或二者皆选。 Key: "PTNet","Marking" */
    private Map<String,JCheckBoxMenuItem> PTNetOrMarkingGraph = new HashMap<>();
    
    /** 编辑功能Actions, 顶点Label的显示位置; undo,redo */
    private Action labelLeftAction, labelRightAction, labelTopAction, labelBottomAction,
                   undoHistoryAction, redoHistoryAction,RRGraph;
    
    protected mxUndoManager undoManager = null;
    /**flag how to create the net */
    protected static String flag = null; 
    protected static String  sourcepath;
    
    /** 
     * 整合所有快捷键,
     * 除了undo，redo是CTRL组合键，其它按键助记符是Alt的组合键;
     * menu与toolBar同级，不能有重复; 不同menu下的item可以重复.
    */
    @SuppressWarnings("serial")
	private static final Map<String,Integer> keyCode = new HashMap<String,Integer>() {
    	{    		
    		/** File menu */
    		put("file",KeyEvent.VK_F); // menu,createMenuBar()定义menu and menu item
    		put("open",KeyEvent.VK_O); // menu item
    		put("save",KeyEvent.VK_S); 
    		put("Save",KeyEvent.VK_Q);
    		put("print",KeyEvent.VK_P); 
    		
    		put("ptnet_item",KeyEvent.VK_N); 
    		put("marking_item",KeyEvent.VK_M); 
    		put("exit",KeyEvent.VK_E); 
    		
    		/** Edit menu */
    		put("edit",KeyEvent.VK_E); // createMenuBar()定义menu and menu item
    		// undo,redo,menu item与toolBar共用,createAction()定义toolBar和menu共用的动作
    		put("undo",KeyEvent.VK_Z); // CTRL-Z，addBindings()
    		put("redo",KeyEvent.VK_Y); // CTRL-Y，addBindings()
    		
    		/** PTNet menu */   		
    		put("ptnet",KeyEvent.VK_P); // createMenuBar()定义menu and menu item
    		// menu item与toolBar共用,graph的布局朝向
    		put("north",KeyEvent.VK_N); 
    		put("west",KeyEvent.VK_W); 
    		put("south",KeyEvent.VK_S); 
    		put("east",KeyEvent.VK_E); 
    		
    		/** Making menu */
    		put("marking",KeyEvent.VK_M); // createMenuBar()定义menu and menu item
    		// item与"PTNet menu"一致
    		
    		/** Label menu */
    		put("label",KeyEvent.VK_A); // createMenuBar()定义menu and menu item
    		// menu item与toolBar共用,顶点label的显示位置，createAction()定义toolBar和menu共用的动作
    		put("left",KeyEvent.VK_L);  // 如果与"label"同键，执行"left"对应的动作
    		put("right",KeyEvent.VK_R); 
    		put("top",KeyEvent.VK_T); 
    		put("bottom",KeyEvent.VK_B);
    		
    		/** Generator menu */
    		put("generator",KeyEvent.VK_G); // menu, createMenuBar()定义menu and menu item
    		put("samplePTNet",KeyEvent.VK_S); // menu item
    		put("sharedResource",KeyEvent.VK_R); // menu item
    		put("producerConsumer",KeyEvent.VK_C); // menu item
    		put("boundedPipeline",KeyEvent.VK_P); // menu item    	
    		
    		put("tools", KeyEvent.VK_0);
    	}
    };
    
    /**
     * 构造PTNetGraph对象，PTNet Graph and It's marking graph, 提供可视化编辑功能 
     * @param ptnet PTNet对象
     */
    public PTNetGraph(PTNet ptnet) {
    	this.setPtNet(ptnet);
    	
        /**
         * 生成对应PTNet对象的Graph组件和markingGraph组件
         */
        graphComponent();
        
        // Create the actions shared by the toolBar and menu. toolBar和menu共用的动作在这里生成, 按键助记符是Alt的组合键
	    createAction();
	    	
	    //Add a couple of emacs key bindings for undo,redo,undo,redo快捷按键（CTRL-Z/CTRL-Y）
		addBindings();
		    
        //////////////undo
		this.undoManager = ptnetGraphComponent.getUndoManager();
		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);
	        
    }
    
    public PTNetGraph() {
		// TODO Auto-generated constructor stub
	}

	/**
     * 生成对应PTNet对象的Graph组件和markingGraph组件
     */
    public void graphComponent() {
    	//////////// PTNet graph
    	this.ptnetGraphComponent = new PTNetGraphComponent(getPtNet()); 
    	try {
    		ptnetGraphComponent.initialize();
    		
    		///////////////// marking graph
    		isMarkingGraphReady = false;
    		this.ptMarkingGraphComponent = new PTMarkingGraphComponent(getPtNet());
    		IMarkingGraphReady ready = new IMarkingGraphReady() {
    			@Override
    			public void graph(boolean isReady) {
    				//if (isReady) status("marking graph is ready.");
    				isMarkingGraphReady = isReady;
    			}
    		};
    		// 非阻塞方式，接口回调：计算markingGaph，结果通过参数获取
    		this.ptMarkingGraphComponent.markingGraphReady(ready);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	// Clears the command history.
    	if(undoManager != null) {
    	  undoManager.clear();
    	}
    }
       
    /** 生成菜单条MenuBar */
	private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        // Create the menu bar.
        menuBar = new JMenuBar();

        // Build the menu: "File"
        menu = new JMenu("File");
        menu.setMnemonic(keyCode.get("file"));
        menuBar.add(menu);

        // JMenuItems for the menu
        menuItem = new JMenuItem("Open");
        //menuItem.setMnemonic(keyCode.get("open"));
        //menuItem.addActionListener(this);
        menuItem.setAction(new OpenAction("Open","Open file...",keyCode.get("open"))); 
        menu.add(menuItem);
        
        // save
        menuItem = new JMenuItem("Save");
        //menuItem.setMnemonic(keyCode.get("save")); // 无效
        menuItem.setAction(new SaveAction("Save","Save graph...",keyCode.get("save"))); // 第三个参数指定了助记键（ALT组合键）
        menu.add(menuItem);
        
        menu.addSeparator();
        
        // save
        menuItem = new JMenuItem("Save");
        //menuItem.setMnemonic(keyCode.get("save")); // 无效
        menuItem.setAction(new SaveAction1("Save","Save Info...",keyCode.get("save"))); // 第三个参数指定了助记键（ALT组合键）
        menu.add(menuItem);
        
        // print
        menuItem = new JMenuItem("Print");
        //menuItem.setMnemonic(keyCode.get("save")); // 无效
        menuItem.setAction(new PrintAction("Print","Print graph...",keyCode.get("print"))); // 第三个参数指定了助记键（ALT组合键）
        menu.add(menuItem);
        
        menu.addSeparator();
        
        //a group of check box menu items, 表示选择PTNet graph或Making graph,或二者皆选
        cbMenuItem = new JCheckBoxMenuItem("PTNet");
        cbMenuItem.setMnemonic(keyCode.get("ptnet_item"));
        cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(this);
        menu.add(cbMenuItem);
        PTNetOrMarkingGraph.put("PTNet", cbMenuItem);

        
        cbMenuItem = new JCheckBoxMenuItem("Marking");
        cbMenuItem.setMnemonic(keyCode.get("marking_item"));
        cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(this);
        menu.add(cbMenuItem);
        PTNetOrMarkingGraph.put("Marking", cbMenuItem);
        

        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(keyCode.get("exit"));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        // Build the menu: "Edit"
        menu = new JMenu("Edit");
        menu.setMnemonic(keyCode.get("edit"));
        menuBar.add(menu);
        
        menuItem = new JMenuItem(undoHistoryAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        // undo,redo,menu item与toolBar共用,addBindings()定义快捷键(CTRL—Z,CTRL-Y)）
        
        menuItem = new JMenuItem(redoHistoryAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        

        // Build the menu: "PTNet"
        menu = new JMenu("PTNet");
        menu.setMnemonic(keyCode.get("ptnet"));
        menuBar.add(menu);
        
        //a group of radio button menu items
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
        	rbMenuItem = new JRadioButtonMenuItem(orientationStr[i]);
            group.add(rbMenuItem);
            rbMenuItem.addActionListener(this);
            menu.add(rbMenuItem);
            netOrientationRadioBtn.put(orientationStr[i],rbMenuItem);
        }
        // default selected
        netOrientationRadioBtn.get("NORTH").setSelected(true);
        // Sets the keyboard mnemonic，按键助记符是Alt的组合键
        netOrientationRadioBtn.get("NORTH").setMnemonic(keyCode.get("north"));
        netOrientationRadioBtn.get("WEST").setMnemonic(keyCode.get("west"));
        netOrientationRadioBtn.get("SOUTH").setMnemonic(keyCode.get("south"));
        netOrientationRadioBtn.get("EAST").setMnemonic(keyCode.get("east"));
        
        // Build the menu: "Marking"
        menu = new JMenu("Marking");
        menu.setMnemonic(keyCode.get("marking"));
        menuBar.add(menu);
    
        //a group of radio button menu items
        ButtonGroup group1 = new ButtonGroup();
        for (int i = 0; i < orientationStr.length; i++) {
        	rbMenuItem = new JRadioButtonMenuItem(orientationStr[i]);
            group1.add(rbMenuItem);
            rbMenuItem.addActionListener(this);
            menu.add(rbMenuItem);
            markingOrientationRadioBtn.put(orientationStr[i],rbMenuItem);
        }
        // default selected
        markingOrientationRadioBtn.get("NORTH").setSelected(true);
        // Sets the keyboard mnemonic 
        markingOrientationRadioBtn.get("NORTH").setMnemonic(keyCode.get("north"));
        markingOrientationRadioBtn.get("WEST").setMnemonic(keyCode.get("west"));
        markingOrientationRadioBtn.get("SOUTH").setMnemonic(keyCode.get("south"));
        markingOrientationRadioBtn.get("EAST").setMnemonic(keyCode.get("east"));
        
        // Build the menu: "Label"
        menu = new JMenu("Label");
        menu.setMnemonic(keyCode.get("label"));
        menuBar.add(menu);
	    
        // menu item与toolBar共用,顶点label的显示位置，createAction()定义toolBar和menu共用的动作
        menuItem = new JMenuItem(labelLeftAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(labelRightAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(labelTopAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(labelBottomAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        // Build the Menu: "Generator"
        menu = new JMenu("Generator");
        menu.setMnemonic(keyCode.get("generator"));
        menuBar.add(menu);
        
        menuItem = new JMenuItem("samplePTNet");
        menuItem.setAction(new GeneratorPTNetAction("samplePTNet", "samplePTNet", keyCode.get("samplePTNet"))); // 第三个参数指定了助记键（ALT组合键）
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem("sharedResource");
        menuItem.setAction(new GeneratorPTNetAction("sharedResource", "sharedResourcePTNet", keyCode.get("sharedResource"))); // 第三个参数指定了助记键（ALT组合键）
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem("producerConsumer");
        menuItem.setAction(new GeneratorPTNetAction("producerConsumer", "producerConsumerPTNet", keyCode.get("producerConsumer"))); // 第三个参数指定了助记键（ALT组合键）
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem("boundedPipeline");
        menuItem.setAction(new GeneratorPTNetAction("boundedPipeline", "boundedPipelinePTNet", keyCode.get("boundedPipeline"))); // 第三个参数指定了助记键（ALT组合键）
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
             
        
        // Build the menu: "tools"
        menu = new JMenu("tools");
        menu.setMnemonic(keyCode.get("tools"));
        menuBar.add(menu);
        
        menuItem = new JMenuItem("showRRGraph");
        menuItem.setAction(new GeneratorResourceRelationAction("ResourceRelationGraph", "ResourceRelationGraph", keyCode.get("ResourceRelationGraph")));
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem("...");
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        
        return menuBar;
    }
	
	/** 
	 * 生成工具条 ，顶点Label显示位置，undo，redo
	 * menu item与toolBar共用，createAction()定义toolBar和menu共用的动作
	 */
	private JToolBar createToolBar() {
		JButton button = null;

		// Create the toolBar.
		JToolBar toolBar = new JToolBar();
	
		// first button
		button = new JButton(labelLeftAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// second button
		button = new JButton(labelRightAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// third button
		button = new JButton(labelTopAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		// forth button
		button = new JButton(labelBottomAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		toolBar.addSeparator();
		
		// undo,redo
		button = new JButton(undoHistoryAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		button = new JButton(redoHistoryAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		
		for(int i = 0; i<80;i++)
	    {
	    	 toolBar.addSeparator();
	    }
		toolBar.addSeparator();
	    toolBar.addSeparator();
		 
		 
		JLabel JLfont=new JLabel("NetInfornation");
		
	    toolBar.add(JLfont);
	    toolBar.addSeparator();

	    //利用上面定义的ComboStr数建立一个JComboBox组件.
        JComboBox jcb = new JComboBox(ComboStr);
        //将JComboBar加入事件处理模式.这里是将所选到的组件名称填入JTextArea中.
        jcb.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        
        String aString = ((JComboBox) e.getSource()).getSelectedItem()+"";
        HandleResourceFile resourceFile = createPetriNetByFile.resourceFile;
        int[][] incidenceMatrix = null;
        String cal_P_Inv = null;
        String cal_T_Inv = null;
        if(aString.equals("ResourceRelationMatrix"))
        {
        	
        	int[][] resourceRelationMatrix = resourceFile.getResourceRelationMatrix();
        	String RRM = myUtils.MatrixToFormatString("ResourceRelationMatrix", resourceRelationMatrix);
        	status1(RRM);
        }else if(aString.equals("IncidenceMatrix")){
        	
        	if(flag.equals("pnt")){
        		incidenceMatrix = handlePNTFile.IncidenceMatrix;
        	}else{
        		incidenceMatrix = resourceFile.getIncidenceMatrix();
        	}
        	
        	String IM = myUtils.MatrixToFormatString("IncidenceMatrix", incidenceMatrix);
        	status1(IM);
        }else if(aString.equals("P-InvariantMatrix")){
        	if(flag.equals("pnt")){
        		int[][] incidence = handlePNTFile.IncidenceMatrix;
        		cal_P_Inv = ComputeInv.Cal_P_Inv(incidence);
        	}else{
        		int[][] incidence = resourceFile.getIncidenceMatrix();
        		cal_P_Inv = ComputeInv.Cal_P_Inv(incidence);
        	}
        	
        	status1(cal_P_Inv);
        	
        }else if(aString.equals("T-InvariantMatrix")){
        	if(flag.equals("pnt")){
        		int[][] incidence = handlePNTFile.IncidenceMatrix;
        	 	cal_T_Inv = ComputeInv.Cal_T_Inv(incidence);
        	}else{
	        	int[][] incidence = resourceFile.getIncidenceMatrix();
			   	cal_T_Inv = ComputeInv.Cal_T_Inv(incidence);
        	}
		   	status1(cal_T_Inv);
        }else if(aString.equals("Calculate_SMS")){
        	S3PR createS3PR = createPetriNetByFile.s3pr;
        	System.out.println("s3pr"+createS3PR);
        	ArrayList<String> calculateSMS = new calculateSMS().calculateSMS(createS3PR);
        	
        	//存储为字符串的形式：
        	String Siphons = calculateSMS.get(0);
        	String SiphonComs = calculateSMS.get(1);
        	String stringCmatrix = calculateSMS.get(2);
        	/*System.out.println("dog");
        	System.out.println("result:\n"+ Siphons);
     	    System.out.println("result:\n"+ SiphonComs);
     	    System.out.println(stringCmatrix);
        	System.out.println("dog");*/
        	
        	status1(Siphons);
        	status1(SiphonComs);
        	status1(stringCmatrix);
        	
        }else if(aString.equals("...")){
        	
        	//可以继续增加功能块
        	//setDialogAndLable("待开发..", lable, dialog);
        }
 
      }});
	      toolBar.add(jcb);

		return toolBar;
	}
	/**
	 * 创建资源有向图 
	 *
	 */
	public class GeneratorResourceRelationAction extends AbstractAction{

		public GeneratorResourceRelationAction(String name, String desc, Integer mnemonic) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if(flag.equals("pnt")){
				//待开发
			}else{
				S3PR createS3PR = createPetriNetByFile.s3pr;
				if(createS3PR !=null){
					CreateRRG.Create(createS3PR);
				}
				
			}
			
		}
	}
	
	/** Label显示在顶点的左边 */
	@SuppressWarnings("serial")
	public class LabelLeftAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelLeftAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			 ptnetGraphComponent.changeLabelPosition(SwingConstants.LEFT);
		}
	}
	

	/** Label显示在顶点的右边 */
	@SuppressWarnings("serial")
	public class LabelRightAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelRightAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			 ptnetGraphComponent.changeLabelPosition(SwingConstants.RIGHT);
		}
	}
	

	/** Label显示在顶点的上边 */
	@SuppressWarnings("serial")
	public class LabelTopAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelTopAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			ptnetGraphComponent.changeLabelPosition(SwingConstants.TOP);
		}
	}
	
	/** Label显示在顶点的下边 */
	@SuppressWarnings("serial")
	public class LabelBottomAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public LabelBottomAction(String name, ImageIcon icon, String desc, Integer mnemonic) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			ptnetGraphComponent.changeLabelPosition(SwingConstants.BOTTOM);
		}
	}
	
	/** 生成PTNet对象 */
	@SuppressWarnings("serial")
	public class GeneratorPTNetAction extends AbstractAction {
		/**
		 * 生成PTNet对象
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public GeneratorPTNetAction(String name, String desc, Integer mnemonic) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem)(e.getSource());
			String name = source.getText();
			System.out.println("source = " + e.getSource() + "," + name);
			PTNet newPTNet = null;
			if (name.equalsIgnoreCase("samplePTNet")) {
				newPTNet = CreatePetriNet.createPTnet2(); 
				
			}
			if (name.equalsIgnoreCase("sharedResource")) {
				newPTNet = PNGenerator.sharedResource(2, 1);
			}
			if (name.equalsIgnoreCase("producerConsumer")) {
				newPTNet = PNGenerator.producerConsumer(2, 1);
			}
			if (name.equalsIgnoreCase("boundedPipeline")) {
				newPTNet = PNGenerator.boundedPipeline(2, 1);  
			}
			
			if ((newPTNet != null) && (newPTNet != getPtNet())) {
				/**
			     * 生成对应PTNet对象的Graph组件和markingGraph组件
			     */
			    graphComponent();
				setPtNet(newPTNet);
			}
		}
	}
	
	//wu
	
	/** open file
	 *  根据文件生成Petri网
	 *  
	 *  
	 *  */
	public class OpenAction extends AbstractAction{

		protected String fileName = null;
		protected String lastDir = null;
		JFileChooser jChooser2 = null;
		
		public OpenAction(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			jChooser2 = new JFileChooser();
		    jChooser2.setCurrentDirectory(new File("e:/"));//设置默认打开路径
		    jChooser2.setDialogType(JFileChooser.SAVE_DIALOG);//设置保存对话框
		    //将设置好了的两种文件过滤器添加到文件选择器中来
		    TxtFileFilter txtFileFilter = new TxtFileFilter();
		    XlsFileFilter xlsFileFilter = new XlsFileFilter();
		    jChooser2.addChoosableFileFilter(txtFileFilter);
		    jChooser2.addChoosableFileFilter(xlsFileFilter);
		    
		    int index = jChooser2.showDialog(null, "创建Petri网");
			File f = jChooser2.getSelectedFile();// f为选择到的目录  
			if(f== null)
			{
				return;
			}else{
			   //C:\Users\xd\Desktop\txt1.pnt
			   String path = f.getAbsolutePath();
			   
			   //判断文件是txt还是pnt结尾
			   String fileName=f.getName();
			   String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
			   System.out.println(prefix);
			   createPetriNetByFile = new CreatePetriNetByFile();
			   if(prefix.equals("pnt")){
				   //pnt文件创建网
				   //CreatePetriNetByPnt(path);
				   output1.removeAll();
				   flag = "pnt";
				   System.out.println(flag+"---------------------------");
				   ptNet = createPetriNetByFile.CreatePetriNetByPnt(path);
				   System.out.println(path);
		    	   frame.setVisible(false);
		    	  
			   }else if(prefix.equals("txt")){
				   output1.removeAll();
				   //采用工作流来创建网
				   //CreatePetriNetBytxt(path);
				   
				   sourcepath = path;
				   
				   flag = "txt";
				   ptNet = createPetriNetByFile.CreatePetriNetBytxt(path);
				   frame.setVisible(false); 
			   }else{
				   return;
			   }
			   
			   javax.swing.SwingUtilities.invokeLater(new Runnable() {
	               public void run() {
	                   //createAndShowGUI(ptnetGraphComponent); // 显示PTNet对应的图形
	                   frame = createAndShowGUI(ptNet); // 显示PTNet对应的图形
	               }
	           });
				
				
				//System.out.println(f.getAbsolutePath());
				//txtLocalRoad.setText(f.getAbsolutePath());
			}
		}

	}
	//重写文件过滤器，设置打开类型中几种可选的文件类型，这里设了两种，一种pnt，一种txt
	class TxtFileFilter extends FileFilter {
	 @Override
	 public boolean accept(File f) {
	  // TODO Auto-generated method stub
	  String nameString = f.getName();
	  return nameString.toLowerCase().endsWith(".pnt");
	 }
	 @Override
	 public String getDescription() {
	  // TODO Auto-generated method stub
	  return "*.pnt";
	 }
	 
	}
	class XlsFileFilter extends FileFilter {
	 @Override
	 public boolean accept(File f) {
	  // TODO Auto-generated method stub
	  String nameString = f.getName();
	  return nameString.toLowerCase().endsWith(".txt");
	 }
	 @Override
	 public String getDescription() {
	  // TODO Auto-generated method stub
	  return "*.txt";
	 }
	 
	}
	
	
	
	
	
	/** Save Graph */
	@SuppressWarnings("serial")
	public class SaveAction extends AbstractAction {
		protected String lastDir = null;
		
		protected String fileName = null;
		
		protected String ext = null; // .png,.jpg,...
		
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public SaveAction(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		
		
		public void actionPerformed(ActionEvent e) {
			getFileName();
			if (fileName == null) return;
			
			mxGraphComponent graphComponent = new mxGraphComponent(ptnetGraphComponent.getGraph());
			mxGraph graph = ptnetGraphComponent.getGraph();
			
			Color bg = null; // .png文件 没有背景，才是原汁原味;  .jpg和.jpeg无背景不好看; .wbmp有无背景均不能保存; .bmp必须有背景才能保存
			
//			if ((!ext.equalsIgnoreCase(".gif") && !ext.equalsIgnoreCase(".png"))
//					&& JOptionPane.showConfirmDialog(graphComponent, "transparentBackground") == JOptionPane.YES_OPTION)
//			{
//				bg = graphComponent.getBackground();
//			}
			if (ext.equalsIgnoreCase(".bmp") || ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".jpeg")) {  
				bg = graphComponent.getBackground();
			}
			BufferedImage image = mxCellRenderer
					.createBufferedImage(graph, null, 1, bg,
							graphComponent.isAntiAlias(), null,
							graphComponent.getCanvas());

			if (image != null)
			{
				try {
					ImageIO.write(image, ext.substring(1), new File(fileName));
					status("Save " + fileName + " ok！");
				} catch (IOException e1) {
					e1.printStackTrace();
					status("Sorry,Save file " + fileName + "failed！");
				}
			}
			else
			{
				JOptionPane.showMessageDialog(graphComponent, "noImageData");
			}
		}
		
		/**
		 * get:fileName and ext
		 */
		private void getFileName() {
			FileFilter selectedFilter = null;
			DefaultFileFilter PngFilter = new DefaultFileFilter(".png","Png file (.png)");
						
			String wd;

			if (lastDir != null) {
				wd = lastDir;
			} else {
				wd = System.getProperty("user.dir");
			}

			JFileChooser fc = new JFileChooser(wd);

			// Adds the default file format
			FileFilter defaultFilter = PngFilter;
			fc.addChoosableFileFilter(defaultFilter);

			// Adds a filter for each supported image format
			Object[] imageFormats = ImageIO.getReaderFormatNames();

			// Finds all distinct extensions
			HashSet<String> formats = new HashSet<String>();

			for (int i = 0; i < imageFormats.length; i++) {
				String ext = imageFormats[i].toString().toLowerCase();
				formats.add(ext);
			}

			imageFormats = formats.toArray();

			for (int i = 0; i < imageFormats.length; i++) {
				String ext = imageFormats[i].toString();
				fc.addChoosableFileFilter(new DefaultFileFilter("." + ext,ext.toUpperCase() + " (." + ext + ")"));
			}

			// Adds filter that accepts all supported image formats
			fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter("allImages"));
			fc.setFileFilter(defaultFilter);
			int rc = fc.showDialog(null, "Save");

			if (rc != JFileChooser.APPROVE_OPTION) {
				fileName = null;
				return;
			} else {
				lastDir = fc.getSelectedFile().getParent();
			}

			fileName = fc.getSelectedFile().getAbsolutePath();
			selectedFilter = fc.getFileFilter();

			if (selectedFilter instanceof DefaultFileFilter) {
				ext = ((DefaultFileFilter) selectedFilter).getExtension();

				if (!fileName.toLowerCase().endsWith(ext)) {
					fileName += ext;
				}
			}

			if (new File(fileName).exists() && JOptionPane.showConfirmDialog(ptnetGraphComponent,"overwriteExistingFile") != JOptionPane.YES_OPTION) {
				fileName = null;
			}
		}
	}
	
	/** Save Information */
	@SuppressWarnings("serial")
	public class SaveAction1 extends AbstractAction {
		protected String lastDir = null;
		
		protected String fileName = null;
		
		protected String ext = null; // .png,.jpg,...
		
		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public SaveAction1(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
		//wu change	
			
			JFileChooser jfc = new JFileChooser();// 文件选择器  
			TxtFileFilter1 txtFileFilter1 = new TxtFileFilter1();
        	jfc.addChoosableFileFilter(txtFileFilter1);
        	int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句 
			File f = jfc.getSelectedFile();// f为选择到的目录  
			String path =null;
			if(f== null)
			{
				
			}else{
				path = f.getAbsolutePath()+".txt";
				String targetpath = f.getAbsolutePath()+".pnt";
				
				String[] splitDoc = path.split("\\\\");
	        	StringBuffer stringBuffer = new StringBuffer();
	        	for(int i = 0; i < splitDoc.length-1; i++)
	        	{
	        		stringBuffer.append(splitDoc[i]);
	        		stringBuffer.append("\\\\");
	        	}
	        	stringBuffer.append(splitDoc[splitDoc.length-1]);
	        	path = stringBuffer.toString();
				
				System.out.println("sourcepath= "+sourcepath);
				System.out.println("targetpath= "+targetpath);
				new txtAndPntTraverseEachOther().createPNT(sourcepath, targetpath);
				
				String createPntBytxt = f.getAbsolutePath()+".pnt";
				
				String SaveInfo = output1.getText().toString();
				write(SaveInfo,path);
				JOptionPane.showMessageDialog(null, "保存成功", "Welcome to SCA", 1);
	            frame.setVisible(false);
	            frame.dispose();
				
			}
		}
	}
	BufferedWriter bw = null;
 	public void write(String netinfo, String ph){
        try {
      	  String path = ph;
      	  
      	  File f=new File(path);//向指定文本框内写入
  		  System.out.println(f.getParentFile());
  		  //如果目录不存在，则先创建目录
  		  if (!f.getParentFile().exists()) {  
  			  boolean result = f.getParentFile().mkdirs();  
  		      if (!result) {  
  	            System.out.println("创建失败");  
  		      }
  		  }
        OutputStream os = new FileOutputStream(path,false);
        bw = new BufferedWriter(new OutputStreamWriter(os));
        for (String value : netinfo.split("\n")) {
      	  System.out.println(value);
            bw.write(value);
            bw.newLine();//换行
        }
  		  
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
	 }
	//重写文件过滤器，设置打开类型中几种可选的文件类型，这里设了一种，一种txt
  	class TxtFileFilter1 extends FileFilter {
  		 @Override
  		 public boolean accept(File f) {
  		  // TODO Auto-generated method stub
  		  String nameString = f.getName();
  		  return nameString.toLowerCase().endsWith(".txt");
  		 }
  		 @Override
  		 public String getDescription() {
  		  // TODO Auto-generated method stub
  		  return "*.txt(文本文件)";
  		 }
  	}
	/** Print Graph */
	@SuppressWarnings("serial")
	public class PrintAction extends AbstractAction {
		public PrintAction(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {

			mxGraphComponent graphComponent1 = new mxGraphComponent(ptnetGraphComponent.getGraph());
			mxGraphComponent graphComponent2 = new mxGraphComponent(ptMarkingGraphComponent.getGraph());
	
			PrinterJob pj = PrinterJob.getPrinterJob();

			if (pj.printDialog()) {
				PageFormat pf = graphComponent1.getPageFormat();
				Paper paper = new Paper();
				double margin = 36;
				paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
				pf.setPaper(paper);
				
				pj.setPrintable(graphComponent1, pf);
				try {
					pj.print();
					status("print PTNet graph...");
				} catch (PrinterException e2) {
					System.out.println(e2);
					status("Sorry,print PTNet graph failed!");
				}
				
				if (!isMarkingGraphReady) {
					status("Warning, the marking graph is not ready!");
					return;
				}
				pj.setPrintable(graphComponent2, pf);
				try {
					pj.print();
					status("print marking graph...");
				} catch (PrinterException e2) {
					System.out.println(e2);
					status("Sorry,print marking graph failed!");
				}
			}
		}
	}
	
	/** 历史动作 */
	@SuppressWarnings("serial")
	public class HistoryAction extends AbstractAction {
		protected boolean undo;
		public HistoryAction(String name, ImageIcon icon, String desc,boolean undo) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			//putValue(MNEMONIC_KEY, mnemonic); // 在addBindings()定义了control-z and control-y
			this.undo = undo;
		}
		
		public HistoryAction(boolean undo) {
			this.undo = undo;
		}

		public void actionPerformed(ActionEvent e) {
			if (ptnetGraphComponent.getGraph() != null) {
				if (undo) {
					undoManager.undo();
				} else {
					undoManager.redo();
				}
			}
		}
	}
	
	/** Keeps the selection in sync with the command history */
	private mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			mxGraph graph = ptnetGraphComponent.getGraph();
			List<mxUndoableChange> changes = ((mxUndoableEdit) evt
					.getProperty("edit")).getChanges();
			graph.setSelectionCells(graph
					.getSelectionCellsForChanges(changes));
		}
	};

	/** Returns an ImageIcon, or null if the path was invalid. */
	private static ImageIcon createNavigationIcon(String imageName) {
		String imgLocation = "images/" + imageName + ".gif";
		java.net.URL imageURL = PTNetGraph.class.getResource(imgLocation);

		if (imageURL == null) {
			System.err.println("Resource not found: " + imgLocation);
			return null;
		} else {
			return new ImageIcon(imageURL);
		}
	}
	
	/**
	 * Create the actions shared by the toolBar and menu.
	 * toolBar和menu共用的动作在这里生成,
	 * 按键助记符是Alt的组合键
	 * undo,redo快捷按键（CTRL-Z/CTRL-Y）在addBindings()中定义
	 */
	private void createAction() {

		labelLeftAction = new LabelLeftAction("Left", createNavigationIcon("left"), "left label",
				new Integer(keyCode.get("left")));

		labelRightAction = new LabelRightAction("Right", createNavigationIcon("right"), "right label",
				new Integer(keyCode.get("right")));

		labelTopAction = new LabelTopAction("Top", createNavigationIcon("top"), "top label",
				new Integer(keyCode.get("top")));

		labelBottomAction = new LabelBottomAction("Bottom", createNavigationIcon("bottom"), "bottom label",
				new Integer(keyCode.get("bottom")));

		undoHistoryAction = new HistoryAction("Undo", createNavigationIcon("undo"), "undo", true);
		redoHistoryAction = new HistoryAction("Redo", createNavigationIcon("redo"), "redo", false);

	}
	
	/**
	 * Add a couple of emacs key bindings for undo,redo.
	 * undo,redo快捷按键（CTRL-Z/CTRL-Y）在addBindings()中定义
	 * toolBar和menu共用的动作在这里生成,按键助记符是Alt的组合键,在createAction()中定义
	 */
    private void addBindings() {
    	// The component has the keyboard focus
    	//InputMap inputMap = ptnetGraphComponent.getInputMap();  // 无效
    	
    	// The component has the keyboard focus
    	//InputMap inputMap = ptnetGraphComponent.getInputMap( JComponent.WHEN_FOCUSED ); // 无效，相当于无参的形式，组件获得焦点时
    	
    	// The component contains (or is) the component that has the focus. 
    	InputMap inputMap = ptnetGraphComponent.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // 有效，当父组件获得焦点时
    	
    	// The component's window either has the focus or contains the component that has the focus. 
    	//InputMap inputMap = ptnetGraphComponent.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW); // 有效，尽量不用，因为窗口的子组件太多
    	
    	ActionMap actionMap = ptnetGraphComponent.getActionMap();
    	
    	actionMap.put("undo", undoHistoryAction);
    	actionMap.put("redo", redoHistoryAction);
    	
    	//CTRL-Z to go undo
    	KeyStroke key = KeyStroke.getKeyStroke(keyCode.get("undo"), Event.CTRL_MASK);
    	inputMap.put(key, "undo");
    	
    	//CTRL-Y to go undo
    	key = KeyStroke.getKeyStroke(keyCode.get("redo"), Event.CTRL_MASK);
    	inputMap.put(key, "redo");
    }

	/** 
	 * toolBar和menu共用的动作（createAction()中生成），在各个对应的Action类中响应
	 * JMenuItem菜单项，JRadioButtonMenuItem单选按钮项，在actionPerformed(ActionEvent e)中响应
	 * JCheckBoxMenuItem复选按钮，在itemStateChanged(ItemEvent e)中响应
	 */
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource()); 
        // 单选按钮,图的朝向
        if (source instanceof JRadioButtonMenuItem) {
        	String selected = null;
        	
        	// PTNet graph
        	for (Map.Entry<String, JRadioButtonMenuItem> entry : netOrientationRadioBtn.entrySet()) {
        	    if (source == entry.getValue()) {
        	    	selected = entry.getKey();
        	    	//System.out.println("PTNet selected:" + selected);
        	    	int orientation = SwingConstants.NORTH;
        	    	if (selected.equalsIgnoreCase("WEST")) orientation = SwingConstants.WEST;
        	    	else if (selected.equalsIgnoreCase("EAST")) orientation = SwingConstants.EAST;
        	    	else if (selected.equalsIgnoreCase("SOUTH")) orientation = SwingConstants.SOUTH;
        	    	ptnetGraphComponent.setOrientation(orientation); // 改变图的朝向
        	    	break;
        	    }
        	}
        	
        	// Making graph
        	if (selected == null) {
		    	for (Map.Entry<String, JRadioButtonMenuItem> entry : markingOrientationRadioBtn.entrySet()) {
		    	    if (source == entry.getValue()) {
		    	    	selected = entry.getKey();
		    	    	//System.out.println("Marking selected:" + selected);
		    	    	int orientation = SwingConstants.NORTH;
	        	    	if (selected.equalsIgnoreCase("WEST")) orientation = SwingConstants.WEST;
	        	    	else if (selected.equalsIgnoreCase("EAST")) orientation = SwingConstants.EAST;
	        	    	else if (selected.equalsIgnoreCase("SOUTH")) orientation = SwingConstants.SOUTH;
	        	    	ptMarkingGraphComponent.setOrientation(orientation); // 改变图的朝向
		    	    	break;
		    	    }
		    	}
        	}
        }
        
        // 菜单项,Open,Exit, 如果是上述JRadioButtonMenuItem实例的菜单项，也符合本条件，因此不必用instanceof区分菜单项。
        if (source instanceof JMenuItem) {
        	//System.out.println("Menu item selected:" + source.getText());
        	if (source.getText().equalsIgnoreCase("Exit")) {
        		exit();
        	}
        }
        
        String s = "Action event detected."
                   + newline
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    Event source: " + source.getText();
        status(s);
    }

    /** 
	 * toolBar和menu共用的动作（createAction()中生成），在各个对应的Action类中响应
	 * JMenuItem菜单项，JRadioButtonMenuItem单选按钮项，在actionPerformed(ActionEvent e)中响应
	 * JCheckBoxMenuItem复选按钮，在itemStateChanged(ItemEvent e)中响应
	 */
    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        
        // 复选框选择，PTNet or Marking graph
        for (Map.Entry<String, JCheckBoxMenuItem> entry : PTNetOrMarkingGraph.entrySet()) {
    	    if (source == entry.getValue()) {
    	        boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
    	    	System.out.println("selected:" + selected + "," + entry.getKey());
    	    	break;
    	    }
    	}
        
        String s = "Item event detected."
                   + newline
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    Event source: " + source.getText()
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        status(s);
    }
    
    /**
     * 显示状态   下--左
     * @param status 状态信息
     */
    public void status(String status) {
    	 output.append(status + newline);
         output.setCaretPosition(output.getDocument().getLength());
         System.out.println(status);
    }
    /**
     * 显示状态：下--中
     * @param status 状态信息
     */
    public void status1(String status) {
    	 output1.append(status + newline);
         output1.setCaretPosition(output1.getDocument().getLength());
         System.out.println(status);
    }
	/**
	 * toolBar,ptnetGraphComponent,status<br>
	 * 使用BorderLayout布局，其五个区域如下：<br>
	 * BorderLayout.PAGE_START<br>
	 * BorderLayout.LINE_START，BorderLayout.CENTER，BorderLayout.LINE_END<br>
	 * BorderLayout.PAGE_END<br>
	 *  
	 * @return
	 */
    private Container addComponentsToPane() {
		
		JPanel contentPane = new JPanel(new BorderLayout());
		//
		JPanel contentPane1 = new JPanel(new BorderLayout());
		contentPane1.setOpaque(true);
		//
		contentPane.setOpaque(true);
		
		JToolBar toolBar = createToolBar();
		contentPane.add(toolBar, BorderLayout.PAGE_START);
		
		// the ptnetGraphComponent
		JScrollPane netPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		netPane.setViewportView(ptnetGraphComponent);
			
		// the markingGraphComponent
		JScrollPane markingPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		markingPane.setViewportView(ptMarkingGraphComponent);
			
		// JSplitPane is used to divide two (and only two) Components
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,netPane, markingPane);
		// true to specify that the split pane should provide a collapse/expand widget
		splitPane.setOneTouchExpandable(true);
		// true if the components should continuously be redrawn as the divider changes position
		splitPane.setContinuousLayout(true);
		
		//markingPane.setVisible(false);
		//netPane.setVisible(false);
		
		//contentPane.add(splitPane, BorderLayout.LINE_START);
		contentPane.add(splitPane, BorderLayout.CENTER);  // 这一部分区域大小是动态的，随着窗口大小或内容大小动态改变，其他部分非动态
		//contentPane.add(splitPane, BorderLayout.LINE_END);
          
		
		 contentPane.add(contentPane1, BorderLayout.PAGE_END);
		
		//Create a scrolled status text area.
        output2 = new JTextArea(15,50);
        output2.setEditable(false);
        JScrollPane statusPane = new JScrollPane(output2);
        
        output1 = new JTextArea(15, 50);
        output1.setEditable(false);
        JScrollPane statusPane1 = new JScrollPane(output1);
        
        output = new JTextArea(15, 50);
        output.setEditable(false);
        JScrollPane statusPane2 = new JScrollPane(output);

        //Add the text area to the content pane.
        
        contentPane1.add(statusPane2, BorderLayout.WEST);
        contentPane1.add(statusPane1, BorderLayout.CENTER);
        contentPane1.add(statusPane, BorderLayout.EAST);
        
        
        return contentPane;
	}
	
    // Returns just the class name -- no package info.
    public String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
    
	/**
	 * exit program
	 */
	public void exit()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(ptnetGraphComponent);

		if (frame != null)
		{
			frame.dispose();
		}
	}
   
    /**
     * Create the GUI and show it.  For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    public static JFrame createAndShowGUI(PTNet ptnet) {		
    	//Create and set up the window.
    	JFrame frame = new JFrame("PTNet and MarkingGraph");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	//Create and set up the content pane.
    	PTNetGraph ptgraph = new PTNetGraph(ptnet);
    	frame.setJMenuBar(ptgraph.createMenuBar());
    	
    	frame.setContentPane(ptgraph.addComponentsToPane());
    	
    	//Display the window.
    	//frame.setSize(450, 260);
    	frame.pack();
    	frame.setVisible(true);
    	return frame;
    }
    
    public static PTNet Create(){
    	
    
    	
    	PTNetGraph pGraph = new PTNetGraph();
    	PTNet ptnet = pGraph.getPtNet();
    	
    	if(ptnet == null){
    		//如果为空，则给定一个默认的网
    		ptnet = CreatePetriNet.createPTnet1();
    	}else{
    		//如果不为空：则说明是点击open按钮来创建的网：可能是pnt文件形式创建的网，也可能是txt创建的网
    	}
    	return ptnet;
    }
    
    public static void main(String[] args) {
    	//PTNet ptnet = CreatePetriNet.createPTnet1(); // 创建PTNet对象
    	//PTNet ptnet = PNGenerator.sharedResource(2, 1);  // states: 15
    	//PTNet ptnet = PNGenerator.producerConsumer(5, 1);  // states: 1860
//    	PTNetGraphComponent ptnetGraphComponent = new PTNetGraphComponent(ptnet); 
// 		try {
// 			ptnetGraphComponent.initialize();
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		
    	PTNet ptnet = Create();
    	
    	
    	// 设置本属性将改变窗口边框样式定义----连接beautyeye(美化包)
		BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			// 设置按钮不可见
			UIManager.put("RootPane.setupButtonVisible", false);
			/*new PntWindow(null);*/
			 javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		                frame = createAndShowGUI(ptnet); // 显示PTNet对应的图形
		            }
		        });
    				
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public PTNet getPtNet() {
		return ptNet;
	}

	public void setPtNet(PTNet ptNet) {
		this.ptNet = ptNet;
	}
}
