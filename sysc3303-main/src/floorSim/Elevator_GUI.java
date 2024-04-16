package floorSim;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeListener;

import elevatorSim.ElevatorMain;
import scheduleServer.SchedulerMain;

import javax.swing.event.ChangeEvent;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;

public class Elevator_GUI {

	private JFrame frame;
	int numFloors = 0;
	int numElevators = 0;
	ArrayList<JRadioButton> buttonList;
	JTextArea textArea = new JTextArea();
	List<Integer> dirList = new ArrayList<Integer>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Elevator_GUI window = new Elevator_GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Elevator_GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 962, 709);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane.setViewportView(textArea);
		buttonList = new ArrayList<JRadioButton>();
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setLayout(new GridBagLayout());
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridBagLayout());
		JLabel lblNewLabel = new JLabel("Number of Elevators:");
		JSpinner spinner = new JSpinner();
		
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if((int)spinner.getValue() > 21) {
		        	spinner.setEnabled(false);
		        }
				int num = (int) spinner.getValue();
				textArea.append("Floor: "+spinner.getValue()+" added.\n");
				numFloors = num;
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("Number of Floors:");
		JSpinner spinner_1 = new JSpinner();
		spinner_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GridBagConstraints pgbc = new GridBagConstraints();
		        pgbc.insets = new Insets(1,2,1,2);
				int num1 = (int) spinner_1.getValue();
				
				JLabel elevator = new JLabel("Elevator: "+num1+"  ");
				textArea.append("Elevator: "+spinner_1.getValue()+" added.\n");
				panel_1.add(elevator,pgbc);
				panel_1.revalidate();
	            panel_1.repaint();
				numElevators = num1;
				
			}
		});
		
		
		
		JButton btnNewButton = new JButton("Start");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GridBagConstraints gbc = new GridBagConstraints();
		        gbc.insets = new Insets(0,1,1,0);
		        for(int x = 0; x < numElevators ; x++){
		            for(int y = 0; y < numFloors; y++){
		                gbc.gridx=x;
		                gbc.gridy=numFloors-1-y;
		                JRadioButton button = new JRadioButton("Floor: "+(y));
		                buttonList.add(button);
		                panel.add(button,gbc);
		                panel.revalidate();
		                panel.repaint();
		            }
		        }
		        start(numFloors);
			}
		});
		
		JLabel lblNewLabel_2 = new JLabel("Console Output:");
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
								.addGap(65)
								.addComponent(btnNewButton))
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblNewLabel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addGap(2)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnNewButton)))
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
							.addGap(25)
							.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		frame.getContentPane().setLayout(groupLayout);
	}
	
	public void start(int num) {
		FloorMain.startFloor(num, this);
		
	}
	
	public void print(String msg) {
		textArea.append(msg);
	}
	
	public void setFloor(int num, int elev) {
		for(int i = 0; i < 22; i++) {
			buttonList.get((elev)*numFloors + i).setSelected(false);
		}
		buttonList.get((elev)*numFloors + num).setSelected(true);
	}
	
	public void setLamp(int num, boolean upOn, boolean dwnOn ) {
		String str = "Floor: "+num;
		if(upOn) {
			str += " (Up)";
		}
		if(dwnOn) {
			str += " (Down)";
		}
		buttonList.get(num).setText(str);
		
	}
	
	
}
