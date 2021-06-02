import java.io.Serializable;

enum Type{
	STRING,INT,CHAR,DOUBLE
}

public class DbStructure implements Serializable {
	String name;
	Type type;
	int size;
	int idx;

	DbStructure(int idx) {
		name = null;
		type = null;
		size = 0;
		this.idx = idx;
	}
}

