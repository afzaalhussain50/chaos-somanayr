package circos;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataSet {

	public static DataSet read(File f) throws IOException{
		BufferedReader read = new BufferedReader(new FileReader(f));
		DataSet ds = new DataSet();
		String s;
		while((s = read.readLine()).startsWith("#DEF"))
				ds.define(s.split(" ")[1], s.split(" ")[2]);
		String[] set = s.split(" "); //color scheme
		ds.colorScheme = new int[set.length];
		for (int i = 0; i < set.length; i++) {
			ds.colorScheme[i] = getColor(set[i]).getRGB();
		}

		ds.bezierOutter = getColor(read.readLine()).getRGB(); //bezier outer

		set = read.readLine().split(" "); //sizes
		ds.sizes = new double[set.length];
		ds.labels = new String[set.length];
		for (int i = 0; i < set.length; i++) {
			ds.labels[i] = set[i].substring(0, set[i].lastIndexOf('|'));
			ds.sizes[i] = Double.parseDouble(set[i].substring(set[i].lastIndexOf('|') + 1));
		}

		set = read.readLine().split(" ");
		if(set.length == 2 && set[0].equalsIgnoreCase("No") && set[1].equalsIgnoreCase("groups")){ //no groups
			ds.groupNames = null;
			ds.groupEnd = null;
		} else { // yes groups
			ds.groupNames = new String[set.length];
			ds.groupEnd = new int[set.length];
			for (int i = 0; i < set.length; i++) {
				set[i] = set[i].replace('_', ' ');
				ds.groupNames[i] = set[i].substring(0, set[i].lastIndexOf('|'));
				ds.groupEnd[i] = Integer.parseInt(set[i].substring(set[i].lastIndexOf('|') + 1));
			}
		}

		ds.dist = Integer.parseInt(read.readLine()); //dist

		set = read.readLine().split(" ");//data*
		ds.data1 = new int[set.length];
		ds.data2 = new int[set.length];
		ds.type = new int[set.length];
		for (int i = 0; i < set.length; i++) {
			ds.data1[i] = Integer.parseInt(set[i].substring(0, set[i].indexOf("|")));
			ds.data2[i] = Integer.parseInt(set[i].substring(set[i].indexOf("|") + 1, set[i].lastIndexOf(':')));
			ds.type[i] = Integer.parseInt(set[i].substring(set[i].lastIndexOf(':') + 1));
		}
		read.close();
		if(!ds.isValid())
			throw new RuntimeException("The dataset is not valid.");
		return ds;
	}

	private void define(String property, String value) {
		if(property.equals("INNER_ARC_THICKNESS"))
			innerArcThickness = Integer.parseInt(value);
		else if(property.equals("INNER_TEXT_OFFSET"))
			innerTextOffset = Integer.parseInt(value);
		else if(property.equals("OUTER_ARC_OFFSET"))
			outerArcOffset = Integer.parseInt(value);
		else if(property.equals("OUTER_ARC_THICKNESS"))
			outerArcThickness = Integer.parseInt(value);
		else if(property.equals("OUTER_TEXT_OFFSET"))
			outerTextOffset = Integer.parseInt(value);
		else if(property.equals("ALPHA"))
			alpha = Integer.parseInt(value);
		else
			throw new IllegalArgumentException("Property " + property + " not found");
	}

	private static Color getColor(String s){
		if(s.equalsIgnoreCase("BLUE")){
			return Color.BLUE;
		} else if(s.equalsIgnoreCase("GREEN")){
			return Color.GREEN;
		} else if(s.equalsIgnoreCase("RED")){
			return Color.RED;
		} else if(s.equalsIgnoreCase("BLACK")){
			return Color.BLACK;
		} else if(s.equalsIgnoreCase("YELLOW")){
			return Color.YELLOW;
		} else if(s.equalsIgnoreCase("CYAN")){
			return Color.CYAN;
		} else if(s.equalsIgnoreCase("MAGENTA")){
			return Color.MAGENTA;
		} else if (s.contains(".")){
			String[] set = s.replace('.', (char)0xF7).split((char)0xF7 + "");
			System.out.println((char)0xF7);
			int r = Integer.parseInt(set[0]) & 0xFF;
			int g = Integer.parseInt(set[1]) & 0xFF;
			int b = Integer.parseInt(set[2]) & 0xFF;
			return new Color(r, g, b);
		} else{
			return new Color(Integer.parseInt(s));
		}
	}

	protected DataSet(){}; //do not allow instantiation, but allow subclassing

	/* Define color scheme */

	//TODO: make colorScheme an abstract class so that you don't have to use an array -- you can calculate on the fly

	public int[] colorScheme; //RGB because these are not used frequently

	public int bezierOutter;

	public boolean isColorSchemeValid(){ //does this color scheme have redundancies?
		for (int i = 0; i < colorScheme.length; i++) {
			for (int j = i + 1; j < colorScheme.length; j++) {
				if(colorScheme[i] == colorScheme[j])
					return false;
			}
		}
		return true;
	}

	/* Arc Data in polar grid form  */
	public double[] sizes; //sizes of arcs in radians
	public String[] labels;
	public int dist; //distance from the center


	public boolean isArcValid(){ //can this arc data create a proper plot?
		double sum = 0;
		for (int i = 0; i < sizes.length; i++) {
			sum += sizes[i];
		}
		return sum <= Math.PI * 2; //arcs cannot exceed 2 PI
	}

	/* Gives gap between arcs in radians */
	public double calcGap(){
		double sum = 0.0;
		for (double d : sizes) {
			sum += d;
		}
		return
			(Math.PI * 2 - sum) //the leftover angles to divide up into gaps
			/ sizes.length; //divide is between all the gaps. There's one gap per data point
	}

	/* Group data */
	public String[] groupNames;
	public int[] groupEnd;

	public boolean isGroupSetValid(){
		if(groupNames == null) //no groups
			return true;
		for (int i = 0; i < groupNames.length; i++) { //check for overlapping group names
			for (int j = i + 1; j < groupNames.length; j++) {
				if(groupNames[i].equalsIgnoreCase(groupNames[j]))
					return false;
			}
		}
		for (int i = 1; i < groupEnd.length; i++) { //check groups are in order
			if(groupEnd[i - 1] >= groupEnd[i])
				return false;
		}
		if(groupEnd[groupEnd.length - 1] != sizes.length - 1) //check that everything is represented by a group
			return false;
		return true;
	}

	/* Relationship data */

	/* Relationship data is composed of two parallel arrays, data1 and data2.
	 * data1[i] is related to data2[i]. data*[i] is the index in sizes[], such
	 * that a bezier spline is drawn between the points representing sizes[data1[i]]
	 * and sizes[data2[i]].
	 */
	public int[] data1;
	public int[] data2;
	public int[] type;

	public boolean isRelationshipDataValid(){
		if(data1.length != data2.length)
			return false;
		for (int i = 0; i < data1.length; i++) {
//			if(data1[i] == data2[i])
//				return false;
			for (int j = i + 1; j < data1.length; j++) {
				if (
						(data1[i] == data1[j]) && (data2[i] == data2[j]) //check if relationship is the same
						|| (data2[i] == data1[j]) && (data1[i] == data2[j]) //check if relationship is flipped
				) /* end if, begin then */
					return false; //this ONLY IF the above conditions are true
				/* no else */
			}
		}
		return true;
	}

	public void organizeData(){

		for (int i = 0; i < data1.length; i++) {
			if(data1[i] > data2[i]){
				//swap
				int temp = data1[i];
				data1[i] = data2[i];
				data2[i] = temp;
			}
		}

		/* bubble sort */
		boolean b = true;
		while(b){
			b = false;
			for (int i = 1; i < data1.length; i++) {
				if(data1[i - 1] > data1[i]){
					b = true;

					//swap data1
					int temp = data1[i];
					data1[i] = data1[i - 1];
					data1[i - 1] = temp;

					//swap data2
					temp = data2[i];
					data2[i] = data2[i - 1];
					data2[i - 1] = temp;

				}
			}
		}
	}

	/* General validity test*/
	public boolean isValid(){
		return data1 != null
		&& data2 != null
		&& sizes != null
		&& dist > 0
		&& isColorSchemeValid()
		&& isArcValid()
		&& isGroupSetValid()
		&& isRelationshipDataValid();
	}
	
	/*
	 * Control variables. Feel free to modify on the fly.
	 */
	public int innerArcThickness = 10;
	public int innerTextOffset = 5;
	public int outerArcOffset = 5;
	public int outerArcThickness = 10;
	public int outerTextOffset = 5;
	public int alpha = 255;
	
}
