
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2023/day/20
 */
public class Y23Day20 {

	/*
	 * Example:
	 * 
	 * broadcaster -> a, b, c
	 * %a -> b
	 * %b -> c
	 * %c -> inv
	 * &inv -> a
	 * 
	 */

	private static final String INPUT_RX = "^([%&]?)([a-z]+) -> ([a-z, ]+)$";
	
	public static record InputData(String type, String name, List<String> outputs) {
	}
	
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
			String line = scanner.nextLine().trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
			}
			if (line.matches(INPUT_RX)) {
				String type = line.replaceFirst(INPUT_RX, "$1");
				String name = line.replaceFirst(INPUT_RX, "$2");
				List<String> outputs = Arrays.asList(line.replaceFirst(INPUT_RX, "$3").split("[, ]+"));
				return new InputData(type, name, outputs);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}

	
	enum SIGNAL { LOW, HIGH, NOTHING };
	
	static record Event(String source, String target, SIGNAL signal) {
		@Override public String toString() { return source + " -"+signal+"-> "+target; }
	}

	public static abstract class LModule {
		String name;
		List<String> outputs;
		List<String> inputs;
		public LModule(String name, List<String> outputs) {
			this.name = name;
			this.outputs = outputs;
			this.inputs = new ArrayList<>();
		}
		public void addInput(String name) {
			inputs.add(name);
		}
		protected List<Event> send(SIGNAL signal) {
			if (signal == SIGNAL.NOTHING) {
				return Collections.emptyList();
			}
			return outputs.stream().map(target -> new Event(name, target, signal)).toList();
		}
		
		protected abstract List<Event> processInput(String input, SIGNAL signal);
		
		@Override public String toString() { return name+" -> " + outputs; }
	}
	
	public static class FlipFlopModule extends LModule {
		boolean on;
		public FlipFlopModule(String name, List<String> outputs) {
			super(name, outputs);
			this.on = false;
		}
		@Override public String toString() { return "%"+name+" -> " + outputs; }
		@Override protected List<Event> processInput(String input, SIGNAL signal) {
			if (signal == SIGNAL.LOW) {
				on = !on;
				SIGNAL sig = on ? SIGNAL.HIGH : SIGNAL.LOW; 
				return send(sig);
			}
			return send(SIGNAL.NOTHING);
		}
	}
	
	public static class ConjunctionModule extends LModule {
		Map<String, SIGNAL> lastInputs;
		public ConjunctionModule(String name, List<String> outputs) {
			super(name, outputs);
			this.lastInputs = new LinkedHashMap<>();
		}
		@Override public void addInput(String name) {
			super.addInput(name);
			lastInputs.put(name, SIGNAL.LOW);
		}
		@Override protected List<Event> processInput(String input, SIGNAL signal) {
			lastInputs.put(input, signal);
			boolean notAllHigh = lastInputs.values().stream().anyMatch(sig -> sig != SIGNAL.HIGH);
			SIGNAL sig = notAllHigh ? SIGNAL.HIGH : SIGNAL.LOW;
			return send(sig);
		}
		@Override public String toString() { return "&"+name+" -> " + outputs; }
	}

	public static class BroadcasterModule extends LModule {
		public BroadcasterModule(String name, List<String> outputs) {
			super(name, outputs);
		}
		@Override
		protected List<Event> processInput(String input, SIGNAL signal) {
			return send(signal);
		}
	}
	
	public static class OutputModule extends LModule {
		public OutputModule(String name) {
			super(name, Collections.emptyList());
		}
		@Override protected List<Event> processInput(String input, SIGNAL signal) {
			return send(SIGNAL.NOTHING);
		}
	}
	
	
	public static class World {
		Map<String, LModule> modules;
		Queue<Event> events;
		int ticks;
		int buttonPresses;
		long receivedRXLOW;
		int countLowEvents;
		int countHighEvents;
		public World() {
			this.modules = new LinkedHashMap<>();
			this.events = new LinkedList<>();
			this.countLowEvents = 0;
			this.countHighEvents = 0;
			this.buttonPresses = 0;
			this.receivedRXLOW = 0;
		}
		public void addModule(String type, String name, List<String> outputs) {
			switch (type) {
			case "":
				modules.put(name, new BroadcasterModule(name, outputs));
				break;
			case "%":
				modules.put(name, new FlipFlopModule(name, outputs));
				break;
			case "&":
				modules.put(name, new ConjunctionModule(name, outputs));
				break;
			}
		}
		public void pressButton() {
			buttonPresses++;
			addEvent("button", "broadcaster", SIGNAL.LOW);
		}
		private void addEvent(String source, String target, SIGNAL signal) {
			events.add(new Event(source, target, signal));
		}
		public boolean hasEvents() {
			return !events.isEmpty();
		}
		Map<String, Long> loops = new LinkedHashMap<>();
		public void tick() {
			ticks++;
			Event event = events.poll();
			if (event.signal == SIGNAL.LOW) {
				countLowEvents++;
				if (event.target.equals("rx")) {
					System.out.println("rx received LOW after "+buttonPresses+" button presses");
					if (receivedRXLOW == 0) {
						receivedRXLOW = buttonPresses;
					}
				}
			}
			else {
				countHighEvents++;
				if (event.target.startsWith("rxloop")) {
					if (!loops.containsKey(event.target)) {
						System.out.println(event.target + " received HIGH after "+buttonPresses+" button presses");
						loops.put(event.target, (long)buttonPresses);
						if (loops.size() == 4) {
							receivedRXLOW = kgv(loops.values());
							System.out.println("KGV: "+receivedRXLOW);
						}
					}
				}
			}
//			System.out.println(event);
			LModule module = modules.get(event.target);
			List<Event> outputEvents = module.processInput(event.source, event.signal);
			events.addAll(outputEvents);
		}
		@Override
		public String toString() {
			return modules.toString();
		}
		public void init() {
			List<LModule> outputModules = new ArrayList<>();
			modules.values().forEach(module -> {
				for (String output:module.outputs) {
					LModule receiver = modules.get(output);
					if (receiver == null) {
						receiver = new OutputModule(output);
						outputModules.add(receiver);
						System.out.println("created output "+output);
					}
					receiver.addInput(module.name);
				}
			});
			outputModules.forEach(outputModule -> {
				modules.put(outputModule.name, outputModule);
			});
		}
	}
	
	
	public static void mainPart1(String inputFile) {
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addModule(data.type, data.name, data.outputs);
		}
		System.out.println(world.toString());
		world.init();
		for (int i=0; i<1000; i++) {
			world.pressButton();
			while (world.hasEvents()) {
				world.tick();
			}
		}
		System.out.println("HIGHS: "+world.countHighEvents+", LOWS: "+world.countLowEvents+", PRODUCT: "+(world.countHighEvents*world.countLowEvents));
	}

	
	public static long kgv(Collection<Long> values) {
		long result = values.iterator().next();
		for (long value:values) {
			result = kgV(result, value);
		}
		return result;
	}
	// https://www.programmieren-ist-einfach.de/Java/F009.html
    public static long ggT(long a, long b) {
        // Die Funktion ggT berechnet den größten gemeinsamen Teiler zweier Zahlen a und b.
        // Die Zwischenergebnisse und das Endergebnis der Funktion ggT werden in einer Variable gespeichert. Dafür wird die Variable resultat deklariert.
    	long resultat;
        
        // Im Fall, dass die erste Zahl a gleich 0 ist, ist das Ergebnis gleich b (der zweiten Zahl). Im Fall, dass a jedoch ungleich 0 ist, wird die ggT Funktion mit den geänderten beziehungsweise angepassten Argumenten ggT(b MOD a, a).
        if (a == 0) {
            resultat = b;
        } else {
            resultat = ggT(b % a, a);
        }
        return resultat;
    }
    
	// https://www.programmieren-ist-einfach.de/Java/F009.html
    public static long kgV(long a, long b) {
    	if ((a==0) || (b==0)) {
    		return 0;
    	}
        // Die Funktion kgV soll das kleinste gemeinsame Vielfach zweier Zahlen a und b berechnen. Die zwei Zahlen wurden als Argument an die kgV Funktion übergeben.
        // Um das Ergebnis zu speichern, wird in einer Variable gespeichert. Dafür wird die Variable resultat deklariert.
    	long resultat;
        
        // Um das kgV zu berechnen werden die zwei Zahlen a und b zuerst multipliziert und das Ergebnis wird dann durch den größten gemeinsamen Teiler der zwei Zahlen a und b geteilt. Das Ergebnis wird in der Variablen resultat gespeichert.
        resultat = (long) ((a * b) / ggT(a, b));
        
        return resultat;
    }

	public static void mainPart2(String inputFile) {
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
//			System.out.println(data);
			world.addModule(data.type, data.name, data.outputs);
		}
		System.out.println(world.toString());
		world.init();
		while (world.receivedRXLOW == 0) {
			world.pressButton();
			while (world.hasEvents()) {
				world.tick();
			}
		}
		System.out.println("RECEIVED RX LOW: "+world.receivedRXLOW);
	}


	public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day20/Feri/input-example.txt");
//		mainPart1("exercises/day20/Feri/input-example-2.txt");
		mainPart1("exercises/day20/Feri/input.txt");               
		mainPart1("exercises/day20/Feri/input-sorted.txt");               
		System.out.println("---------------");                           
		System.out.println("--- PART II ---");
		System.out.println("--- PART I ---");
//		mainPart2("exercises/day20/Feri/input-example.txt");
//		mainPart2("exercises/day20/Feri/input-example-3.txt");
//		mainPart2("exercises/day20/Feri/input.txt");
		mainPart2("exercises/day20/Feri/input-loopdetection.txt");               
		System.out.println("---------------");    
	}
	
}
