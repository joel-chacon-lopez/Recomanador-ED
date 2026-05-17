package cat.urv.deim;

public class Node<E> {
    private E element;
    private Node<E> seguent;

    public Node(E element) {
        this.element = element;
        this.seguent = null;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public Node<E> getSeguent() {
        return seguent;
    }

    public void setSeguent(Node<E> seguent) {
        this.seguent = seguent;
    }

}
