package dobreee;

public class Group {
    private  int size;

    @Override
    public String toString() {
        return "Group{" +
                "size=" + size +
                '}';
    }

    public Group(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
    public static int getRandomGroupSize() {
        return (int) (Math.random() * 3) + 1;

    }
}
