import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

public class Graf {
    private int[] x;
    private int[] t;
    private ArrayList<Integer> mnozinaE;
    private ArrayList<Integer> napovedy;
    private LinkedList<Hrana> hrany;
    private ArrayList<Vrchol> vrcholy;
    private int riadok;
    private static final int NEKONECNO = 1000000;

    public Graf() {
        this.riadok = 0;
        this.mnozinaE = new ArrayList<>();
        this.vrcholy = new ArrayList<>();
        this.napovedy = new ArrayList<>();
        this.hrany = new LinkedList<>();
    }

    public void nacitajGraf(String f) {
        this.napovedy.add(0);
        try {
            File file = new File(f);
            Scanner s = new Scanner(file);
            int aktRiadok = 1;
            while (s.hasNext()) {
                int z = s.nextInt();
                while (aktRiadok != z) { // zistujem na ktorom riadku sa menia vrcholy
                    this.napovedy.add(this.riadok);
                    aktRiadok++;
                }

                Vrchol vrcholZ = new Vrchol(z);
                Vrchol vrcholDo = new Vrchol(s.nextInt());
                int cena = s.nextInt();
                Hrana hrana = new Hrana(vrcholZ, vrcholDo, cena);
                vrcholZ.pridajHranu(hrana);
                this.hrany.add(hrana);
                this.riadok++;
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.err.println("Subor neexistuje!");
        }
        this.napovedy.add(this.riadok);
    }

    private void inicializujXaT() {
        this.x = new int[this.napovedy.size()];
        this.t = new int[this.napovedy.size()];

        for (int i = 0; i < this.t.length; i++) {
            this.t[i] = NEKONECNO;
        }
    }

    /**
     * Zistujem najmensiu znacku v mnozine E, a odstranujem dany prvok
     * 
     * @return riadiaciVrchol pre pouzitie v Label-Set algoritme
     */
    private int getRiadiaciVrchol() {
        int pozicia = 0;
        int minHodnota = NEKONECNO;
        for (int i = 0; i < this.mnozinaE.size(); i++) {
            int vrchol = this.mnozinaE.get(i);
            if (this.t[vrchol] < minHodnota) {
                minHodnota = this.t[vrchol];
                pozicia = i;
            }
        }
        return this.mnozinaE.remove(pozicia);
    }

    public void labelSet(int u, int v) {
        this.inicializujXaT();
        this.t[u] = 0;
        this.mnozinaE.add(u);
        while (!this.mnozinaE.isEmpty()) {
            int riadiaciVrchol = this.getRiadiaciVrchol();
            if (riadiaciVrchol == v) {
                this.vytlacCestu(v);
                return;
            }
            int zaciatok = this.napovedy.get(riadiaciVrchol - 1);
            int koniec = this.napovedy.get(riadiaciVrchol);
            for (int i = zaciatok; i < koniec; i++) {
                int doVrcholu = this.hrany.get(i).getKoncovyVrhol().getCislo();
                int cena = this.hrany.get(i).getCenaHrany();
                if (this.t[doVrcholu] > this.t[riadiaciVrchol] + cena) {
                    this.t[doVrcholu] = this.t[riadiaciVrchol] + cena;
                    this.x[doVrcholu] = riadiaciVrchol;
                    System.out.printf("Zlepsenie pri vrchole: %d na %d|%d%n", doVrcholu,
                            this.t[riadiaciVrchol] + cena,
                            riadiaciVrchol);
                    this.mnozinaE.add(doVrcholu);

                }
            }

        }
        System.out.println("Cesta sa nenasla.");
    }

    private void vytlacCestu(int v) {
        ArrayList<Integer> cesta = this.urobCestu(v);
        System.out.printf("Cena: %d%n", this.t[v]);
        StringBuilder sb = new StringBuilder();

        for (int i : cesta) {
            sb.append(i).append(" -> ");
        }
        sb.delete(sb.length() - " -> ".length(), sb.length());
        System.out.println(sb);
    }

    private ArrayList<Integer> urobCestu(int v) {
        ArrayList<Integer> cesta = new ArrayList<>();
        for (int i = v; i > 0; i = this.x[i]) {
            cesta.add(i);
        }
        Collections.reverse(cesta);
        return cesta;
    }

    private void zoradHrany(String zoradenie) {
        Comparator<Hrana> zoradPodlaCeny = ((o1, o2) -> o1.getCenaHrany().compareTo(o2.getCenaHrany()));
        Collections.sort(this.hrany, zoradPodlaCeny);
        if (zoradenie.equals("zostupne")) {
            Collections.reverse(this.hrany);
        }
    }

    public void kruskalov() {
        this.zoradHrany("vzostupne");
        int[] k = new int[this.getVrcholy().size() + 1];
        for (int i = 0; i < k.length; i++) {
            k[i] = i;
        }
        ArrayList<Hrana> kostra = new ArrayList<>();
        int pocetVrcholov = this.getVrcholy().size();
        int u = 0;
        int v = 0;
        int pocetVybranychHran = 0;
        int cena = 0;
        while (!((pocetVybranychHran == pocetVrcholov - 1) || this.hrany.isEmpty())) {
            u = this.hrany.getFirst().getZaciatocnyVrchol().getCislo();
            v = this.hrany.getFirst().getKoncovyVrhol().getCislo();
            cena += this.hrany.getFirst().getCenaHrany();
            Hrana hrana = this.hrany.removeFirst();
            if (k[u] != k[v]) {
                kostra.add(hrana);
                int kmin = Math.min(k[u], k[v]);
                int kmax = Math.max(k[u], k[v]);
                pocetVybranychHran++;
                for (int j = 0; j < pocetVrcholov; j++) {
                    if (k[j] == kmax) {
                        k[j] = kmin;
                    }
                }
            }
        }

        for (int i = 0; i < kostra.size(); i++) {
            kostra.get(i).vypisHrany();
        }

        System.out.println(cena);
    }

    private ArrayList<Integer> getVrcholy() {
        ArrayList<Integer> unikatne = new ArrayList<>();
        for (Hrana h : this.hrany) {
            this.vrcholy.add(h.getKoncovyVrhol());
            this.vrcholy.add(h.getZaciatocnyVrchol());

        }

        for (int i = 0; i < this.vrcholy.size(); i++) {
            if (!unikatne.contains(this.vrcholy.get(i).getCislo())) {
                unikatne.add(this.vrcholy.get(i).getCislo());
            }
        }

        return unikatne;
    }

}
