package edu.xidian.petrinet;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;

import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;

/**
 * PTNet Graph and It's marking graph 
 */
public class PTNetGraph implements ActionListener, ItemListener {
	/** 输出状态信息 */
    private JTextArea output;
    private static final String newline = "\n";
    
    /** PTNet Graph */
    private PTNetGraphComponent ptnetGraph = null;
    
    /** 图的朝向,如果改变，请注意在createMenuBar()中，修改快捷键，现在是：N,W,S,E */
    private String[] orientationStr = {"NORTH","WEST","SOUTH","EAST"};
    
    /** 表示PTnet graph的朝向的单选按钮，key: orientationStr */
    private Map<String,JRadioButtonMenuItem> netOrientationRadioBtn = new HashMap<>();
    
    /** 表示Marking graph的朝向的单选按钮，key: orientationStr */
    private Map<String,JRadioButtonMenuItem> markingOrientationRadioBtn = new HashMap<>();
    
    /** 表示选择PTNet graph或Making graph,或二者皆选。 Key: "PTNet","Marking" */
    private Map<String,JCheckBoxMenuItem> PTNetOrMarkingGraph = new HashMap<>();
    
    protected mxUndoManager undoManager;
    
    public PTNetGraph(PTNetGraphComponent ptnetGraph) {
		this.ptnetGraph = ptnetGraph;
		
		this.undoManager = ptnetGraph.getUndoManager();
	
		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);
	}
    
    private Action LabelLeftAction, LabelRightAction, LabelTopAction, LabelBottomAction,
                   undoHistoryAction, redoHistoryAction;

	private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        // Create the menu bar.
        menuBar = new JMenuBar();

        // Build the first menu.
        menu = new JMenu("Graph");
        menu.setMnemonic(KeyEvent.VK_G);
        menuBar.add(menu);

        // JMenuItems for first menu
        menuItem = new JMenuItem("Open");
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        //a group of check box menu items, 表示选择PTNet graph或Making graph,或二者皆选
        cbMenuItem = new JCheckBoxMenuItem("PTNet");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(this);
        menu.add(cbMenuItem);
        PTNetOrMarkingGraph.put("PTNet", cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Marking");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(this);
        menu.add(cbMenuItem);
        PTNetOrMarkingGraph.put("Marking", cbMenuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(this);
        menu.add(menuItem);


        // Build the second menu， "PTNet"
        menu = new JMenu("PTNet");
        menu.setMnemonic(KeyEvent.VK_P);
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
        // Sets the keyboard mnemonic 
        netOrientationRadioBtn.get("NORTH").setMnemonic(KeyEvent.VK_N);
        netOrientationRadioBtn.get("WEST").setMnemonic(KeyEvent.VK_W);
        netOrientationRadioBtn.get("SOUTH").setMnemonic(KeyEvent.VK_S);
        netOrientationRadioBtn.get("EAST").setMnemonic(KeyEvent.VK_E);
        
        // Build the third menu， "Marking"
        menu = new JMenu("Marking");
        menu.setMnemonic(KeyEvent.VK_M);
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
        markingOrientationRadioBtn.get("NORTH").setMnemonic(KeyEvent.VK_N);
        markingOrientationRadioBtn.get("WEST").setMnemonic(KeyEvent.VK_W);
        markingOrientationRadioBtn.get("SOUTH").setMnemonic(KeyEvent.VK_S);
        markingOrientationRadioBtn.get("EAST").setMnemonic(KeyEvent.VK_E);
        
        // Build the forth menu， "Label"
        menu = new JMenu("Label");
        menu.setMnemonic(KeyEvent.VK_L);
        menuBar.add(menu);
        
    	createAction();
        menuItem = new JMenuItem(LabelLeftAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(LabelRightAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(LabelTopAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(LabelBottomAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        // Build the "Edit" menu
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);
        
        menuItem = new JMenuItem(undoHistoryAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        menuItem = new JMenuItem(redoHistoryAction);
        menuItem.setIcon(null); //arbitrarily chose not to use icon
        menu.add(menuItem);
        
        return menuBar;
    }
	
	private JToolBar createToolBar() {
		JButton button = null;

		// Create the toolbar.
		JToolBar toolBar = new JToolBar();
	
		// first button
		button = new JButton(LabelLeftAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// second button
		button = new JButton(LabelRightAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);

		// third button
		button = new JButton(LabelTopAction);
		if (button.getIcon() != null) {
			button.setText(""); // an icon-only button
		}
		toolBar.add(button);
		
		// forth button
		button = new JButton(LabelBottomAction);
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
	
	public class LabelLeftAction extends AbstractAction {
		private static final long serialVersionUID = -108378340049716606L;
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
			 ptnetGraph.changeLabelPosition(SwingConstants.LEFT);
		}
	}
	
	public class LabelRightAction extends AbstractAction {
		private static final long serialVersionUID = 2385080638496501057L;
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
			 ptnetGraph.changeLabelPosition(SwingConstants.RIGHT);
		}
	}
	
	public class LabelTopAction extends AbstractAction {
		private static final long serialVersionUID = -4044696356454034562L;
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
			ptnetGraph.changeLabelPosition(SwingConstants.TOP);
		}
	}
	
	public class LabelBottomAction extends AbstractAction {
		private static final long serialVersionUID = 245505196846345639L;
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
			ptnetGraph.changeLabelPosition(SwingConstants.BOTTOM);
		}
	}
	
	@SuppressWarnings("serial")
	public class HistoryAction extends AbstractAction {
		protected boolean undo;

		/**
		 * Creates an Action with the specified name and small icon.
		 * @param name the name (Action.NAME) for the action, a value of null is ignored
		 * @param icon the small icon (Action.SMALL_ICON) for the action; a value of null is ignored
		 * @param desc description for the action, used for tooltip text
		 * @param mnemonic
		 * @param undo true,undo; false,redo
		 */
		public HistoryAction(String name, ImageIcon icon, String desc, Integer mnemonic,boolean undo) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
			this.undo = undo;
		}
		
		public HistoryAction(boolean undo) {
			this.undo = undo;
		}

		public void actionPerformed(ActionEvent e) {
			if (ptnetGraph.getGraph() != null) {
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
			mxGraph graph = ptnetGraph.getGraph();
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
	 * Create the actions shared by the toolbar and menu.
	 */
	private void createAction() {

		LabelLeftAction = new LabelLeftAction("Left", createNavigationIcon("left"), "left label",
				new Integer(KeyEvent.VK_L));

		LabelRightAction = new LabelRightAction("Right", createNavigationIcon("right"), "right label",
				new Integer(KeyEvent.VK_R));

		LabelTopAction = new LabelTopAction("Top", createNavigationIcon("top"), "top label",
				new Integer(KeyEvent.VK_T));
		
		LabelBottomAction = new LabelBottomAction("Bottom", createNavigationIcon("bottom"), "bottom label",
				new Integer(KeyEvent.VK_B));
		
		undoHistoryAction = new HistoryAction("undo", createNavigationIcon("undo"), "undo",
				new Integer(KeyEvent.VK_U),true);
		redoHistoryAction = new HistoryAction("redo", createNavigationIcon("redo"), "redo",
				new Integer(KeyEvent.VK_W),false);
		
		
	}

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
        	    	ptnetGraph.setOrientation(orientation); // 改变图的朝向
        	    	
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
        	System.out.println("Menu item selected:" + source.getText());
        }
        
        String s = "Action event detected."
                   + newline
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    Event source: " + source.getText();
        status(s);
    }

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
    }
    
	private Container addComponentsToPane( ) {
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		
		JToolBar toolBar = createToolBar();
		contentPane.add(toolBar, BorderLayout.PAGE_START);
		
		// Add the ptnetGraph to the content pane.
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(ptnetGraph);
		contentPane.add(scroll, BorderLayout.CENTER);  // 这一部分区域大小是动态的，随着窗口大小或内容大小动态改变，其他部分非动态
//		contentPane.add(scroll, BorderLayout.PAGE_START);
//		contentPane.add(scroll, BorderLayout.LINE_START);
		
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
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    /**
     * Create the GUI and show it.  For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(PTNetGraphComponent ptnetGraph) {		
        //Create and set up the window.
        JFrame frame = new JFrame("PTNet and MarkingGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        PTNetGraph demo = new PTNetGraph(ptnetGraph);
        frame.setJMenuBar(demo.createMenuBar());
       
		frame.setContentPane(demo.addComponentsToPane());

        //Display the window.
        //frame.setSize(450, 260);
		frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
    	PTNet ptnet = CreatePetriNet.createPTnet1();
    	PTNetGraphComponent ptnetGraph = new PTNetGraphComponent(ptnet);
 		try {
 			ptnetGraph.initialize();
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
    	//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(ptnetGraph);
            }
        });
    }
}
