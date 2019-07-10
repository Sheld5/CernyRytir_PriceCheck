package com.github.sheld5.CernyRytir_PriceCheck.java.main;

import javax.swing.*;
import java.awt.*;

class Menu extends JPanel {

    private JLabel label;
    private JLabel price;
    private Font font;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton submit;

    Menu() {
        setBackground(Color.black);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipady = 24;

        font = new Font(Font.SANS_SERIF, Font.BOLD, 18);

        label = new JLabel("\"Černý Rytíř\" deck price calculator");
        label.setFont(font);
        label.setForeground(Color.orange);
        c.weighty = 1;
        c.gridy = 0;
        add(label, c);

        price = new JLabel("Price: ___ Kč");
        price.setFont(font);
        price.setForeground(Color.white);
        c.weighty = 1;
        c.gridy = 1;
        add(price, c);


        textArea = new JTextArea("");
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(Color.black);
        scrollPane.setPreferredSize(new Dimension(250, 150));
        c.weighty = 5;
        c.gridy = 2;
        add(scrollPane, c);

        submit = new JButton("Submit");
        submit.addActionListener(e -> submit());
        c.weighty = 1;
        c.gridy = 3;
        add(submit, c);

    }

    private void submit() {
        price.setText("Price: " + Main.getTotalPrice(textArea.getText()) + " Kč");
    }

}
