package cat.urv.deim;

public class Escolta {
    private final String idArtista;
    private final String nomArtista;
    private final int reproduccions;

    public Escolta(String idArtista, String nomArtista, int reproduccions) {
        this.idArtista = idArtista;
        this.nomArtista = nomArtista;
        this.reproduccions = reproduccions;
    }

    public String getIdArtista() {
        return idArtista;
    }

    public String getNomArtista() {
        return nomArtista;
    }

    public int getReproduccions() {
        return reproduccions;
    }
}
