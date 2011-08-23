
//Haha: realized later this is a just a bucket sort! Still leaving it up, though.

public class NLogNBreak {
	public static class LinkedListBack {
		public LinkedListBack(int val){
			first = new Node();
			first.val = val;
		}
		public Node first = null;
		public void insert(int i){
			Node n = new Node();
			n.val = i;
			n.next = first;
			first = n;
		}
	}
	
	private static class Node {
		public Node next = null;
		public int val;
	}
	
	//max > in[i] > 0
	public static LinkedListBack[] sorted(int[] in, int max){
		LinkedListBack[] ar = new LinkedListBack[max + 1];
		for (int i = 0; i < in.length; i++) {
			int val = in[i];
			if(ar[val] == null){
				ar[val] = new LinkedListBack(val);
			} else {
				ar[val].insert(val);
			}
		}
		return ar;
	}
	
	public static void main(String[] args){
		int[] i = {1,3,5,5,2,2,1,5,5,3,3,3,1,3,3,4,4,3,2,1,1,3,4,5,4,5};
		Object obj = sorted(i, 5);
		System.out.println("Success");
		System.out.println("Success!");
	}
}
