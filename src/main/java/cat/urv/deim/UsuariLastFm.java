package cat.urv.deim;

import cat.urv.deim.exceptions.LlistaBuida;
import cat.urv.deim.exceptions.LlistaPlena;

public class UsuariLastFm {
    private static final int MAX_ESCOLTES_USUARI = 20000;

    private final String id;
    private final String sexe;
    private final Integer edat;
    private final String pais;
    private final String alta;
    private final TADLlista<Escolta> escoltes;

    public UsuariLastFm(String id, String sexe, Integer edat, String pais, String alta) {
        this.id = id;
        this.sexe = sexe == null ? "" : sexe;
        this.edat = edat;
        this.pais = pais == null ? "" : pais;
        this.alta = alta == null ? "" : alta;
        this.escoltes = new LlistaDinamica<>(MAX_ESCOLTES_USUARI);
    }

    public void afegirEscolta(Escolta escolta) {
        try {
            escoltes.inserir(escolta);
        } catch (LlistaPlena e) {
            throw new IllegalStateException("L'usuari supera el maxim d'escoltes previst", e);
        }
    }

    public String getId() {
        return id;
    }

    public String getSexe() {
        return sexe;
    }

    public Integer getEdat() {
        return edat;
    }

    public String getPais() {
        return pais;
    }

    public String getAlta() {
        return alta;
    }

    public TADLlista<Escolta> getEscoltes() {
        return escoltes;
    }

    public boolean haEscoltat(String nomArtista) {
        for (int i = 0; i < escoltes.numElem(); i++) {
            try {
                if (escoltes.consultar(i).getNomArtista().equalsIgnoreCase(nomArtista)) {
                    return true;
                }
            } catch (LlistaBuida e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    public TADLlista<String> artistesPreferits(int quantitat) {
        TADLlista<String> preferits = new LlistaDinamica<>(quantitat);
        for (int posicio = 0; posicio < quantitat; posicio++) {
            Escolta millor = null;

            for (int i = 0; i < escoltes.numElem(); i++) {
                try {
                    Escolta escolta = escoltes.consultar(i);
                    if ((millor == null || escolta.getReproduccions() > millor.getReproduccions())
                            && !preferits.existeix(escolta.getNomArtista())) {
                        millor = escolta;
                    }
                } catch (LlistaBuida e) {
                    throw new IllegalStateException(e);
                }
            }

            if (millor != null) {
                try {
                    preferits.inserir(millor.getNomArtista());
                } catch (LlistaPlena e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return preferits;
    }
}
