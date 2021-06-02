import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.text.Document;

class DbOperators implements Serializable {
	DbSystem dbInst;
	DbOperators(DbSystem dbInstance){
		this.dbInst = dbInstance;
	}
	
	void freeAllField() {
		dbInst.root = null;
		dbInst.dbControl = new DbStructure[dbInst.maxFieldNum];
		dbInst.first = null;
		dbInst.last = null;
		dbInst.recordCount =0;
		dbInst.fieldIdx = 0;
		
	}
	
	public void define() {
		freeAllField();
		// set max field
		do {
			try {
				
				System.out.print(String.format("Input field size (1 ~ %s) :", (Integer.toString(dbInst.maxFieldNum))));
				dbInst.fieldIdx = dbInst.sc.nextInt();

			} catch (NumberFormatException | InputMismatchException e) {
				System.out.println("Wrong Input");
				dbInst.sc = new Scanner(System.in);
				dbInst.fieldIdx = -1;
			}
		} while ((dbInst.fieldIdx <= 0 || dbInst.fieldIdx > dbInst.maxFieldNum));
		dbInst.sc.nextLine();
		for (int i = 0; i < dbInst.fieldIdx; i++) {
			DbStructure currentField = new DbStructure(i);
			dbInst.dbControl[i] = currentField;
			// set field name
			do {
				try {
					System.out.print("Input name of field (max is 20): ");
					currentField.name = dbInst.sc.nextLine();
					if (currentField.name.length() == 0)
						System.out.println("Wrong input");
				} catch (Exception e) {
					System.out.println("Wrong input");
					dbInst.sc = new Scanner(System.in);
					currentField.name = null;
				}
			} while (currentField.name == null || currentField.name.length() == 0 || currentField.name.length() > 20);
			
			// set field type
			System.out.println("Type of fields are = (S)tring, (C)har, (D)ouble, (I)nt");
			char tmpType;
			do {
				try {
					System.out.print("Input type of field: ");
					tmpType = dbInst.sc.next().charAt(0);
					dbInst.sc.nextLine();
					tmpType = Character.toLowerCase(tmpType);
					switch (tmpType) {
					case 's':
						currentField.type = Type.STRING;
						break;
					case 'c':
						currentField.type = Type.CHAR;
						break;
					case 'd':
						currentField.type = Type.DOUBLE;
						break;
					case 'i':
						currentField.type = Type.INT;
						break;
					default:
						throw new Exception();
					}
					break;
				} catch (Exception e) {
					dbInst.sc = new Scanner(System.in);
				}
			} while (true);
			// set field size
			do {
				try {
					if(currentField.type == Type.CHAR) {
						currentField.size = 1; 
						break;
					}
					System.out.print(String.format("Input number of %s field's value length (1 ~ %s) :",
							currentField.name, (Integer.toString(dbInst.maxFieldSize))));
					currentField.size = dbInst.sc.nextInt();
					dbInst.sc.nextLine();
				} catch (NumberFormatException | InputMismatchException e) {
					System.out.println("Wrong Input");
					dbInst.sc = new Scanner(System.in);
					currentField.size = -1;
				}
			} while ((currentField.size <= 0 || currentField.size > dbInst.maxFieldSize));
			System.out.println("[field information]");
			displayCurrentField(currentField);
			System.out.println();
		}
		displayField();
		do {
			System.out.println(String.format("select sorted field : 1 ~ %d",dbInst.fieldIdx));
		}while(!dbInst.sc.hasNextInt());
		
		System.out.println("Key is 1");
		dbInst.sortField = 0;
		//dbInst.sortField = dbInst.sc.nextInt()-1;
		//dbInst.sc.nextLine();
//		if(dbInst.sortField+1 > dbInst.fieldIdx || dbInst.sortField < 0) {
//			System.out.println("Wrong field.\nset sort field to 1.");
//			dbInst.sortField = 0;
//		}
		
		
		System.out.println("m_way is 5");
		dbInst.m_way = 5;
		dbInst.bpInst = new BPlusTree(dbInst.m_way,dbInst.sortField);
		
		
	
	}
	
	
	public void defineDebug() {
		freeAllField();
		// set max field
		
		String nameArr[] =  {"key","country","description","designation","points","price","province","region_1","rigion_2","taster_name","taster_twitter_handle","title","variety","winery"};
		dbInst.fieldIdx = nameArr.length;
		dbInst.dbControl = new DbStructure[dbInst.fieldIdx];
		
		for (int i = 0; i < dbInst.fieldIdx; i++) {
			DbStructure currentField = new DbStructure(i);
			dbInst.dbControl[i] = currentField;
			// set field name
			currentField.name = nameArr[i];
			
			// set field type
			currentField.type = Type.STRING;
			//-1 means infinity
			currentField.size = -1;
			
		}
		
		System.out.println("Key is first int value always.(row index)");
		dbInst.sortField = 0;
		System.out.println("m_way is 100");
		dbInst.m_way = 100;
		dbInst.bpInst = new BPlusTree(dbInst.m_way,dbInst.sortField);
		
	}
	
	
	void displayCurrentField(DbStructure currentField) {
		System.out.println(String.format("current field %s's size : %d , type : %s",currentField.name, currentField.size, currentField.type));
	}
	
	int displayField() {
		for (int i = 0; i < dbInst.fieldIdx; i++) {
			System.out.println("Field name of " + String.valueOf(i + 1) + "st = " + (dbInst.dbControl[i].name));
		}
		return 0;
	}
	
	public void printValueArr(Object[] value) {
		for(int i=0;i<value.length;i++) {
			
			System.out.print(value[i]);
			for(int j=0;j<dbInst.dbControl[i].name.length();j++) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}
	
	String[] returnFieldNameArr() {
		String tmpArr[] = new String[dbInst.fieldIdx];
		for(int i=0;i<dbInst.fieldIdx;i++) {
			tmpArr[i] = dbInst.dbControl[i].name;
		}
		return tmpArr;
	}
	// this method return Records as a 2 dimensional array its need for some method
	String[][] returnRecords2Dimension() {
		String recordArr[][] = 
				new String[dbInst.recordCount][dbInst.fieldIdx];
		DLNode recordPointer = dbInst.first;
		for(int i=0;i<dbInst.recordCount;i++) {
			recordArr[i] = returnRecordsArr(recordPointer);
			recordPointer = recordPointer.next;
		}
		return recordArr;
	}
	// same as returnRecrods2Dimension case, same reason
	String[] returnRecordsArr(DLNode curPointer) {
		String recordArr[] = new String[dbInst.fieldIdx];
		for(int i=0;i<dbInst.fieldIdx;i++) {
			recordArr[i] = curPointer.key;
		}
		return recordArr;
	}
	
	boolean isFieldOk(DbSystem dbInst) {
		if(dbInst.fieldIdx == 0) {
			System.out.println("You should define field first");
			return false;
		}
		return true;
	}
	
	boolean isRecordOk(DbSystem dbInst) {
		if(dbInst.recordCount == 0) {
			System.out.println("You should input records first");
			return false;
		}
		return true;
	}
	
	DbStructure returnField(int fieldIdx) {
		DbStructure objectField = null;
		objectField = this.dbInst.dbControl[fieldIdx];
		return objectField;
	}
	
	//type checking
	public boolean isOkValueType(String selectedValue,DbStructure curField) {
		try {
			if(curField.type == Type.DOUBLE) {
				Double.parseDouble(selectedValue);
			}
			if(curField.type == Type.INT) {
				Integer.parseInt(selectedValue);
			}
		}catch(Exception e) {
			System.out.println("Wrong value type");
			return false;
		}
		return true;
	}
	// proper type value return
	public Object returnRightTypeValue(String value,Type type) {
		Object resultValue = null;
		if(type == Type.CHAR) {
			resultValue = value.charAt(0);
		}
		if(type == Type.INT) {
			resultValue = Integer.parseInt(value);
		}
		if(type == Type.DOUBLE) {
			resultValue = Double.parseDouble(value);
		}
		if(type == Type.STRING) {
			resultValue = value;
		}
		return resultValue;
	}
	// proper type value return , this is return arr version
	public Object[] returnRightTypeValue(String[] valueArr,DbStructure[] dbControl) {
		Object resultValue = null;
		Object resultArr[] = new Object[valueArr.length];
		
		for(int i=0;i<valueArr.length;i++) {
			Type type = dbControl[i].type;
			if(type == Type.CHAR) {
				resultValue = valueArr[i].charAt(0);
			}
			if(type == Type.INT) {
				resultValue = Integer.parseInt(valueArr[i]);
			}
			if(type == Type.DOUBLE) {
				resultValue = Double.parseDouble(valueArr[i]);
			}
			if(type == Type.STRING) {
				resultValue = valueArr[i];
			}
			resultArr[i] = resultValue;
		}
		return resultArr;
		
	}
	
	
	void serializableSave(DbSystem dbInst) {
		try {
			String fileName = "";
			fileName = dbInst.sc.nextLine();
			FileOutputStream fileStream= new FileOutputStream(fileName);
			//ByteArrayOutputStream byteArrOutputStream = new ByteArrayOutputStream();
			//ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrOutputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
			objectOutputStream.writeObject(dbInst);
			objectOutputStream.close();
			
		}catch(IOException e) {
		}
		
	}
	
	DbSystem serializableLoad(DbSystem dbInst)  {
		try {
			Scanner fileNameSc = new Scanner(System.in);
			String fileName = fileNameSc.nextLine();
			FileInputStream fileStream= new FileInputStream(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
			
			return (DbSystem) objectInputStream.readObject();
			
		}catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("error occur");
		}
		return dbInst;
	}
	
	//print..
	public void printerPrint(String inputText) throws PrintException, IOException{
		
		
		
		inputText = inputText.replace("\n", "\r\n");
		String defaultPrinter =
		PrintServiceLookup.lookupDefaultPrintService().getName();
		System.out.println("Default printer: " + defaultPrinter);
		
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		
	    InputStream is = new ByteArrayInputStream(inputText.getBytes());
	    PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));

		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(is, flavor, null);
		DocPrintJob job = service.createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		pjw.waitForDone();
		is.close();
	}
	
	
	
	void umjunsick3(BPlusTreeNode curTree) {
		if(curTree == null){
			return;
		}
		
		InternalNode curNode = curTree.firstNode;
		while(curNode!= null) {
			System.out.println(curNode.key);
			curNode =curNode.next;
		}
		curNode = curTree.firstNode;
		while(curNode != null) {
			if(curTree.firstNode.left instanceof BPlusTreeNode) {
				System.out.println("left");
				umjunsick3((BPlusTreeNode) curNode.left);
				System.out.println("right");
				umjunsick3((BPlusTreeNode) curNode.right);
				System.out.println();
			}else {
				if(curTree.firstNode.left instanceof BPlusNode) {
					
					System.out.println("left2");
					umjunsick2((BPlusNode)curNode.left);
					System.out.println("right2");
					umjunsick2((BPlusNode)curNode.right);
					
				}
			}
			curNode = curNode.next;

		}
		
	}
	
	
	
	
	private void umjunsick2(BPlusNode curTree) {
		if(curTree == null){
			return;
		}
		DLNode curNode = curTree.firstNode;
		while(curNode!= null) {
			System.out.println(curNode.key);
			curNode =curNode.next;
		}
		System.out.println();
		
		
	}

	void umjunsick() {
			
			BPlusTreeNode curTree = (BPlusTreeNode) dbInst.bpInst.root;
			if(curTree == null) {
				return;
			}
			InternalNode curNode = curTree.firstNode;
			umjunsick3(curTree);
			System.out.println();
			
	}
	
	
	void umjunsick2() {
		
		BPlusTreeNode curTree = (BPlusTreeNode) dbInst.bpInst.root;
		InternalNode curNode = curTree.firstNode;
		while(curNode != null) {
			System.out.println();
			System.out.println("ROOT");
			System.out.println(curNode.key);
			
			
			BPlusTreeNode leftTree = (BPlusTreeNode)curNode.left;
			InternalNode leftNode = leftTree.firstNode;
			System.out.println();
			
			System.out.println("LEFT");
			while(leftNode != null) {
				System.out.println(leftNode.key);
				leftNode = leftNode.next;
			}
			
			BPlusTreeNode rightTree = (BPlusTreeNode)curNode.right;
			InternalNode rightNode = rightTree.firstNode;
			System.out.println();
			System.out.println("RIGHT");
			while(rightNode != null) {
				System.out.println(rightNode.key);
				rightNode = rightNode.next;
			}
			
			
			
			curNode = curNode.next;
		}
		
		
		
	}
	void umjunsick(int i2) {
		System.out.println("***Print all DL");
		BPlusNode um = dbInst.bpInst.BPFirst;
		DLNode um2 = um.firstNode;
		int i = 1;
		while(um!=null) {
			um2 = um.firstNode;
			System.out.println(1+"st BPlusNode");
			while(um2 != null) {
				System.out.println(um2.key);
				um2 = um2.next;
			}
			um = um.next;
			i++;
		}
		
		System.out.println("Print all DL End***");
		
		System.out.println("\n\n***Print all Tree\n\n");
		notInorder(dbInst.bpInst.root);
		System.out.println("\n\n***END PRINT TREE\n\n");
	}

	private void notInorder(BPlusTreeNode root) {
		InternalNode tmp = root.firstNode;
		BPlusTreeNode tmp2;
		InternalNode tmp2Node;
		try {
			while(tmp!= null) {
				System.out.println("root");
				System.out.println(tmp.key);
				tmp2 = (BPlusTreeNode) tmp.left;
				tmp2Node = tmp2.firstNode;
				System.out.println("left");
				while(tmp2Node!=null) {
					System.out.println(tmp2Node.key);
					tmp2Node = tmp2Node.next;
				}
				System.out.println("right");
				tmp2 = (BPlusTreeNode) tmp.right;
				tmp2Node = tmp2.firstNode;
				while(tmp2Node!=null) {
					System.out.println(tmp2Node.key);
					tmp2Node = tmp2Node.next;
				}
				tmp = tmp.next;
			}
		}catch(Exception e) {
			System.out.println("TREE PRINT END");
		}
		
		
		
		
	}
	
}

class PrintJobWatcher implements Serializable{
	  boolean done = false;

	  PrintJobWatcher(DocPrintJob job) {
	    job.addPrintJobListener(new PrintJobAdapter() {
	      public void printJobCanceled(PrintJobEvent pje) {
	        allDone();
	      }
	      public void printJobCompleted(PrintJobEvent pje) {
	        allDone();
	      }
	      public void printJobFailed(PrintJobEvent pje) {
	        allDone();
	      }
	      public void printJobNoMoreEvents(PrintJobEvent pje) {
	        allDone();
	      }
	      void allDone() {
	        synchronized (PrintJobWatcher.this) {
	          done = true;
	          System.out.println("Printing done ...");
	          PrintJobWatcher.this.notify();
	        }
	      }
	    });
	  }
	  public synchronized void waitForDone() {
	    try {
	      while (!done) {
	        wait();
	      }
	    } catch (InterruptedException e) {
	    }
	  }
	}