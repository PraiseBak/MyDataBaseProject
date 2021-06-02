import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.print.PrintException;

public class DbSystem implements Serializable {
	private static final long serialVersionUID = 1L;
	transient Scanner sc = new Scanner(System.in);
	int m_way = 0; 
	int recordCount = 0;
	//max field size
	int maxFieldNum = 10;
	//max data size
	int maxFieldSize = 20;
	//current set field size
	int fieldIdx = 0;
	int sortField = 0;
	//gui에서 선택된 레코드의 위치 의미
	int selectFieldIdx = 0;
	int selectRecordIdx = 0;
	//필드를 각각 요소로 가지는 배열
	DbStructure dbControl[] = new DbStructure[maxFieldNum];
	//DoubleLinkedList에 접근하기 위한 인스턴스
	DoubleLinkedList dlInst = new DoubleLinkedList();
	BPlusTree bpInst = new BPlusTree(dlInst);
	//DbOper에 접근하기 위한 인스턴스
	DbOperators dbOper = new DbOperators(this);
	BPlusTree root = null;
	DLNode first = null;
	DLNode last = null;
	
	public static void main(String args[]) throws InterruptedException, NumberFormatException, IOException {
		DbSystem dbInst = new DbSystem();
		while(true) {
			dbInst = dbInst.dbStart();
			System.out.println(dbInst.fieldIdx);
			dbInst.sc = new Scanner(System.in);
		}
	}

	
	
	//@Override
	   public int selectMenu() {
	      System.out.println("\n0. quit");
	      //System.out.println("1. debug");
	      //System.out.println("2. Enter Information");
	      System.out.println("3. Browse");
	      System.out.println("4. Search");
	      //System.out.println("5. Modify");
	      System.out.println("6. Delete");
	      System.out.println("7. InputFromFile");
	      System.out.println("8. Save Database");
	      System.out.println("9. Load Database (현재 데이터베이스 초기화됨)");
	      System.out.println("10.define");
	      
	      
	      int choice = 0;
	      do {
	         System.out.print("select a menu:");
	         try {
	            choice = Integer.parseInt(sc.nextLine());
	         } catch (NumberFormatException e) {
	            choice = -1;
	            sc = new Scanner(System.in);
	         }
	         System.out.println();
	         if (!(choice < 0 || choice > 12)) {
	         //if (!(choice < 0 || choice > 9)) {
	            return choice;
	         }
	         System.out.println("wrong input");
	      //  } while (choice < 0 || choice > 9);
	      } while (choice < 0 || choice > 12);
	      return choice;
	   }
	   
	
	//@Override
	public void define() {
		//goto DbOperators.define
	}

	//@Override
	public DbSystem dbStart() throws InterruptedException, NumberFormatException, IOException {
		// f Auto-generated method stub
		int choice = 0;
		
		while (true) {
			choice = selectMenu();
			switch (choice) {
			case 0:
				System.out.print("Program Exit.\nBye");
				System.exit(0);
				
			case 1:
				break;
				
			case 2:
				inputCase();
				break;
			case 3:
				browse();
				break;
			case 4:
				search();
				break;
			case 5:
				break;
			case 6:
				delete();
				break;
			case 7:
				inputFromFile();
				break;
			case 8:
				save();
				break;
			case 9:
				
				return load();
			
			case 10:
				dbOper.defineDebug();
				break;
				
			}
			
				
		}
		
	}



	private void saveRecord(int key) throws IOException {
		BPlusNode BNode = bpInst.BPFirst;
		
		
		File file = new File("record.dat");
		byte[] fileContent = Files.readAllBytes(file.toPath());
		ByteArrayInputStream recordBIS = new ByteArrayInputStream(fileContent);
		ObjectInputStream recordOOI = new ObjectInputStream(recordBIS);
		
		FileOutputStream fos = new FileOutputStream("tmp.dat");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		int count=0;
		BPlusNode first = bpInst.BPFirst;
		if(first == null) {
			System.out.println("데이터 입력되지 않음");
			recordBIS.close();
			recordOOI.close();
			fos.close();
			bos.close();
			oos.close();
			
			return;
		}
		System.out.println("삭제된 값뺀 데이터가 tmp.dat에 저장됩니다.\n 기존의 데이터인 record.dat는 유지됩니다.\n직렬화 값에서 삭제할 값="+key);
		DLNode node = first.firstNode;
		while(first!= null) {
			node = first.firstNode;
			while(node!=null) {
				node= node.next;
				count++;
			}
			first = first.next;
		}
		//System.out.println("카운트="+count);
		try {
			for(int k = 0;k<=count;k++) {
				String[] tmp = (String[]) recordOOI.readObject();
				
				if(Integer.parseInt(tmp[0]) != key) {
					oos.writeObject(tmp);
					oos.flush();
				}else {
						System.out.println("다른 데이터"+key);
						for(int j=0;j<tmp.length;j++) {
							System.out.print(tmp[j]+ " ");
						}
						System.out.println();
				}
				recordOOI = new ObjectInputStream(recordBIS);
			}
			}catch(EOFException e) {
				System.out.println("종료");
				try {
					recordBIS.close();
					recordOOI.close();
					fos.close();
					bos.close();
					oos.close();
				} catch (IOException e1) {
					
				}
				return;
				
			} catch (ClassNotFoundException e) {
			}
		fos.close();
		bos.close();
		oos.close();	
		recordBIS.close();
		recordOOI.close();
		
	}



	private void inputCase() {
		this.dlInst.input("1", 0, bpInst);
		this.dlInst.input("2", 0, bpInst);
		this.dlInst.input("3", 0, bpInst);
		this.dlInst.input("11", 0, bpInst);
		this.dlInst.input("12", 0, bpInst);
		this.dlInst.input("13", 0, bpInst);
		this.dlInst.input("21", 0, bpInst);
		this.dlInst.input("22", 0, bpInst);
		this.dlInst.input("23", 0, bpInst);
		this.dlInst.input("31", 0, bpInst);
		this.dlInst.input("32", 0, bpInst);
		this.dlInst.input("33", 0, bpInst);
		this.dlInst.input("41", 0, bpInst);
		this.dlInst.input("42", 0, bpInst);
		this.dlInst.input("43", 0, bpInst);
		this.dlInst.input("51", 0, bpInst);
		this.dlInst.input("52", 0, bpInst);
		this.dlInst.input("53", 0, bpInst);
		this.dlInst.input("61", 0, bpInst);
		
	}
	



	private void inputFromFile() {
		if(this.fieldIdx == 0) {
			System.out.println("먼저 define을 해주십시오");
			return;
		}
		//초기화 용도입니다
		try {
			FileWriter file2 = new FileWriter("record.dat");
			file2.close();
		} catch (IOException e1) {
			System.out.println("???");
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
		}
		
		int limit=0;
		String fileName = "WineData2.csv";
		try {
			FileChannel fcr = new RandomAccessFile("record.dat","rw").getChannel();
			fcr.force(true);
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			String line = "";
			String[] tmp = null;
			String[] data = new String[fieldIdx];
			String description = "";
			while((line = br.readLine()) != null) {
				//if(limit == 10000) {
					//break;
				//}
				//limit++;
				data = new String[fieldIdx];
				tmp = line.split("\t");
				description = "";
				for(int i=2; i<tmp.length-11;i++) {
					description += tmp[i];
				}
				data[2] = description;
				
				for(int i=0;i<2;i++) {
					data[i] = tmp[i];
				}
				int j=3;
				try {
					for(int i=tmp.length-11;i<tmp.length;i++) {
						
						if(tmp[i].equals("")) {
							tmp[i] = ".";
						}
						
						data[j++] = tmp[i];
					}
				}catch(Exception e) {
					continue;
				}
				dlInst.insertIntoKeyFile(data[0],data,bpInst,fcr);
				recordCount++;
				
				
			}
			System.out.println("fcr close 성공");
			fr.close();
			br.close();
			fcr.close();
			
			
			
		} catch (FileNotFoundException e) {
			System.out.println("파일을 불러오는데 실패하였습니다.");
		} catch (IOException e) {
			System.out.println("IO 문제가 발생하였습니다.");
		}
		
		
	}

	//@Override
	public void search() {
		System.out.println("찾을 키 값을 입력하세요");
		String key = sc.nextLine();
		
		boolean find =false;
		find = bpInst.search(key);
		if(find) {
			System.out.println(key+"를 찾았습니다.");
		}else {
			System.out.println(key+"가 없습니다.");
		}
	}
	
	
	

	
	
		
	//@Override
	public void browse() {
		BPlusNode BNode = bpInst.BPFirst;
		int pos = 0;
		
		if(BNode == null) {
			System.out.println("데이터 입력되지 않음");
			return;
		}
		
		File file = new File("record.dat");
		byte[] fileContent;
		ByteArrayInputStream recordBIS;
		ObjectInputStream recordOOI = null;
		try {
			fileContent = Files.readAllBytes(file.toPath());
			recordBIS = new ByteArrayInputStream(fileContent);
			recordOOI = new ObjectInputStream(recordBIS);
			
			
			while(BNode != null) {
				DLNode tmpNode = BNode.firstNode;
				while(tmpNode!= null) {
					int key = Integer.parseInt(tmpNode.key);
					dlInst.printSpecificPos(key,recordOOI,recordBIS);
					tmpNode = tmpNode.next;
					
				}
				System.out.println();
				BNode = BNode.next;
			}
			
			
			recordBIS.close();
			recordOOI.close();
		} catch (IOException e) {
			System.out.println("L368");
			
		}
		
		
		
		
		
		
	}		


	
	//@Override
	public void delete() throws NumberFormatException, IOException {
		
		String check = "y";
		while(check.equals("y")) {
			System.err.print("Enter delete Key:");
			String deleteKey = sc.nextLine();
			if(deleteKey == null || deleteKey.equals("")) {
				System.out.println("delete 종료");
				return;
			}
			bpInst.delete(deleteKey);
			System.out.println("Enter Y to deleteAgain");
			check= sc.nextLine();
			saveRecord(Integer.parseInt(deleteKey));
		}
		

	
	}

	
	
		
	//@Override
	public void save() {
		String fileName = this.sc.nextLine();
		dlInst.saveDataFile(fileName,bpInst);
		return;
		
	}



	//@Override
	public DbSystem load() {
		String filePath = sc.nextLine();
		dbOper.defineDebug();
		dlInst.loadFromFile(filePath,bpInst);
		
		return this;
	}
	
	
	
}	







//수정할 기능
//public void printerValues() {
//	DLNode p = new DLNode();
//	p = first;
//	System.out.println("Print...");
//	String fieldNameArr [] = dbOper.returnFieldNameArr();
//	String inputText = "";
//	for(int i=0;i<fieldNameArr.length;i++) {
//		System.out.print(fieldNameArr[i] +" ");
//		inputText = inputText + fieldNameArr[i] + " ";
//	}
//	inputText = inputText.trim();
//	System.out.println();
//	while(p!=null) {
//		inputText += "\n";
//		for(int i=0;i<p.value.length;i++) {
//			inputText += p.value[i].toString();
//			for(int j=0;j<dbControl[i].name.length();j++) {
//				inputText += " ";
//			}
//		}
//		inputText = inputText.trim();
//		dbOper.printValueArr(p.value);
//		p=p.next;
//	}
//	try {
//		dbOper.printerPrint(inputText);
//	} catch (PrintException | IOException e) {
//		System.out.println("printer error");
//	}
//}
//
//not printer print its just print fields and records
//@Override

//
//public void print() {
//	DLNode p = new DLNode();
//	p = first;
//	String fieldNameArr [] = dbOper.returnFieldNameArr();
//	for(int i=0;i<fieldNameArr.length;i++) {
//		System.out.print(fieldNameArr[i] +" ");
//	}
//	System.out.println();
//	while(p!=null) {
//		dbOper.printValueArr(p.value);
//		p=p.next;
//	}
//	
//}

















//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능
//////////////////////빼놓은 기능









//@Override
//public void enterInfoLinkedList() {
//	String select = "";
//	Object valueArr[] = new Object[fieldIdx];
//	System.out.println("***enter***\n");
//	do {
//		String value = "";
//		valueArr = new Object[fieldIdx];
//		for(int i=0;i<fieldIdx;i++) {
//			do {
//				System.out.println(String.format("\ncurrent field = %s size of value is %d ",dbControl[i].name,dbControl[i].size));
//				System.out.print("Enter value = ");
//				value = sc.nextLine();
//				if(!dbOper.isOkValueType(value,dbControl[i])) {
//					value = "";
//				}
//			}while(value.length() > dbControl[i].size || value.length() == 0);
//			valueArr[i] = dbOper.returnRightTypeValue(value,dbControl[i].type);
//		}
//		System.out.println("\n***bPInsert***\n\n");
//		bpInst.insert(this.dlInst.returnNewNode(valueArr));
//		System.out.println("\n***bPEnd***\n\n");
//		recordCount++;
//		
//		System.out.println("entered value");
//		for(int i=0;i<fieldIdx;i++) {
//			System.out.println(valueArr[i]);
//		}
//		System.out.println("enter more?(y/n)");
//		select = sc.nextLine();
//		select = select.toLowerCase();
//	}while(select.equals("y"));
//	System.out.println("\n***enter end***\n");
//	
//}










//@Override
//public void modify() {
//	DbGui guiInst= new DbGui(this,"modify");
//	guiInst.browseGuiOn();
//	selectFieldIdx = guiInst.frameInst.selectFieldIdx;
//	selectRecordIdx = guiInst.frameInst.selectRecordIdx;
//	String value = guiInst.frameInst.selectedValue;
//	if(value.equals("")) {
//		return;
//	}
//	dlInst.modify(selectFieldIdx,selectRecordIdx,value,this);
//}

