package cat.urv.deim;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.LlistaBuida;
import cat.urv.deim.exceptions.LlistaPlena;

public class Recomanador {
    private final TADHashMap<String, UsuariLastFm> usuaris;
    private final TADHashMap<String, Artista> artistes;

    public Recomanador() {
        this(2048, 8192);
    }

    public Recomanador(int capacitatUsuaris, int capacitatArtistes) {
        this.usuaris = new HashingIndirecte<>(capacitatUsuaris);
        this.artistes = new HashingIndirecte<>(capacitatArtistes);
    }

    public void carregaPerfils(Path fitxerPerfils) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(fitxerPerfils, StandardCharsets.UTF_8)) {
            String linia;
            while ((linia = br.readLine()) != null) {
                // cada linia del fitxer conte les dades basiques d'un usuari
                String[] camps = linia.split("\t", -1);
                if (camps.length < 5) {
                    continue;
                }

                String id = camps[0].trim();
                String sexe = camps[1].trim();
                Integer edat = parseEdat(camps[2].trim());
                String pais = camps[3].trim();
                String alta = camps[4].trim();
                usuaris.inserir(id, new UsuariLastFm(id, sexe, edat, pais, alta));
            }
        }
    }

    public void carregaEscoltes(Path fitxerEscoltes) throws IOException {
        carregaEscoltes(fitxerEscoltes, -1);
    }

    public void carregaEscoltes(Path fitxerEscoltes, int maximLinies) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(fitxerEscoltes, StandardCharsets.UTF_8)) {
            String linia;
            int liniesLlegides = 0;
            while ((linia = br.readLine()) != null) {
                // el limit permet provar el dataset real sense carregar-lo sencer
                if (maximLinies >= 0 && liniesLlegides >= maximLinies) {
                    break;
                }
                liniesLlegides++;

                String[] camps = linia.split("\t", -1);
                if (camps.length < 4) {
                    continue;
                }

                String idUsuari = camps[0].trim();
                if (!usuaris.buscar(idUsuari)) {
                    // si no tenim el perfil, no podem usar les seves escoltes
                    continue;
                }

                String idArtista = camps[1].trim();
                String nomArtista = camps[2].trim();
                int reproduccions = parseEnter(camps[3].trim());

                if (!artistes.buscar(nomArtista)) {
                    artistes.inserir(nomArtista, new Artista(idArtista, nomArtista));
                }

                try {
                    UsuariLastFm usuari = usuaris.consultar(idUsuari);
                    usuari.afegirEscolta(new Escolta(idArtista, nomArtista, reproduccions));
                } catch (ElementNoTrobat e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public TADLlista<String> recomanacio(String userid, int numMinEscoltes, boolean sexe, int rangEdat, boolean pais, boolean preferits) {
        int maximRecomanacions = Math.max(1, artistes.numElements());
        TADLlista<String> resultat = new LlistaDinamica<>(maximRecomanacions);
        if (!usuaris.buscar(userid)) {
            return resultat;
        }

        try {
            UsuariLastFm objectiu = usuaris.consultar(userid);
            boolean usaEdat = rangEdat >= 0 && objectiu.getEdat() != null;
            TADLlista<String> preferitsObjectiu = objectiu.artistesPreferits(2);
            // aquesta taula evita recomanar el mateix artista mes d'una vegada
            TADHashMap<String, String> recomanacionsSenseRepetir = new HashingIndirecte<>(maximRecomanacions);

            for (UsuariLastFm candidat : usuaris) {
                if (candidat.getId().equals(userid)) {
                    continue;
                }
                if (!esSimilar(objectiu, candidat, sexe, usaEdat, rangEdat, pais, preferits, preferitsObjectiu)) {
                    continue;
                }

                TADLlista<Escolta> escoltes = candidat.getEscoltes();
                for (int i = 0; i < escoltes.numElem(); i++) {
                    Escolta escolta = escoltes.consultar(i);
                    String nomArtista = escolta.getNomArtista();
                    // nomes recomanem artistes prou escoltats i nous per a l'usuari objectiu
                    if (escolta.getReproduccions() >= numMinEscoltes
                            && !objectiu.haEscoltat(nomArtista)
                            && !recomanacionsSenseRepetir.buscar(nomArtista)) {
                        recomanacionsSenseRepetir.inserir(nomArtista, nomArtista);
                        inserirSegur(resultat, nomArtista);
                    }
                }
            }
        } catch (ElementNoTrobat | LlistaBuida e) {
            throw new IllegalStateException(e);
        }

        return resultat;
    }

    public UsuariLastFm getUsuari(String userid) {
        try {
            return usuaris.consultar(userid);
        } catch (ElementNoTrobat e) {
            return null;
        }
    }

    public int totalUsuaris() {
        return usuaris.numElements();
    }

    public int totalArtistes() {
        return artistes.numElements();
    }

    private boolean esSimilar(UsuariLastFm objectiu, UsuariLastFm candidat, boolean sexe, boolean usaEdat,int rangEdat, boolean pais, boolean preferits, TADLlista<String> preferitsObjectiu) {
        if (sexe && (objectiu.getSexe().isBlank() || !objectiu.getSexe().equalsIgnoreCase(candidat.getSexe()))) {
            return false;
        }

        // l'edat es comprova amb un marge indicat per parametre
        if (usaEdat) {
            Integer edatCandidat = candidat.getEdat();
            if (edatCandidat == null || Math.abs(objectiu.getEdat() - edatCandidat) > rangEdat) {
                return false;
            }
        }

        if (pais && (objectiu.getPais().isBlank() || !objectiu.getPais().equalsIgnoreCase(candidat.getPais()))) {
            return false;
        }

        if (preferits) {
            // amb preferits, cal que els dos usuaris comparteixin algun artista principal
            TADLlista<String> preferitsCandidat = candidat.artistesPreferits(2);
            for (int i = 0; i < preferitsCandidat.numElem(); i++) {
                try {
                    if (preferitsObjectiu.existeix(preferitsCandidat.consultar(i))) {
                        return true;
                    }
                } catch (LlistaBuida e) {
                    throw new IllegalStateException(e);
                }
            }
            return false;
        }

        return true;
    }

    private void inserirSegur(TADLlista<String> llista, String valor) {
        try {
            llista.inserir(valor);
        } catch (LlistaPlena e) {
            throw new IllegalStateException("S'ha superat el maxim de recomanacions previst", e);
        }
    }

    private Integer parseEdat(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        int edat = parseEnter(valor);
        return edat > 0 ? edat : null;
    }

    private int parseEnter(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
