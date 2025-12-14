package aoc;

public class Dial_0x434C49434B {
    int size;
    int position;
    int count = 0;

    public Dial_0x434C49434B(int size, int position) {
        this.size = 100; // 0..99
        this.position = position;
    }
    public void performMove(String line) {
        if (line.length()<2) {
            return;
        }
        char direction = line.charAt(0);
        int step = Integer.parseInt(line.substring(1));
        int fullRotations = Math.floorDiv(step, size);
        int restStep = (step%size);
        switch (direction) {
            case 'L' : {
                position -= restStep;
                if (position < 0) {
                    if ( -position != restStep) {
                        count += 1;
                    }
                    position += size;
                }
                if (position == 0) {
                    count += 1;
                }
                break;
            }
            case 'R' : {
                position += restStep;
                if (position > size) {
                    count += 1;
                    position -= size;
                }
                if (position == size) {
                    count += 1;
                    position = 0;
                }
                break;
            }
            default : {
                return;
            }
        }
        if (fullRotations > 0) {
            count += fullRotations;
        }

    }
    public int getCount() {
        return this.count;
    }
}
