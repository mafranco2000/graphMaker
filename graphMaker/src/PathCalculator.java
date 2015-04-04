import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class PathCalculator {
	JFrame window;
	JPanel topPanel = new JPanel();
	JPanel middlePanel = new JPanel();
	JScrollPane bottomPanel = new JScrollPane();
	JTextField mapDescInput = new JTextField(15);
	EdgeManager em;
	NodeManager nm;
	int [][] weights = new int[999][999];
	int nodesCtr;
	Waypoint[] points;
	int[][] nextHop;
	int[] parent;
	int[] dist;
	
	public PathCalculator(NodeManager nm, EdgeManager em){
		this.nm = nm;
		this.em = em;
		makeGUI();
	}
	
	private void calculate(){
		nodesCtr = nm.getNodesCtr();
		weights = em.getEdges();
		points = nm.getPoints();
		parent = new int[nodesCtr];
		dist = new int[nodesCtr];
		boolean[] inspected = new boolean[nodesCtr];
		nextHop = new int[nodesCtr][nodesCtr];
		showDialog("Calculating shortest paths...");
		for (int src=0; src<nodesCtr; src++){
			for (int i=0; i<nodesCtr; i++){
				inspected[i] = false;
				dist[i] = (int)Double.POSITIVE_INFINITY;
				parent[i] = (int)Double.POSITIVE_INFINITY;
			}
			dist[src] = 0;
			parent[src] = src;
			int minIndex = 0;
			for (int j=0; j<nodesCtr; j++){
				int dmin = (int)Double.POSITIVE_INFINITY;
				for (int i=0; i<nodesCtr; i++){
					if ((dist[i] < dmin) && (!inspected[i])){
						minIndex = i;
						dmin = dist[i];
					}
				}
				inspected[minIndex] = true;
				for (int adj=0; adj<nodesCtr; adj++){
					if ((weights[minIndex][adj] != -1) && (dist[minIndex] + weights[minIndex][adj] < dist[adj])){
						dist[adj] = dist[minIndex] + weights[minIndex][adj];
						parent[adj] = minIndex;
					}
				}
			}
			for (int i=0; i<nodesCtr; i++){
				int j = i;
				while ((parent[j] != src) && (parent[j] != (int)Double.POSITIVE_INFINITY)){
					j = parent[j];
				}
				nextHop[src][i] = j;
			}
		}
		showDialog("Calculation completed");
	}
	
	private void saveXML(){
		if (nextHop == null) {
			showDialog("No calculated paths to save");
		} else {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("map1.xml"));
				out.write("<map>");
				out.newLine();
				out.write("<mapDescription desc='" + mapDescInput.getText() + "'></mapDescription>");
				for (int i=0; i<nodesCtr; i++){
					out.newLine();
					out.write("<node type='" + points[i].getName() + "' code='" + points[i].getId() + "' floor='" + points[i].getFloor() + "' posX='" + points[i].getX() + "' posY='" + points[i].getY() + "'>");
					out.newLine();
					if ((points[i].getName()).equals("QRPoint")){
						for (int j=0; j<nodesCtr; j++){
							out.write("<nextHop dest='" + j + "' next='" + nextHop[i][j] + "'></nextHop>");
							out.newLine();
						}
					} else {
						out.write("<nearby name='SAMPLE Name'></nearby>");
						out.newLine();
					}
					out.write("</node>");
				}
				out.newLine();
				out.write("</map>");
				out.close();
				showDialog("XML saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveCodes(){
		Waypoint[] pointList = nm.getPoints();
		String hour;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("codes.txt"));
			for (int i=0; i<nodesCtr; i++){
				if ((points[i].getName()).equals("QRPoint")){
					out.write("" + points[i].getId() + "X");
					for (int j=0; j<nodesCtr; j++){
						if (weights[i][j] != -1){
							if ((pointList[i].getFloor()).equals(pointList[j].getFloor())){
								hour = angleToHour((getAbsoluteAngle(pointList[i].getX(), pointList[i].getY(), pointList[j].getX(), pointList[j].getY()) + pointList[i].getNormalizer()) % 360);
							} else {
								if ((pointList[i].getFloor()).compareToIgnoreCase(pointList[j].getFloor()) < 0){
									hour = "U";
								} else {
									hour = "V";
								}
							}
							out.write("" + points[j].getId() + hour);
						}
					}
					out.newLine();
				}
			}
			out.close();
			showDialog("Codes saved");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getAbsoluteAngle(int x1, int y1, int x2, int y2){
		double angle = 0.0;
		if (x1 != x2){
			angle = Math.toDegrees(Math.atan2((y1 - y2), (x2 - x1)));
		} else {
			if (y1 < y2){
				angle = -90;
			} else{
				angle = 90;
			}
		}
		if (angle < 0){
			angle = 360 + angle;
		}
		return (int) Math.round(angle);
	}
	
	private String angleToHour(int angle){
		String hour = "D";
		if (angle > 15 && angle <= 45) hour = "C";
		if (angle >45 && angle <= 75) hour = "B";
		if (angle > 75 && angle <= 105) hour = "A";
		if (angle > 105 && angle <= 135) hour = "L";
		if (angle > 135 && angle <= 165) hour = "K";
		if (angle > 165 && angle <= 195) hour = "J";
		if (angle > 195 && angle <= 225) hour = "I";
		if (angle > 225 && angle <= 255) hour = "H";
		if (angle > 255 && angle <= 285) hour = "G";
		if (angle > 285 && angle <= 315) hour = "F";
		if (angle > 315 && angle <= 345) hour = "E";
		return hour;
	}
	
	private void makeGUI(){
		window = new JFrame("Path Calculator");
		window.setLayout(new GridLayout(3, 1));
		window.add(topPanel);
		window.add(middlePanel);
		window.add(bottomPanel);
		window.setSize(330, 135);
		window.setLocation(0, 675);
		JButton calculateButton = new JButton("Calculate paths");
		calculateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				calculate();
			}
		});
		JButton saveButton = new JButton("Generate XML");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveXML();
				saveCodes();
			}
		});
		topPanel.add(calculateButton);
		topPanel.add(saveButton);
		middlePanel.add(new JLabel("Map description:"));
		middlePanel.add(mapDescInput);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	private void showDialog(String s){
		bottomPanel.getViewport().add(new JLabel(s));
		bottomPanel.getViewport().validate();
	}
}
