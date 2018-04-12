package knowledge.algorithm.chainTable;


public class LinkList {
    private ListNode node;

    /**
     * 插入
     *
     * @param data
     */
    public void insert(int data) {
        if (node == null) {
            node = new ListNode(data);
            return;
        }
        ListNode cur = node;
        while (cur.next != null) {
            cur = cur.next;
        }
        cur.next = new ListNode(data);

    }

    /**
     * 遍历
     */
    public void print() {
        ListNode cur = node;
        if (node == null) {
            return;
        }
        while (cur != null) {
            System.out.println(cur.val);
            cur = cur.next;
        }
    }

    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        LinkList list = new LinkList();
        list.insert(1);
        list.insert(2);
        list.insert(3);
        list.print();
    }
}
