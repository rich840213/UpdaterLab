package com.jk.java;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Updater extends JFrame {

    private JLabel[] jLabels = new JLabel[4];

    public Updater() {
        super("更新狂人");
        init();

        String[] url = new String[]{"https://www.softwareok.com/?Download=Q-Dir",
                "https://winscp.net/eng/download.php",
                "https://www.2brightsparks.com/download-syncbackfree.html",
                "https://notepad-plus-plus.org/downloads/"};

        UpdaterHttp updaterHttp = new UpdaterHttp(url);
        updaterHttp.addDataListener(new IDataListener() {
            public void getAppDatas(final LinkedHashMap<String, LinkedHashMap<String, String>> datas) {
                int index = 0;
                Set set = datas.entrySet();
                Iterator i = set.iterator();

                while (i.hasNext()) {
                    final Map.Entry map = (Map.Entry) i.next();
                    jLabels[index].setText("   " + map.getKey().toString() + "          最新版本: " + datas.get(map.getKey()).get("version"));
                    jLabels[index].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                String u = datas.get(map.getKey()).get("link");
                                open(new URI(u));
                            } catch (URISyntaxException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    index++;
                }
            }
        });
    }

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) { /* TODO: error handling */ }
        } else { /* TODO: error handling */ }
    }

    private void init() {
        setLayout(new GridLayout(4, 1));
        setSize(640, 480);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        for (int i = 0; i < jLabels.length; i++) {
            jLabels[i] = new JLabel(" 載入中...");
            jLabels[i].setFont(new Font("Default", Font.BOLD, 24));
            add(jLabels[i]);
        }
    }

    public static void main(String[] args) {
        new Updater();
    }
}

class UpdaterHttp {

    HttpClient httpClient;
    private LinkedHashMap<String, LinkedHashMap<String, String>> appDatas = new LinkedHashMap<String, LinkedHashMap<String, String>>();

    UpdaterHttp(String[] url) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        httpClient = new HttpClient(client, url) {

            public void parseHtmlText(Response response) {
                try {
                    LinkedHashMap<String, String> appData = new LinkedHashMap<String, String>();

                    String html = response.body().string();
                    Document doc = Jsoup.parse(html);
                    String title = doc.title();
                    String[] titles = title.split(" ");

                    if (title.contains("Q-Dir")) {
                        Elements elements = doc.select("#XXXX > tbody > tr > td > table:nth-child(1) > tbody > tr:nth-child(5) > td > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(4) > td:nth-child(1) > a");

                        appData.put("version", titles[1]);
                        appData.put("link", elements.attr("href"));
                        appDatas.put(titles[0], appData);
                    } else if (title.contains("WinSCP")) {
                        Elements link = doc.select("#pageDownload > main > section.gradient-bg-reverse.download-info > div > ul > li:nth-child(1) > a");

                        appData.put("version", link.text().trim().split(" ")[2]);
                        appData.put("link", "https://winscp.net/" + link.attr("href"));
                        appDatas.put(titles[0], appData);
                    } else if (title.contains("SyncBackFree")) {
                        Elements ver = doc.select("#boxedWrapper > div.container > div:nth-child(2) > div > div.span7 > h3 > strong");

                        appData.put("version", ver.text().split(" ")[2].substring(1));
                        appData.put("link", "https://www.2brightsparks.com/assets/software/SyncBack_Setup.exe");
                        appDatas.put(titles[6], appData);
                    } else if (title.contains("Notepad++")) {
                        Elements href = doc.select("#main > ul > li:nth-child(1) > h2 > a");
                        String ver = href.attr("href").split("/")[4].substring(1);

                        appData.put("version", ver);
                        appData.put("link", "https://github.com/notepad-plus-plus/notepad-plus-plus/releases/download/v" + ver + "/npp." + ver + ".Installer.x64.exe");
                        appDatas.put(titles[2], appData);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
    }

    public void addDataListener(IDataListener listener) {
        listener.getAppDatas(appDatas);
    }
}
