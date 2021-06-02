import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

//DoubleLinkedListNode
class DLNode implements Serializable{
	
	String key;
	DLNode next=null;
	DLNode prev=null;
	int pos;
	DLNode(String key,int pos){
		this.key = key;
		this.pos = pos;
	}
}

class DoubleLinkedList implements Serializable {
	
	
	public DLNode returnNewNode(String key,int pos) {
		DLNode newNode = new DLNode(key,pos);
		return newNode;
	}
	
	
	void insertIntoFront(DLNode curNode,DLNode newNode, BPlusNode curBPNode) {
		
		if(curNode.prev == null) { 
			curBPNode.firstNode = newNode;
		}else {
			curNode.prev.next = newNode;
			newNode.prev = curNode.prev;
		}
		curNode.prev = newNode;
		newNode.next = curNode;
	}
	
	
	void readObjectSet() throws FileNotFoundException {
		int c;
		StringBuilder sb = new StringBuilder("");
		FileInputStream tmp = new FileInputStream("record.dat");
		BufferedInputStream bis = new BufferedInputStream(tmp);
		try {
			while((c = bis.read()) != -1)
			{
				sb.append(c);
			}
			
			ByteArrayInputStream bais2 = new ByteArrayInputStream(sb.toString().getBytes());
			ObjectInputStream oisRecord2 = new ObjectInputStream(bais2);
			
			String[] tmp2;
			try {
				tmp2 = (String[]) oisRecord2.readObject();
				for(int i=0;i<tmp2.length;i++) {
					System.out.println(tmp2[i]);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	int writeRecord(String[] record,FileChannel fcr){
		byte[] se=null;
		int posRecord=0;
		
		try {
			se = serialize(record);
			posRecord = (int)fcr.position();
			fcr.write(ByteBuffer.wrap(se));
			fcr.position(fcr.size());
			
			return posRecord;
			
		
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	
	void insertIntoKeyFile(String key, String[] record,BPlusTree bpInst, FileChannel fcr) throws FileNotFoundException{
		int pos = writeRecord(record,fcr);
		input(key,pos,bpInst);
		
	}
		
	
	
	void input(String key,int pos,BPlusTree bpInst) {
		bpInst.DLInst = this;
		DLNode newNode = returnNewNode(key,pos);
		bpInst.insert(newNode);
		
	}
	
		
		
		
		

	 	

	byte[] serialize(String[] record)  throws IOException {
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     ObjectOutputStream oos = new ObjectOutputStream(baos);
	     oos.writeObject(record);
	     oos.flush();
	     oos.close();
		 return baos.toByteArray();
	 }
	


	void insertIntoBack(DLNode curNode,DLNode newNode,BPlusNode curBPNode) {
		
		if(curNode.next == null) { 
			curBPNode.lastNode = newNode;
		}
		curNode.next = newNode;
		newNode.prev = curNode;
	}
	
	
	//@Override
	//standard is sort field, 
	//using double LinkedList.
	//insert and sorting at the same time
//	public DLNode insert(String key,DbSystem dbInst) {
//		DLNode newNode = new DLNode();
//		Object newValue = "";
//		String curValue = "";
//		newNode.key = key;
//		if(dbInst.first == null) {
//			dbInst.first = newNode;
//			dbInst.last = newNode;
//		}else {
//			DLNode curNode = dbInst.first;
//			DLNode prevNode = curNode;
//			newValue = key;
//			do {
//				curValue = curNode.key;
//				 if(Integer.parseInt(curValue) - Integer.parseInt(newValue.toString()) > 0){
//					 if(curNode == dbInst.first) {
//						 dbInst.first = newNode;
//					 }else {
//						 prevNode.next= newNode;
//						 newNode.prev = prevNode;
//					 }
//					 newNode.next = curNode;
//					 curNode.prev = newNode;
//					 break;
//				 }
//				 prevNode = curNode;
//				 curNode = curNode.next;
//			}while(curNode != null);
//			
//			dbInst.last = newNode;
//			prevNode.next = newNode;
//			newNode.prev = prevNode;
//			
//		}
//		return newNode;
//	}
	
		void insert(BPlusTreeNode curBPTreeNode,InternalNode newNode) {
			InternalNode curNode = curBPTreeNode.firstNode;
			String newValue= newNode.key;
			String curValue = curNode.key;
			if(Integer.parseInt(curValue) - Integer.parseInt(newValue) > 0) {
				curNode.prev = newNode;
				newNode.next = curNode;
				curBPTreeNode.firstNode =newNode;
				curNode.left = newNode.right;
			}else {
				do {
					curValue = curNode.key;
					if(Integer.parseInt(curValue) - Integer.parseInt(newValue) > 0) {
						newNode.prev = curNode.prev;
						newNode.next = curNode;
						curNode.prev.next = newNode;
						curNode.prev = newNode;
						curNode.left = newNode.right;
						break;
					}
						
					if(curNode.next == null) {
						
						curBPTreeNode.lastNode = newNode;
						curNode.next = newNode;
						newNode.prev = curNode;
						curNode.right = newNode.left;
						break;
					}
					curNode = curNode.next;
				}while(curNode != null);
				
			}
			curBPTreeNode.nodeCount++;
		}
		
		
	//첫占쏙옙째占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙치占싹댐옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙
	public void deleteOnlyFirstMatched(int fieldIdx, DbSystem dbInst) {
		System.out.println("Enter delete value");
		String deleteValue = dbInst.sc.nextLine();
		DLNode curNode = dbInst.first;
		String curValue;
		DLNode prevNode = curNode;
		do {
			curValue = curNode.key;
			if(deleteValue.equals(curValue)) {
				if(curNode == dbInst.first) {
					dbInst.first = curNode.next;
				}else if(curNode == dbInst.last) {
					dbInst.last = curNode.prev;
				}
				if(curNode.prev != null) {
					curNode.prev.next = curNode.next;	
				}
				if(curNode.next != null) {
					curNode.next.prev = curNode.prev;
				}
				dbInst.recordCount--;
				System.out.println("Delete complete");
				break;
			}
			prevNode = curNode;
			curNode = curNode.next;
		}while(curNode != null);
		
	}
	
	//parameter ..idx meaning selected records
	//delete selected records
	//@Override
	public void delete(int selectFieldIdx, int selectRecordIdx, DbSystem dbInst) {
		DLNode curNode = dbInst.first;
		DLNode prevNode = curNode;
		for(int i=0;i<selectRecordIdx;i++) {
			prevNode = curNode;
			curNode = curNode.next;
		}
		
		if(curNode == dbInst.first) {
			dbInst.first = curNode.next;
		}
		if(curNode == dbInst.last) {
			dbInst.last = curNode.prev;
		}
		if(curNode.prev != null) {
			curNode.prev.next = curNode.next;	
		}
		if(curNode.next != null) {
			curNode.next.prev = curNode.prev;
		}
		dbInst.recordCount--;
		System.out.println("Delete complete");
		
	}


//	public void showEveryData() {
//		try {
//				String[] tmp2;
//				System.out.println();
//				while(true) {
//					try {
//						tmp2 = (String[]) recordOOI.readObject();
//						for(int i=0;i<tmp2.length;i++) {
//							System.out.print(tmp2[i]+ " ");
//						}
//						System.out.println();
//						recordOOI = new ObjectInputStream(this.recordBIS);
//					}catch(EOFException e2) {
//						
//						System.out.println();
//						break;
//					}
//				}
//						
//		} catch (IOException | ClassNotFoundException e) {
//			e.printStackTrace();
//		} 
//	}


	public void printSpecificPos(int key, ObjectInputStream recordOOI,ByteArrayInputStream recordBIS) {
		
		try {
			
			String[] tmp = null;
			try {
				
				do {
					
					tmp = (String[]) recordOOI.readObject();
					recordOOI = new ObjectInputStream(recordBIS);
					if(Integer.parseInt(tmp[0]) == key) {
						for(int i=0;i<tmp.length;i++) {
							System.out.print(tmp[i]+" ");
						}
						System.out.println();
						return;
					}
					
				}while(Integer.parseInt(tmp[0]) != key);
				
			}catch(EOFException e) {
				for(int i=0;i<tmp.length;i++) {
					System.out.print(tmp[i]+" ");
				}
				System.out.println();
				System.out.println("EOF");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("종료");
			
		} catch (ClassNotFoundException e) {
			
		}
		
		//String line = ois.read
		
		
	}


	public void saveDataFile(String filePath,BPlusTree bpInst) {
		
		
		RandomAccessFile file;
		File inputFile = new File(filePath);
		FileOutputStream newFos;
		
		
		
		try {
			File recordFile = new File("record.dat");
			byte[] fileContent = Files.readAllBytes(recordFile.toPath());
			ByteArrayInputStream recordBIS = new ByteArrayInputStream(fileContent);
			ObjectInputStream recordOOI = new ObjectInputStream(recordBIS);
			
			
			newFos = new FileOutputStream(inputFile);
			ObjectOutputStream newOos = new ObjectOutputStream(newFos);
			BPlusNode BNode = bpInst.BPFirst;
			
			if(BNode == null) {
				System.out.println("레코드가 입력되어있지 않습니다");
				return;
			}
			
			while(BNode != null) {
				DLNode tmpNode = BNode.firstNode;
				while(tmpNode!= null) {
					String[] tmp = null;
						try {
							tmp = (String[]) recordOOI.readObject();
							newOos.writeObject(tmp);
							recordOOI = new ObjectInputStream(recordBIS);
							
						}catch(EOFException e2) {
							System.out.println("종료");
							recordBIS.close();
							recordOOI.close();
							newFos.close();
							newOos.close();
							return;
							
						}
						catch(ClassNotFoundException | IOException e) {

						}
						
						tmpNode = tmpNode.next;
					}
					
					BNode = BNode.next;
					
				}
				
		}catch (IOException e1) {
			System.out.println("L46");
		}

	}

	//레코드로부터 입력받으면 안됨 same file 2 way
	public void loadFromFile(String filePath,BPlusTree bpInst) {
		FileChannel fcr = null;
		FileInputStream fis2= null;
		BufferedInputStream bis2 = null;
		ObjectInputStream ois2 = null;
		String [] tmpRecord = null;
		try {
			FileWriter file2 = new FileWriter("record.dat");
			file2.close();
			
			fcr = new RandomAccessFile("record.dat","rw").getChannel();
			fcr.force(true);
			
			
			
			fis2 = new FileInputStream(filePath);
			bis2 = new BufferedInputStream(fis2);
			ois2 = new ObjectInputStream(bis2);	
			
			int count = 0;
			while(true) {
				tmpRecord = (String[])ois2.readObject();
				if(Integer.parseInt(tmpRecord[0]) < count){
					break;
				}
				count++;
							
				for(int i=0;i<tmpRecord.length;i++) {
					System.out.print(tmpRecord[i]+ " ");
				}
				System.out.println();
				insertIntoKeyFile(tmpRecord[0], tmpRecord, bpInst,fcr);
			}
			
			
		}catch(EOFException e) {
			try {
				
				fis2.close();
				bis2.close();
				ois2.close();
				fcr.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.out.println("EOF");
			return;
		}catch(IOException | ClassNotFoundException e) {
			
		}
		
		
		
	}


}
