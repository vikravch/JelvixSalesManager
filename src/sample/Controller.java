package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sample.utils.Const;

import java.io.*;
import java.util.ArrayList;

public class Controller {

    @FXML
    private Label countLabel ;

    @FXML
    private void increment() {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://example.com/").get();
            String title = doc.title();
            countLabel.setText("Title: "+title);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void connectToFile() {
        readFile();
    }

    private void readFile() {
        BufferedReader br = null;
        FileReader fr = null;
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(Const.PATH_TO_FILE);
            br = new BufferedReader(fr);

            fw = new FileWriter(Const.PATH_TO_RES_FILE);
            bw = new BufferedWriter(fw);

            String sCurrentLine;
            sCurrentLine = br.readLine();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] arrRes = sCurrentLine.split(",");
                String urlToPage = arrRes[Const.URL_TO_PAGE_POSITION];
                ArrayList<String> res = connectToPage("http://"+urlToPage, arrRes);
                for (int i = 0; i < res.size(); i++) {
                    bw.write(res.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private ArrayList<String> connectToPage(String urlToPage, String[] arrRes) {
        Document doc = null;
        ArrayList<String> res = new ArrayList<>();

        int errorCounter = 0;
        try {
            doc = Jsoup.connect(urlToPage).get();
            Elements members = doc.select(".company-team__member");
            String nameOfCompany = arrRes[Const.NAME_POSITION];
            String companyUrl = arrRes[Const.COMPANY_URL_POSITION];
            String city = arrRes[Const.CITY_POSITION];
            String foundingAndProductStage = "founding stage - "+arrRes[Const.FUNDING_STAGE_POSITION]+" product stage - "+arrRes[Const.PRODUCT_STAGE_POSITION];
            System.out.println("--------------------"+nameOfCompany+"-------------------------------------------------------------");

            for (Element man:members){
                String name = man.select("div.company-team__name").first().text();
                String position = man.select("div.company-team__position").first().text().replace(";"," ");
                position = position.replace("&"," ");
                position = position.replace(","," ");
                res.add(name+";"+
                        position+";"+
                        city+";"+
                        nameOfCompany+";"+
                        companyUrl+";"+
                        urlToPage+";"+
                        foundingAndProductStage+"\n");

                System.out.println(name+";"+
                        position+";"+
                        city+";"+
                        nameOfCompany+";"+
                        companyUrl+";"+
                        urlToPage+";"+
                        foundingAndProductStage);
            }

        } catch (Exception e) {
            System.out.println("Error happened on page - "+urlToPage);
            errorCounter++;
        }
        countLabel.setText("Completed with "+errorCounter+" error");
        return res;
    }
}
