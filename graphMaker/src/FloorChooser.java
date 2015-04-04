import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class FloorChooser {

	public FloorChooser(File[] floorImgs, final NodeManager nm, final EdgeManager em) throws IOException{
		JFrame window = new JFrame("Floor selection");
		window.setSize(220, 220);
		window.setLayout(new GridLayout(0, 1));
		JButton[] btnList = new JButton[floorImgs.length];
		window.add(new JLabel("Available images:"));
		for (int i=0; i < floorImgs.length; i++){
			if (floorImgs[i].isFile()){
				btnList[i] = new JButton(floorImgs[i].toString());
				btnList[i].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						try {
							new FloorViewer(((JButton)e.getSource()).getText(), nm, em);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				window.add(btnList[i]);
			}
		}
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
