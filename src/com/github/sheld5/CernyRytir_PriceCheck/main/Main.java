package com.github.sheld5.CernyRytir_PriceCheck.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.regex.Pattern;

public class Main {

    private static final String DEFAULT_URL_1 = "http://cernyrytir.cz/index.php3?akce=3&limit=";
    private static final String DEFAULT_URL_2 = "&searchtype=card&searchname=";
    private static final String DEFAULT_URL_3 = "&hledej_pouze_magic=1&submit=Vyhledej";
    private static final String QUANTITY_REGEX = "^[0-9]+[x]?$";

    public static void main(String[] args) {
        

    }

    private static int getTotalPrice(String cardList) {
        // Create String[] of all cards and int[] of their quantities
        Pattern pattern = Pattern.compile(QUANTITY_REGEX);
        String[] list = cardList.split("\n");
        int[] quantities = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            String[] words = list[i].split(" ");
            if (pattern.matcher(words[0]).matches()) {
                quantities[i] = Integer.parseInt(words[0].replace("x", ""));
                list[i] = "";
                for (int o = 1; o < words.length; o++) {
                    if (o == 1) {
                        list[i] += words[o];
                    } else {
                        list[i] += " " + words[o];
                    }
                }
            } else {
                quantities[i] = 1;
            }
        }

        int priceTotal = 0;
        for (int i = 0; i < list.length; i++) {
            try {
                priceTotal += quantities[i] * getCardPrice(list[i]);
            } catch (IOException e) {
                System.out.println("ERROR: Could not find price for card \"" + list[i] + "\"");
                System.out.println("The card has been excluded from the total calculated price.");
            }
        }
        return priceTotal;
    }

    // Finds the last page of the printings of the card and returns the last price on that page. (That is the lowest price on "cernyrytir.cz".)
    private static int getCardPrice(String card) throws IOException {
        int page = 0;
        String cardName = normalizeCardName(card);
        Document doc = Jsoup.connect(DEFAULT_URL_1 + (page * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
        Elements ele = doc.getElementsByAttributeValue("class", "kusovkytext");
        if (ele.size() == 2) {
            return Integer.parseInt(ele.get(1).getElementsByTag("tr").last().getElementsByTag("td").get(2).text().replace(" Kč", ""));
        } else {
            while (true) {
                doc = Jsoup.connect(DEFAULT_URL_1 + ((page + 1) * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
                if (doc.getElementsByAttributeValue("class", "highslide").size() == 0) {
                    doc = Jsoup.connect(DEFAULT_URL_1 + (page * 30) + DEFAULT_URL_2 + cardName + DEFAULT_URL_3).get();
                    ele = doc.getElementsByAttributeValue("class", "kusovkytext");
                    int eleSize = ele.size();
                    int target = eleSize - ((eleSize - 2) / 2) - 1;
                    return Integer.parseInt(ele.get(target).getElementsByTag("tr").last().getElementsByTag("td").get(2).text().replace(" Kč", ""));
                } else {
                    page++;
                }
            }
        }
    }

    private static String normalizeCardName(String card) {
        card = card.replace(" ", "%20");
        card = card.replace("'", "%B4");
        return card;
    }

}
