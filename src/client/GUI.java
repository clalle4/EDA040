package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

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
		cameraBox = new JComboBox<>(cameraOptions);
		cameraBox.addActionListener(new cameraBoxListener());
		viewBox = new JComboBox<>(viewOptions);
		viewBox.addActionListener(new viewBoxListener());

		getContentPane().setLayout(new BorderLayout());
		icon1 = new ImageIcon();
		icon2 = new ImageIcon();

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

	public void run() {
		while (true) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String s = "Current camera mode: ";
					if (mon.movieMode()) {
						s += "Movie";
					} else {
						s += "Idle";
					}
					cameraModeJTA.setText(s);
					LinkedList<byte[]> images = mon.getImages();
					refreshImage(images);
				}
			});
		}
	}

	public void refreshImage(LinkedList<byte[]> images) {
		System.out.println("refreshed...............................................");
		Image image1 = getToolkit().createImage(images.get(0));
		getToolkit().prepareImage(image1, -1, -1, null);
		Image image2 = getToolkit().createImage(images.get(1));
		getToolkit().prepareImage(image2, -1, -1, null);
		icon1.setImage(image1);
		icon1.paintIcon(this, this.getGraphics(), 640, 80);
		icon2.setImage(image2);
		icon2.paintIcon(this, this.getGraphics(), 0, 80);
	}

	private class cameraBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			String mode = (String) combo.getSelectedItem();
			if (mode.equals("Movie")) {
				mon.setMovieMode(true);
			} else if (mode.equals("Idle")) {
				mon.setMovieMode(false);
			}
			System.out.println(mode);
		}
	}

	private class viewBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			String mode = (String) combo.getSelectedItem();
			System.out.println(mode);
		}
	}
}
