//Shon_Salamon
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */





public class FibonacciHeap {
	private HeapNode min;
	private HeapNode first;
	private int size = 0;
	private int markedNodes = 0;
	static int totalLinks = 0;
	static int totalCuts = 0;
	private int numOfTrees = 0;
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return min == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
    	if(this.isEmpty()) { //if the tree is empty, just add the new node
    		this.min = newNode;
    		this.first = newNode;
    		this.first.setNext(newNode);
    		this.first.setPrev(newNode);
    		this.size = 1;
    		this.setNumOfTrees(1);
    	}
    	else { 
    		newNode.next = first;
    		newNode.prev = first.prev;
    		newNode.prev.next = newNode;
    		newNode.next.prev = newNode;
    		this.first = newNode;
    		this.size += 1;
    		this.setNumOfTrees(this.getNumOfTrees() + 1);
    		this.min = key<this.min.getKey()?newNode:min;
    	}
    	return newNode; 
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	if(this.isEmpty())
    		return;
    	if(this.min == this.first)
    		if(this.first.getChild() != null) {
    			HeapNode tmp1 = this.min;
    			this.first = this.first.getChild();
    			this.min = tmp1;
    		}else {
    			this.first = this.min.getNext();
    		}
    	if(min.getChild() == null) {
    		min.getPrev().setNext(min.getNext());
    		min.getNext().setPrev(min.getPrev());
	
    	}else {
        	HeapNode tmp = this.min.getChild();
        	this.min.getPrev().setNext(tmp);
        	tmp.setPrev(min.getPrev());
        	tmp.setParent(null);
        	while (tmp.getNext() != this.min.getChild() ) {
        		if(tmp.mark == true) {
        			tmp.mark = false;
        			this.markedNodes--;
        		}
        		tmp = tmp.getNext();
        		tmp.setParent(null);	
        	}
        	this.min.getNext().setPrev(tmp);
        	tmp.setNext(min.getNext());	
    	}
    	
    	this.min.setNext(null);
    	this.min.setPrev(null);
    	this.min.setChild(null);

    	this.size-- ;
    	if(size == 0) {
    		this.first = null;
    		this.min = null;
    		this.numOfTrees = 0;
    		return;
    	}
    	this.Consolidate();

    		
    }

    private void Consolidate() {
    	HeapNode [] buckets = toBuckets();
    	fromBuckets(buckets);
    }
    private HeapNode [] toBuckets() {
    	HeapNode x = this.first;
    	int n = (int)((Math.log(size))/(Math.log((1 + Math.pow(5, 0.5)) / 2)));
    	HeapNode [] buckets = new HeapNode [n+1];
    	for (int i = 0; i < buckets.length; i++ ) {
    		buckets[i] = null;
    	}
    	x.getPrev().setNext(null);
    	
    	HeapNode y;
    	while(x != null) {
    		 y = x;
    		 x = x.getNext();
    		 while(buckets[y.getRank()] != null) {
    			 int tmp = y.getRank();
    			 y = link(y,buckets[y.getRank()]);
    			 totalLinks++;
    			 buckets[tmp] = null;
    		 }
    		 buckets[y.getRank()] = y;		  		
    	}
    	return buckets;
    }
    private void fromBuckets(HeapNode [] buckets) {
    	HeapNode x = null;
    	for ( int i = 0; i < buckets.length; i++) {
    		if(buckets[i] != null) {
    			if(x == null) {
    				x = buckets[i];
    				x.setNext(x);
    				x.setPrev(x);
    				this.first = x;
    				this.min = x;
    				this.setNumOfTrees(1);	
    			}else {
    				setNumOfTrees(getNumOfTrees() + 1);
    				insertAfter(x,buckets[i]);
    				x = buckets[i];
    				if(buckets[i].getKey() < this.min.getKey()) {
    					this.min = buckets[i];
    				}
    			}
    		}
    	}  
    }
    
    
    /**
     * private HeapNode link (HeapNode x, HeapNode y) {
     * link between two heaps. 
     *
     * 
     */
    private HeapNode link (HeapNode x, HeapNode y) {
    	if(x.getKey() > y.getKey()) {
    		HeapNode tmp = x;
    		x = y;
    		y = tmp;
    	}
    	if(x.getChild() == null) {
    		y.setNext(y);
    		y.setPrev(y);
    	}else {
    		y.setNext(x.getChild());
    		y.setPrev(x.getChild().getPrev());
    		x.getChild().getPrev().setNext(y);
    		x.getChild().setPrev(y);
    		}
    	x.setChild(y);
    	y.setParent(x);
    	x.setRank(x.getRank() + 1);
    	return x;
    }
    
    
    /**
     * private void insertAfter(HeapNode x , HeapNode tmp) {
     * insert the HeapNode tmp after HeapNode x.
     *
     * 
     */
    private void insertAfter(HeapNode x , HeapNode tmp) {
    	x.setNext(tmp);
    	tmp.setPrev(x);
    	tmp.setNext(first);
    	first.setPrev(tmp);
    }


   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	if(this.isEmpty()) {
    		this.first = heap2.first;
    		this.min = heap2.min;
    	}
    	else {
    		HeapNode last1 = this.first.prev;
    		HeapNode last2 = heap2.first.prev;
    		
    		heap2.first.prev = last1;
    		last1.next = heap2.first;
    		this.first.prev = last2;
    		last2.next = this.first;

    		this.min = this.min.key<heap2.min.key?min:heap2.min;
    	}
    	this.setNumOfTrees(getNumOfTrees() + heap2.getNumOfTrees()) ;
    	this.size += heap2.size();
    	
    }
   
    
   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	if(this.isEmpty()) {
    		int[] arr = new int[0];
    		return arr;
    	}
    	// finding the maximum rank of a tree in the heap.
    	HeapNode tmp = min;
    	int maxRank = min.getRank();
    	while(tmp.getNext() != min) {
    		tmp = tmp.getNext();
    		if (tmp.getRank() > maxRank) {
    			maxRank = tmp.getRank();
    		}
    		
    	}
    	int[] arr = new int[maxRank+1];
    	tmp = min;
    	arr[min.getRank()]++;
    	while(tmp.getNext() != min) {
    		tmp = tmp.getNext();
    		arr[tmp.getRank()]++;
    	}
	
        return arr; 
    }
    
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, x.getKey() - min.getKey() + 1 );
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key = x.key - delta;
    	HeapNode y = x.getParent();
    	if(x.getKey() < this.min.getKey()) {
    		this.min = x;
    	}
    	if(y == null) {
    		return;
    	}
    	if(y.getKey() < x.getKey()) {
    		return;
    	}
    	cascadingCuts(x,  y );
    }
    private void cut (HeapNode child, HeapNode parent ) {
    	totalCuts++;
    	child.setParent(null);
    	if(child.mark == true) {
    		this.markedNodes--;
    	}
    	child.setMark(false);
    	this.numOfTrees++;
    	parent.setRank(parent.getRank() - 1);
    	if(child.getNext() == child) {
    		parent.setChild(null);
    	}else {
    		if(parent.getChild() == child){
    			parent.setChild(child.getNext());
    			child.getNext().setParent(parent);
    		}
			child.getPrev().setNext(child.getNext());
			child.getNext().setPrev(child.getPrev());
    	}
    	insertFirst(child);
    }
    
    /**
     * public void insertFirst(HeapNode x)
     * insert the node on the beginning of the heap.
     *
     * 
     */
    private void insertFirst(HeapNode x) {
    	HeapNode prevFirst = this.first;
    	this.first = x;
    	first.setNext(prevFirst);
    	first.setPrev(prevFirst.getPrev());
    	prevFirst.setPrev(first);
    	first.getPrev().setNext(first);
    }
    
    private void cascadingCuts(HeapNode child, HeapNode parent) {
    	cut(child,  parent);
    	if(parent.getParent() != null ) {
    		if(parent.isMark() == false) {
    			parent.setMark(true);
    			this.markedNodes++;
    		}else {
    			cascadingCuts(parent,parent.getParent());	
    		}
    	}
    }
    
    
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.getNumOfTrees()+ 2 * this.getMarkedNodes() ;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    
    
   //insert to the tmp FibonacciHeap all of the current min's children - O(log(2^deg(H)) = O(deg(H))
    private static void insertChildren (FibonacciHeap H,FibonacciHeap tmp, HeapNode cur,int[]arr) {
    	if(cur.child!=null && cur.child!=cur) {
    		HeapNode child = cur.child;
    		insertForKmin(tmp,child);
    		HeapNode nextChild = child.next;
    		while(nextChild != child && nextChild != null) {
    				insertForKmin(tmp,nextChild);
    				nextChild = nextChild.next;
    		}
    	}
    }
    //insert to tmp with saving a pointer to node in H - O(1)
    private static void insertForKmin(FibonacciHeap tmp,HeapNode node) {
    	HeapNode newNode = tmp.insert(node.key);
    	newNode.node = node;
    }
    
    public static int[] kMin(FibonacciHeap H, int k)
    {    
    	if(H.size == 0 || k==0) {
    		int[] arr = new int[0];
    		return arr;
    	}
    	int i = 0;
        int[] arr = new int[k];
        FibonacciHeap tmp = new FibonacciHeap();
        HeapNode curMin = H.min;
        insertForKmin(tmp,H.min);
        while(i<k) { // O(k)
        	insertChildren(H,tmp,curMin,arr); //O(deg(H))
        	arr[i] = curMin.key;
        	i++;
        	tmp.deleteMin(); //O(logk) 
        	curMin = tmp.min.node;
        }
        return arr; 
    }
    
    
   public int getMarkedNodes() {
		return markedNodes;
	}

	public void setMarkedNodes(int markedNodes) {
		this.markedNodes = markedNodes;
	}

	public int getNumOfTrees() {
		return numOfTrees;
	}

	public void setNumOfTrees(int numOfTrees) {
		this.numOfTrees = numOfTrees;
	}

/**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	
	public int key;
	public int rank;
	public boolean mark;
	public HeapNode child;
	public HeapNode parent;
	public HeapNode next = this;
	public HeapNode prev = this;
	public HeapNode node = null;
	

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public HeapNode getNext() {
		return next;
	}

	public void setNext(HeapNode next) {
		this.next = next;
	}

	public HeapNode getPrev() {
		return prev;
	}

	public void setPrev(HeapNode prev) {
		this.prev = prev;
	}

	public HeapNode getChild() {
		return child;
	}

	public void setChild(HeapNode child) {
		this.child = child;
	}

	public HeapNode getParent() {
		return parent;
	}

	public void setParent(HeapNode parent) {
		this.parent = parent;
	}

  	public HeapNode(int key) {
	    this.key = key;
      }

  	public int getKey() {
	    return this.key;
      }

    }

    public HeapNode getFirst() {
	return this.first;
    }
}
