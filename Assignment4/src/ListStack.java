// Simple generic linked-list stack used by StackDriver.
// Methods used by the driver: push, pop, top, topAndPop, isEmpty.

public class ListStack<T> {

    // singly-linked node
    private static class Node<E> {
        E data;
        Node<E> next;
        Node(E d, Node<E> n) { data = d; next = n; }
    }

    private Node<T> top; // head of the stack

    public ListStack() {
        top = null;
    }

    public boolean isEmpty() {
        return top == null;
    }

    // push item onto the stack
    public void push(T x) {
        top = new Node<>(x, top);
    }

    // return (but do NOT remove) top item
    public T top() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow: top on empty stack");
        return top.data;
    }

    // remove top item (void)
    public void pop() {
        if (isEmpty()) throw new IllegalStateException("Stack underflow: pop on empty stack");
        top = top.next;
    }

    // convenience: return AND remove top item
    public T topAndPop() {
        T t = top();
        pop();
        return t;
    }

    // optional helper
    public void makeEmpty() {
        top = null;
    }
}
