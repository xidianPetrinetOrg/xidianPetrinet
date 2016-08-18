package edu.xidian.petrinet;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

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
    
    /** vertex label position,如果改变，请注意在createMenuBar()中，修改快捷键，现在是：L,R,T,B */
    private String[] labelPositionStr = {"Left","Right","Top","Bottom"};
    
    /** 表示vertex label position 菜单项，key: SwingConstants.LEFT,RIGHT,TOP,BOTTOM */
    private Map<String,JMenuItem> labelPositionItem = new HashMap<>();
   
    public PTNetGraph(PTNetGraphComponent ptnetGraph) {
		this.ptnetGraph = ptnetGraph;
	}

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
        
        // JMenuItems for forth menu
        for (int i = 0; i < labelPositionStr.length; i++) {
        	menuItem = new JMenuItem(labelPositionStr[i]);
        	menuItem.addActionListener(this);
        	menu.add(menuItem);
        	labelPositionItem.put(labelPositionStr[i], menuItem);
        }
     
        // Sets the keyboard mnemonic 
        labelPositionItem.get("Left").setMnemonic(KeyEvent.VK_L);
        labelPositionItem.get("Right").setMnemonic(KeyEvent.VK_R);
        labelPositionItem.get("Top").setMnemonic(KeyEvent.VK_T);
        labelPositionItem.get("Bottom").setMnemonic(KeyEvent.VK_B);
        
        return menuBar;
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
        	for (Map.Entry<String, JMenuItem> entry : labelPositionItem.entrySet()) {
        		if (source == entry.getValue()) {
        			if (entry.getKey() == "Left")
        			    ptnetGraph.changeLabelPosition(SwingConstants.LEFT);
        			else if (entry.getKey() == "Right")
    			        ptnetGraph.changeLabelPosition(SwingConstants.RIGHT);
        			else if (entry.getKey() == "Top")
        				ptnetGraph.changeLabelPosition(SwingConstants.TOP);
        			else if (entry.getKey() == "Bottom")
        				ptnetGraph.changeLabelPosition(SwingConstants.BOTTOM);
        		}
        	}
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
