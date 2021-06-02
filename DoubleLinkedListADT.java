
public interface DoubleLinkedListADT {
	void insert(Object value[], DbSystem dbInst);
	void modify(int selectFieldIdx, int selectRecordIdx, String modifyValue, DbSystem dbInst);
	void delete(int selectFieldIdx, int selectRecordIdx, DbSystem dbInst);
	
}
