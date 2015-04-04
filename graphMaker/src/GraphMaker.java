import java.io.*;

public class GraphMaker {
	static final String IMGPATH = "res";
		
	public static void main(String[] args) throws IOException {
		  File[] floorImgs = getFloors(IMGPATH);
		  NodeManager nm = new NodeManager();
		  EdgeManager em = new EdgeManager(nm);
		  new PathCalculator(nm, em);
		  new FloorChooser(floorImgs, nm, em);
	}
	
	private static File[] getFloors(String path){
		  File folder = new File(path);
		  File[] listOfFiles = folder.listFiles();
		  return listOfFiles;
	}

}