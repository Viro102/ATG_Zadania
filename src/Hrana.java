public class Hrana {

    private Vrchol zVrchol;
    private Vrchol doVrchol;
    private int cena;

    public Hrana(Vrchol vrcholZ, Vrchol vrcholDo, int cena) {
        this.zVrchol = vrcholZ;
        this.doVrchol = vrcholDo;
        this.cena = cena;
    }

    public Vrchol getZaciatocnyVrchol() {
        return this.zVrchol;
    }

    public Vrchol getKoncovyVrhol() {
        return this.doVrchol;
    }

    public Integer getCenaHrany() {
        return this.cena;
    }

    public void vypisHrany() {
        System.out.printf("{ %d, %d, %d }%n", this.zVrchol.getCislo(), this.doVrchol.getCislo(), this.cena);
    }

    public void vypisHranyBezCeny() {
        System.out.printf("{ %d, %d }%n", this.zVrchol.getCislo(), this.doVrchol.getCislo());
    }
}
