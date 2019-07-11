package com.github.sheld5.CernyRytir_PriceCheck.java.main;

import javax.swing.*;
import java.awt.*;

class Menu extends JPanel {

    private JLabel label;
    private JLabel price;
    private JLabel output;
    private Font font;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton submit;

    private int errorCount;
    private String errors;

    Menu() {
        errorCount = 0;
        errors = "";

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

        output = new JLabel("<html>Errors: 0");
        output.setForeground(Color.white);
        c.weighty = 1;
        c.gridy = 2;
        add(output, c);

        textArea = new JTextArea("");
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(Color.black);
        scrollPane.setPreferredSize(new Dimension(250, 150));
        c.weighty = 5;
        c.gridy = 3;
        add(scrollPane, c);

        submit = new JButton("Submit");
        submit.addActionListener(e -> submit());
        c.weighty = 1;
        c.gridy = 4;
        add(submit, c);

    }

    private void submit() {
        clearErrors();
        price.setText("Price: ___ Kč");
        price.setText("Price: " + Main.getTotalPrice(textArea.getText()) + " Kč");
        if (errorCount > 0) {
            errors += "<br>Prices of these cards have not been included in the total price.";
            updateErrors();
        }
    }

    public void addError(String error) {
        errorCount++;
        errors += "<br>" + error;
        updateErrors();
    }

    private void clearErrors() {
        errorCount = 0;
        errors = "";
        updateErrors();
    }

    private void updateErrors() {
        output.setText("<html>Errors: " + errorCount + "<br>" + errors);
    }

}
