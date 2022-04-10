import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Graf {
    private int[] x;
    private int[] t;
    private ArrayList<Integer> mnozinaE;
    private ArrayList<Integer> napovedy;
    private ArrayList<Integer> vrcholZ;
    private ArrayList<Integer> vrcholDo;
    private ArrayList<Integer> ceny;
    private ArrayList<Hrana> hrany;
    private int riadok;
    private static final int NEKONECNO = 1000000;

    public Graf() {
        this.riadok = 0;
        this.mnozinaE = new ArrayList<>();
        this.napovedy = new ArrayList<>();
        this.vrcholDo = new ArrayList<>();
        this.vrcholZ = new ArrayList<>();
        this.ceny = new ArrayList<>();
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
                this.vrcholZ.add(z);
                this.vrcholDo.add(s.nextInt());
                this.ceny.add(s.nextInt());
                this.riadok++;
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
                int doVrcholu = vrcholDo.get(i);
                int cena = ceny.get(i);
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

    private void zoradHrany(String zoradenie) {
        spravHrany();
        int n = this.hrany.size();
        boolean zmena;
        for (int i = 0; i < n - 1; i++) {
            zmena = false;
            for (int j = 0; j < n - i - 1; j++)
                if (zoradenie.equals("vzostupne")) {
                    if (this.hrany.get(j).getCenaHrany() > this.hrany.get(j + 1).getCenaHrany()) {
                        // swap arr[j+1] and arr[j]
                        Hrana temp = this.hrany.get(j);
                        this.hrany.set(j, hrany.get(j + 1));
                        this.hrany.set(j + 1, temp);
                        zmena = true;
                    }
                } else if (zoradenie.equals("zostupne")) {
                    if (this.hrany.get(j).getCenaHrany() < this.hrany.get(j + 1).getCenaHrany()) {
                        Hrana temp = this.hrany.get(j);
                        this.hrany.set(j, hrany.get(j + 1));
                        this.hrany.set(j + 1, temp);
                        zmena = true;
                    }
                } else {
                    System.err.println("Zoradit sa da iba vzostupne alebo zostupne");
                    break;
                }
            if (!zmena) {
                break;
            }
        }
    }

    private void spravHrany() {
        this.hrany = new ArrayList<>();
        for (int i = 0; i < this.riadok; i++) {
            this.hrany.add(new Hrana(vrcholZ.get(i), vrcholDo.get(i), ceny.get(i)));
        }
    }

    public void kruskalov() {
        zoradHrany("vzostupne");
        ArrayList<Integer> k = new ArrayList<>();
        for (int i = 0; i < napovedy.size(); i++) {
            k.add(i);
        }
        ArrayList<Hrana> kostra = new ArrayList<>();
        int pocetVybranychHran = 0;
        int cena = 0;
        for (int i = 0; i < hrany.size(); i++) {
            if ((pocetVybranychHran == napovedy.size() - 2) || hrany.isEmpty()) {
                break;
            }
            int u = k.get(hrany.get(i).getZaciatocnyVrchol());
            int v = k.get(hrany.get(i).getKoncovyVrhol());
            cena += hrany.get(i).getCenaHrany();
            hrany.remove(i);
            if (u != v) {
                kostra.add(new Hrana(u, v));
                int kmin = Math.min(u, v);
                int kmax = Math.max(u, v);
                pocetVybranychHran++;
                for (int j = 0; j < napovedy.size() - 1; j++) {
                    if (k.get(j) == kmax) {
                        k.set(j, kmin);
                    }
                }
            }
        }

        for (int i = 0; i < kostra.size(); i++) {
            kostra.get(i).vypisHranyBezCeny();
        }

        System.out.println(cena);
    }
}
