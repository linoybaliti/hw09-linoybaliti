public class List {
    private Node first;
    private int size;
    
    public List() {
        first = null;
        size = 0;
    }
    
    public int getSize() { return size; }

    public CharData getFirst() {
        if (first == null) return null;
        return first.cp;
    }

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
            st += current.cp.toString();
            if (current.next != null) st += " ";
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
        if (index != -1)
            get(index).count++;
        else
             addFirst(chr);
    }

    public boolean remove(char chr) {
        if (first == null) return false;
        if (first.cp.equals(chr)){
            first = first.next;
            size--;
            return true;
        }
        Node current = first;
        while(current.next != null) {
            if (current.next.cp.equals(chr)){
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public CharData get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        Node current = first;
        for (int i = 0; i < index; i++) current = current.next;
        return current.cp;
    }

    public CharData[] toArray() {
        CharData[] arr = new CharData[size];
        Node current = first;
        int i = 0;
        while (current != null) {
            arr[i++] = current.cp;
            current = current.next;
        }
        return arr;
    }

    public ListIterator listIterator(int index) {
        if (size == 0) return null;
        Node current = first;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        return new ListIterator(current);
    }
}
