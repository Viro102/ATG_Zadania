import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class Graf {
    private int[] x;
    private int[] t;
    private ArrayList<Integer> mnozinaE;
    private ArrayList<Integer> napovedy;
    private LinkedList<Hrana> hrany;
    private int riadok;
    private static final int NEKONECNO = 1000000;

    public Graf() {
        this.riadok = 0;
        this.mnozinaE = new ArrayList<>();
        this.napovedy = new ArrayList<>();
        this.hrany = new LinkedList<>();
    }

    public void nacitajGraf(String f) {
        napovedy.add(0);
        try {
            File file = new File(f);
            Scanner s = new Scanner(file);
            int aktRiadok = 1;
            while (s.hasNext()) {
                int z = s.nextInt();
                while (aktRiadok != z) { // zistujem na ktorom riadku sa menia vrcholy
                    napovedy.add(riadok);
                    aktRiadok++;
                }

                Vrchol vrcholZ = new Vrchol(z);
                Vrchol vrcholDo = new Vrchol(s.nextInt());
                int cena = s.nextInt();
                Hrana hrana = new Hrana(vrcholZ, vrcholDo, cena);
                vrcholZ.pridajHranu(hrana);
                hrany.add(hrana);
                riadok++;
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.err.println("Subor neexistuje!");
        }
        napovedy.add(riadok);
    }

    private void inicializujXaT() {
        this.x = new int[napovedy.size()];
        this.t = new int[napovedy.size()];

        for (int i = 0; i < t.length; i++) {
            t[i] = NEKONECNO;
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
        for (int i = 0; i < mnozinaE.size(); i++) {
            int vrchol = mnozinaE.get(i);
            if (t[vrchol] < minHodnota) {
                minHodnota = t[vrchol];
                pozicia = i;
            }
        }
        return mnozinaE.remove(pozicia);
    }

    public void labelSet(int u, int v) {
        inicializujXaT();
        t[u] = 0;
        mnozinaE.add(u);
        while (!mnozinaE.isEmpty()) {
            int riadiaciVrchol = getRiadiaciVrchol();
            if (riadiaciVrchol == v) {
                vytlacCestu(v);
                return;
            }
            int zaciatok = napovedy.get(riadiaciVrchol - 1);
            int koniec = napovedy.get(riadiaciVrchol);
            for (int i = zaciatok; i < koniec; i++) {
                int doVrcholu = hrany.get(i).getKoncovyVrhol().getCislo();
                int cena = hrany.get(i).getCenaHrany();
                if (this.t[doVrcholu] > t[riadiaciVrchol] + cena) {
                    this.t[doVrcholu] = t[riadiaciVrchol] + cena;
                    this.x[doVrcholu] = riadiaciVrchol;
                    System.out.printf("Zlepsenie pri vrchole: %d na %d|%d%n", doVrcholu,
                            t[riadiaciVrchol] + cena,
                            riadiaciVrchol);
                    this.mnozinaE.add(doVrcholu);

                }
            }

        }
        System.out.println("Cesta sa nenasla.");
    }

    private void vytlacCestu(int v) {
        ArrayList<Integer> cesta = urobCestu(v);
        System.out.printf("Cena: %d%n", t[v]);
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

    private void zoradHranyZostupne() {
        int n = this.hrany.size();
        boolean zmena;
        for (int i = 0; i < n - 1; i++) {
            zmena = false;
            for (int j = 0; j < n - i - 1; j++)
                if (this.hrany.get(j).getCenaHrany() < this.hrany.get(j + 1).getCenaHrany()) {
                    Hrana temp = this.hrany.get(j);
                    this.hrany.set(j, hrany.get(j + 1));
                    this.hrany.set(j + 1, temp);
                    zmena = true;
                }

            if (!zmena) {
                break;
            }
        }
    }

    /*
     * bubble sort
     */
    private void zoradHranyVzostupne() {
        int n = this.hrany.size();
        boolean zmena;
        for (int i = 0; i < n - 1; i++) {
            zmena = false;
            for (int j = 0; j < n - i - 1; j++)
                if (this.hrany.get(j).getCenaHrany() > this.hrany.get(j + 1).getCenaHrany()) {
                    Hrana temp = this.hrany.get(j);
                    this.hrany.set(j, hrany.get(j + 1));
                    this.hrany.set(j + 1, temp);
                    zmena = true;
                }

            if (!zmena) {
                break;
            }
        }

    }

    public void kruskalov() {
        zoradHranyVzostupne();
        ArrayList<Integer> k = new ArrayList<>();
        for (int i = 0; i < getPocetVrcholov() + 1; i++) {
            k.add(i);
        }
        ArrayList<Hrana> kostra = new ArrayList<>();
        int pocetVrcholov = getPocetVrcholov();
        int u = 0;
        int v = 0;
        int pocetVybranychHran = 0;
        int cena = 0;
        while (!((pocetVybranychHran == pocetVrcholov - 1) || hrany.isEmpty())) {
            u = hrany.getFirst().getZaciatocnyVrchol().getCislo();
            v = hrany.getFirst().getKoncovyVrhol().getCislo();
            cena += hrany.getFirst().getCenaHrany();
            if (!k.get(u).equals(k.get(v))) {
                kostra.add(hrany.removeFirst());
                int kmin = Math.min(u, v);
                int kmax = Math.max(u, v);
                pocetVybranychHran++;
                for (int j = 0; j < k.size(); j++) {
                    if (k.get(j) == kmax) {
                        k.set(j, kmin);
                    }
                }
            }
        }

        for (int i = 0; i < kostra.size(); i++) {
            kostra.get(i).vypisHrany();
        }

        System.out.println(cena);
    }

    private int getPocetVrcholov() {
        Set<Integer> vrcholy = new HashSet<>();
        for (Hrana h : hrany) {
            vrcholy.add(h.getKoncovyVrhol().getCislo());
            vrcholy.add(h.getZaciatocnyVrchol().getCislo());

        }
        return vrcholy.size();
    }
}
