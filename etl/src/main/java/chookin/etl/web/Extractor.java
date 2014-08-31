package chookin.etl.web;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;

public class Extractor {

	private JFrame frmWebExtractor;
	private JTextField textSite;
	private JLabel lblStartUrl;
	private JTextField textStartUrl;
	private JTextField textTraversalDepth;
	private JLabel lblTrvaersaldepth;
	private JTable table;
	private JButton btnStop;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Extractor window = new Extractor();
					window.frmWebExtractor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Extractor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWebExtractor = new JFrame();
		frmWebExtractor.setTitle("Web Extractor");
		frmWebExtractor.setBounds(100, 100, 530, 499);
		frmWebExtractor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWebExtractor.getContentPane().setLayout(null);
		
		JLabel lblSite = new JLabel("site");
		lblSite.setBounds(31, 24, 85, 15);
		frmWebExtractor.getContentPane().add(lblSite);
		
		textSite = new JTextField();
		textSite.setBounds(133, 21, 235, 21);
		frmWebExtractor.getContentPane().add(textSite);
		textSite.setColumns(10);
		
		lblStartUrl = new JLabel("start url");
		lblStartUrl.setBounds(31, 52, 85, 15);
		frmWebExtractor.getContentPane().add(lblStartUrl);
		
		textStartUrl = new JTextField();
		textStartUrl.setColumns(10);
		textStartUrl.setBounds(133, 49, 235, 21);
		frmWebExtractor.getContentPane().add(textStartUrl);
		
		textTraversalDepth = new JTextField();
		textTraversalDepth.setColumns(10);
		textTraversalDepth.setBounds(133, 77, 92, 21);
		frmWebExtractor.getContentPane().add(textTraversalDepth);
		
		lblTrvaersaldepth = new JLabel("trvaersalDepth");
		lblTrvaersaldepth.setBounds(31, 80, 85, 15);
		frmWebExtractor.getContentPane().add(lblTrvaersaldepth);
		
		table = new JTable();
		table.setBounds(31, 188, 449, 263);
		frmWebExtractor.getContentPane().add(table);
		
		JButton btnStart = new JButton("start");
		btnStart.setBounds(107, 122, 93, 23);
		frmWebExtractor.getContentPane().add(btnStart);
		
		btnStop = new JButton("stop");
		btnStop.setBounds(275, 122, 93, 23);
		frmWebExtractor.getContentPane().add(btnStop);
	}
}
