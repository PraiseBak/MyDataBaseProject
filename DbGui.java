import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


class frame extends Listeners{
	JFrame mainFrame = new JFrame();
	DbSystem dbInst;
	DbGui dbGui;
	JTable table;
	browseListener browseAction;
	int selectFieldIdx = 0;
	int selectRecordIdx = 0;
	String selectedBtn = "";
	String selectedValue= "";
	
	frame(DbSystem dbInstance,DbGui dbGuiInstance){
		this.dbGui = dbGuiInstance;
		this.dbInst = dbInstance;
		mainFrame.setSize(600,300);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	JFrame getFrame() {
		return mainFrame;
	}
	
	void addTableComponent (JFrame browseFrame) {
		
		browseFrame.setTitle(dbGui.mode);
		JScrollPane scrollPane;
		String head[] = dbInst.dbOper.returnFieldNameArr();
		String contents[][] = 
				new String[dbInst.recordCount][dbInst.fieldIdx];
		contents = dbInst.dbOper.returnRecords2Dimension();
		table = new JTable(contents, head) {
			public boolean isCellEditable(int i,int j) { return false; }
		};
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setEnabled(false);
		if(dbGui.mode == "modify" || dbGui.mode == "browse") {
			browseAction = new browseListener(table,this,dbGui.mode);
			table.addMouseListener(browseAction);
		}
		scrollPane = new JScrollPane(table);
		browseFrame.add(scrollPane);
		browseFrame.setVisible(true);
	}
	
	/*
	 * 주로 browse에서 쓰이는 기능으로 현재 선택된 레코드가 맞는지
	 * Yes or No로 선택하는 기능
	 */
	int showToggleButton(String selectedValue,frame originFrame) {
		this.selectedValue = selectedValue;
		JTextField value = new JTextField("Current value is: "+ selectedValue+ " .confirm select this value?");
		int result;
		value.setEditable(false);
		result = JOptionPane.showConfirmDialog(null, 
				"Current value is: "+ selectedValue+ " .confirm select this value?",
				"confirm dialog", JOptionPane.OK_CANCEL_OPTION);
		return result;
		
	}
	/*
	 * modify용으로 선택한 값을 무엇으로 바꿀 것인가 묻는 dialog 출력 
	 */
	
	void showModifyDialog(DbStructure curField) {
		ConfirmDialog modifyDialog = new ConfirmDialog(dbGui.mode+" dialog",curField,this);
	}
	
	
	void showSearchDialog() {
		String input ="";
		int tmpResult = 0;
		input = JOptionPane.showInputDialog(String.format("input %s data:",dbGui.mode));
		if(input ==  null) {
			this.selectFieldIdx = -1;
			return;
		}
		for(int i=0;i<dbInst.recordCount;i++) {
			for(int j=0;j<dbInst.fieldIdx;j++) {
				if(input.equals(table.getValueAt(i, j))){
					MyRenderer myRenderer = new MyRenderer(i,j);
					table.setDefaultRenderer(Object.class, myRenderer);
					table.changeSelection(i, j, false, false);
					tmpResult = JOptionPane.showConfirmDialog(null, "search continue?");
					if(tmpResult == 1) {
						this.selectFieldIdx = j;
						this.selectRecordIdx = i;
						return;
					}
					if(tmpResult == 2) {
						this.selectFieldIdx = 0;
						this.selectRecordIdx = 0;
						if(dbGui.mode.equals("delete")) {
							this.selectFieldIdx = -1;
						}
						return;
					
					}
				}
			}
		}
		this.selectFieldIdx = -1;
		if(tmpResult != -1) 
			JOptionPane.showMessageDialog(null, "there is no matched value");
	}
	
	class ConfirmDialog extends JDialog {
		JLabel curSizeLabel = new JLabel("");
		JButton yesBtn = new JButton("Ok");
		JButton noBtn = new JButton("Cancel");
		String title = "";
		JTextField text = new JTextField(10);
		JFrame dialogFrame = new JFrame();
		int result = 0;
		frame frameInst = null;
		
		public ConfirmDialog(String title,DbStructure curField,frame frameInstance) {
			this.frameInst = frameInstance;
			
			curSizeLabel.setText("max value size = "+curField.size);
			dialogFrame.setSize(300,200);
			dialogFrame.setLayout(new FlowLayout());
			dialogFrame.setTitle(title);
			dialogFrame.add(text);
			dialogFrame.add(yesBtn);
			dialogFrame.add(noBtn);
			dialogFrame.add(curSizeLabel);
			
			yesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton src = (JButton) e.getSource();
					dialogFrame.dispose();
					dialogFrame.setVisible(false);
					if(src.getText()=="Ok") {
						selectedValue = text.getText();
						if(frameInst.dbInst.dbOper.isOkValueType(selectedValue,curField)) {
							frameInst.mainFrame.setVisible(false);
							frameInst.mainFrame.dispose();
						}else {
							JOptionPane.showMessageDialog(null, "Wrong value type");
						}
					}
					dialogFrame.dispose();
					dialogFrame.setVisible(false);
				}
			});
			text.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent ke) {
					JTextField src = (JTextField) ke.getSource();
					if(src.getText().length()>curField.size-1) ke.consume();
				}
			});
			dialogFrame.setVisible(true);
		}
		
	}
	
}

public class DbGui {
	String mode = "";
	DbSystem dbInst;
	frame frameInst;
	public DbGui(DbSystem dbInstance,String mode) {
		this.mode = mode;
		this.dbInst = dbInstance;
	}
	
	void browseGuiOn() {
		frameInst = new frame(dbInst,this);
		JFrame frame = frameInst.getFrame();
		frameInst.addTableComponent(frame);
		if(!mode.equals("search") && !mode.equals("delete")){
			checkFrameExit(frame);
		}
	}
	
	void checkFrameExit(JFrame frame) {
		System.out.println("X를 눌러 GUI를 종료해주세요");
		while(frame.isVisible()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void addModifyGui(DbStructure curField) {
		frameInst.showModifyDialog(curField);
	}

	public void addSearchGui() {
		JOptionPane.showInputDialog("input search data");
		
	}
	
}


class MyRenderer extends DefaultTableCellRenderer 
{
	int i = 0;
	int j = 0;
    MyRenderer(int i,int j){
    	this.i = i;
    	this.j = j;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean   isSelected, boolean hasFocus, int row, int column)
    {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(Color.white);
        if(row == i && column == j) {
        	c.setBackground(Color.lightGray);
        }else {
        	c.setBackground(Color.white);
        }
        return c;
        
    }
}


