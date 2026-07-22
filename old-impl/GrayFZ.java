package vpt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import vpt.Algorithm;
import vpt.BooleanImage;
import vpt.GlobalException;
import vpt.Image;
import vpt.IntegerImage;

/**
 * 
 * Gray Flat Zones
 * 
 * Direct implementation by me.
 * 
 * @author yoktish
 *
 */
public class GrayFZ extends Algorithm{
	
	public Image input;
	public IntegerImage output;
	
	private ArrayList<Point> list2 = new ArrayList<Point>();
	private Stack<Point> s = new Stack<Point>();
	private int xdim;
	private int ydim;
	
	// this is for knowing if a pixel has already been
	// added to the stack but not yet processed.
	BooleanImage temp;
	
	// 4 neighbourhood
	//public Point[] N = {new Point(1,0), new Point(0,1), new Point(-1,0),new Point(0,-1)};

	// 8 neighbourhood
	private Point[] N = {new Point(1,0), new Point(0,1), new Point(-1,0),new Point(0,-1),
								new Point(1,1), new Point(-1,-1), new Point(-1,1),new Point(1,-1)};
	
	public GrayFZ(){
		inputFields = "input";
		outputFields = "output";
	}

	public void execute() throws GlobalException {
		
		xdim = input.getXDim();
		ydim = input.getYDim();
		
		if(input.getCDim() != 1) throw new GlobalException("This implementation is only for grayscale images.");
		
		output = new IntegerImage(xdim, ydim, 1);
		output.fill(0);
		
		temp = new BooleanImage(xdim, ydim, 1);
		temp.fill(false);
		
		int label = 1;
		
		for(int x = 0; x < xdim; x++){
		//for(int x = xdim-1; x >= 0; x--){
			
			System.out.println("Sutun " + x);
			
			for(int y = 0; y < ydim; y++){
			//for(int y = ydim-1; y >= 0; y--){
				
				if(output.getXYInt(x, y) > 0) continue;
				
				Point t = new Point(x, y);
				
				createFZ(t, label++);
			}
		}
		
		System.out.println("Total number of quasi flat zones: " + (label - 1));
	}
	
	/**
	 * creates a FZ starting from coordinate x, y
	 * 
	 * @param x
	 * @param y
	 */
	private void createFZ(Point r, int label){
		
		s.clear();
		s.add(r);
		temp.setXYBoolean(r.x, r.y, true);
		list2.add(r);
		
		while(!s.isEmpty()){
			
			Point tmp = s.pop();
			
			int x = tmp.x;
			int y = tmp.y;
			
			int p = input.getXYByte(x, y);
			output.setXYInt(x, y, label);
			
			for(int i = 0; i < N.length; i++){
				int _x = x + N[i].x;
				int _y = y + N[i].y;
				
				if(_x < 0 || _x >= xdim || _y < 0 || _y >= ydim) continue;
				
				if(temp.getXYBoolean(_x, _y) == true) continue;
				
				int q = input.getXYByte(_x, _y);
			
				if(q == p){

					Point t = new Point(_x, _y);
					s.add(t);
					
					temp.setXYBoolean(t.x, t.y, true);
					list2.add(t);
				}
			}
		}
		
		for(Point t: list2)
			temp.setXYBoolean(t.x, t.y, false);
		list2.clear();
	}
	
	public static Image invoke(Image img) {
		try{
			return (IntegerImage)new GrayFZ().preprocess(img);			
		}catch(GlobalException e){
			e.printStackTrace();
			return null;
		}
	}
}
