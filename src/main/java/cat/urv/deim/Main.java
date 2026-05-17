package cat.urv.deim;

import java.io.IOException;
import java.nio.file.Path;

import cat.urv.deim.exceptions.LlistaBuida;

public class Main {
    private static final Path PERFILS = Path.of("Dades/usersha1-profile.tsv");
    private static final Path ESCOLTES = Path.of("Dades/usersha-artname49271.tsv");
    private static final Path PERFILS_REAL = Path.of("Dades/lastfm-dataset-360K/usersha1-profile.tsv");
    private static final Path ESCOLTES_REAL = Path.of("Dades/lastfm-dataset-360K/usersha1-artmbid-artname-plays.tsv");

    public static void main(String[] args) throws IOException {
        boolean dadesReals = args.length > 0 && args[0].equalsIgnoreCase("real");
        int limitEscoltes = (dadesReals && args.length > 1) ? Integer.parseInt(args[1]) : -1;
        Path perfils = dadesReals ? PERFILS_REAL : PERFILS;
        Path escoltes = dadesReals ? ESCOLTES_REAL : ESCOLTES;

        // es dona mes espai a la taula quan es treballa amb el dataset gran
        Recomanador recomanador = dadesReals ? new Recomanador(524288, 1048576) : new Recomanador();
        recomanador.carregaPerfils(perfils);
        recomanador.carregaEscoltes(escoltes, limitEscoltes);

        System.out.println("Recomanador carregat amb " + recomanador.totalUsuaris() + " usuaris i " + recomanador.totalArtistes() + " artistes.");
        if (dadesReals) {
            if (limitEscoltes == -1) {
                System.out.println("S'ha carregat tot el fitxer d'escoltes del dataset real.");
            } else {
                System.out.println("S'han carregat les primeres " + limitEscoltes + " linies d'escoltes del dataset real.");
            }
        }

        String uGermany = "003059a886782e4d7936da913d3f064f637d0b2b";
        String uGermany2 = "0093a82573bc14f2e94187d8b208706223c977b8";
        String uFrance = "007858e2a8c142a06b169c17f41e16a60f4a363b";
        String uAcdc = "00277ccecc376837e57b6d6b58330d1bafc90c73";
        String uBeatsteaks = "0014ffc91d3a5b59cce9bceaf22ef0d72e5711b8";
        String uBadReligion = "0060a50a536692e9a3d0c648c60889d3fd09cc56";
        String uDreamTheater = "002d07853e3d855dc359da5bd23f10ff11444b36";

        // cada cas activa filtres diferents per veure com canvien les recomanacions
        imprimeixCas(recomanador, "1. Imprescindible: edat", uGermany, 100, false, 5, false, false);
        imprimeixCas(recomanador, "2. Imprescindible: pais", uGermany, 100, false, -1, true, false);
        imprimeixCas(recomanador, "3. Imprescindible: preferits", uAcdc, 50, false, -1, false, true);
        imprimeixCas(recomanador, "4. Imprescindible: edat i pais", uGermany, 100, false, 5, true, false);
        imprimeixCas(recomanador, "5. Sexe", uGermany2, 150, true, -1, false, false);
        imprimeixCas(recomanador, "6. Sexe i pais", uGermany2, 150, true, -1, true, false);
        imprimeixCas(recomanador, "7. Sexe i edat", uFrance, 200, true, 4, false, false);
        imprimeixCas(recomanador, "8. Pais i preferits", uBeatsteaks, 50, false, -1, true, true);
        imprimeixCas(recomanador, "9. Edat, sexe i preferits", uBadReligion, 50, true, 5, false, true);
        imprimeixCas(recomanador, "10. Edat, pais, sexe i preferits", uDreamTheater, 50, true, 5, true, true);
    }

    private static void imprimeixCas(
            Recomanador recomanador,
            String titol,
            String userid,
            int numMinEscoltes,
            boolean sexe,
            int rangEdat,
            boolean pais,
            boolean preferits
    ) {
        UsuariLastFm usuari = recomanador.getUsuari(userid);
        TADLlista<String> artistes = recomanador.recomanacio(userid, numMinEscoltes, sexe, rangEdat, pais, preferits);

        System.out.println();
        System.out.println(titol);
        System.out.println("Usuari: " + userid + resumUsuari(usuari));
        System.out.println("Parametres: min=" + numMinEscoltes + ", sexe=" + sexe + ", rangEdat=" + rangEdat + ", pais=" + pais + ", preferits=" + preferits);
        System.out.println("Total recomanacions: " + artistes.numElem());
        System.out.println("Primers artistes: " + primers(artistes, 15));
    }

    private static String resumUsuari(UsuariLastFm usuari) {
        if (usuari == null) {
            return "";
        }
        return " [sexe=" + usuari.getSexe() + ", edat=" + usuari.getEdat() + ", pais=" + usuari.getPais() + "]";
    }

    private static String primers(TADLlista<String> artistes, int limit) {
        if (artistes.esBuida()) {
            return "Sense recomanacions";
        }

        StringBuilder sb = new StringBuilder();
        int max = Math.min(limit, artistes.numElem());
        for (int i = 0; i < max; i++) {
            try {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(artistes.consultar(i));
            } catch (LlistaBuida e) {
                throw new IllegalStateException(e);
            }
        }
        if (artistes.numElem() > limit) {
            sb.append("...");
        }
        return sb.toString();
    }
}
