package tatoeba;

import java.util.ArrayList;

/*
 reimplementation of a stack so that we can run up and down in the stack without tearing it down.
 */
public class ClusterStack {

    /* ClusterStack differs from a normal stack only by the method descend() which is the same as pop()
     but the top of the element is not removed. It stays on the stack, above the top. 
     The method rise() is the opposite of descend()
     */
    ArrayList<Cluster> stack;
    int head;  // pointer to the top of the stack. -1 if stack is empty
    int headHighMark; // end of the array. Is different from 'head' after descend() was called

    public ClusterStack() {
        stack = new ArrayList<>();
        head = -1;
        headHighMark = -1;
    }
    
    public void reset(){
        stack.clear();
        head=-1;
        headHighMark=-1;
    }

    public Cluster pop() {
        if (head < 0) {
            return null;
        } else {
            head--;
            return stack.get(head + 1);
        }
    }

    public boolean isEmpty() {
        return (head < 0);
    }

    public int size() {
        return head + 1;
    }

    public Cluster peekFirst() {
        if (head < 0) {
            return null;
        } else {
            return stack.get(head);
        }

    }

    public void push(Cluster c) {
        // can only push to the end of the array !
        stack.add(c);
        headHighMark++;
        head = headHighMark;
    }

    public boolean descend() {
        if (head < 0) {
            return false;
        } else {
            head--;
            return true;
        }
    }

    public boolean rise() {
        if (head < headHighMark) {
            head++;
            return true;
        } else {
            return false;
        }
    }

}
