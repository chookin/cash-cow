package chookin.etl.web;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import chookin.utils.configuration.ConfigManager;

import chookin.etl.common.LocalDir;
import chookin.etl.web.site.LocalFileSaver;
import chookin.etl.web.site.SiteCrawler;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrmSiteSaver extends JFrame {

	private static final long serialVersionUID = 1327436340091060739L;
	private JPanel contentPane;
	private JTextField textFieldTraversalDepth;
	private JTextField textFieldStartUrl;
	private JTextField textFieldDomain;
	private LocalFileSaver saver;
	private JTextField textFieldSaveDir;
	private JLabel lblSaveDir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigManager.reConfig();
					FrmSiteSaver frame = new FrmSiteSaver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmSiteSaver() {
		setTitle("save domain pages to local dir");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDomain = new JLabel("domain");
		lblDomain.setBounds(52, 40, 85, 15);
		contentPane.add(lblDomain);

		JLabel lblStartUrl = new JLabel("start url");
		lblStartUrl.setBounds(52, 68, 85, 15);
		contentPane.add(lblStartUrl);

		JLabel lblTraversaldepth = new JLabel("traversalDepth");
		lblTraversaldepth.setBounds(52, 96, 85, 15);
		contentPane.add(lblTraversaldepth);

		textFieldTraversalDepth = new JTextField();
		textFieldTraversalDepth.setColumns(10);
		textFieldTraversalDepth.setBounds(154, 93, 92, 21);
		textFieldTraversalDepth.setName("traversal depth");
		textFieldTraversalDepth.setText(ConfigManager
				.getProperty("traversalDepth"));
		contentPane.add(textFieldTraversalDepth);

		textFieldStartUrl = new JTextField();
		textFieldStartUrl.setColumns(10);
		textFieldStartUrl.setBounds(154, 65, 235, 21);
		textFieldStartUrl.setName("start url");
		contentPane.add(textFieldStartUrl);

		textFieldDomain = new JTextField();
		textFieldDomain.setColumns(10);
		textFieldDomain.setBounds(154, 37, 235, 21);
		textFieldDomain.setName("domain");
		textFieldDomain.setText(ConfigManager.getProperty("domain"));
		contentPane.add(textFieldDomain);

		JButton btnStart = new JButton("start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					FrmSiteSaver frm = FrmSiteSaver.this;
					Validation.notBlank(frm.textFieldDomain,
							frm.textFieldSaveDir, frm.textFieldStartUrl,
							frm.textFieldTraversalDepth);

					String strDepth = frm.textFieldTraversalDepth.getText();
					String domain = frm.textFieldDomain.getText();
					String startUrl = frm.textFieldStartUrl.getName();
					String strDir = frm.textFieldSaveDir.getText();
					int maxTraversalDepth = Integer.parseInt(strDepth);
					SiteCrawler site = new SiteCrawler(domain, startUrl);
					LocalDir dir = new LocalDir(strDir);
					frm.saver = new LocalFileSaver(site, dir);
					frm.saver.saveSitePages(maxTraversalDepth);
				} catch (chookin.etl.web.ValidationException ex) {
					JOptionPane.showMessageDialog(FrmSiteSaver.this,
							ex.getMessage());
				} catch (Throwable ex) {
					JOptionPane.showMessageDialog(FrmSiteSaver.this,
							ex.getStackTrace());
				}
			}
		});
		btnStart.setBounds(265, 184, 93, 23);
		contentPane.add(btnStart);

		textFieldSaveDir = new JTextField();
		textFieldSaveDir.setColumns(10);
		textFieldSaveDir.setBounds(154, 121, 235, 21);
		textFieldSaveDir.setName("save dir");
		textFieldSaveDir.setText(ConfigManager.getProperty("saveDir"));
		contentPane.add(textFieldSaveDir);

		lblSaveDir = new JLabel("save dir");
		lblSaveDir.setBounds(52, 124, 85, 15);
		contentPane.add(lblSaveDir);
	}
}
