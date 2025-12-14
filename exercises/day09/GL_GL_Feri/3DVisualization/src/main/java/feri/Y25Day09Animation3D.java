package feri;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import feri.Y25GUIOutput3D09.DDDBlockObject;



/**
 * see: https://adventofcode.com/2025/day/09
 *
 */
public class Y25Day09Animation3D {
	
	static Y25GUIOutput3D09 output = null;

	public static record InputData(int x, int y) {}

	private static final String INPUT_RX = "^([0-9]+),([0-9]+)$";
	
	public static class InputProcessor implements Iterable<InputData>, Iterator<InputData> {
		private Scanner scanner;
		public InputProcessor(String inputFile) {
			try {
				scanner = new Scanner(new File(inputFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		@Override public Iterator<InputData> iterator() { return this; }
		@Override public boolean hasNext() { return scanner.hasNext(); }
		@Override public InputData next() {
			String rawLine = scanner.nextLine();
			String line = rawLine.trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
			}
			if (line.matches(INPUT_RX)) {
				int x = Integer.parseInt(line.replaceFirst(INPUT_RX, "$1"));
				int y = Integer.parseInt(line.replaceFirst(INPUT_RX, "$2"));
				return new InputData(x,y);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}
	
	public static record Pos(long x, long y) {
		@Override
		public final String toString() {
			return "("+x+","+y+")";
		}
		public long area(Pos other) {
			long dx = Math.abs(x - other.x);
			long dy = Math.abs(y - other.y);
			return (dx+1)*(dy+1);
		}
		public Pos min(Pos other) {
			if (other == null) {
				return this;
			}
			long x = Math.min(this.x, other.x);
			long y = Math.min(this.y, other.y);
			return new Pos(x, y);
		}
		public Pos max(Pos other) {
			if (other == null) {
				return this;
			}
			long x = Math.max(this.x, other.x);
			long y = Math.max(this.y, other.y);
			return new Pos(x, y);
		}
		public List<Pos> neighbours() {
			List<Pos> result = new ArrayList<>();
			for (long dx=-1; dx<=1; dx++) {
				for (long dy=-1; dy<=1; dy++) {
					if (dx == 0 && dy==0) {
						continue;
					}
					result.add(new Pos(x+dx, y+dy));
				}
			}
			return result;
		}
		public List<Pos> directNeighbours() {
			List<Pos> result = new ArrayList<>();
			result.add(new Pos(x, y-1));
			result.add(new Pos(x+1, y));
			result.add(new Pos(x, y+1));
			result.add(new Pos(x-1, y));
			return result;
		}
		public double distanceTo(Pos other) {
			double dx = x - other.x;
			double dy = y - other.y;
			return Math.sqrt(dx*dx+dy*dy);
		}
	}
	
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		List<Pos> redTiles = new ArrayList<>();
		for (InputData data:new InputProcessor(inputFile)) {
			redTiles.add(new Pos(data.x, data.y));
		}
		long maxArea = -1;
		for (int i=0; i<redTiles.size(); i++) {
			Pos pos1 = redTiles.get(i);
			for (int j=i+1; j<redTiles.size(); j++) {
				Pos pos2 = redTiles.get(j);
				long area = pos1.area(pos2);
				System.out.println("Area between "+pos1+" and "+pos2+" = "+area);
				if (area > maxArea) {
					maxArea = area;
				}
			}
		}
		System.out.println("MaxArea: "+maxArea);
	}

	public static record Line(Pos minPos, Pos maxPos) {
		@Override
		public String toString() {
			return minPos+"->"+maxPos;
		}
		public boolean isHorizontal() {
			return minPos.x != maxPos.x;
		}
		public boolean cutsArea(Pos areaFrom, Pos areaTo) {
			Pos minAreaP = areaFrom.min(areaTo);
			Pos maxAreaP = areaFrom.max(areaTo);
			if (isHorizontal()) {
				if (minPos.y > minAreaP.y && minPos.y < maxAreaP.y) {
					if (maxPos.x > minAreaP.x && minPos.x < maxAreaP.x) {
						return true;
					}
				}
			}
			else {
				if (minPos.x > minAreaP.x && minPos.x < maxAreaP.x) {
					if (maxPos.y > minAreaP.y && minPos.y < maxAreaP.y) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public static class World {
		Set<Pos> redTiles;
		List<Line> lines;
		Pos firstPos = null;
		Pos lastPos = null;
		Pos minPos = null;
		Pos maxPos = null;
		public World() {
			this.redTiles = new HashSet<>();
			this.lines = new ArrayList<>();
			this.firstPos = null;
			this.lastPos = null;
			this.minPos = null;
			this.maxPos = null;
		}
		public void init() {
			System.out.println("Initializing world...");
			addRedTile(firstPos);
			System.out.println("INIT finished");
		}
		public void addRedTile(Pos pos) {
			redTiles.add(pos);
			if (firstPos == null) {
				firstPos = pos;
			}
			minPos = pos.min(minPos);
			maxPos = pos.max(maxPos);
			if (lastPos != null) {
				Pos minP = pos.min(lastPos);
				Pos maxP = pos.max(lastPos);
				lines.add(new Line(minP, maxP));
			}
			lastPos = pos;
		}
		public long findMaxArea() {
			long maxArea = -1;
			List<Pos> redTs = new ArrayList<>(this.redTiles);
			for (int i=0; i<redTs.size(); i++) {
				Pos pos1 = redTs.get(i);
				for (int j=i+1; j<redTs.size(); j++) {
					Pos pos2 = redTs.get(j);
					long area = pos1.area(pos2);
					if (checkFilled(pos1, pos2)) {
						if (area > maxArea) {
							showWorld(pos1+"->"+pos2+": "+area, pos1, pos2);
							System.out.println("Area between "+pos1+" and "+pos2+" = "+area);
							maxArea = area;
						}
					}
				}
			}
			return maxArea;
		}
		private boolean checkFilled(Pos pos1, Pos pos2) {
			for (Line line:lines) {
				if (line.cutsArea(pos1, pos2)) {
					return false;
				}
			}
			return true;
		}
		public void showWorld(String info, Pos corner1, Pos corner2) {
			double scale = maxPos.distanceTo(minPos) / 100.0;
			int type = 1;
			List<Y25GUIOutput3D09.DDDObject> points = new ArrayList<>();
			for (int i=0; i<lines.size(); i++) {
				Line line = lines.get(i);
				double x1 = line.minPos.x;
				double y1 = line.minPos.y;
				double z1 = 0;
				double x2 = line.maxPos.x;
				double y2 = line.maxPos.y;
				double z2 = 0;
				int color = 0;
				int lineType = 30+color;
				double lineSize = 1.0*scale;
				Y25GUIOutput3D09.DDDObject dddLine = new Y25GUIOutput3D09.DDDLineObject("line"+i, x1, y1, z1, x2, y2, z2, lineSize, lineType);
				points.add(dddLine);
			}
			
//			Y25GUIOutput3D09.DDDObject dddLine = new Y25GUIOutput3D09.DDDLineObject("lineD", minPos.x,minPos.y,0.3, maxPos.x,maxPos.y,0.3, scale, 30);
//			points.add(dddLine);
			if (corner1 != null && corner2 != null) {
				DDDBlockObject cornerBlock = new DDDBlockObject("area", corner1.x,corner1.y,-0.1, corner2.x,corner2.y,0.1, scale, 41);
				points.add(cornerBlock);
			}
			
			if (output.scale == 1) {
				output.adjustScale(points);
			}
			output.addStep(info, points);
		}
	}


	public static void mainPart2(String inputFile) {
		output = new Y25GUIOutput3D09("2025 Day 9 Part II", true);
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			world.addRedTile(new Pos(data.x, data.y));
		}
		world.init();
		world.showWorld("INIT", null, null);
//		System.out.println(world.toString());
		long maxArea = world.findMaxArea();
		System.out.println("MaxArea: "+maxArea);
	}

	

	public static void main(String[] args) throws FileNotFoundException {
//		System.out.println("--- PART I ---");
//		mainPart1("exercises/day09/Feri/input-example.txt");
//		mainPart1("exercises/day09/Feri/input.txt");  
//		System.out.println("---------------");
		System.out.println("--- PART II ---");
//		mainPart2("../../../../exercises/day09/Feri/input-example.txt");
		mainPart2("../../../../exercises/day09/Feri/input.txt");   
//		mainPart2("../../../../exercises/day09/Feri/input2.txt");   
		System.out.println("---------------");    // 
	}


}