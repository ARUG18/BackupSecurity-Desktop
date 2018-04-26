import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class downloaderUI implements ActionListener {

    JFrame frame;
    JPanel p1;
    JProgressBar bar;
    JLabel l1, l2, l3, l4, l5, l6, l7, l8;
    JButton b2;
    downloader obj2 = new downloader();


    public void initialise() {

        frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p1 = new JPanel();
        p1.setSize(590, 590);
        p1.setLayout(null);
        frame.add(p1);
        l2 = new JLabel("Waiting for connection");
        b2 = new JButton("Stop Backup");
        l3 = new JLabel("Total Time: ");
        b2.addActionListener(this);
        l1 = new JLabel("Backup And Security");
        l4 = new JLabel("Next Backup After:");
        l5 = new JLabel();
        l6 = new JLabel();
        l7 = new JLabel("Number of Files Transferred:");
        l8 = new JLabel("0");
    }

    public void pagestructure() {
        l1.setBounds(125, 30, 400, 100);
        l1.setFont(new Font("Impact", Font.BOLD, 35));
        p1.add(l1);
        l2.setFont(new Font("Impact", Font.BOLD, 25));
        l7.setFont(new Font("Impact", Font.BOLD, 19));
        l8.setFont(new Font("Impact", Font.BOLD, 19));
        l2.setForeground(Color.red);
        l7.setBounds(140, 310, 400, 100);
        l8.setBounds(400, 310, 400, 100);
        p1.add(l7);
        p1.add(l8);
        l2.setBounds(170, 150, 400, 100);
        b2.setSize(100, 100);
        b2.setBounds(200, 400, 170, 50);
        l3.setFont(new Font("Impact", Font.BOLD, 20));
        l3.setBounds(150, 450, 180, 80);
        l4.setFont(new Font("Impact", Font.BOLD, 20));
        l5.setFont(new Font("Impact", Font.BOLD, 20));
        l4.setBounds(150, 480, 180, 80);
        l5.setBounds(260, 450, 180, 80);
        l5.setForeground(Color.green);
        l6.setFont(new Font("Impact", Font.BOLD, 20));
        l6.setForeground(Color.green);
        l6.setBounds(320, 480, 180, 80);
        p1.add(l5);
        p1.add(l6);
        p1.add(l3);
        p1.add(l4);
        p1.add(l2);
        p1.add(b2);
        bar = new JProgressBar();
        bar.setBounds(100, 300, 380, 30);
        bar.setString("Completed %");
        bar.setValue(0);
        bar.setStringPainted(true);
        p1.add(bar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b2) {

            obj2.pause = true;

        }
    }

    public void progress() {
        downloader obje = new downloader();

    }

}
