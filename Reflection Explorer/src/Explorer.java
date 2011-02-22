import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


public class Explorer extends JTree implements TreeExpansionListener, TreeSelectionListener{
	private static ArrayList<Explorer> explorer = new ArrayList<Explorer>(); //For importing and indexing
	private static Dimension listDimension = new Dimension(300,200); //Ease of access
	private static Class<?>[] primitiveTypes = {
			Boolean.class,
			Boolean.TYPE,
			Integer.class,
			Integer.TYPE,
			Double.class,
			Double.TYPE,
			Short.class,
			Short.TYPE,
			Long.class,
			Long.TYPE,
			Float.class,
			Float.TYPE,
			Byte.class,
			Byte.TYPE,
			Character.TYPE,
			Character.class
	};
	private static final long serialVersionUID = 1L; //no warnings, LOL
	/**
	 * Check if an object is primitive
	 * @param obj The object to examine
	 * @return if it's primitive
	 */
	public static boolean isPrimitive(Object obj){
		if(obj == null)
			return true;
		Class<?> c = obj.getClass();
		if(c.isArray())
			return false;
		for (int i = 0; i < primitiveTypes.length; i++) {
			if(c.equals(primitiveTypes[i]))
				return true;
		}
		return false;
	}
	public static void main(String[] args){
		new Explorer(new Object[20]);
	}
	/**
	 * Register an explorer for indexing and importing. Internal use only
	 * @param e The explorer. Usually this
	 */
	private static void register(Explorer e){
		explorer.add(e);
		updateTitles();
	}
	/**
	 * Remove an explorer from indexing and importing. Internal use only.
	 * @param e The explorer. Usually this
	 */
	private static void remove(Explorer e){
		explorer.remove(e);
		updateTitles();
	}
	/**
	 * Updates the titles of all explorers to display index
	 */
	private static void updateTitles(){
		if(explorer.isEmpty()) //exit if there are no open explorers
			System.exit(0);
		for (int i = 0; i < explorer.size(); i++) {
			explorer.get(i).container.setTitle("Reflection explorer " + i);
		}
	}
	//Variables declaration
	private JButton addArg;
	private JButton addNull;
	private JList args;
	private Vector<Object> argV = new Vector<Object>(); //store in a vector for compatibility
	private JFrame container;
	private JButton createPrimitive;
	private JButton explore;
	private JButton fetchStatic;
	private JButton imp0rt;
	private JButton instantiate;
	private JButton invoke;
	private JList methods;
	
	private JList objects;
	private Vector<Object> objectV = new Vector<Object>(); //store in a vector for compatibility
	private JButton remove;
	private JButton removeArg;
	
	public Explorer(Object obj){
		super(new ClassNode(obj, null, false, "Root", obj.getClass().getName(), null), false); //call super with specified root
		container = new JFrame("Reflection explorer"); //set name, make frame
		/* Initialize main GUI */
		JSplitPane contentPane = new JSplitPane();
		contentPane.add(new JScrollPane(this), JSplitPane.TOP);
		contentPane.add(initTabs(), JSplitPane.BOTTOM); //build control tabs
		container.setContentPane(contentPane);
		container.pack();
		container.setVisible(true);
		register(this); //Allow indexing
		addTreeExpansionListener(this);
		addTreeSelectionListener(this);
		setModel(new DefaultTreeModel((TreeNode) this.getModel().getRoot()));
		final Explorer this$0 = this; //for reference when fields are hidden
		container.addWindowListener(new WindowListener() {
			
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				remove(this$0); //unregister
				container.dispose(); //dispose
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
	}
	
	/**
	 * Adds an object to the object list
	 * @param obj The object
	 * @param container The container field that defined the object
	 */
	private void addObject(Object obj, Field container){
		ObjectWrapper wrapper = new ObjectWrapper(obj, container);
		addWrapper(wrapper);
	}
	
	/**
	 * Adds an ObjectWrapper to the object list
	 * @param ow The wrapper
	 */
	private void addWrapper(ObjectWrapper ow){
		if(objectV.contains(ow)) //no repeats pl0x
			return;
		objectV.add(ow);
		updateObjects();
	}
	
	/**
	 * Initializes the control pane. Only call once :D
	 * @return The control pane
	 */
	private final JTabbedPane initTabs(){
		/* Initialize objects */
		JTabbedPane tabs = new JTabbedPane();
		methods = new JList();
		objects = new JList();
		args = new JList();
		invoke = new JButton("Invoke method");
		remove = new JButton("Remove object");
		explore = new JButton("Explore object");
		fetchStatic = new JButton("Fetch static");
		instantiate = new JButton("Instantiate new object");
		removeArg = new JButton("Remove argument");
		addArg = new JButton("Add to arguments");
		addNull = new JButton("Add null argument");
		imp0rt = new JButton("Import from external explorer");
		createPrimitive = new JButton("Create primitive object");
		/* Create the method tab */
		JPanel methodPane = new JPanel();
		JScrollPane sp = new JScrollPane(methods);
		sp.setPreferredSize(listDimension);
		methodPane.add(sp, BorderLayout.NORTH);
		methodPane.add(invoke, BorderLayout.SOUTH);
		tabs.addTab("Method control", methodPane);
		/* Create the object tab */
		JPanel objectPane = new JPanel();
		sp = new JScrollPane(objects);
		sp.setPreferredSize(listDimension);
		objectPane.add(sp, BorderLayout.NORTH);
		JPanel objectControlPane = new JPanel(new FlowLayout());
		objectControlPane.add(fetchStatic);
		objectControlPane.add(imp0rt);
		objectControlPane.add(createPrimitive);
		objectPane.add(objectControlPane, BorderLayout.CENTER);
		JPanel objectControlPane2 = new JPanel(new FlowLayout()); //lots of buttons, so we need two panels to fit them
		objectControlPane2.add(addArg);
		objectControlPane2.add(remove);
		objectControlPane2.add(explore);
		objectControlPane2.add(instantiate);
		objectPane.add(objectControlPane2, BorderLayout.SOUTH);
		objectPane.setPreferredSize(new Dimension(350, -1)); //can't be bigger than 350 width
		tabs.addTab("Object control", objectPane);
		/* Create the argument tab */
		JPanel argsPane = new JPanel();
		sp = new JScrollPane(args);
		sp.setPreferredSize(listDimension);
		argsPane.add(sp, BorderLayout.CENTER);
		JPanel argsPane2 = new JPanel(new FlowLayout());
		argsPane2.add(removeArg);
		argsPane2.add(addNull);
		argsPane.add(argsPane2, BorderLayout.SOUTH);
		tabs.addTab("Argument control", argsPane);
		/* Add listeners */
		/* You don't really have to read through all this. It's the same crap over and over again. Get to the good stuff. Just look for bunches of code :P */
		createPrimitive.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				final JDialog dialog = new JDialog();
				dialog.setTitle("Create primitive data type " + container.getTitle().replace("Reflection explorer", ""));
				JPanel cp = new JPanel();
				final JComboBox cb = new JComboBox(new String[]{"String", "Double", "Float", "Byte", "Short", "Int", "Long", "Boolean"});
				final JTextField sel = new JTextField(10);
				JButton send = new JButton("Create");
				cp.setLayout(new FlowLayout());
				cp.add(cb);
				cp.add(sel);
				cp.add(send);
				dialog.add(cp);
				dialog.pack();
				dialog.setVisible(true);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				send.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
					}
					
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						String str = (String)cb.getSelectedItem();
						if(str.equals("String"))
							set(sel.getText());
						else if(str.equals("Double"))
							set(Double.parseDouble(sel.getText()));
						else if(str.equals("Float"))
							set(Float.parseFloat(sel.getText()));
						else if(str.equals("Byte"))
							set(Byte.parseByte(sel.getText()));
						else if(str.equals("Short"))
							set(Short.parseShort(sel.getText()));
						else if(str.equals("Int"))
							set(Integer.parseInt(sel.getText()));
						else if(str.equals("Long"))
							set(Long.parseLong(sel.getText()));
						else
							set(Boolean.parseBoolean(sel.getText()));
					}
					
					private void set(Object obj){
						addObject(obj, null);
						dialog.dispose();
					}
				});
			}
		});
		imp0rt.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int idx = -1;
				try{
				idx = Integer.parseInt(JOptionPane.showInputDialog("Explorer index:"));
				} catch (Exception ex){
					return;
				}
				Explorer ex = explorer.get(idx);
				for(Object obj : ex.objectV)
					objectV.add(obj);
				updateObjects();
			}
		});
		addNull.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				argV.add(new ObjectWrapper(null, null));
				args.setListData(argV);
			}
		});
		removeArg.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				for(Object obj : args.getSelectedValues())
					argV.remove(obj);
				args.setListData(argV);
			}
		});
		addArg.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				for(Object obj : objects.getSelectedValues())
					argV.add(obj);
				args.setListData(argV);
			}
		});
		invoke.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent evt) {
				try{
					Object obj = ((MethodWrapper)methods.getSelectedValue()).invoke(argV.toArray());
					if(obj != null && !obj.getClass().equals(void.class))
						new Explorer(obj);
				} catch (Throwable t){
					/* Give the user a GUI with the warning */
					JDialog warning = new JDialog(container);
					warning.setTitle("Error " + container.getTitle().replace("Reflection explorer", ""));
					JTextArea error = new JTextArea();
					error.setEditable(false);
					StringBuffer msg = new StringBuffer();
					msg.append(t.toString()).append('\n');
					for(StackTraceElement elm : t.getStackTrace())
						msg.append('\t').append(elm.toString()).append('\n');
					error.setText(msg.toString());
					warning.add(new JScrollPane(error), BorderLayout.CENTER);
					warning.pack();
					warning.setVisible(true);
				}
			}
		});
		remove.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent evt) {
				for(Object obj : objects.getSelectedValues())
					removeObject((ObjectWrapper)obj);
			}
		});
		instantiate.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent evt) {
				final JDialog creator = new JDialog();
				creator.setTitle("Object creator " + container.getTitle().replace("Reflection explorer", ""));
				final JButton create = new JButton("Instantiate");
				creator.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				final JTextField className = new JTextField("java.lang.Object");
				final JList constructors = new JList(Object.class.getConstructors());
				final JList objects = new JList(argV);
				JPanel pane = new JPanel();
				pane.setLayout(new FlowLayout());
				JScrollPane sp = new JScrollPane(objects);
				sp.setPreferredSize(listDimension);
				pane.add(sp);
				pane.add(new JLabel("Class name:"));
				pane.add(className);
				pane.add(create);
				pane.add(new JScrollPane(constructors));
				className.addKeyListener(new KeyListener() {
					
					@Override
					public void keyPressed(KeyEvent arg0) {
					}
					
					@Override
					public void keyReleased(KeyEvent arg0) {
					}
					
					@Override
					public void keyTyped(KeyEvent arg0) {
						try {
							constructors.setListData(ClassLoader.getSystemClassLoader().loadClass(className.getText()).getConstructors());
							className.setForeground(Color.BLACK);
							constructors.setEnabled(true);
							create.setEnabled(true);
						} catch (ClassNotFoundException e) {
							className.setForeground(Color.RED);
							constructors.setEnabled(false);
							create.setEnabled(false);
						}
					}
				});
				create.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
					}
					
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						try{
							Constructor<?> c = (Constructor<?>)constructors.getSelectedValue();
							c.setAccessible(true);
							addObject(c.newInstance(ObjectWrapper.extract(argV.toArray())), null);
							creator.dispose();
						} catch (Throwable t){
							JDialog warning = new JDialog(container);
							warning.setTitle("Error " + container.getTitle().replace("Reflection explorer", ""));
							JTextArea error = new JTextArea();
							error.setEditable(false);
							StringBuffer msg = new StringBuffer();
							msg.append(t.toString()).append('\n');
							for(StackTraceElement elm : t.getStackTrace())
								msg.append('\t').append(elm.toString()).append('\n');
							error.setText(msg.toString());
							warning.add(new JScrollPane(error), BorderLayout.CENTER);
							warning.pack();
							warning.setVisible(true);
						}
					}
				});
				creator.add(pane);
				creator.pack();
				creator.setVisible(true);
			}
		});
		fetchStatic.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				final JDialog dialog = new JDialog();
				dialog.setTitle("Fetch static " + container.getTitle().replace("Reflection explorer", ""));
				JPanel pane = new JPanel();
				final JList objects = new JList(Integer.class.getFields());
				final JButton send = new JButton("Fetch");
				final JTextField className = new JTextField("java.lang.Integer");
				pane.setLayout(new FlowLayout());
				pane.add(new JLabel("Location: "));
				pane.add(className);
				JScrollPane sp = new JScrollPane(objects);
				sp.setPreferredSize(listDimension);
				pane.add(sp);
				pane.add(send);
				dialog.add(pane);
				className.addKeyListener(new KeyListener() {
					@Override
					public void keyPressed(KeyEvent arg0) {
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						try{
							objects.setListData(ClassLoader.getSystemClassLoader().loadClass(className.getText()).getFields());
							className.setForeground(Color.BLACK);
							objects.setEnabled(true);
							send.setEnabled(true);
						} catch (ClassNotFoundException ex){
							className.setForeground(Color.RED);
							objects.setEnabled(false);
							send.setEnabled(false);
						}
					}
				});
				send.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseClicked(MouseEvent arg0) {
					}
					
					@Override
					public void mouseEntered(MouseEvent arg0) {
					}
					
					@Override
					public void mouseExited(MouseEvent arg0) {
					}
					
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						try {
							Field f = ((Field)objects.getSelectedValue());
							f.setAccessible(true);
							addObject(f.get(null), f);
							dialog.setVisible(false);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				});
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		explore.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseReleased(MouseEvent evt) {
				new Explorer(objects.getSelectedValue());
			}
		});
		return tabs;
	}
	
	private void removeObject(ObjectWrapper obj){
		if(!objectV.contains(obj))
			return;
		objectV.remove(obj);
		updateObjects();
	}
	
	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		ClassNode node = (ClassNode)event.getPath().getLastPathComponent();
		node.dumpChildren();
	}
	
	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		ClassNode node = (ClassNode)event.getPath().getLastPathComponent();
		node.generateChildren();
	}
	
	private void updateMethods(Object obj){
		if(obj == null)
			return;
		Class<?> c = obj.getClass();
		Vector<Object> mw = new Vector<Object>();
		do{
			for(Method m : c.getDeclaredMethods()){
				m.setAccessible(true);
				mw.add(new MethodWrapper(obj, m));
			}
			c = c.getSuperclass();
		} while(c != null);
		methods.setListData(mw);
	}
	
	private void updateObjects(){
		objects.setListData(objectV);
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		ObjectWrapper obj = ((ClassNode)evt.getPath().getLastPathComponent()).getWrapper();
		updateMethods(obj.get());
		addWrapper(obj);
	}
	
}