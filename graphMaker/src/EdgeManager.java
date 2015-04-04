import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class EdgeManager {
	JFrame window;
	JPanel topPanel = new JPanel();
	JPanel middlePanel = new JPanel();
	JScrollPane bottomPanel = new JScrollPane();
	@SuppressWarnings("rawtypes")
	JComboBox picker1 = new JComboBox(), picker2 = new JComboBox();
	JTextField weightInput = new JTextField(3);
	int[] pointIds = new int[999];
	int[][] edges = new int[999][999];
	int nodesCtr;
	FloorViewer fv;
	
	public EdgeManager(NodeManager nm){
		nm.setEdgeManager(this);
		for (int i=0; i<999; i++){
			for (int j=0; j<999; j++){
				edges[i][j] = -1;
			}
		}
		makeGUI();
	}
	
	public void setFloorViewer(FloorViewer fv){
		this.fv = fv;
	}
	
	public int[][] getEdges(){
		return edges;
	}
	
	@SuppressWarnings("unchecked")
	public void setPointList(Waypoint[] ptList, int nodesCtr){
		for (int i=0; i<nodesCtr; i++){
			pointIds[i] = ptList[i].getId();
		}
		picker1.removeAllItems();
		picker2.removeAllItems();
		for (int i=0; i<nodesCtr; i++){
			picker1.addItem(pointIds[i]);
			picker2.addItem(pointIds[i]);
		}
		showDialog("There are " + nodesCtr + " nodes");
	}
	
	private void makeEdge(int nodeA, int nodeB){
		if (!(weightInput.getText().equals(""))){
			edges[nodeA][nodeB] = Integer.parseInt(weightInput.getText());
			edges[nodeB][nodeA] = Integer.parseInt(weightInput.getText());
			showDialog("Added edge between " + nodeA + " and " + nodeB + " with Weight " + Integer.parseInt(weightInput.getText()));
			if (fv != null) fv.window.paintAll(fv.window.getGraphics());
		} else {
			showDialog("Weight must be specified!");
		}

	}
	
	private void deleteEdge(int nodeA, int nodeB){
		edges[nodeA][nodeB] = -1;
		edges[nodeB][nodeA] = -1;
		bottomPanel.getViewport().add(new JLabel("Deleted edge between " + nodeA + " and " + nodeB));
		bottomPanel.getViewport().validate();
		if (fv != null) fv.window.paintAll(fv.window.getGraphics());
	}
	
	private void loadEdges() throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("edges.bak"));
		edges = (int[][])in.readObject();
		in.close();
		if (fv != null) {
			fv.window.paintAll(fv.window.getGraphics());
		}
		showDialog("Edges loaded");
	}
	
	private void saveEdges() throws FileNotFoundException, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("edges.bak"));
		out.writeObject(edges);
		out.close();
		showDialog("Edges saved");
	}
	
	private void makeGUI(){
		window = new JFrame("Edge manager");
		window.setLayout(new GridLayout(3, 1));
		window.add(topPanel);
		window.add(middlePanel);
		window.add(bottomPanel);
		window.setSize(330, 230);
		window.setLocation(0, 415);
		JButton loadButton = new JButton("Load edges");
		loadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					loadEdges();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton saveButton = new JButton("Save edges");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					saveEdges();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		topPanel.add(loadButton);
		topPanel.add(saveButton);
		middlePanel.add(new JLabel("Node A: "));
		middlePanel.add(picker1);
		middlePanel.add(new JLabel("Node B: "));
		middlePanel.add(picker2);
		middlePanel.add(new JLabel("Weight: "));
		middlePanel.add(weightInput);
		JButton makeButton = new JButton("Make edge");
		middlePanel.add(makeButton);
		makeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				makeEdge(picker1.getSelectedIndex(), picker2.getSelectedIndex());
			}
		});
		JButton deleteButton = new JButton("Delete edge");
		middlePanel.add(deleteButton);
		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteEdge(picker1.getSelectedIndex(), picker2.getSelectedIndex());
			}
		});
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	private void showDialog(String s){
		bottomPanel.getViewport().add(new JLabel(s));
		bottomPanel.getViewport().validate();
	}
}
