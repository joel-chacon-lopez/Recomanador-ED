package cat.urv.deim;

import cat.urv.deim.exceptions.*;

/**
 * tad llista generica no ordenada amb acces per posicio.
 * permet elements duplicats. les posicions son 0-indexed.
 * la cerca d'elements es fa mitjancant equals().
 *
 * a mes, la interficie defineix una comparacio lexicografica entre llistes.
 * aquesta comparacio es fa element a element i, si totes dues llistes
 * coincideixen fins a la longitud minima comuna, la llista mes curta es
 * considera mes petita. perque aquesta comparacio tingui sentit, els
 * elements guardats han de ser comparables entre si.
 *
 * @param <E> el tipus d'elements de la llista
 */
public interface TADLlista<E> extends Comparable<TADLlista<E>> {

    /* metodes per a gestionar la llista */

    /**
     * insereix un element al final de la llista.
     * @param elem element a inserir
     * @throws LlistaPlena si la llista ha arribat a la capacitat maxima
     */
    public void inserir(E elem) throws LlistaPlena;

    /**
     * insereix un element a la posicio indicada (0-indexed).
     * els elements existents a partir d'aquesta posicio es desplacen una posicio cap a la dreta.
     * @param elem element a inserir
     * @param pos posicio on inserir (0 <= pos <= numElem())
     * @throws LlistaPlena si la llista ha arribat a la capacitat maxima
     */
    public void inserir(E elem, int pos) throws LlistaPlena;

    /**
     * esborra l'element a la posicio indicada (0-indexed).
     * els elements posteriors es desplacen una posicio cap a l'esquerra.
     * @param pos posicio de l'element a esborrar (0 <= pos < numElem())
     * @throws LlistaBuida si la llista es buida
     */
    public void esborrar(int pos) throws LlistaBuida;

    /**
     * retorna l'element a la posicio indicada sense esborrar-lo.
     * @param pos posicio de l'element (0 <= pos < numElem())
     * @return l'element a la posicio indicada
     * @throws LlistaBuida si la llista es buida
     */
    public E consultar(int pos) throws LlistaBuida;

    /**
     * retorna el nombre d'elements de la llista.
     * @return nombre d'elements
     */
    public int numElem();

    /**
     * comprova si la llista es buida.
     * @return true si no conte cap element
     */
    public boolean esBuida();

    /**
     * comprova si la llista es plena (ha arribat a la capacitat maxima).
     * @return true si numElem() == capacitat maxima
     */
    public boolean esPlena();

    /* metodes per a validar l'estructura de la llista */

    /**
     * comprova si un element existeix a la llista (utilitza equals()).
     * @param elem element a buscar
     * @return true si l'element es troba a la llista
     */
    public boolean existeix(E elem);

    /**
     * retorna la posicio de la primera ocurrencia de l'element (utilitza equals()).
     * la primera posicio es la posicio 0.
     * @param elem element a buscar
     * @return posicio de la primera ocurrencia (0-indexed)
     * @throws ElementNoTrobat si l'element no es troba a la llista
     */
    public int posicio(E elem) throws ElementNoTrobat;

    /**
     * retorna l'element anterior a la primera ocurrencia de l'element donat.
     * si l'element es troba a la posicio 0, retorna null.
     * @param elem element de referencia
     * @return l'element anterior, o null si l'element es el primer
     * @throws ElementNoTrobat si l'element no es troba a la llista
     */
    public E anterior(E elem) throws ElementNoTrobat;

    /**
     * retorna l'element seguent a la primera ocurrencia de l'element donat.
     * si l'element es troba a l'ultima posicio, retorna null.
     * @param elem element de referencia
     * @return l'element seguent, o null si l'element es l'ultim
     * @throws ElementNoTrobat si l'element no es troba a la llista
     */
    public E seguent(E elem) throws ElementNoTrobat;

    /**
     * compara aquesta llista amb una altra llista de manera lexicografica.
     *
     * es comparen primer els elements de la posicio 0, despres els de la
     * posicio 1, i aixi successivament fins a trobar una diferencia. si tots
     * els elements coincideixen fins a la longitud de la llista mes curta,
     * la comparacio depen de la mida: la llista mes curta es considera menor.
     *
     * si algun dels elements no implementa {@code Comparable}, el metode
     * llança una {@code IllegalStateException}.
     *
     * @param altra llista amb la qual es vol comparar
     * @return valor negatiu si aquesta llista es menor, 0 si son iguals i
     *         valor positiu si aquesta llista es major
     */
    @Override
    public default int compareTo(TADLlista<E> altra) {
        int minim = Math.min(numElem(), altra.numElem());
        for (int i = 0; i < minim; i++) {
            E esquerra = consultarSenseExcepcio(this, i);
            E dreta = consultarSenseExcepcio(altra, i);
            int comparacio = compararElements(esquerra, dreta);
            if (comparacio != 0) {
                return comparacio;
            }
        }
        return Integer.compare(numElem(), altra.numElem());
    }

    private static <T> T consultarSenseExcepcio(TADLlista<T> llista, int pos) {
        try {
            return llista.consultar(pos);
        } catch (LlistaBuida e) {
            throw new IllegalStateException("La llista no pot estar buida en aquesta comparacio", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> int compararElements(T esquerra, T dreta) {
        if (esquerra == dreta) {
            return 0;
        }
        if (esquerra == null) {
            return -1;
        }
        if (dreta == null) {
            return 1;
        }
        if (!(esquerra instanceof Comparable<?>)) {
            throw new IllegalStateException("Els elements de la llista han de ser comparables");
        }
        return ((Comparable<Object>) esquerra).compareTo(dreta);
    }
}
