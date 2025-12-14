package aoc;

public class Dial {

    int size;
    int position;
    int count = 0;

    public Dial(int size, int position) {
        this.size = 100; // 0..99
        this.position = position;
    }
    public void performMove(String line) {
        if (line.length()<2) {
            return;
        }
        char direction = line.charAt(0);
        int step = Integer.parseInt(line.substring(1));
        switch (direction) {
            case 'L' : {
                position -= step;
                break;
            }
            case 'R' : {
                position += step;
                break;
            }
            default : {
            }
        }
        position %= size;
        if (position == 0) {
            ++count;
        }

    }
    public int getCount() {
        return this.count;
    }
}
