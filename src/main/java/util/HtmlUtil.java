package main.java.util;

public class HtmlUtil {
    /*
     * Add beacon for use the html syntax
     */
    public static String createHTMLString(String... message) {
        StringBuilder stringB = new StringBuilder();
        stringB.append("<html><body style='color:white;'>\n");
        for (String string : message) {
            stringB.append(string);
        }
        stringB.append("</body></html>");
        return stringB.toString();
    }
}
