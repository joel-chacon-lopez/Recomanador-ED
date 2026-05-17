package cat.urv.deim;

import cat.urv.deim.exceptions.LlistaBuida;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRecomanador {

    @TempDir
    Path directoriTemporal;

    // -------------------------------------------------------------------------
    // carregaperfils i carregaescoltes
    // -------------------------------------------------------------------------

    @Test
    public void testCarregaPerfilsComptaUsuaris() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        assertEquals(6, recomanador.totalUsuaris());
    }

    @Test
    public void testCarregaEscoltesComptaArtistesUnics() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        assertEquals(8, recomanador.totalArtistes());
    }

    @Test
    public void testCarregaEscoltesIgnoraUsuarisSensePerfil() throws IOException {
        Path perfils = escriure("perfils.tsv",
                "u1\tm\t20\tSpain\tJan 1, 2020\n");
        Path escoltes = escriure("escoltes.tsv",
                "u1\ta1\tartist one\t100\n" +
                "missing\ta2\tartist two\t200\n");

        Recomanador recomanador = new Recomanador();
        recomanador.carregaPerfils(perfils);
        recomanador.carregaEscoltes(escoltes);

        assertEquals(1, recomanador.totalUsuaris());
        assertEquals(1, recomanador.totalArtistes());
    }

    @Test
    public void testGetUsuariInexistentRetornaNull() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        assertNull(recomanador.getUsuari("missing"));
    }

    // -------------------------------------------------------------------------
    // recomanacio
    // -------------------------------------------------------------------------

    @Test
    public void testRecomanacioUsuariInexistentRetornaLlistaBuida() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("missing", 100, false, -1, false, false);

        assertTrue(resultat.esBuida());
    }

    @Test
    public void testRecomanacioNoInclouArtistesJaEscoltats() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, -1, false, false);

        assertFalse(resultat.existeix("artist common"));
        assertTrue(resultat.existeix("artist country"));
    }

    @Test
    public void testRecomanacioRespectaMinimEscoltes() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, -1, false, false);

        assertFalse(resultat.existeix("artist low"));
        assertTrue(resultat.existeix("artist high"));
    }

    @Test
    public void testRecomanacioNoRepeteixArtistes() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, -1, false, false);

        assertEquals(1, aparicions(resultat, "artist country"));
    }

    @Test
    public void testRecomanacioPerPais() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, -1, true, false);

        assertTrue(resultat.existeix("artist country"));
        assertFalse(resultat.existeix("artist other-country"));
    }

    @Test
    public void testRecomanacioPerSexe() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, true, -1, false, false);

        assertTrue(resultat.existeix("artist country"));
        assertFalse(resultat.existeix("artist female"));
    }

    @Test
    public void testRecomanacioPerEdat() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, 5, false, false);

        assertTrue(resultat.existeix("artist country"));
        assertFalse(resultat.existeix("artist older"));
    }

    @Test
    public void testRecomanacioPerEdatIgnoraDimensioSiUsuariNoTeEdat() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u6", 100, false, 5, false, false);

        assertFalse(resultat.existeix("artist country"));
        assertTrue(resultat.existeix("artist common"));
        assertTrue(resultat.existeix("artist older"));
    }

    @Test
    public void testRecomanacioPerPreferits() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, -1, false, true);

        assertTrue(resultat.existeix("artist country"));
        assertFalse(resultat.existeix("artist high"));
    }

    @Test
    public void testRecomanacioPerEdatIPais() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, false, 5, true, false);

        assertTrue(resultat.existeix("artist country"));
        assertFalse(resultat.existeix("artist other-country"));
        assertFalse(resultat.existeix("artist older"));
    }

    @Test
    public void testRecomanacioPerTotesLesDimensions() throws IOException {
        Recomanador recomanador = crearRecomanadorBase();

        TADLlista<String> resultat = recomanador.recomanacio("u1", 100, true, 5, true, true);

        assertEquals(1, resultat.numElem());
        assertTrue(resultat.existeix("artist country"));
    }

    private Recomanador crearRecomanadorBase() throws IOException {
        Path perfils = escriure("perfils.tsv",
                "u1\tm\t20\tSpain\tJan 1, 2020\n" +
                "u2\tm\t22\tSpain\tJan 2, 2020\n" +
                "u3\tm\t35\tSpain\tJan 3, 2020\n" +
                "u4\tf\t21\tSpain\tJan 4, 2020\n" +
                "u5\tm\t20\tFrance\tJan 5, 2020\n" +
                "u6\tm\t\tSpain\tJan 6, 2020\n");
        Path escoltes = escriure("escoltes.tsv",
                "u1\ta1\tartist common\t500\n" +
                "u1\ta2\tartist own\t300\n" +
                "u2\ta1\tartist common\t450\n" +
                "u2\ta3\tartist country\t220\n" +
                "u3\ta1\tartist common\t430\n" +
                "u3\ta4\tartist older\t210\n" +
                "u4\ta1\tartist common\t410\n" +
                "u4\ta5\tartist female\t230\n" +
                "u5\ta1\tartist common\t420\n" +
                "u5\ta6\tartist other-country\t240\n" +
                "u6\ta7\tartist low\t90\n" +
                "u6\ta8\tartist high\t190\n" +
                "u6\ta3\tartist country\t180\n");

        Recomanador recomanador = new Recomanador();
        recomanador.carregaPerfils(perfils);
        recomanador.carregaEscoltes(escoltes);
        return recomanador;
    }

    private Path escriure(String nomFitxer, String contingut) throws IOException {
        Path fitxer = directoriTemporal.resolve(nomFitxer);
        Files.writeString(fitxer, contingut, StandardCharsets.UTF_8);
        return fitxer;
    }

    private int aparicions(TADLlista<String> llista, String valor) {
        int total = 0;
        for (int i = 0; i < llista.numElem(); i++) {
            try {
                if (llista.consultar(i).equals(valor)) {
                    total++;
                }
            } catch (LlistaBuida e) {
                throw new IllegalStateException(e);
            }
        }
        return total;
    }
}
