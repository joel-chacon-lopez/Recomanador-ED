package cat.urv.deim;

public class Artista {
    private final String id;
    private final String nom;

    public Artista(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
}
