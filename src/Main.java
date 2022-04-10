public class Main {
    public static void main(String[] args) throws Exception {
        Graf g = new Graf();
        // g.nacitajGraf("velky_graf.txt");
        // g.labelSet(63454, 1320);
        g.nacitajGraf("pr1.txt");
        // g.labelSet(4, 7);
        g.kruskalov();
    }
}
