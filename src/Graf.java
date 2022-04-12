import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

public class Graf {
    private int[] x;
    private int[] t;
    private ArrayList<Integer> mnozinaE;
    private ArrayList<Integer> napovedy;
    private Hrana[] hrany;
    private static final int NEKONECNO = 1000000;

    public Graf() {
        this.mnozinaE = new ArrayList<>();
        this.napovedy = new ArrayList<>();
    }

    public void nacitajGraf(String f) {
        ArrayList<Integer> zVrchol = new ArrayList<>();
        ArrayList<Integer> doVrcholu = new ArrayList<>();
        ArrayList<Integer> ceny = new ArrayList<>();
        int riadok = 0;
        this.napovedy.add(0);
        try {
            File file = new File(f);
            Scanner s = new Scanner(file);
            int aktRiadok = 1;
            while (s.hasNext()) {
                int z = s.nextInt();
                while (aktRiadok != z) { // zistujem na ktorom riadku sa menia vrcholy
                    this.napovedy.add(riadok);
                    aktRiadok++;
                }
                zVrchol.add(z);
                doVrcholu.add(s.nextInt());
                ceny.add(s.nextInt());
                riadok++;
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.err.println("Subor neexistuje!");
        }
        this.napovedy.add(riadok);

        this.hrany = new Hrana[riadok];
        for (int i = 0; i < riadok; i++) {
            hrany[i] = new Hrana(new Vrchol(zVrchol.get(i)), new Vrchol(doVrcholu.get(i)), ceny.get(i));
        }
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
                int doVrcholu = this.hrany[i].getKoncovyVrhol().getCislo();
                int cena = this.hrany[i].getCenaHrany();
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
        Arrays.sort(this.hrany, zoradPodlaCeny);
        if (zoradenie.equals("zostupne")) {
            Collections.reverse(Arrays.asList(this.hrany));
        }
    }

    public void kruskalov(String typKostry) {
        this.zoradHrany(typKostry);
        LinkedList<Hrana> postupnostP = new LinkedList<>();
        for (int i = 0; i < this.hrany.length; i++) {
            postupnostP.add(this.hrany[i]);
        }
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
        while (!((pocetVybranychHran == pocetVrcholov - 1) || postupnostP.isEmpty())) {
            u = postupnostP.getFirst().getZaciatocnyVrchol().getCislo();
            v = postupnostP.getFirst().getKoncovyVrhol().getCislo();
            cena += postupnostP.getFirst().getCenaHrany();
            Hrana hrana = postupnostP.removeFirst();
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
        ArrayList<Vrchol> temp = new ArrayList<>();
        for (Hrana h : this.hrany) {
            temp.add(h.getKoncovyVrhol());
            temp.add(h.getZaciatocnyVrchol());

        }

        for (int i = 0; i < temp.size(); i++) {
            if (!unikatne.contains(temp.get(i).getCislo())) {
                unikatne.add(temp.get(i).getCislo());
            }
        }

        Collections.sort(unikatne);

        return unikatne;
    }

    public void monotonneOcislovanie() {
        int pocetVrcholov = getVrcholy().size();
        int[] ideg = new int[pocetVrcholov];
        int[] d = new int[pocetVrcholov];
        for (int v = 0; v < pocetVrcholov; v++) {
            d[v] = ideg[v];
        }
    }

    public void vypisVrcholy() {
    }

}
