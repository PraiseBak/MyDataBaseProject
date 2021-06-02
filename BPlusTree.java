import java.io.Serializable;
import java.util.Scanner;



/*
 * 
 * 처음엔, BPlusNode에 트리와 key를 다 넣을려고 하였다.
 * 하지만 메모리를 우선지향하여
 * 일일이 구분 짓었다.
 * 
 */

//

//each internal node
class InternalNode implements Serializable{
	String key = null;
	InternalNode next = null;
	InternalNode prev = null;
	Object left = null;
	Object right = null;
	InternalNode(String key,Object left,Object right){
		this.key = key;
		this.left = left;
		this.right = right;
	}
}

//internal node control object
class BPlusTreeNode implements Serializable{
	InternalNode firstNode = null;
	InternalNode lastNode = null;
	int nodeCount = 0;
	BPlusTreeNode(InternalNode first){
		firstNode = first;
		nodeCount++; 
	}
}

//leaf node control object
class BPlusNode implements Serializable{
	DLNode firstNode = null;
	DLNode lastNode = null;
	BPlusNode next = null;
	BPlusNode prev = null;
	int nodeCount = 0;

	BPlusNode(DLNode newNode){
		firstNode = newNode;
		lastNode = newNode;
		nodeCount++;
	}
	BPlusNode(){
		
	}
}


public class BPlusTree implements BPlusTreeInterface,Serializable {
	BPlusNode BPFirst = null;
	BPlusTreeNode root = null;
	BPlusNode BPLast = null;
	DoubleLinkedList DLInst = null;
	int mWay = 0;
	int sortField = 0;
	int count = 0;
	
	BPlusTree(int mWay,int sortField){
		this.mWay = mWay; 
		this.sortField = sortField;
		
	}
	
	public BPlusTree(DoubleLinkedList DLInst) {
		this.DLInst = DLInst;
		
	}

	
	
	//objectkey에 접근할 수 있는 적절한 노드 리턴
	BPlusNode returnCorrectNodePosition(String objectKey) {
		BPlusTreeNode tmpTreeNode = root;
		int rightKey;
		int objectIntKey = Integer.parseInt(objectKey);
		if(root == null) {
			return BPFirst;
		}
		while(!(tmpTreeNode.firstNode.left instanceof BPlusNode)) {
			InternalNode RightInternal = returnRightNode(tmpTreeNode, objectKey);
			rightKey = Integer.parseInt(RightInternal.key);
			//System.out.println("right값 ="+RightInternal.key);
			if(rightKey - objectIntKey > 0) {
				tmpTreeNode = (BPlusTreeNode)RightInternal.left;
				//System.out.println("왼");
			}else if(rightKey - objectIntKey < 0){
				tmpTreeNode = (BPlusTreeNode)RightInternal.right;
				//System.out.println("오");
			}
			
		}
		InternalNode RightInternal = returnRightNode(tmpTreeNode, objectKey);
		rightKey = Integer.parseInt(RightInternal.key);
		//System.out.println(rightKey);
		//System.out.println(objectIntKey);
		
		if(rightKey - objectIntKey > 0) {
			//System.out.println(RightInternal.key);
			//System.out.println(((BPlusNode)RightInternal.left).firstNode.key());
			//System.out.println(((BPlusNode)RightInternal.right).firstNode.key());
			return (BPlusNode)RightInternal.left;
		}else {
			return (BPlusNode)RightInternal.right;
		}
		
	}
		
	
	public void insert(DLNode newNode) {
			
			if(BPFirst == null) {
				BPFirst = new BPlusNode(newNode);
				BPLast = BPFirst;
			}else {
				BPlusNode curBPlusNode = BPFirst;
				String curNodeValue = null;
				String newNodeValue = newNode.key;
				System.out.print("목표값");
				System.out.println("="+newNodeValue);
				curBPlusNode = returnCorrectNodePosition(newNodeValue);
				
				do {
					DLNode curNode = curBPlusNode.firstNode;
					do { 	
						curNodeValue = curNode.key.toString();
						
						if(Integer.parseInt(newNodeValue) - Integer.parseInt(curNodeValue) < 0  || curNode.next == null) {
						//새 노드가 현재 노드보다 값이 작음
							if(Integer.parseInt(newNodeValue) - Integer.parseInt(curNodeValue) <0) {
								//System.out.println("insertIntoFront");
								DLInst.insertIntoFront(curNode,newNode,curBPlusNode);
								curBPlusNode.nodeCount++;
								if(curBPlusNode.nodeCount == mWay) {
									System.out.println("splitInto two rec");
									splitIntoTwoRecord(curBPlusNode,sortField,root);
									
								}
								return;
							//마지막 BPlusNode (즉 마지막 노드) 즉슨 제일 큰 값	
							}else if(curBPlusNode.next == null){
								//System.out.println("insertIntoBack");
								DLInst.insertIntoBack(curNode, newNode,curBPlusNode);
								curBPlusNode.nodeCount++;
								if(curBPlusNode.nodeCount == mWay) {
									System.out.println("splitInto two rec");
									splitIntoTwoRecord(curBPlusNode,sortField,root);
								}
								return;
							//curBPlusNode에서는 가장 크지만 curBPlusNode.next에서는 가장 작다면 curBPlusNode에서 연결하는게 맞다.
							}else if(curNode.next == null) {
								String tmpKey = curBPlusNode.next.firstNode.key;
								if(Integer.parseInt(newNodeValue) - Integer.parseInt(tmpKey) < 0) {
									//System.out.println("insertIntoBack2");
									DLInst.insertIntoBack(curNode, newNode, curBPlusNode);
									curBPlusNode.nodeCount++;
									if(curBPlusNode.nodeCount == mWay) {
										System.out.println("splitInto two rec");
										splitIntoTwoRecord(curBPlusNode,sortField,root);
										
									} 
									return;
								}
							}
							
						}
						
						curNode = curNode.next;
						
					}while(curNode != null);
					//System.out.println("NEXT!");
					curBPlusNode = curBPlusNode.next;
				}while(curBPlusNode != null);
				
			}
			
		}
		
	
	//Record 
	void splitIntoTwoRecord(BPlusNode curBPlusNode,int sortField,BPlusTreeNode root) {
		DLNode middleNode = returnMiddleNode(curBPlusNode.firstNode);
		String key = middleNode.key.toString();
		//삭제
		DLNode remainNode = middleNode.prev; 
		//레코드 2개로 분할 cur과 right
		
		remainNode.next = null;
		middleNode.prev = null;
		BPlusNode rightBPlusNode = new BPlusNode(middleNode);
		
		rightBPlusNode.lastNode = curBPlusNode.lastNode;
		curBPlusNode.lastNode = remainNode;
		
		
		rightBPlusNode.next = curBPlusNode.next;
		int countMovedNode = returnMovedNodeCount(rightBPlusNode.firstNode);
		curBPlusNode.nodeCount-= countMovedNode;
		rightBPlusNode.nodeCount+= countMovedNode-1;
		curBPlusNode.next = rightBPlusNode;
		rightBPlusNode.prev = curBPlusNode;
		//여기까지 레코드 분할
		
		//TODO presentation
		InternalNode newInternalNode = new InternalNode(key,curBPlusNode,rightBPlusNode);
		
		if(root == null) {
			this.root = new BPlusTreeNode(newInternalNode);
		}else {
			//루트에서부터 연결만 하면 됨
			//System.out.println("split한 것 루트에서 연결 할 값 ="+key);
			BPlusTreeNode parent = returnParentNode(key);
			//parent.next에 newInternalNode 연결함.
			connectFromRoot(parent,newInternalNode);
		}
	}
	
	//InternalNode version
	private InternalNode splitIntoTwoInternal(BPlusTreeNode fromRootNode,String objectKey) {
		
		//curBPlusTree은 현재 BPlusTreeNode의 objectKey와 동일한 인덱스의 값 (비교가능한 동일선상)
		
		//일단 root에서부터 내려간다음 찾으면 값 추가할거임
		//값 추가하고나서 parent에서 mway 터진다? 바로 리컬전
		InternalNode RightInternal = returnRightNode(fromRootNode,objectKey);
		//System.out.println("현재값");
		//System.out.println(RightInternal.key);
		//System.out.println("목표값:"+objectKey);
		//현재값이 목표값보다 크다 -> 왼쪽으로
		InternalNode returnNode = null;
		int rightIntKey = Integer.parseInt(RightInternal.key);
		int objectIntKey = Integer.parseInt(objectKey);
		
		if(fromRootNode == root && fromRootNode.nodeCount == mWay) {
			//System.out.println("루트에서 스플릿발생");
			root = new BPlusTreeNode(split(RightInternal,fromRootNode));
			return null;
		}
		
		
		if(rightIntKey - objectIntKey > 0) {
			//System.out.println("왼");
			returnNode = splitIntoTwoInternal((BPlusTreeNode)RightInternal.left,objectKey);
			//System.out.println("왼에서받음");
			//현재값이 목표값보다 작다 -> 오른쪽으로
		}else if(rightIntKey - objectIntKey < 0){
			//System.out.println("오");
			returnNode = splitIntoTwoInternal((BPlusTreeNode)RightInternal.right,objectKey);
			//System.out.println("오에서받음");
			//현재값이 목표값과 같다-> 분할해야함
		}else {
			//이 RightInternal 는 middle이다
			//return 새롭게 스플릿되는 중간 노드
			//System.out.println("같");
			
			if(fromRootNode == root) {
				System.out.println("루트에서 스플릿발생");
				root = new BPlusTreeNode(split(RightInternal,fromRootNode));
				return null;
			}
			return split(RightInternal,fromRootNode);
		}
		
		if(returnNode != null) {
			DLInst.insert(fromRootNode, returnNode);
		}
		
		
		if(fromRootNode.nodeCount == mWay) {
			InternalNode middle = returnMiddleNode(fromRootNode.firstNode);
			if(fromRootNode == root) {
				root = new BPlusTreeNode(split(RightInternal,fromRootNode));
				return null;
			}else {
				return split(middle,fromRootNode);
			}
		}
		
		
		return returnNode;
			
		
		
	}
	
	private InternalNode split(InternalNode middleNode,BPlusTreeNode curBPlusTree) {
		//System.out.println("SPLIT. middleNode 값 ="+middleNode.key+"curBPLusTree첫번값 ="+curBPlusTree.firstNode.key);
		InternalNode leftNode = middleNode.prev;
		InternalNode rightNode = middleNode.next;
		curBPlusTree.lastNode = leftNode;
				
		middleNode.prev = null;
		middleNode.next = null;
		
		leftNode.next = null;
		rightNode.prev = null;
		
		BPlusTreeNode rightTreeNode = new BPlusTreeNode(rightNode);
		middleNode = new InternalNode(middleNode.key,curBPlusTree,rightTreeNode);
		
		rightTreeNode.nodeCount= this.returnMovedNodeCount(rightNode);
		curBPlusTree.nodeCount-= rightTreeNode.nodeCount+1;
		return middleNode;
	}
	
	

	

	
	
	//record에서 split된 경우 루트에서 연결함
	private void connectFromRoot(BPlusTreeNode parent, InternalNode newNode) {
		DLInst.insert(parent, newNode);
		/*
		 * Internal 재분배
		 */
		if(parent.nodeCount == mWay) {
			
			InternalNode middle = returnMiddleNode(parent.firstNode);
			String key = middle.key;
			//System.out.println("TreeNode 분할할 값 = "+key);
			splitIntoTwoInternal(root,key);
			
		}else {
			//System.out.print(parent.nodeCount);
			//System.out.println("이라서 분할 안할거임ㅋ");
		}
				
	}
	
	
	
	//Record Ver
	private DLNode returnMiddleNode(DLNode first) {
		DLNode curNode = first;
		for(int i=0;i<mWay/2;i++) {
			curNode = curNode.next;
		}
		return curNode;
		                                                
	}
	
	
	
	//키값을 비교하여 적절한 트리를 리턴
	InternalNode returnRightNode(BPlusTreeNode root,String middleKey) {
		InternalNode curInternalNode = root.firstNode;
		int middleKeyInt = Integer.parseInt(middleKey);
		while(true) {
			String curValue = curInternalNode.key;
			int curValueInt = Integer.parseInt(curValue);
			
			
			if(curValueInt - middleKeyInt >=  0 || curInternalNode.next == null) {
				//middle key보다 크거나 같다
				return curInternalNode;
			}
			curInternalNode = curInternalNode.next;
			
		}
		
	}
	
	
	//삭제버전
		InternalNode returnRightNodeDelete(BPlusTreeNode root,String deleteKey) {
			InternalNode curInternalNode = root.firstNode;
			int middleKeyInt = 0;
			try {
				middleKeyInt = Integer.parseInt(deleteKey);
			}catch(NumberFormatException e) {
				System.out.println("잘못된 삭제데이터 입력입니다");
				return null;
			}
			
			while(true) {
				String curValue = curInternalNode.key;
				int curValueInt = Integer.parseInt(curValue);
				
				
				if(curValueInt - middleKeyInt >  0 || curInternalNode.next == null) {
					//middle key보다 크거나 같다
					
					return curInternalNode;
				}
				curInternalNode = curInternalNode.next;
				
			}
			
		}
	
	
	
	//키값을 비교하여 적절한 BPlusTreeNode노드 리턴
	//returnCorrectNodePositon와 함께 사용함
	
	DLNode returnRightNode(BPlusNode curBPlusNode,String objectString)
	{
		DLNode curNode = curBPlusNode.firstNode;
		
		int curKey = 0;
		int objectKey = Integer.parseInt(objectString);
		while(curBPlusNode != null) {
			while(curNode!=null) {
				
				//키는 항상 0번째임 데이터에서 pk로서 기준 키를 사용할 수 있다고 생각하기 때문에 우선 0으로 둠
				curKey = Integer.parseInt(curNode.key);
				if(curKey == objectKey) {
					return curNode;
				}
				curNode = curNode.next;
				
			}
			curBPlusNode = curBPlusNode.next;
				
		}
		return curNode;
	}
	
	//returnRightNode와 동일하다. Count --를 한다는 점만 제외하고.
	private DLNode returnDeleteNode(BPlusNode curBPlusNode,String objectString) {
		DLNode curNode = curBPlusNode.firstNode;
			
		int curKey = 0;
		int objectKey = Integer.parseInt(objectString);
		//System.out.println("목표값="+objectKey+"찾기.");
		while(curBPlusNode != null) {
			while(curNode!=null) {
				curKey = Integer.parseInt(curNode.key);
				//키는 항상 0번째임 데이터에서 pk로서 기준 키를 사용할 수 있다고 생각하기 때문에 우선 0으로 둠
				if(curKey == objectKey) {
					return curNode;
				}
				curNode = curNode.next;
					
			}
			curBPlusNode = curBPlusNode.next;
					
		}
		//System.out.println("같은키없는레후");
		return curNode;
	}
	
	//삭제할 위치의 리프 노드를 주면 적절히 삭제 하는 메소드
	void deleteCorrectly(DLNode deleteNode,BPlusNode curBPlus) {
		//첫번째값일때
		//System.out.println("삭제값 ="+deleteNode.value[0]);
		if(deleteNode.prev == null) {
			//System.out.println("1번케이스 삭제");
			curBPlus.firstNode = deleteNode.next;
			if(deleteNode.next != null)
				deleteNode.next.prev = null;
			else
				curBPlus.lastNode = deleteNode.next;
			deleteNode.next = null;
		}
		//끝 값일때
		else if(deleteNode.next == null) {
			//System.out.println("12번케이스 삭제");
			curBPlus.lastNode = deleteNode.prev;
			if(deleteNode.prev!= null) 
				deleteNode.prev.next = null;
			else
				curBPlus.firstNode = deleteNode.prev;
			deleteNode.prev = null;
			
			
		//중간값일때
		}else {
			//System.out.println("123번케이스 삭제");
			deleteNode.prev.next = deleteNode.next;
			deleteNode.next.prev = deleteNode.prev;
			deleteNode.next = null;
			deleteNode.prev = null;
			
		}
		
		curBPlus.nodeCount--;
	}
	

	//삭제할 위치의 리프 노드를 주면 적절히 삭제 하는 메소드
	void deleteCorrectly(InternalNode deleteNode,BPlusTreeNode curBPlus) {
		//System.out.println("인터널노드에서 삭제할값 = "+deleteNode.key);
		//첫번째값일때
		if(deleteNode.prev == null) {
			//System.out.println("1번케이스삭제");
			curBPlus.firstNode = deleteNode.next;
			if(deleteNode.next != null)
				deleteNode.next.prev = null;
			else
				curBPlus.lastNode = deleteNode.next;
			deleteNode.next = null;
		}
		//끝 값일때
		else if(deleteNode.next == null) {
			//System.out.println("2번케이스삭제");
			curBPlus.lastNode = deleteNode.prev;
			if(deleteNode.prev!= null) 
				deleteNode.prev.next = null;
			deleteNode.prev = null;
			
			
		//중간값일때
		}else {
			//System.out.println("3번케이스삭제");
			deleteNode.prev.next = deleteNode.next;
			deleteNode.next.prev = deleteNode.prev;
			deleteNode.next = null;
			deleteNode.prev = null;
			
		}
		
		curBPlus.nodeCount--;
	}
	
	
	void deleteDummy(InternalNode deleteNode,BPlusTreeNode curBPlus) {
		
		//첫번째값일때
		if(deleteNode.prev == null) {
			//System.out.println("삭제노드 첫");
			curBPlus.firstNode = deleteNode.next;
			if(deleteNode.next != null)
				deleteNode.next.prev = null;
			else
				curBPlus.lastNode = deleteNode.next;
			deleteNode.next = null;
		}
		//끝 값일때
		else if(deleteNode.next == null) {
			//System.out.println("삭제노드 끝");
			curBPlus.lastNode = deleteNode.prev;
			if(deleteNode.prev!= null) 
				deleteNode.prev.next = null;
			else
				curBPlus.firstNode = null;
			deleteNode.prev = null;
			
			
		//중간값일때
		}else {
			//System.out.println("중간값이");
			deleteNode.prev.next = deleteNode.next;
			deleteNode.next.prev = deleteNode.prev;
			deleteNode.next = null;
			deleteNode.prev = null;
			
		}
		
	}
	
	
	//curBPlusNode overflow 발생한 노드
	//이것은 레코드에서 발생한 split에서만 사용함
	private BPlusTreeNode returnParentNode(String middleKey) {
		BPlusTreeNode curTreeNode = root;
		BPlusTreeNode prevTreeNode = curTreeNode;
		InternalNode curInternalNode = null;
		
		String curKey;
		while(true) {
			curInternalNode = returnRightNode(curTreeNode,middleKey);
			curKey = curInternalNode.key;
			prevTreeNode = curTreeNode;
			if(curTreeNode.firstNode.left instanceof BPlusNode) {
				//System.out.println("리턴한부모값");
				//System.out.println(prevTreeNode.firstNode.key);
				return prevTreeNode;
			}else {
				if(Integer.parseInt(curKey) - Integer.parseInt(middleKey) > 0) {
					curTreeNode = (BPlusTreeNode)curInternalNode.left;
				}else {
					curTreeNode= (BPlusTreeNode)curInternalNode.right;
				}
			}
		}
			
		
	}
		

	//Internal Version//
	private int returnMovedNodeCount(InternalNode curNode) {
		int count = 0;
		//curNode == middleNode.
		//middleNode count too
		while(curNode!= null) {
			curNode = curNode.next;
			count++;
		}
		return count;
	}
	
	private int returnMovedNodeCount(DLNode curNode) {
		int count = 0;
		//curNode == middleNode.
		//middleNode count too
		while(curNode!= null) {
			curNode = curNode.next;
			count++;
		}
		return count;
	}
	
	
	private InternalNode returnMiddleNode(InternalNode first) {
		InternalNode curNode = first;
		for(int i=0;i<mWay/2;i++) {
			curNode = curNode.next;
		}
		
		return curNode;
	}
	


	@Override
	public void delete() {
		
	}

	@Override
	public void modify() {
		
	}

	@Override
	public void search() {
		
	}

	@Override
	public void insert() {
		
	}

	public void delete(String deleteKey) {
		
		if(root == null) {
			//리프노드에서 삭제
			//System.out.println("리프노드에서 삭제합니다.");
			deleteFromLeafNode(BPFirst,deleteKey);
		}else {
			//root에서부터..
			//System.out.println("루트에서부터 삭제연산 시작");
			deleteFromRootRecursively(root,deleteKey);
		}
		//System.out.println("삭제완료");
		
	}
	
	void deleteFromLeafNode(BPlusNode first,String deleteKey) {
		int deleteIntKey = Integer.parseInt(deleteKey);
		int curKey;
		BPlusNode curBPlusNode = first;
		
		while(curBPlusNode != null) {
			
			DLNode curNode = curBPlusNode.firstNode;
			while(curNode != null) {
				curKey = Integer.parseInt(curNode.key);
				if(curKey == deleteIntKey) {
					deleteCorrectly(curNode,curBPlusNode);
				}
				curNode = curNode.next;
			}
			curBPlusNode = curBPlusNode.next;
		
		}
	}
	
		
	//curNode는 underflow일어난 노드의 부모노드입니다.
	private void checkAndMerge(InternalNode curNode,BPlusTreeNode underflowNode,String leftKey,BPlusTreeNode curBPlusTree) {
		if(canMergeFromLeftThenMerge(curNode,underflowNode,leftKey,curBPlusTree)) {
			
			
		}else if(canMergeFromRightThenMerge(curNode,underflowNode,leftKey,curBPlusTree)) {
			//System.out.println("오른쪽과 멀지함");
		}else {
			//System.out.println("L843cantMerge");
		}
		
		
	}

	
	private boolean canMergeFromLeftThenMerge(InternalNode curNode,BPlusTreeNode underflowNode,String leftKey,BPlusTreeNode curBPlusTree) {
		if(curNode.right == underflowNode) {
			//System.out.println(curNode.key+"의 오른쪽에서 underflow 발생해서 왼쪽과 병합");
			if(curNode.prev != null) {
				curNode.prev.next = null;
			}
			curNode.prev = null;
			mergeToLeft((BPlusTreeNode)curNode.left,curNode,underflowNode,leftKey);
			curBPlusTree.lastNode = curNode.prev;
			curBPlusTree.nodeCount--;
			return true;
		}
		
		if(curNode.left == underflowNode) {
			if(curNode.prev != null) {
				//System.out.println(curNode.key+"의 왼쪽이 underflow 발생해서 왼쪽과병합");
				mergeToLeft((BPlusTreeNode)curNode.prev.left,curNode,underflowNode,leftKey);
				curBPlusTree.firstNode = curNode;
				curNode.prev = null;
				curBPlusTree.nodeCount--;
				return true;
			}
		}
		
		return false;
	}
	
	private boolean canMergeFromRightThenMerge(InternalNode curNode,BPlusTreeNode underflowNode,String leftKey,BPlusTreeNode curBPlusTree) {
		
		
		
		if(curNode.left == underflowNode) {
			//System.out.println("오른쪽과 멀지함");
			curBPlusTree.firstNode = curBPlusTree.firstNode.next;
			curBPlusTree.firstNode.prev = null;
			mergeToRight((BPlusTreeNode)curNode.right,curNode,underflowNode,leftKey);
			curBPlusTree.nodeCount--;
			return true;
		}else {
			
		}
		
		
		return false;
	}
	
	void mergeToLeft(BPlusTreeNode objectMergeNode,InternalNode rootNode,BPlusTreeNode underflowNode,String leftKey){
		
		rootNode.left = objectMergeNode;
		
		InternalNode rootInternal = rootNode.prev;
		if(rootNode.prev == null) {
			rootInternal =rootNode;
		}
		InternalNode objectInternal = objectMergeNode.lastNode;
		InternalNode underflowInternal = underflowNode.firstNode;
		
		//System.out.println("\nunderflow 값 = "+underflowInternal.key);
		//System.out.println("objectMergede 값 =  "+objectInternal.key);
		//System.out.println(rootNode.key+"가 루트노드 키");
		//System.out.println("남은값="+leftKey);
		
		rootInternal.right = underflowInternal.left;
		rootInternal.left = objectInternal.right;
		//System.out.println(objectInternal.key+"와"+rootInternal.key+"와"+underflowInternal.key+"연결");
		
		
		objectInternal.next = rootInternal;
		rootInternal.next = underflowInternal;
		rootInternal.prev = objectInternal;
		underflowInternal.prev = rootInternal;
		objectMergeNode.lastNode = underflowNode.lastNode;
		objectMergeNode.nodeCount+=underflowNode.nodeCount+1;
		underflowNode.nodeCount = 0;
		//System.out.println("크기 ="+objectMergeNode.nodeCount);
		umjun(objectMergeNode);
	}
	
	void mergeToRight(BPlusTreeNode objectMergeNode,InternalNode rootNode,BPlusTreeNode underflowNode,String leftKey){
		
		rootNode.left = objectMergeNode;
		
		InternalNode rootInternal = rootNode;
		InternalNode objectInternal = objectMergeNode.firstNode;
		InternalNode underflowInternal = underflowNode.lastNode;
		
		//System.out.println("\nunderflow 값 = "+underflowInternal.key);
		//System.out.println("objectMergede 값 =  "+objectInternal.key);
		//System.out.println(rootNode.key+"가 루트노드 키");
		//System.out.println("남은값="+leftKey);
		
		//System.out.println(rootInternal.key+"의 왼쪽을"+((BPlusNode)underflowInternal.right).firstNode.value[0]);
		rootInternal.left = underflowInternal.right;
		//System.out.println(rootInternal.key+"의 오른을"+((BPlusNode)objectInternal.left).firstNode.value[0]);
		rootInternal.right = objectInternal.left;
	
		//System.out.println(objectInternal.key+"와"+rootInternal.key+"와"+underflowInternal.key+"연결");
		
		
		objectInternal.prev = rootInternal;
		rootInternal.prev = underflowInternal;
		rootInternal.next = objectInternal;
		underflowInternal.next = rootInternal;
		
		objectMergeNode.firstNode = underflowNode.firstNode;
		objectMergeNode.nodeCount+=underflowNode.nodeCount+1;
		underflowNode.nodeCount = 0;
		//System.out.println("크기 ="+objectMergeNode.nodeCount);
		umjun(objectMergeNode);
	}
	
	
	
	private boolean isRightInternalBorrowable(BPlusTreeNode rightTreeNode) {
		if(rightTreeNode.nodeCount > mWay/2) {
			return true;
		}
			
		
		return false;
	}
	

	private boolean isLeftInternalBorrowable(InternalNode leftNode) {
		//왼쪽노드없음
		if(leftNode == null) {
			//System.out.println("L859 없어.");
			return false;
		}
		
		BPlusTreeNode leftTreeNode = (BPlusTreeNode) leftNode.left;
		if(leftTreeNode.nodeCount > mWay/2) {
			//System.out.println("가능.");
			return true;
		}
		//System.out.println("없어.");
		return false;
	}
	
	private boolean isLeftInternalBorrowable(BPlusTreeNode leftTreeNode) {
		if(leftTreeNode.nodeCount > mWay/2) {
			return true;
		}
		//System.out.println(leftTreeNode.nodeCount+"이므로 false");
		return false;
	}

	private void checkAndMerge(BPlusNode underflowNode,BPlusTreeNode parentTreeNode,InternalNode parentNode,String deleteKey) {
    	
		if(canMergeFromLeft(underflowNode,parentNode,deleteKey)){
    		mergeWithLeft(underflowNode,parentTreeNode,parentNode);
    		
    	}else if(canMergeFromRight(underflowNode,parentNode,deleteKey)){
    		mergeWithRight(underflowNode,parentTreeNode,parentNode);
    	}
	}
	
	private void mergeWithRight(BPlusNode underflowNode,BPlusTreeNode curTreeNode,InternalNode curNode) {
		//underflowNode를 왼쪽에 붙인다.
		BPlusNode mergedNode = underflowNode.next;
		DLNode mergeFirst = mergedNode.firstNode;
		DLNode underflowLast = underflowNode.lastNode;
				
		mergeFirst.prev = underflowLast;
		underflowLast.next = mergeFirst;
				
		mergedNode.prev = underflowNode.prev;
		if(underflowNode.prev != null) {
			underflowNode.prev.next = mergedNode;
		}
				
		mergedNode.nodeCount += underflowNode.nodeCount;
		mergedNode.firstNode = underflowNode.firstNode;
		underflowNode.nodeCount = 0;
		if(curNode.next != null) {
			curNode.next.left = mergedNode;
			this.deleteCorrectly(curNode, curTreeNode);
		}else {
			//이케이스 없을걸
			root = null;
			this.deleteCorrectly(curNode, curTreeNode);
		}
}
	
	void umjun(BPlusTreeNode um) {
		InternalNode um2 = um.firstNode;
		while(um2 !=null) {
			//System.out.println(um2.key);
			um2 = um2.next;
		}
	}
	
	private void mergeWithLeft(BPlusNode underflowNode,BPlusTreeNode curTreeNode,InternalNode curNode){
		//underflowNode를 왼쪽에 붙인다.
		BPlusNode mergedNode = underflowNode.prev;
		DLNode mergeLast = mergedNode.lastNode;
		DLNode underflowFirst = underflowNode.firstNode;
		
		mergeLast.next = underflowFirst;
		underflowFirst.prev = mergeLast;
		
		mergedNode.next = underflowNode.next;
		if(underflowNode.next != null) {
			underflowNode.next.prev = mergedNode;
		}
		
		mergedNode.nodeCount += underflowNode.nodeCount;
		mergedNode.lastNode = underflowNode.lastNode;
		underflowNode.nodeCount = 0;
		
		//삭제하는거 확인점
		if(curNode.next != null) {
			//System.out.println("이거.");
			curNode.left = mergedNode;
			
			this.deleteCorrectly(curNode.prev, curTreeNode);
		}else {
			if(curNode.right == underflowNode) {
				curNode.right = null;
			}else {
			}
			//System.out.println("저거.");
			//System.out.println("현재키="+curNode.key+"이전키="+curNode.prev.key);
			curNode.left = mergedNode;
			//System.out.println("이전키삭제");
			this.deleteCorrectly(curNode.prev, curTreeNode);
		}
		
	}
	
	


	//BPlusNode버전
	private boolean borrowIfCan(BPlusNode underflowNode,BPlusTreeNode parentTreeNode,InternalNode parentNode,String deleteKey) {
		String leftValue = "";
		//삭제되고 남은 값이 삭제된 루트값의 대체됨
		//왼쪽 우선 확인
		if(canBorrowFromLeft(underflowNode,parentNode,deleteKey)){
			//System.out.println("왼쪽 리프노드 빌릴수있어서 왼쪽 빌림");
			BorrowFromLeft(underflowNode,parentNode);
		//오른쪽 확인
		}else if(canBorrowFromRight(underflowNode,parentNode,deleteKey)){
			//System.out.println("오른쪽 리프노드 빌릴수있어서 오른쪽 빌림");
			leftValue = BorrowFromRight(underflowNode,parentNode);
			exchangeFromInternalNode(parentTreeNode, parentNode.key, leftValue);
		}else {
			return false;
		}
		
		//삭제된 노드나 뺏긴노드가 parentNode에 있으면 key 교체해줘야함
		return true;
		
	}


	private void BorrowFromLeft(BPlusNode underflowNode,InternalNode parentNode) {
		
		BPlusNode borrowedBNode = underflowNode.prev;
		DLNode borrowedNode = borrowedBNode.lastNode;
		DLNode underflowFirst = underflowNode.firstNode;
		
		borrowedBNode.lastNode = borrowedNode.prev;
		
		underflowFirst.prev = borrowedNode;
		borrowedNode.prev.next = null;
	
		borrowedNode.next = underflowFirst;
		borrowedNode.prev = null;
		
		underflowNode.firstNode = borrowedNode;
		
		borrowedBNode.nodeCount--;
		underflowNode.nodeCount++;
		
		
	}

	private String BorrowFromRight(BPlusNode underflowNode, InternalNode parentNode) {
		BPlusNode borrowedBNode = underflowNode.next;
		DLNode borrowedNode = borrowedBNode.firstNode;
		DLNode underflowLast = underflowNode.lastNode;
		
		borrowedBNode.firstNode = borrowedNode.next;
		
		underflowLast.next = borrowedNode;
		borrowedNode.next.prev = null;
	
		borrowedNode.prev = underflowLast;
		borrowedNode.next = null;
		
		underflowNode.lastNode = borrowedNode;
		
		borrowedBNode.nodeCount--;
		underflowNode.nodeCount++;
		
		return borrowedBNode.firstNode.key;
		
	}
	
	//왼쪽에서 빌려올수있는지 보고싶다.
	//왼쪽의 노드가 있는지
	//왼쪽의 노드가 같은 parentNode를 가지는지
	//왼쪽의 노드가 Mway 조건을 만족하는지
	
	private boolean canBorrowFromLeft(BPlusNode curBPlusNode, InternalNode parentNode ,String deleteKey) {
		int parentNodeKey = Integer.parseInt(parentNode.key);
		int deleteIntKey = Integer.parseInt(deleteKey);
		
		//왼쪽의 노드가 없다면 false 리턴
		if(curBPlusNode.prev == null)
			return false;
		
		//왼쪽의 노드가 같은 parentNode를 가지는지 봄
		//parentNode의 left에 curBPlusNode가 있다
		if(parentNodeKey >= deleteIntKey) {
			//parentNode의 prev가 있다면 무조건 왼쪽노드가 같은 Tree에 있다. 리프노드는 정렬되니까.
			if(parentNode.prev == null) {
				return false;
			}
		}
		//mWayCheck
		if(curBPlusNode.prev.nodeCount <= mWay/2) {
			return false;
		}
		return true;
	}
	
	
	private boolean canBorrowFromRight(BPlusNode curBPlusNode, InternalNode parentNode,String deleteKey) {
		int parentNodeKey = Integer.parseInt(parentNode.key);
		int deleteIntKey = Integer.parseInt(deleteKey);
		
		//오른쪽의 노드가 없다면 false 리턴
		if(curBPlusNode.next == null)
			return false;
		
		if(parentNodeKey < deleteIntKey) {
			//만약 deleteIntKey가 큰값이라면 parentNode의 next는 없으므로
			return false;
		}
		//mWayCheck
		if(curBPlusNode.next.nodeCount <= mWay/2) {
			return false;
		}
		return true;
		
	}
	
	
	private boolean canMergeFromLeft(BPlusNode curBPlusNode, InternalNode parentNode ,String deleteKey) {
		int parentNodeKey = Integer.parseInt(parentNode.key);
		int deleteIntKey = Integer.parseInt(deleteKey);
		
		//왼쪽의 노드가 없다면 false 리턴
		//System.out.print("왼쪽 노드자체가없는가??");
		if(curBPlusNode.prev == null)
			return false;
		
		//System.out.println("NO");
		//왼쪽의 노드가 같은 parentNode를 가지는지 봄
		//parentNode의 left에 curBPlusNode가 있다
		
		if(parentNodeKey >= deleteIntKey) {
			//parentNode의 prev가 있다면 무조건 왼쪽노드가 같은 Tree에 있다. 리프노드는 정렬되니까.
			if(parentNode.prev == null) {
				return false;
			}
		}
		return true;
	}
	
	
	private boolean canMergeFromRight(BPlusNode curBPlusNode, InternalNode parentNode,String deleteKey) {
		int parentNodeKey = Integer.parseInt(parentNode.key);
		int deleteIntKey = Integer.parseInt(deleteKey);
		
		//오른쪽의 노드가 없다면 false 리턴
		if(curBPlusNode.next == null)
			return false;
		
		if(parentNodeKey < deleteIntKey) {
			//만약 deleteIntKey가 큰값이라면 parentNode의 next는 없으므로
			return false;
		}
		return true;
		
	}
	

	private boolean isChildInternalUnderflow(BPlusTreeNode treeNode) {
		//System.out.println("TreeNode 노드카운트 ="+treeNode.nodeCount);
		if(treeNode.nodeCount < mWay/2) {
			return true;
		}
		
		return false;
	}


	//키를 굳이 삭제안하고 바꿔치면됨.
	private boolean exchangeFromInternalNode(BPlusTreeNode curTreeNode, String deleteKey,String exchangeKey) {
		InternalNode curNode = curTreeNode.firstNode;
		String curKey = "";
		//System.out.print("교체시도:");
		//System.out.print(deleteKey);
		//System.out.println("->"+exchangeKey);
		while(curNode != null) {
			curKey = curNode.key;
			if(curKey.equals(deleteKey)) {
				//System.out.print("parentNode에서 ");
				//System.out.println(curNode.key+"를"+exchangeKey+"로 변경.");
				curNode.key = exchangeKey;
				
				return true;
			}
			curNode = curNode.next;
		}
		//System.out.println("L1285:exchangeKey 존재 X");
		return false;
	}

	
	//boolean methods
	
	
	
	private boolean isUnderFlowOccur(BPlusNode curBPlusNode) {
		//삭제하고나서니까 +1해주자.
		if(curBPlusNode.nodeCount+1 <= mWay/2) {
			return true;
		}
		return false;
	}

	private boolean isCurNodeIsClosestTreeNode(InternalNode curNode) {
		if(curNode.left instanceof BPlusNode) {
			return true;
		}
		return false;
	}

	boolean isObjectIsLeftChild(int curNodeKey,int objectKey) {
		if(curNodeKey > objectKey) {
			return true;
		}
		return false;
	}

	boolean isTreeNodeBorrowAble(BPlusTreeNode treeNode){
		if(treeNode.nodeCount<=mWay/2) {
			return false;
		}
		return true;
		
	}
	
	boolean isSameParentTree(BPlusNode objectBPlusNode,BPlusNode rootChild) {
		//System.out.println("L988 오브젝트 B+ 노드의 값 = "+objectBPlusNode.firstNode.value[0]+"루트차일드는 ="+rootChild.firstNode.value[0]);
		if(objectBPlusNode == rootChild) {
			return true;
		}
		return false;
		
	}
	
	
	private void BorrowInternalNodeFromRight(BPlusTreeNode underflowedLeftChild, InternalNode rootNode, BPlusTreeNode siblingRightChild,String deleteKey) {
		//System.out.print("루트 ="+ rootNode.key);
		InternalNode borrowedInternal = siblingRightChild.firstNode;
		//System.out.println("오른쪽에서 빌려올 키 " + borrowedInternal.key);
		siblingRightChild.firstNode = borrowedInternal.next;
		siblingRightChild.nodeCount--;
		borrowedInternal.next.prev = null;
		borrowedInternal.next = null;
		//현재 오른쪽 형제에서 첫번 노드를 분리해서 惠塚 상태 (borrowedInternal)
		
		//분리한 노드를 underflow일어난 트리에 넣어주고
		InternalNode connectNode = underflowedLeftChild.lastNode;
		
		borrowedInternal.right = borrowedInternal.left;
		borrowedInternal.left = connectNode.right;
		borrowedInternal.prev = connectNode.next; 
		connectNode.next = borrowedInternal;
		underflowedLeftChild.lastNode = borrowedInternal;
		
		//System.out.println("루트기준으로 왼쪽에 위치한 언더플로우 발생한 노드의 남은 키 = "+connectNode.key);
		
		
		underflowedLeftChild.nodeCount++;
		
		
		String tmpRootKey = rootNode.key;
		rootNode.key = borrowedInternal.key;
		borrowedInternal.key = tmpRootKey;
		//System.out.println(tmpRootKey+"와"+rootNode.key+"바꿈");
		
		
	}
	
	

	private void BorrowInternalNodeFromLeft(BPlusTreeNode underflowedLeftChild, InternalNode rootNode, BPlusTreeNode siblingLeftChild,String deleteKey) {
		
		
		InternalNode borrowedInternal = siblingLeftChild.lastNode;
	
		siblingLeftChild.lastNode = borrowedInternal.prev;
		siblingLeftChild.nodeCount--;
		borrowedInternal.prev.next = null;
		borrowedInternal.prev = null;
		//현재 오른쪽 형제에서 첫번 노드를 분리해서 惠塚 상태 (borrowedInternal)
		
		//분리한 노드를 underflow일어난 트리에 넣어주고
		InternalNode connectNode = underflowedLeftChild.firstNode;
		borrowedInternal.left = borrowedInternal.right;
		borrowedInternal.right = connectNode.left;
		
		//System.out.println("버로우 왼쪽");
		//System.out.println("버로우 오른쪽");
		
		
		System.out.print("루트 ="+ rootNode.key);
		System.out.println("왼쪽에서 빌려올 키 " + borrowedInternal.key);
		System.out.println("연결키="+connectNode.key);
		
		underflowedLeftChild.firstNode = borrowedInternal;
		borrowedInternal.next = connectNode;
		connectNode.prev = borrowedInternal;
		
		
		//child 연결해줘야함
		
		
		
		InternalNode tmp = underflowedLeftChild.firstNode;
		while(tmp!=null) {
			//System.out.println(tmp.key);
			tmp = tmp.next;
		}
		
		underflowedLeftChild.nodeCount++;
		String tmpRootKey ="";
		//TODO 삭제 이거 잘 해야함
		if(rootNode.next == null) {
			
			tmpRootKey = rootNode.key;
			rootNode.key = borrowedInternal.key;
			borrowedInternal.key = tmpRootKey;
			System.out.println(tmpRootKey+"와"+rootNode.key+"바꿈2222222");
			if(underflowedLeftChild.firstNode.right instanceof BPlusNode){
				connectNode.key = ((BPlusNode)underflowedLeftChild.firstNode.right).firstNode.key;	
			}
			
			if(connectNode.right instanceof BPlusNode) {
				if((BPlusNode)connectNode.right== null) {
					
					borrowedInternal.right= ((BPlusNode)borrowedInternal.left).next;
					connectNode.right = connectNode.left;
					connectNode.left = borrowedInternal.right;
					System.out.println("근데 또 "+((BPlusNode)underflowedLeftChild.firstNode.right).firstNode.key+"로 커넥트노드키 바꿈");
					connectNode.key = ((BPlusNode)connectNode.right).firstNode.key;
				}
					
			}
			
			
			
			
				
		}else if(rootNode.prev != null) {
			tmpRootKey = rootNode.prev.key;
			rootNode.prev.key = borrowedInternal.key;
			borrowedInternal.key = tmpRootKey;
			//System.out.println(tmpRootKey+"와"+rootNode.prev.key+"바꿈");
		}
		
	}
	
	
	private void checkBNodeAllNode(BPlusNode BNode) {
		if(BNode == null) {
			//System.out.println("null");
			return;
		}
		DLNode um = BNode.firstNode;
		while(um!=null) {
			//System.out.println(um.value[0]);
			um= um.next;
		}
		
	}

	void connectDifferentBPlusTree(InternalNode connectNode,Object treeToSetLeft,Object treeToSetRight){
		connectNode.left = treeToSetLeft;
		connectNode.right = treeToSetRight;
	}



//root가 있을 때 삭제의 3가지 요소
//1.borrow - 리프노드에서 일어나는 값 빌려오기
//2.exchange - internalNode에서 일어나는 키 값 변경
//3.merge - 모든 노드에서 발생가능함
//TODO1
	private String deleteFromRootRecursively(BPlusTreeNode curTreeNode,String deleteKey) {
		InternalNode curNode = null;
		int curNodeKey = 0;
		int objectKey = 0;
		BPlusNode curBPlusNode = null;
		String leftKey = "";
		//비교하여 적절한 위치의 노드 리턴
		curNode = returnRightNodeDelete(curTreeNode,deleteKey);
		if(curNode == null) {
			System.out.println("잘못된 입력입니다");
			return null;
		}
		curNodeKey = Integer.parseInt(curNode.key);
		objectKey = Integer.parseInt(deleteKey);
		//리프노드에서의 연산
		
		if(isCurNodeIsClosestTreeNode(curNode)) {
			//System.out.println("리프연산");
			System.out.println("현재키 ="+curNode.key);
			//왼쪽이나 오른쪽으로 한칸이동();
			if(isObjectIsLeftChild(curNodeKey,objectKey)) {
				//System.out.println("현재 키 TreeNode의 왼쪽리프노드로감");
				curBPlusNode = (BPlusNode)curNode.left;
			}else {
				//System.out.println("현재 키 TreeNode의 오른쪽리프노드로감");
				curBPlusNode = (BPlusNode)curNode.right;
			}
			if(curBPlusNode == null) {
				return null;
			}
			DLNode deleteNode = returnDeleteNode(curBPlusNode,deleteKey);
			if(deleteNode == null) {
				return null;
			}
			deleteCorrectly(deleteNode,curBPlusNode);
			if(exchangeFromInternalNode(curTreeNode,deleteKey,curBPlusNode.firstNode.key)) {
				deleteKey = curBPlusNode.firstNode.key;
			}
			
			if(isUnderFlowOccur(curBPlusNode)) {
				
				//빌리기
				boolean didIBorrow = false;
				didIBorrow = borrowIfCan(curBPlusNode,curTreeNode,curNode,deleteKey);
				
				if(exchangeFromInternalNode(curTreeNode,deleteKey,curBPlusNode.firstNode.key)) {
					deleteKey = curBPlusNode.firstNode.key;
				}
				if(!didIBorrow) {
					
					checkAndMerge(curBPlusNode,curTreeNode,curNode,deleteKey);
					//exchangeFromInternalNode(curTreeNode,deleteKey,leftKey);
					//leftKey가 루트에 있는건 삭제를 해야하는 거에요
					
				}
				
			}
			
			System.out.println("리프연산 끝");
			return deleteKey;
			
			
		//InternalNode에서의 연산
		}else {
			//System.out.println("Internal 연산");
			//System.out.println("현재 키 = "+curNode.key);
			//오른쪽,왼쪽키에서 남아있는 키
			
			//목표가 왼쪽에 있는가
			if(isObjectIsLeftChild(curNodeKey,objectKey)) {
				leftKey = deleteFromRootRecursively((BPlusTreeNode)curNode.left,deleteKey);
				if(leftKey == null) {
					return null;
				}
				//System.out.print("레프트키 =");
				//System.out.println(leftKey);
				//System.out.println("\n\n\n\n");
				//왼쪽으로 child internalNode에서 언더 플로우 발생함?
				//curNode.left로 간거는 확정이다 왼쪽값은 무조건 curNode.prev.right에 존재한다
				//System.out.println("L734현재키="+curNode.key);
				if(isChildInternalUnderflow((BPlusTreeNode)(curNode.left))) {
					//System.out.println("왼쪽 언덜플로우 발생");
					
					if(isLeftInternalBorrowable(curNode.prev)){
						//System.out.println("왼쪽에서 빌려올래");
						BorrowInternalNodeFromLeft((BPlusTreeNode)curNode.left, curNode, (BPlusTreeNode)curNode.prev.left,leftKey);
				
					}else if(isRightInternalBorrowable((BPlusTreeNode)curNode.right)) {	
						//System.out.println("오른쪽에서 빌려올래");
						BorrowInternalNodeFromRight((BPlusTreeNode)curNode.left, curNode, (BPlusTreeNode)curNode.right,leftKey);
					}else {
						//TODO merge만 하고 디버깅해보고
						//System.out.println("병합");
						checkAndMerge(curNode,(BPlusTreeNode)curNode.left,leftKey,curTreeNode);
					}
					
				}
				
			}else{
				leftKey = deleteFromRootRecursively((BPlusTreeNode)curNode.right,deleteKey);
				if(leftKey == null) {
					return null;
				}
				//System.out.println("레프트키 받음");
				//System.out.println(leftKey);
				//System.out.println("\n\n\n\n");
				//System.out.println("L783현재키="+curNode.key);
				if(isChildInternalUnderflow((BPlusTreeNode)(curNode.right))) {
					//System.out.println("오른쪽 언덜플로우 발생");
					//System.out.println("오른쪽은 항상 큰값이므로 왼쪽에서만 보면된다. 오른쪽에는 다른 트리이므로 고려하지 않아.");
					
					//InternalNode um2 = ((BPlusTreeNode)curNode.left).firstNode;
					
					//System.out.println();
					if(isLeftInternalBorrowable((BPlusTreeNode)curNode.left)){
						BorrowInternalNodeFromLeft((BPlusTreeNode)curNode.right, curNode, (BPlusTreeNode)curNode.left,leftKey);
						
					}else {
						checkAndMerge(curNode,(BPlusTreeNode)curNode.right,leftKey,curTreeNode);
					}
					
				}
			}
			
			return leftKey;
			
			
		}
		
	}

	public boolean search(String key) {
		
		try {
			
			if(root == null) {
				//단지 리프노드만있어서 리프노드 순회하면서 찾기
				BPlusNode first = BPFirst;
				if(first == null) {
					System.out.println("데이터 입력되지 않음");
					return false;
				}
				while(first!=null) {
					DLNode tmpNode = first.firstNode;
					while(tmpNode!=null) {
						if(tmpNode.key.equals(key)) {
							return true;
						}
						tmpNode = tmpNode.next;
					}
					first = first.next;
				}
			}
			BPlusTreeNode rootTmp = root;
			int searchKeyInt = Integer.parseInt(key); 
			int curKeyInt = Integer.parseInt(rootTmp.firstNode.key);
			InternalNode nodeTmp = rootTmp.firstNode;
			while(rootTmp.firstNode.left instanceof BPlusTreeNode) {
				
				nodeTmp = rootTmp.firstNode;
			
				while(nodeTmp !=null) {
					curKeyInt = Integer.parseInt(nodeTmp.key);
					if(nodeTmp.next == null) {
						if(curKeyInt > searchKeyInt) {
							rootTmp = (BPlusTreeNode)nodeTmp.left;
							break;
						}else {
							rootTmp = (BPlusTreeNode)nodeTmp.right;
							break;
						}
					}else {
						if(curKeyInt > searchKeyInt ) {
							rootTmp = (BPlusTreeNode)nodeTmp.left;
							break;
							
						}else {
							nodeTmp = nodeTmp.next;
						}
						
					}
					if(nodeTmp == null) {
					}
					
					
					
				}
			}
			
			nodeTmp = rootTmp.firstNode;
			BPlusNode BPNode = null;
			while(nodeTmp != null) {
				curKeyInt = Integer.parseInt(nodeTmp.key);
				if(nodeTmp.next == null) {
					if(curKeyInt > searchKeyInt) {
						BPNode = (BPlusNode)nodeTmp.left;
						break;
					}else {
						BPNode = (BPlusNode)nodeTmp.right;
						break;
					}
				}else {
					if(curKeyInt > searchKeyInt ) {
						BPNode = (BPlusNode)nodeTmp.left;
						break;
						
					}else {

						nodeTmp = nodeTmp.next;
					}
					
				}
				
				
			}
			
			DLNode BRecordNode = BPNode.firstNode;
			while(BRecordNode != null) {
				curKeyInt = Integer.parseInt(BRecordNode.key);
				if(curKeyInt == searchKeyInt) {
					return true;
				}
				BRecordNode = BRecordNode.next;
			}
			
			return false;
		}catch(NullPointerException e) {
		}
			return false;
		}
		
}