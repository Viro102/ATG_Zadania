public class Hrana {

    private int zVrchol;
    private int doVrchol;
    private int cena;

    public Hrana(int zVrchol, int doVrchol, int cena) {
        this.zVrchol = zVrchol;
        this.doVrchol = doVrchol;
        this.cena = cena;
    }

    public Hrana(int zVrchol, int doVrchol) {
        this.zVrchol = zVrchol;
        this.doVrchol = doVrchol;
    }

    public int getZaciatocnyVrchol() {
        return this.zVrchol;
    }

    public int getKoncovyVrhol() {
        return this.doVrchol;
    }

    public int getCenaHrany() {
        return this.cena;
    }

    public void vypisHrany() {
        System.out.printf("{ %d, %d, %d }%n", zVrchol, doVrchol, cena);
    }

    public void vypisHranyBezCeny() {
        System.out.printf("{ %d, %d }%n", zVrchol, doVrchol);
    }
}
