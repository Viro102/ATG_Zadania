import java.util.ArrayList;
import java.util.List;

public class Vrchol {
    private ArrayList<Hrana> susedneHrany;
    private int cislo;

    public Vrchol(int cislo) {
        this.susedneHrany = new ArrayList<>();
        this.cislo = cislo;
    }

    public Hrana pridajHranu(Hrana e) {
        this.susedneHrany.add(e);
        return e;
    }

    public void odstranHranu(Hrana e) {
        if (this.susedneHrany.contains(e)) {
            this.susedneHrany.remove(e);
        } else {
            System.out.println("neexistuej");
        }
    }

    public int getCislo() {
        return this.cislo;
    }

    public List<Hrana> getSusedneHrany() {
        return this.susedneHrany;
    }

    public void vypisSusedneHrany() {
        for (Hrana v : susedneHrany) {
            v.vypisHrany();
        }
    }

}
