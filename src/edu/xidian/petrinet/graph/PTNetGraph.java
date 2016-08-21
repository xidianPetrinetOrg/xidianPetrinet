package edu.xidian.petrinet.graph;

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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;
import edu.xidian.petrinet.CreatePetriNet;

/**
 * PTNet Graph and It's marking graph, 提供可视化编辑功能 ：<br>
 * 图形显示朝向：东、西、南、北<br>
 * 顶点Label的显示位置; undo,redo<br>
 */
public class PTNetGraph implements ActionListener, ItemListener {
	/** 输出状态信息 */
    private JTextArea output;
    private static final String newline = "\n";
    
    /** PTNet GraphComponent */
    private PTNetGraphComponent ptnetGraphComponent = null;
    
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
                   undoHistoryAction, redoHistoryAction;
    
    protected mxUndoManager undoManager;
    
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
    	}
    };
    
    /**
     * 构造PTNetGraph对象，PTNet Graph and It's marking graph, 提供可视化编辑功能 
     * @param ptnetGraphComponent
     */
    public PTNetGraph(PTNetGraphComponent ptnetGraphComponent) {
		this.ptnetGraphComponent = ptnetGraphComponent;
		
		this.undoManager = ptnetGraphComponent.getUndoManager();
		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);
		
		 // Create the actions shared by the toolBar and menu. toolBar和menu共用的动作在这里生成, 按键助记符是Alt的组合键
    	createAction();
    	
    	//Add a couple of emacs key bindings for undo,redo,undo,redo快捷按键（CTRL-Z/CTRL-Y）
	    addBindings();
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

        // JMenuItems for first menu
        menuItem = new JMenuItem("Open");
        menuItem.setMnemonic(keyCode.get("open"));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        // save
        menuItem = new JMenuItem("Save");
        //menuItem.setMnemonic(keyCode.get("save")); // 无效
        menuItem.setAction(new SaveAction("Save","Save graph...",keyCode.get("save"))); // 第三个参数指定了助记键（ALT组合键）
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
        
        // Build the third menu: "Marking"
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
        
        // Build the forth menu: "Label"
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
		toolBar.add(button);
		
		return toolBar;
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
		public LabelRightAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon);
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
		public LabelTopAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon);
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
		public LabelBottomAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			ptnetGraphComponent.changeLabelPosition(SwingConstants.BOTTOM);
		}
	}
	
	
	/** Save Graph */
	@SuppressWarnings("serial")
	public class SaveAction extends AbstractAction {
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
			mxGraphComponent graphComponent = new mxGraphComponent(ptnetGraphComponent.getGraph());
			mxGraph graph = ptnetGraphComponent.getGraph();
			//Color bg = graphComponent.getBackground();
			Color bg = null; // 没有背景，才是原汁原味
			BufferedImage image = mxCellRenderer
					.createBufferedImage(graph, null, 1, bg,
							graphComponent.isAntiAlias(), null,
							graphComponent.getCanvas());

			if (image != null)
			{
				try {
					ImageIO.write(image, "png", new File("test.png"));
					status("test.png ok！");
				} catch (IOException e1) {
					e1.printStackTrace();
					status("test.png failed！");
				}
			}
		}
	}
	
	/** Print Graph */
	@SuppressWarnings("serial")
	public class PrintAction extends AbstractAction {
		/**
		 * Creates an Action with the specified name and small icon.
		 * 
		 * @param name
		 *            the name (Action.NAME) for the action, a value of null is
		 *            ignored
		 * @param desc
		 *            description for the action, used for tooltip text
		 * @param mnemonic
		 */
		public PrintAction(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {

			mxGraphComponent graphComponent = new mxGraphComponent(ptnetGraphComponent.getGraph());
			PrinterJob pj = PrinterJob.getPrinterJob();

			if (pj.printDialog()) {
				PageFormat pf = graphComponent.getPageFormat();
				Paper paper = new Paper();
				double margin = 36;
				paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
				pf.setPaper(paper);
				pj.setPrintable(graphComponent, pf);

				try {
					pj.print();
				} catch (PrinterException e2) {
					System.out.println(e2);
				}
			}
		}
	}
	
	/** 历史动作 */
	@SuppressWarnings("serial")
	public class HistoryAction extends AbstractAction {
		protected boolean undo;

		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param undo true,undo; false,redo
		 */
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
        	for (Map.Entry<String, JRadioButtonMenuItem> entry : netOrientationRadioBtn.entrySet()) {
        	    if (source == entry.getValue()) {
        	    	selected = entry.getKey();
        	    	System.out.println("PTNet selected:" + selected);
        	    	int orientation = SwingConstants.NORTH;
        	    	if (selected == "WEST") orientation = SwingConstants.WEST;
        	    	else if (selected == "EAST") orientation = SwingConstants.EAST;
        	    	else if (selected == "SOUTH") orientation = SwingConstants.SOUTH;
        	    	ptnetGraphComponent.setOrientation(orientation); // 改变图的朝向
        	    	
        	    	break;
        	    }
        	}
        	if (selected == null) {
		    	for (Map.Entry<String, JRadioButtonMenuItem> entry : markingOrientationRadioBtn.entrySet()) {
		    	    if (source == entry.getValue()) {
		    	    	selected = entry.getKey();
		    	    	System.out.println("Marking selected:" + selected);
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
     * 显示状态
     * @param status 状态信息
     */
    public void status(String status) {
    	 output.append(status + newline);
         output.setCaretPosition(output.getDocument().getLength());
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
    private Container addComponentsToPane( ) {
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		
		JToolBar toolBar = createToolBar();
		contentPane.add(toolBar, BorderLayout.PAGE_START);
		
		// Add the ptnetGraphComponent to the content pane.
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(ptnetGraphComponent);
		//contentPane.add(scroll, BorderLayout.LINE_START);
		contentPane.add(scroll, BorderLayout.CENTER);  // 这一部分区域大小是动态的，随着窗口大小或内容大小动态改变，其他部分非动态
		//contentPane.add(scroll, BorderLayout.LINE_END);
          

		// add the marking graph
		//JButton button = new JButton("Line end Button (LINE_END)");
		//contentPane.add(button, BorderLayout.LINE_END);
		
		//Create a scrolled status text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        JScrollPane statusPane = new JScrollPane(output);

        //Add the text area to the content pane.
        contentPane.add(statusPane, BorderLayout.PAGE_END);
        
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
    public static void createAndShowGUI(PTNetGraphComponent ptnetGraphComponent) {		
        //Create and set up the window.
        JFrame frame = new JFrame("PTNet and MarkingGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        PTNetGraph ptgraph = new PTNetGraph(ptnetGraphComponent);
        frame.setJMenuBar(ptgraph.createMenuBar());
       
		frame.setContentPane(ptgraph.addComponentsToPane());

        //Display the window.
        //frame.setSize(450, 260);
		frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
    	PTNet ptnet = CreatePetriNet.createPTnet1(); // 创建PTNet对象
    	PTNetGraphComponent ptnetGraphComponent = new PTNetGraphComponent(ptnet); 
 		try {
 			ptnetGraphComponent.initialize();
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		
    	//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(ptnetGraphComponent); // 显示PTNet对应的图形
            }
        });
    }
}
