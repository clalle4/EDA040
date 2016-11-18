package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class GUI extends JFrame implements Runnable {

	private static final long serialVersionUID = -5959596772809263279L;
	private ImageIcon icon1;
	private ImageIcon icon2;
	private JComboBox<String> cameraBox;
	private JComboBox<String> viewBox;
	private final String[] viewOptions;
	private final String[] cameraOptions;

	public GUI(Monitor mon) {
		super();
		cameraOptions = new String[] { "Auto", "Idel", "Movie" };
		viewOptions = new String[] { "Auto", "Synchronous", "Asynchronous" };
		cameraBox = new JComboBox<>(cameraOptions);
		cameraBox.addActionListener(new cameraBoxListener());
		viewBox = new JComboBox<>(viewOptions);
		viewBox.addActionListener(new viewBoxListener());

		getContentPane().setLayout(new BorderLayout());
		icon1 = new ImageIcon();
		icon2 = new ImageIcon();

		JPanel modePanel = new JPanel(new GridLayout(3, 2));
		Font font = new Font("TimesRoman", Font.BOLD, 30);
		JTextArea infoJTA = new JTextArea("Movie mode triggered by camera 1 (ex)");
		infoJTA.setEditable(false);
		infoJTA.setFont(font);
		modePanel.add(infoJTA);
		JTextArea jta = new JTextArea("Set the mode");
		jta.setEditable(false);
		jta.setFont(font);
		modePanel.add(jta);
		JTextArea cameraModeJTA = new JTextArea("Current camera mode: Movie");
		cameraModeJTA.setEditable(false);
		cameraModeJTA.setFont(font);
		modePanel.add(cameraModeJTA);
		cameraBox.setFont(font);
		modePanel.add(cameraBox);
		JTextArea viewModeJTA = new JTextArea("Current view mode: Syncronized");
		viewModeJTA.setEditable(false);
		viewModeJTA.setFont(font);
		modePanel.add(viewModeJTA);
		viewBox.setFont(font);
		modePanel.add(viewBox);
		add(modePanel, BorderLayout.PAGE_END);
		pack();
		setSize(1300, 650);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void run() {
		AxisM3006V cam = new AxisM3006V();
		cam.init();
		cam.connect();
		for (int i = 0; i < 1000; i++) {
			byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
			cam.getJPEG(jpeg, 0);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					refreshImage(jpeg);
				}
			});
		}
		cam.close();
		cam.destroy();
	}

	public void refreshImage(byte[] jpeg) {
		Image image = getToolkit().createImage(jpeg);
		getToolkit().prepareImage(image, -1, -1, null);
		icon1.setImage(image);
		icon1.paintIcon(this, this.getGraphics(), 640, 0);
		icon2.setImage(image);
		icon2.paintIcon(this, this.getGraphics(), 0, 0);
	}
	

	private class cameraBoxListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
            String mode = (String)combo.getSelectedItem();
            System.out.println(mode);
		}
	}
	
	private class viewBoxListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
            String mode = (String)combo.getSelectedItem();
            System.out.println(mode);
		}
	}

	public static void main(String[] args) {
		GUI s = new GUI(new Monitor());
		s.run();
	}
}
