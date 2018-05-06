import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class downloaderUI implements ActionListener {

    JFrame frame;
    JPanel p1;
    JProgressBar bar;
    JLabel l1, l2, l3, l4, l7;
    JButton b2;
    downloader obj2 = new downloader();
    static String l7_prefix = "Number of Files Transferred: ";
    static String l3_prefix = "Total Time: ";
    static String l4_prefix = "Next Backup After: ";


    public void initialise() {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(700, 600);
	frame.setTitle("Backup & Security");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p1 = new JPanel();
        p1.setSize(700, 600);
        p1.setLayout(null);
        frame.add(p1);
        l2 = new JLabel("Waiting for connection");
        b2 = new JButton("Stop Backup Service");
        l3 = new JLabel("Total Time: ");
        b2.addActionListener(this);
        l1 = new JLabel("Backup & Security");
        l4 = new JLabel("Next Backup After:");
        l7 = new JLabel(l7_prefix+"0");
    }

    public void pagestructure() {
        l1.setBounds(150, 50, 400, 100);
	l1.setHorizontalAlignment(JLabel.CENTER);
        l1.setFont(new Font("Impact", Font.BOLD, 35));
        p1.add(l1);
        l2.setFont(new Font("Impact", Font.BOLD, 25));
        l7.setFont(new Font("Impact", Font.BOLD, 19));
        l2.setForeground(Color.red);
        l7.setBounds(150, 310, 400, 100);
	l7.setHorizontalAlignment(JLabel.CENTER);
        p1.add(l7);
        l2.setBounds(150, 150, 400, 100);
	l2.setHorizontalAlignment(JLabel.CENTER);
        b2.setSize(100, 100);
        b2.setBounds(265, 400, 180, 50);
        l3.setFont(new Font("Impact", Font.BOLD, 20));
        l3.setBounds(150, 450, 400, 80);
	l3.setHorizontalAlignment(JLabel.CENTER);
        l4.setFont(new Font("Impact", Font.BOLD, 20));
        l4.setBounds(150, 480, 400, 80);
	l4.setHorizontalAlignment(JLabel.CENTER);
        p1.add(l3);
        p1.add(l4);
        p1.add(l2);
        p1.add(b2);
        bar = new JProgressBar();
        bar.setBounds(150, 300, 400, 30);
        bar.setString("0 %");
        bar.setValue(0);
        bar.setStringPainted(true);
        p1.add(bar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b2) {
			System.exit(0);
        }
    }

    public void progress() {
        downloader obje = new downloader();
    }

}
