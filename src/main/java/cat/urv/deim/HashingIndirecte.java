package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.LlistaPlena;
import java.util.ArrayList;
import java.util.Iterator;

public class HashingIndirecte<K extends Comparable<K>, V> implements TADHashMap<K, V> {
    private int capacitat, numElements;
    private ArrayList<NodeHash<K, V>> taula;

    public HashingIndirecte(int capacitat) {
        this.capacitat = capacitat;
        this.numElements = 0;
        this.taula = new ArrayList<>(capacitat);
        for (int i = 0; i < capacitat; i++) {
            taula.add(null);
        }
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % capacitat);
    }

    @Override
    public void inserir(K key, V value) {
        int pos = hash(key);
        NodeHash<K, V> node = taula.get(pos);

        // cada posicio pot tenir una cadena de nodes si hi ha col.lisions
        if (node == null) {
            node = new NodeHash<>(key, value);
            taula.set(pos, node);
        } else {
            while (node != null) {
                if (node.getKey().equals(key)) {
                    node.setValor(value);
                    return;
                }
                node = node.getSeguent();
            }
            NodeHash<K, V> newNode = new NodeHash<>(key, value);
            newNode.setSeguent(taula.get(pos));
            taula.set(pos, newNode);
        }
        numElements++;

        // si la taula queda massa plena, es dobla per reduir col.lisions
        if (factorCarrega() > 0.75) redimensionar();

    }

    @Override
    public V consultar(K key) throws ElementNoTrobat {
        int pos = hash(key);
        NodeHash<K, V> node = taula.get(pos);
        while (node != null) {
            if (node.getKey().equals(key)) {
                return node.getValor();
            }
            node = node.getSeguent();
        }
        throw new ElementNoTrobat();
    }

    @Override
    public void esborrar(K key) throws ElementNoTrobat {
        int pos = hash(key);
        NodeHash<K, V> node = taula.get(pos);
        NodeHash<K, V> prev = null;
        while (node != null) {
            if (node.getKey().equals(key)) {
                if (prev == null) {
                    taula.set(pos, node.getSeguent());
                } else {
                    prev.setSeguent(node.getSeguent());
                }
                numElements--;
                return;
            }
            prev = node;
            node = node.getSeguent();
        }
        throw new ElementNoTrobat();
    }

    @Override
    public boolean buscar(K key) {
        int pos = hash(key);
        NodeHash<K, V> node = taula.get(pos);
        while (node != null) {
            if (node.getKey().equals(key)) {
                return true;
            }
            node = node.getSeguent();
        }
        return false;
    }

    @Override
    public boolean esBuida() {
        return numElements == 0;
    }

    @Override
    public int numElements() {
        return numElements;
    }

    @Override
    public TADLlista<K> obtenirClaus() {
        TADLlista<K> claus = new LlistaDinamica<K>(numElements);
        for (NodeHash<K, V> node : taula) {
            while (node != null) {
                try {
                    claus.inserir(node.getKey());
                } catch (LlistaPlena e) {
                    e.printStackTrace();
                }
                node = node.getSeguent();
            }
        }
        return claus;
    }

    @Override
    public float factorCarrega() {
        return (float) numElements / capacitat;
    }

    @Override
    public int midaTaula() {
        return capacitat;
    }

    @Override
    public Iterator<V> iterator() {
        ArrayList<V> valors = new ArrayList<>();
        for (NodeHash<K, V> node : taula) {
            while (node != null) {
                valors.add(node.getValor());
                node = node.getSeguent();
            }
        }
        return valors.iterator();
    }

    private void redimensionar() {
        this.capacitat = capacitat * 2;
        ArrayList<NodeHash<K,V>> novaTaula = new ArrayList<>(capacitat);
        for(int i = 0; i < capacitat; i++) novaTaula.add(null);
        for(NodeHash<K,V> n : taula) {
            while (n != null) {
                // guardem el seguent abans de moure el node a la nova taula
                NodeHash<K,V> aux = n.getSeguent();
                int pos = hash(n.getKey());

                n.setSeguent(novaTaula.get(pos));
                novaTaula.set(pos, n);
                n = aux;
            }
        }
        taula = novaTaula;
    }
}
