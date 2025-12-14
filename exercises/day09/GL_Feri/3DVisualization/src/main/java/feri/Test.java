package feri;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import feri.Y25GUIOutput3D09.DDDLineObject;
import feri.Y25GUIOutput3D09.DDDObject;


public class Test {
	
	private static Random random = new Random();

	public static double rand() {
		return rand(-1.0, 1.0);
	}

	public static double rand(double from, double to) {
		return from + random.nextDouble() * (to - from);
	}

	public static int irand(int from, int to) {
		return from + random.nextInt(to - from + 1);
	}



	public static void main(String[] args) throws Exception {
		Y25GUIOutput3D09 output = new Y25GUIOutput3D09("GUIOutput3D Test", true);
		output.smaller();
		
		List<DDDObject> state = new ArrayList<>();

		// F
		state.add(new DDDObject("FTL", -1.0, -1.0, -1.0, 1.0, 1));
		state.add(new DDDObject(-1.0,  0.0, -1.0, 1.0, 0));
		state.add(new DDDObject(-1.0,  1.0, -1.0, 1.0, 0));
		state.add(new DDDObject(-1.0,  2.0, -1.0, 1.0, 0));
		state.add(new DDDObject(-1.0,  3.0, -1.0, 1.0, 0));

		state.add(new DDDObject( 0.0,  1.0, -1.0, 1.0, 0));
		
		state.add(new DDDObject( 0.0, -1.0, -1.0, 1.0, 0));
		state.add(new DDDObject( 1.0, -1.0, -1.0, 1.0, 0));
		output.addStep("E", state);		

		// E
		state.add(new DDDObject(-1.0, -1.0,  1.0, 1.0, 10));
		state.add(new DDDObject(-1.0,  0.0,  1.0, 1.0, 10));
		state.add(new DDDObject(-1.0,  1.0,  1.0, 1.0, 10));
		state.add(new DDDObject(-1.0,  2.0,  1.0, 1.0, 10));
		state.add(new DDDObject(-1.0,  3.0,  1.0, 1.0, 10));

		state.add(new DDDObject( 0.0, -1.0,  1.0, 1.0, 10));
		state.add(new DDDObject( 1.0, -1.0,  1.0, 1.0, 10));
		
		state.add(new DDDObject( 0.0,  1.0,  1.0, 1.0, 10));
		
		state.add(new DDDObject( 0.0,  3.0,  1.0, 1.0, 10));
		state.add(new DDDObject( 1.0,  3.0,  1.0, 1.0, 10));
		output.addStep("E", state);		

		
		
		state.add(new DDDLineObject("line1",  0.0,  -1.0,  0.0,  0.0,  3.0,  0.0, 0.1, 30));
		state.add(new DDDLineObject("line2",  0.0,   1.0, -1.0,  0.0,  1.0,  1.0, 0.1, 30));
		state.add(new DDDLineObject("line3", -1.0,   1.0,  0.0,  1.0,  1.0,  0.0, 0.1, 30));

		
		output.adjustScale(state);
		output.addStep("Lines", state);		
		
		
//		state = new ArrayList<>();
//		for (int i = 0; i < 10; i++) {
//			state.add(new DDDObject(rand(), rand(), rand(), rand(0.01, 0.1), irand(0, 2)));
//		}
//		output.adjustScale(state);
//		output.addStep("Erste Szene", state);

		for (int t = 0; t < 20; t++) {
			ArrayList<DDDObject> nextState = new ArrayList<>();
			int i=0;
			for (DDDObject dddo : state) {
				i++;
				int type = dddo.type<10 ? 0 : 10;
				if (dddo.type <30) {
					nextState.add(new DDDObject(dddo.id, dddo.x + rand() * 0.05, dddo.y + rand() * 0.05, dddo.z + rand() * 0.05,
							dddo.size + rand()*0.1 , type+((i/3)%4)));
				}
				else {
					type = 30;
					DDDLineObject dddol = (DDDLineObject) dddo;
					nextState.add(new DDDLineObject(dddol.id, 
							dddol.x + rand() * 0.05, dddol.y + rand() * 0.05, dddol.z + rand() * 0.05,
							dddol.x2 + rand() * 0.05, dddol.y2 + rand() * 0.05, dddol.z2 + rand() * 0.05,
							dddol.size + rand()*0.01, type+((i)%4)));
				}
			}
			output.addStep("Testanimation", state);
			state = nextState;
		}
		
	}
    
}

