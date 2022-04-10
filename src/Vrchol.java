import java.util.ArrayList;

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
        return cislo;
    }

    public ArrayList<Hrana> getSusedneHrany() {
        return this.susedneHrany;
    }

}
