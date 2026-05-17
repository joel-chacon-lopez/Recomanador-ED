package cat.urv.deim;

public class NodeHash<K, V> {
    private final K key;
    private V valor;
    private NodeHash<K, V> seguent;

    public NodeHash(K key, V valor) {
        this.key = key;
        this.valor = valor;
        this.seguent = null;
    }

    public K getKey() {
        return key;
    }

    public V getValor() {
        return valor;
    }

    public NodeHash<K, V> getSeguent() {
        return seguent;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }

    public void setSeguent(NodeHash<K, V> seguent) {
        this.seguent = seguent;
    }

}
