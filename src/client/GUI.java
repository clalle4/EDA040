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

public class GUI extends JFrame implements Runnable {

	private static final long serialVersionUID = -5959596772809263279L;
	private ImageIcon icon1;
	private ImageIcon icon2;
	private JComboBox<String> cameraBox;
	private JComboBox<String> viewBox;
	private final String[] viewOptions;
	private final String[] cameraOptions;
	private ClientMonitor mon;
	private JTextArea cameraModeJTA;

	public GUI(ClientMonitor mon) {
		super();
		this.mon = mon;
		cameraOptions = new String[] { "Auto", "Idle", "Movie" };
		viewOptions = new String[] { "Auto", "Synchronous", "Asynchronous" };
		icon1 = new ImageIcon();
		icon2 = new ImageIcon();
	}

	private void init() {
		cameraBox = new JComboBox<>(cameraOptions);
		cameraBox.addActionListener(new cameraBoxListener());
		viewBox = new JComboBox<>(viewOptions);
		viewBox.addActionListener(new viewBoxListener());

		getContentPane().setLayout(new BorderLayout());
		JPanel namePanel = new JPanel(new GridLayout(1, 2));
		Font f = new Font("TimesRoman", Font.ITALIC, 25);
		JTextArea name1 = new JTextArea("Camera 1");
		name1.setFont(f);
		name1.setEditable(false);
		namePanel.add(name1);
		JTextArea name2 = new JTextArea("Camera 2");
		name2.setFont(f);
		name2.setEditable(false);
		namePanel.add(name2);
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
		cameraModeJTA = new JTextArea("Current camera mode: Idle");
		cameraModeJTA.setEditable(false);
		cameraModeJTA.setFont(font);
		modePanel.add(cameraModeJTA);
		cameraBox.setFont(font);
		modePanel.add(cameraBox);
		JTextArea viewModeJTA = new JTextArea("Current view mode: Synchronous");
		viewModeJTA.setEditable(false);
		viewModeJTA.setFont(font);
		modePanel.add(viewModeJTA);
		viewBox.setFont(font);
		modePanel.add(viewBox);
		add(namePanel, BorderLayout.PAGE_START);
		add(modePanel, BorderLayout.PAGE_END);
		pack();
		setSize(1300, 720);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// TODO All components are locked while the thread is running!
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();
			}
		});
		UpdateCamera1Thread updateCamera1Thread = new UpdateCamera1Thread();
		updateCamera1Thread.start();
		UpdateCamera2Thread updateCamera2Thread = new UpdateCamera2Thread();
		updateCamera2Thread.start();
		
//		UpdateCameraThread updateCameraThread = new UpdateCameraThread();
//		updateCameraThread.start();
	}

	// public void run() {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// while (true) {
	// try {
	// updateText();
	// refreshImage(mon.getCam1Image(), 1);
	// refreshImage(mon.getCam2Image(), 2);
	// } catch (InterruptedException e) {
	// }
	// }
	// }
	// });
	// }

	private void updateText() {
		String s = "Current camera mode: ";
		if (mon.motionDetected()) {
			s += "Movie";
		} else {
			s += "Idle";
			cameraModeJTA.setText(s);
		}
	}

	private void refreshImage(byte[] img, int cameraNbr) {
		Image image = getToolkit().createImage(img);
		getToolkit().prepareImage(image, -1, -1, null);
		if (cameraNbr == 1) {
			icon1.setImage(image);
			icon1.paintIcon(this, this.getGraphics(), 0, 80);
		} else if (cameraNbr == 2) {
			icon2.setImage(image);
			icon2.paintIcon(this, this.getGraphics(), 640, 80);

		}
	}

	private class cameraBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			int cameraMode = combo.getSelectedIndex();
			mon.setCameraMode(cameraMode);
		}
	}

	private class viewBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			String mode = (String) combo.getSelectedItem();
			mon.setViewMode(mode);
		}
	}

	private class UpdateCamera1Thread extends Thread {
		public void run() {
			while (true) {
				try {
					refreshImage(mon.getCam1Image(), 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class UpdateCamera2Thread extends Thread {
		public void run() {
			while (true) {
				try {
					refreshImage(mon.getCam2Image(), 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	private class UpdateCameraThread extends Thread {
//		public void run() {
//			while (true) {
//				try {
//					byte[] im1 = mon.getCam1Image();
//					byte[] im2 = mon.getCam2Image();
//					
//
//					// refreshImage(im1, 1);
//					// refreshImage(im2, 2);
//
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}
