package com.github.sheld5.CernyRytir_PriceCheck.java.main;

import javax.swing.*;
import java.awt.*;

class Menu extends JPanel {

    private JLabel label;
    private JLabel price;
    private JPanel scrollPanel;
    private JScrollPane outputScrollPane;
    private JLabel output;
    private Font font;
    private JScrollPane textScrollPane;
    private JTextArea textArea;
    private JPanel optionsPanel;
    private JCheckBox playedBox;
    private JCheckBox nonenglishBox;
    private JButton submit;

    private int successCount;
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

        price = new JLabel("Price: 0 Kč");
        price.setFont(font);
        price.setForeground(Color.white);
        c.weighty = 1;
        c.gridy = 1;
        add(price, c);

        scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridBagLayout());
        scrollPanel.setPreferredSize(new Dimension(480, 240));
        GridBagConstraints d = new GridBagConstraints();
        d.ipadx = 10;

        output = new JLabel("<html>Successful: 0<br>Errors: 0");
        output.setForeground(Color.black);
        outputScrollPane = new JScrollPane(output);
        outputScrollPane.setBackground(Color.black);
        outputScrollPane.setPreferredSize(new Dimension(220, 220));
        d.gridx = 0;
        scrollPanel.add(outputScrollPane, d);

        textArea = new JTextArea("");
        textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBackground(Color.black);
        textScrollPane.setPreferredSize(new Dimension(220, 220));
        d.gridx = 1;
        scrollPanel.add(textScrollPane, d);

        c.weighty = 5;
        c.gridy = 2;
        add(scrollPanel, c);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());
        optionsPanel.setPreferredSize(new Dimension(480, 32));

        playedBox = new JCheckBox("include played cards");
        nonenglishBox = new JCheckBox("include non-english cards");
        optionsPanel.add(playedBox);
        optionsPanel.add(nonenglishBox);

        c.weighty = 1;
        c.gridy = 3;
        add(optionsPanel, c);

        submit = new JButton("Submit");
        submit.addActionListener(e -> submit());
        c.weighty = 1;
        c.gridy = 4;
        add(submit, c);

    }

    private void submit() {
        clearErrors();
        price.setText("Price: 0 Kč");
        price.setText("Price: " + Main.getTotalPrice(textArea.getText(), playedBox.isSelected(), nonenglishBox.isSelected()) + " Kč");
        if (errorCount > 0) {
            errors += "<br><br>Prices of these cards have not been included in the total price.";
            updateErrors();
        }
    }

    public void addError(String error) {
        errorCount++;
        if (errors.equals("")) {
            errors += "<br><br>Errors while loading prices for:<br>";
        }
        errors += "<br>" + "  " + error;
        updateErrors();
    }

    public void success(int quantity) {
        successCount += quantity;
        updateErrors();
    }

    private void clearErrors() {
        successCount = 0;
        errorCount = 0;
        errors = "";
        updateErrors();
    }

    private void updateErrors() {
        output.setText("<html>Successful: " + successCount + "<br>Errors: " + errorCount + errors);
    }

}
