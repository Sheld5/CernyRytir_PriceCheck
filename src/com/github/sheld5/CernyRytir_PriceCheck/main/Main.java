package com.github.sheld5.CernyRytir_PriceCheck.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

public class Main {

    private static final String DEFAULT_URL_1 = "http://cernyrytir.cz/index.php3?akce=3&limit=";
    private static final String DEFAULT_URL_2 = "&searchtype=card&searchname=";
    private static final String DEFAULT_URL_3 = "&hledej_pouze_magic=1&submit=Vyhledej";

    public static void main(String[] args) {

        try {
            System.out.println(getCardPrice("Forest"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
