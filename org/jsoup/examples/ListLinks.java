package org.jsoup.examples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Example program to list links from a URL.
 */
public class ListLinks {
    public static ArrayList<String>  run(String url)  {
        ArrayList<String> stringList = new ArrayList<String>();
try{
 //       print("Fetching %s...", url);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            stringList.add(link.attr("abs:href"));
//            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        } 
} catch (IOException ioe){};
return stringList;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}
