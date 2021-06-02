import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTable;

public class Listeners{

	class browseListener extends ClassCastException implements MouseListener {
		
		JTable table = null;
		frame frameInst = null;
		frame btnFrameInst = null;
		int okPushed = 0;
		private String mode = "";
		//mode is like its search or modify or ... so on
		browseListener(JTable table,frame frameInstance,String mode){
			this.mode = mode;
			this.table = table;
			this.frameInst = frameInstance;
		}
		//gui events are here
		//Separated by 'mode' variable
		
		/*
		 * 테이블 레코드 마우스 클릭 시 기능에 맞추어 동작
		 * 선택된 레코드의 위치, 토글 버튼 생성 등의 기능.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			int result = 0;
			okPushed = 0;
			DbGui btnGuiInst = new DbGui(frameInst.dbInst, mode);
			btnFrameInst = new frame(frameInst.dbInst,btnGuiInst);
			String selectedValue = (String) table.getValueAt(table.getSelectedRow(),table.getSelectedColumn());
			int fieldIdx = table.getSelectedColumn();
			int recordIdx = table.getSelectedRow();
			result = btnFrameInst.showToggleButton(selectedValue,frameInst);
			if(result == 0) {
				frameInst.selectFieldIdx = fieldIdx;
				frameInst.selectRecordIdx = recordIdx;
				if(mode == "browse") {
					frameInst.mainFrame.setVisible(false);
					frameInst.mainFrame.dispose();
					
				}else if(mode == "modify") {
					DbStructure curField = frameInst.dbInst.dbOper.returnField(fieldIdx);
					frameInst.dbGui.addModifyGui(curField);
				}
			}
		}
		
		

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	

	
}
