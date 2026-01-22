public class List {
    private Node first;
    private int size;
    public List() { first = null; size = 0; }
    public int getSize() { return size; }
    public CharData getFirst() { return (first == null) ? null : first.cp; }
    public void addFirst(char chr) {
        CharData newChar = new CharData(chr);
        Node newNode = new Node(newChar);
        newNode.next = first;
        first = newNode;
        size++;
    }
    public String toString() {
        String st = "(";
        Node current = first;
        while (current != null){
            st += current.cp.toString() + (current.next != null ? " " : "");
            current = current.next;
        }
        return st + ")";
    }
    public int indexOf(char chr) {
        Node current = first;
        int index = 0;
        while (current != null){
            if (current.cp.equals(chr)) return index;
            current = current.next;
            index++;
        }
        return -1;
    }
    public void update(char chr) {
        int index = indexOf(chr);
        if (index != -1) get(index).count++;
        else addFirst(chr);
    }
    public CharData get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = first;
        for (int i = 0; i < index; i++) current = current.next;
        return current.cp;
    }
    public ListIterator listIterator(int index) {
        if (size == 0) return null;
        Node current = first;
        for (int i = 0; i < index; i++) current = current.next;
        return new ListIterator(current);
    }
}
