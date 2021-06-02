
public interface DbInterface {
	int selectMenu();
	void define();
	void dbStart() throws InterruptedException;
	void enterInfo();
	void browse() throws InterruptedException;
	void modify();
	void delete();
	void print();
	void save();
	void load();
	void search();
}
