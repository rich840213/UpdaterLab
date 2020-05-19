package com.jk.java;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Updater extends JFrame implements IDataListener {

    private final JLabel[] jLabels;
    private JProgressBar progressBar;
    static String[] url;

    public Updater() {
        super("更新狂人");
        url = new String[]{"https://www.softwareok.com/?seite=Freeware/Q-Dir",
                "https://winscp.net/eng/download.php",
                "https://www.2brightsparks.com/download-syncbackfree.html",
                "https://notepad-plus-plus.org/downloads/",
                "https://git-scm.com/downloads",
                "https://potplayer.daum.net/",
                "http://www.wisecleaner.com/wise-folder-hider-free.html"};
        jLabels = new JLabel[url.length];
        init(url.length);

        new UpdaterHttp(url, this);
    }

    @Override
    public void getAppData(LinkedHashMap<String, LinkedHashMap<String, String>> datas) {
        DecimalFormat df = new DecimalFormat("#");
        double pc = datas.size() / ((float) url.length) * 100;
        progressBar.setValue(Integer.parseInt(df.format(pc)));

        if (datas.size() != url.length) return;
        Border border = BorderFactory.createTitledBorder("Loading Complete");
        progressBar.setBorder(border);

        int index = 0;
        for (String key : datas.keySet()) {
            jLabels[index].setText("   " + key + "          最新版本: " + datas.get(key).get("version"));
            jLabels[index].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        String u = datas.get(key).get("link");
                        open(new URI(u));
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            index++;
        }
    }

    private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                System.out.println("open");
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) { /* TODO: error handling */ }
        }
    }

    private void init(int rowsLen) {
        setLayout(new GridLayout(rowsLen + 1, 1));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Loading...");
        progressBar.setBorder(border);
        add(progressBar);

        for (int i = 0; i < jLabels.length; i++) {
            jLabels[i] = new JLabel();
            jLabels[i].setFont(new Font("Default", Font.BOLD, 24));
            add(jLabels[i]);
        }

        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Updater();
    }
}

class UpdaterHttp {

    HttpClient httpClient;
    private final LinkedHashMap<String, LinkedHashMap<String, String>> appDatas = new LinkedHashMap<>();

    UpdaterHttp(String[] url, IDataListener listener) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        httpClient = new HttpClient(client, url, listener) {

            public void parseHtmlText(Response response) {
                try {
                    LinkedHashMap<String, String> appData = new LinkedHashMap<>();

                    String html = Objects.requireNonNull(response.body()).string();
                    Document doc = Jsoup.parse(html);
                    String title = doc.title();
                    String[] titles = title.split(" ");

                    if (title.contains("Q-Dir")) {
                        appData.put("version", titles[1]);
                        appData.put("link", "https://www.softwareok.com/Download/Q-Dir_Installer_x64.zip");
                        appDatas.put(titles[0], appData);
                    } else if (title.contains("WinSCP")) {
                        Elements link = doc.select("#pageDownload > main > section.gradient-bg-reverse.download-info > div > ul > li:nth-child(1) > a");

                        appData.put("version", link.text().trim().split(" ")[2]);
                        appData.put("link", "https://winscp.net/" + link.attr("href"));
                        appDatas.put(titles[0], appData);
                    } else if (title.contains("SyncBackFree")) {
                        try {
                            Elements ver = doc.select("#boxedWrapper > div.container > div:nth-child(2) > div.span7 > h3 > strong");
                            appData.put("version", ver.text().split(" ")[2].substring(1));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Elements ver = doc.select("#boxedWrapper > div.container > div.row-fluid.hidden-phone > div.span7 > h3 > strong");
                            appData.put("version", ver.text().split(" ")[2].substring(1));
                        }
                        appData.put("link", "https://www.2brightsparks.com/assets/software/SyncBack_Setup.exe");
                        appDatas.put(titles[6], appData);
                    } else if (title.contains("Notepad++")) {
                        Elements href = doc.select("#main > ul > li:nth-child(1) > h2 > a");
                        String ver = href.attr("href").split("/")[4].substring(1);

                        appData.put("version", ver);
                        appData.put("link", "https://github.com/notepad-plus-plus/notepad-plus-plus/releases/download/v" + ver + "/npp." + ver + ".Installer.x64.exe");
                        appDatas.put(titles[2], appData);
                    } else if (title.contains("Git")) {
                        Elements ver = doc.select("#main > div.two-column > div.column-right > div > span.version");
                        String verText = ver.text().trim();

                        appData.put("version", verText);
                        appData.put("link", "https://github.com/git-for-windows/git/releases/download/v" + verText + ".windows.1/Git-" + verText + "-64-bit.exe");
                        appDatas.put(titles[0], appData);
                    } else if (title.contains("Potplayer")) {
                        Elements ver = doc.select("#vertical-horizontal-scrollbar-demo > div.viewport > div > div.update_version.fst > strong");
                        String verText = ver.text().trim();

                        appData.put("version", verText);
                        appData.put("link", "https://t1.daumcdn.net/potplayer/PotPlayer/Version/Latest/PotPlayerSetup64.exe");
                        appDatas.put(titles[1], appData);
                    } else if (title.contains("Wise Folder Hider")) {
                        Elements link = doc.select("#free-download");
                        Elements ver = doc.select("#banner-free > p");
                        String verText = ver.text().trim().split(" ")[1];

                        appData.put("version", verText);
                        appData.put("link", link.attr("href"));
                        appDatas.put("Wise Folder Hider", appData);
                    }

                    listener.getAppData(appDatas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
