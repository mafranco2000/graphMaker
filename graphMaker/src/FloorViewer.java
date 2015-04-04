import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class FloorViewer {
	NodeManager nm;
	EdgeManager em;
	JFrame window;
	
	public FloorViewer(String imgPath, NodeManager nm, EdgeManager em) throws IOException{
		this.nm = nm;
		this.em = em;
		nm.setFloorViewer(this);
		em.setFloorViewer(this);
		makeGUI(imgPath);
	}
	
	private void makeGUI(String imgPath) throws IOException{
		BufferedImage bimg = ImageIO.read(new File(imgPath)); 
		int width = bimg.getWidth(); 
		int height = bimg.getHeight(); 
		window = new JFrame("Floor plan from " + imgPath);
		window.getContentPane().add(new JPBackground(imgPath));
		window.setSize(width, height);
		window.setLocation(360, 0);
		window.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	public class JPBackground extends JPanel implements MouseListener {
		String imgPath;
		Image floorPlan;
		int mouseX, mouseY;
		
		public JPBackground(String imgPath) throws IOException {
			this.imgPath = imgPath;
			floorPlan = ImageIO.read(new File(imgPath));
			addMouseListener(this);
		}
		
		public void paintComponent(Graphics g){
			nm.setFloorViewer(FloorViewer.this);
			em.setFloorViewer(FloorViewer.this);
			super.paintComponent(g);
			g.setFont(new Font("Arial", 0, 14));
			g.drawImage(floorPlan, 0, 0, null);
			g.setColor(Color.BLUE);
			Waypoint[] wpList = nm.getPoints();
			int[][] edges = em.getEdges();
			for (int i=0; i<nm.getNodesCtr(); i++){
				for (int j=0; j<nm.getNodesCtr(); j++){
					if (wpList[i].getFloor().equals(imgPath) && edges[i][j] != -1){
						if (wpList[j].getFloor().equals(imgPath)){
							g.drawLine(wpList[i].getX(),wpList[i].getY(), wpList[j].getX(), wpList[j].getY());
							g.drawString("" + edges[i][j], (wpList[i].getX() + wpList[j].getX()) / 2, (wpList[i].getY() + wpList[j].getY()) / 2);	
						} else {
							g.drawOval(wpList[i].getX() - 6, wpList[i].getY() - 6, 12, 12);
						}
					}
				}
			}
			g.setFont(new Font("Arial", 0, 12));
			g.setColor(Color.RED);
			for (int i=0; i<nm.getNodesCtr(); i++){
				if (wpList[i].getFloor().equals(imgPath)){
					g.fillOval(wpList[i].getX() - 5, wpList[i].getY() - 5, 10, 10);
					g.drawString("" + wpList[i].getId(), wpList[i].getX() + 5, wpList[i].getY());	
				}
			}
		}
		
		public void mouseClicked(MouseEvent me) {
			int nodeId = nm.getNodesCtr();
			mouseX = me.getX();
			mouseY = me.getY();
			Graphics g = getGraphics();
			g.setColor(Color.RED);
			g.fillOval(mouseX - 5, mouseY - 5, 10, 10);
			g.drawString("" + nodeId, mouseX + 5, mouseY);
			g.dispose();
			window.getContentPane().validate();
			nm.addNode(mouseX, mouseY, nodeId, imgPath);
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}
