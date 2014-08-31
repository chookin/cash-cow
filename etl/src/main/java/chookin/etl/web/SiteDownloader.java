package chookin.etl.web;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import chookin.etl.web.site.LocalFileSaver;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SiteDownloader extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldDomain;
	private JTextField textFieldStartUrl;
	private JTextField textFieldTraversalDepth;
	private LocalFileSaver saver;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SiteDownloader dialog = new SiteDownloader();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SiteDownloader() {
		setTitle("site downloader");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblDomain = new JLabel("domain");
		lblDomain.setBounds(37, 28, 85, 15);
		contentPanel.add(lblDomain);
		
		textFieldDomain = new JTextField();
		textFieldDomain.setColumns(10);
		textFieldDomain.setBounds(139, 25, 235, 21);
		contentPanel.add(textFieldDomain);
		
		textFieldStartUrl = new JTextField();
		textFieldStartUrl.setColumns(10);
		textFieldStartUrl.setBounds(139, 53, 235, 21);
		contentPanel.add(textFieldStartUrl);
		
		JLabel label_1 = new JLabel("start url");
		label_1.setBounds(37, 56, 85, 15);
		contentPanel.add(label_1);
		
		JLabel lblTraversaldepth = new JLabel("traversalDepth");
		lblTraversaldepth.setBounds(37, 84, 85, 15);
		contentPanel.add(lblTraversaldepth);
		
		textFieldTraversalDepth = new JTextField();
		textFieldTraversalDepth.setColumns(10);
		textFieldTraversalDepth.setBounds(139, 81, 92, 21);
		contentPanel.add(textFieldTraversalDepth);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("start");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//						SiteDownloader page = SiteDownloader.this; 
//						SiteCrawler site = new SiteCrawler(page., startAbsUrl)
//						.saver = new LocalFileSaver(site, dir);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
