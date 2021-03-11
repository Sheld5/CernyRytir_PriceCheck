package com.github.sheld5.CernyRytir_PriceCheck.main;

import com.github.sheld5.CernyRytir_PriceCheck.util.Resources;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {

    private static final String DEFAULT_URL_1 = "http://cernyrytir.cz/index.php3?akce=3&limit=";
    private static final String DEFAULT_URL_2 = "&searchtype=card&searchname=";
    private static final String DEFAULT_URL_3 = "&hledej_pouze_magic=1&submit=Vyhledej";

    private static final String FRAME_TITLE = "   \"Černý Rytíř\" deck price calculator";
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;

    private static JFrame frame;
    private static com.github.sheld5.CernyRytir_PriceCheck.main.Menu menu;

    public static void main(String[] args) {
        Resources.load();
        initFrame();
        menu = new com.github.sheld5.CernyRytir_PriceCheck.main.Menu();
        frame.add(menu);
        frame.revalidate();
    }

    private static void initFrame() {
        frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setIconImage(Resources.windowIcon);
        frame.setVisible(true);
    }

    // int played should have values 0 / 1 / 2 according to 'near mint' / 'lightly played' / 'played'
    public static int getTotalPrice(String cardList, int played, boolean nonenglish) {
        // Create String[] of all cards and int[] of their quantities
        Pattern quantity = Pattern.compile("^[0-9]+[x]?$");
        Pattern spaces = Pattern.compile("^ +$");
        Pattern expansionCode = Pattern.compile("^\\([A-Z0-9]{3}\\)$");
        String[] list = cardList.split("\n");
        int[] quantities = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            if (spaces.matcher(list[i]).matches()) {
                list[i] = "";
            }
            String[] words = list[i].split(" ");
            if (quantity.matcher(words[0]).matches()) {
                quantities[i] = Integer.parseInt(words[0].replace("x", ""));
                words[0] = "";
            } else {
                quantities[i] = 1;
            }
            for (int o = 0; o < words.length; o++) {
                words[o].replace(" ", "");
                if (words[o].equals("/")) {
                    words[o] = "//";
                } else if (expansionCode.matcher(words[o]).matches()) {
                    words[o] = "";
                }
            }
            list[i] = "";
            for (String word : words) {
                if (!word.equals("")) {
                    if (list[i].equals("")) {
                        list[i] = word;
                    } else {
                        list[i] += " " + word;
                    }
                }
            }
            System.out.println(list[i]);
        }

        // call getCardPrice() for each cards and calculate price total
        int priceTotal = 0;
        int previousTotal = 0;
        for (int i = 0; i < list.length; i++) {
            if (!list[i].equals("")) {
                try {
                    priceTotal += quantities[i] * getCardPrice(list[i], played, nonenglish);
                    if (priceTotal > previousTotal) {
                        menu.success(quantities[i]);
                    }
                    previousTotal = priceTotal;
                } catch (IOException e) {
                    throwError(list[i]);
                }
            }
        }
        return priceTotal;
    }

    // Find the lowest price for the card with the selected settings.
    private static int getCardPrice(String card, int played, boolean nonenglish) throws IOException {
        int page = 0;
        String cardName = normalizeCardName(card);
        Document doc = Jsoup.connect(DEFAULT_URL_1 + (page * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
        Elements kusovkytext = doc.getElementsByAttributeValue("class", "kusovkytext");
        // if there is only one page
        if (kusovkytext.size() == 2) {
            // if there are no cards on the page
            if (doc.getElementsByAttributeValue("class", "highslide").size() == 0) {
                throwError(card);
                return 0;
            } else {
                int result = goThroughCards(kusovkytext.get(1).getElementsByTag("tr"), played, nonenglish);
                if (result == 0) {
                    throwError(card);
                }
                return result;
            }
        // if there is more than one page
        } else {
            // find the last page
            while (true) {
                doc = Jsoup.connect(DEFAULT_URL_1 + ((page + 1) * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
                if (doc.getElementsByAttributeValue("class", "highslide").size() == 0) {
                    break;
                } else {
                    page++;
                }
            }
            // go through pages from last to first until a card matching the selected settings is found
            while (true) {
                doc = Jsoup.connect(DEFAULT_URL_1 + (page * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
                kusovkytext = doc.getElementsByAttributeValue("class", "kusovkytext");
                int target = kusovkytext.size() - ((kusovkytext.size() - 2) / 2) - 1;
                int result = goThroughCards(kusovkytext.get(target).getElementsByTag("tr"), played, nonenglish);
                if (result != 0) {
                    return result;
                } else if (page == 0) {
                    throwError(card);
                    return result;
                } else {
                    page--;
                }
            }
        }
    }

    private static int goThroughCards(Elements listRows, int played, boolean nonenglish) {
        if (played == 2 && nonenglish) {
            // return the last card
            return Integer.parseInt(listRows.last().getElementsByTag("td").get(2).text().replace(" Kč", ""));
        } else {
            // go through all cards from the last to the first and find the first one that matches the selected settings
            for (int cardFromLast = 0; cardFromLast < listRows.size() / 3; cardFromLast++) {
                int[] cardAttributes = checkCardAttributes(listRows.get(listRows.size() - 1 - 2 - (cardFromLast * 3)).getElementsByTag("td").get(1).text());
                boolean cardMatches;
                if (cardAttributes[0] == 0 && cardAttributes[1] == 0) {
                    cardMatches = true;
                } else if (cardAttributes[0] > played) {
                    cardMatches = false;
                } else if (cardAttributes[1] == 1 && !nonenglish) {
                    cardMatches = false;
                } else {
                    cardMatches = true;
                }
                if (cardMatches) {
                    return Integer.parseInt(listRows.get(listRows.size() - 1 - (cardFromLast * 3)).getElementsByTag("td").get(2).text().replace(" Kč", ""));
                }
            }
            // if failed to find any card matching the selected settings
            return 0;
        }
    }

    // returns int[] {cardCondition, nonenglish} with values 0,1,2 for cardCondition and 0,1 for nonenglish
    private static int[] checkCardAttributes(String cardLabel) {
        String[] words = cardLabel.split(" ");
        int hyphenIndex = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("-")) {
                hyphenIndex = i;
            }
        }
        // {played, nonenglish}
        int[] cardAttributes = new int[] {0, 0};
        // if there is no hyphen, card is normal
        if (hyphenIndex == -1) {
            return cardAttributes;
        }
        // go through words after the hyphen (card attributes)
        ArrayList<Integer> slashIndexes = new ArrayList();
        slashIndexes.add(hyphenIndex);
        for (int i = hyphenIndex + 1; i < words.length; i++) {
            if (words[i].equals("/")) {
                slashIndexes.add(i);
            }
        }
        slashIndexes.add(words.length);
        // go through each part separated by slashes individually
        for (int i = 0; i < slashIndexes.size() - 1; i++) {
            boolean lightly = false;
            boolean played = false;
            boolean foil = false;
            for (int o = slashIndexes.get(i) + 1; o < slashIndexes.get(i + 1); o++) {
                if (words[o].equals("lightly")) {
                    lightly = true;
                } else if (words[o].equals("played")) {
                    played = true;
                } else if (words[o].equals("foil")) {
                    foil = true;
                }
            }
            if (lightly) {
                cardAttributes[0] = 1;
            } else if (played) {
                cardAttributes[0] = 2;
            } else if (!foil) {
                cardAttributes[1] = 1;
            }
        }
        return cardAttributes;
    }

    private static String normalizeCardName(String card) {
        card = card.replace(" ", "%20");
        card = card.replace("'", "%B4");
        return card;
    }

    private static void throwError(String card) {
        System.out.println("ERROR: Could not find card \"" + card + "\"");
        System.out.println("The card has been excluded from the total calculated price.");
        menu.addError(card);
    }

}
