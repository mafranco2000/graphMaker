import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class NodeManager {
	int nodesCtr;
	Waypoint[] pointList;
	String nodeType;
	static int normalizer = 0;
	JFrame window;
	JPanel topPanel = new JPanel();
	JPanel middlePanel1 = new JPanel();
	JPanel middlePanel2 = new JPanel();
	JScrollPane bottomPanel = new JScrollPane();
	JRadioButton nodeTypeQR = new JRadioButton("QR code node", true);
	JRadioButton nodeTypeTerminal = new JRadioButton("Terminal node", false);
	EdgeManager em;
	FloorViewer fv;

	public NodeManager() throws IOException{
		nodesCtr = 0;
		pointList = new Waypoint[999];
		makeGUI();
	}
	
	public void setFloorViewer(FloorViewer fv){
		this.fv = fv;
	}
	
	public void setEdgeManager(EdgeManager em){
		this.em = em;
		em.setPointList(pointList, nodesCtr);
	}
	
	public int getNodesCtr(){
		return nodesCtr;
	}
	
	public Waypoint[] getPoints(){
		return pointList;
	}
	
	public void addNode(int posX, int posY, int id, String floor){
		if (nodeTypeQR.isSelected()){
			nodeType = "QRPoint";
		} else {
			nodeType = "terminal";
		}
		pointList[nodesCtr] = new Waypoint(posX, posY, id, floor, nodeType, normalizer);
		nodesCtr++;
		em.setPointList(pointList, nodesCtr);
		showDialog("Added node " + id + " at (" + posX + ", " + posY + ") of " + floor);
	}
	
	private void loadNodes() throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("nodes.bak"));
		Waypoint[] loadedList = (Waypoint[])in.readObject();
		in.close();
		nodesCtr = loadedList.length;
		for (int i=0; i<nodesCtr; i++){
			pointList[i] = loadedList[i];
		}
		if (fv != null) {
			fv.window.paintAll(fv.window.getGraphics());
		}
		em.setPointList(pointList, nodesCtr);
		showDialog(""+ nodesCtr + " nodes loaded");
	}
	
	private void saveNodes() throws FileNotFoundException, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("nodes.bak"));
		Waypoint[] savingList = new Waypoint[nodesCtr];
		for (int i=0; i<nodesCtr; i++){
			savingList[i] = pointList[i];
		}
		out.writeObject(savingList);
		out.close();
		showDialog(nodesCtr + " nodes saved");
	}
	
	private void deleteLastNode(){
		if (nodesCtr > 0){
			pointList[nodesCtr] = null;
			nodesCtr--;
			em.setPointList(pointList, nodesCtr);
			fv.window.paintAll(fv.window.getGraphics());
			showDialog("Node " + nodesCtr + " deleted");
		}
	}
	
	private void makeGUI(){
		window = new JFrame("Node manager");
		window.setLayout(new GridLayout(4, 1));
		window.add(topPanel);
		window.add(middlePanel1);
		window.add(middlePanel2);
		window.add(bottomPanel);
		window.setSize(330, 135);
		window.setLocation(0, 250);
		JButton loadButton = new JButton("Load nodes");
		loadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					loadNodes();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton saveButton = new JButton("Save nodes");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					saveNodes();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton undoButton = new JButton("Delete last");
		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteLastNode();
			}
		});
		topPanel.add(loadButton);
		topPanel.add(saveButton);
		topPanel.add(undoButton);
		ButtonGroup typeGroup = new ButtonGroup();
		typeGroup.add(nodeTypeQR);
		typeGroup.add(nodeTypeTerminal);
		middlePanel1.add(nodeTypeQR);
		middlePanel1.add(nodeTypeTerminal);
		JRadioButton northBtn = new JRadioButton("North", true);
		northBtn.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				NodeManager.normalizer = 0;
			}
			
		});
		JRadioButton eastBtn = new JRadioButton("East", false);
		eastBtn.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				NodeManager.normalizer = 90;
			}
			
		});
		JRadioButton southBtn = new JRadioButton("South", false);
		southBtn.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				NodeManager.normalizer = 180;
			}
			
		});
		JRadioButton westBtn = new JRadioButton("West", false);
		westBtn.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent arg0) {
				NodeManager.normalizer = 270;
			}
			
		});
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(northBtn);
		btnGroup.add(eastBtn);
		btnGroup.add(southBtn);
		btnGroup.add(westBtn);
		middlePanel2.add(northBtn);
		middlePanel2.add(eastBtn);
		middlePanel2.add(southBtn);
		middlePanel2.add(westBtn);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	private void showDialog(String s){
		bottomPanel.getViewport().add(new JLabel(s));
		bottomPanel.getViewport().validate();
	}
}