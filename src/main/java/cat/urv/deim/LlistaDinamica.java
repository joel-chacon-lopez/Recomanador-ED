package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.LlistaBuida;
import cat.urv.deim.exceptions.LlistaPlena;

public class LlistaDinamica<E> implements TADLlista<E> {
    private Node<E> cap;
    private int numElem, capacitat;

    public LlistaDinamica(int capacitat) {
        this.cap = null;
        this.numElem = 0;
        this.capacitat = capacitat;
    }

    @Override
    public void inserir(E elem) throws LlistaPlena {
        if (esPlena()) {
            throw new LlistaPlena();
        }
        Node<E> nouNode = new Node<>(elem);
        // si no hi ha cap node, el nou element passa a ser el cap
        if (cap == null) {
            cap = nouNode;
        } else {
            Node<E> actual = cap;
            while (actual.getSeguent() != null) {
                actual = actual.getSeguent();
            }
            actual.setSeguent(nouNode);
        }
        numElem++;
    }

    @Override
    public void inserir(E elem, int pos) throws LlistaPlena {
        if (esPlena()) {
            throw new LlistaPlena();
        }
        if (pos < 0 || pos > numElem) {
            throw new IndexOutOfBoundsException("Posició fora de límits");
        }
        Node<E> nouNode = new Node<>(elem);
        // inserir al principi canvia directament el cap de la llista
        if (pos == 0) {
            nouNode.setSeguent(cap);
            cap = nouNode;
        } else {
            Node<E> actual = cap;
            for (int i = 0; i < pos - 1; i++) {
                actual = actual.getSeguent();
            }
            nouNode.setSeguent(actual.getSeguent());
            actual.setSeguent(nouNode);
        }
        numElem++;
    }

    @Override
    public void esborrar(int pos) throws LlistaBuida {
        if (esBuida()) {
            throw new LlistaBuida();
        }
        if (pos < 0 || pos >= numElem) {
            throw new IndexOutOfBoundsException("Posició fora de límits");
        }
        if (pos == 0) {
            cap = cap.getSeguent();
        } else {
            Node<E> actual = cap;
            for (int i = 0; i < pos - 1; i++) {
                actual = actual.getSeguent();
            }
            actual.setSeguent(actual.getSeguent().getSeguent());
        }
        numElem--;
    }

    @Override
    public E consultar(int pos) throws LlistaBuida {
        if (esBuida()) {
            throw new LlistaBuida();
        }
        if (pos < 0 || pos >= numElem) {
            throw new IndexOutOfBoundsException("Posició fora de límits");
        }
        // avancem node a node fins arribar a la posicio demanada
        Node<E> actual = cap;
        for (int i = 0; i < pos; i++) {
            actual = actual.getSeguent();
        }
        return actual.getElement();
    }

    @Override
    public int numElem() {
        return this.numElem;
    }

    @Override
    public boolean esBuida() {
        return this.numElem == 0;
    }

    @Override
    public boolean esPlena() {
        return this.numElem >= this.capacitat;
    }

    @Override
    public boolean existeix(E elem) {
        if (esBuida()) {
            return false;
        }
        Node<E> actual = cap;
        while (actual != null) {
            if (actual.getElement().equals(elem)) {
                return true;
            }
            actual = actual.getSeguent();
        }
        return false;
    }

    @Override
    public int posicio(E elem) throws ElementNoTrobat {
        if (esBuida()) {
            throw new ElementNoTrobat();
        }
        Node<E> actual = cap;
        int pos = 0;
        while (actual != null) {
            if (actual.getElement().equals(elem)) {
                return pos;
            }
            actual = actual.getSeguent();
            pos++;
        }
        throw new ElementNoTrobat();
    }

    @Override
    public E anterior(E elem) throws ElementNoTrobat {
        if (esBuida() || !existeix(elem)) {
            throw new ElementNoTrobat();
        }
        Node<E> actual = cap;
        Node<E> anterior = null;
        while (actual != null) {
            if (actual.getElement().equals(elem)) {
                return anterior != null ? anterior.getElement() : null;
            }
            anterior = actual;
            actual = actual.getSeguent();
        }
        throw new ElementNoTrobat();
    }

    @Override
    public E seguent(E elem) throws ElementNoTrobat {
        if (esBuida() || !existeix(elem)) {
            throw new ElementNoTrobat();
        }
        Node<E> actual = cap;
        while (actual != null) {
            if (actual.getElement().equals(elem)) {
                return actual.getSeguent() != null ? actual.getSeguent().getElement() : null;
            }
            actual = actual.getSeguent();
        }
        throw new ElementNoTrobat();
    }
}
