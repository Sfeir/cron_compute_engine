package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.sfeir.googleapi.CommandLineServiceProvider;

public class SpreadsheetClient {

    private final static String SPREADSHEET_SCOPE ="https://spreadsheets.google.com/feeds";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static void main(String[] args) throws IOException {

        new SpreadsheetClient().run();


    }

    private void run() throws IOException {

        CommandLineServiceProvider provider = new CommandLineServiceProvider("sfeirjenkins");


        String url ="https://spreadsheets.google.com/feeds/worksheets/0AguSyh6NQKAHdDFBZ0tqUlg0T19PSWRmXy12Zlh5N3c/private/basic";
        //String url ="https://spreadsheets.google.com/feeds/spreadsheets/private/full";

        HttpRequestFactory spreadsheetHttpRequestFactory = provider.getSpreadsheetHttpRequestFactory();

        HttpRequest request = spreadsheetHttpRequestFactory.buildGetRequest(new GenericUrl(url));
        InputStream contentIS = getResponseInputStream(request);

        System.out.printf(getStringFromInputStream(contentIS));
    }



    private InputStream getResponseInputStream(HttpRequest request) throws IOException {
        HttpResponse response = request.execute();
        InputStream content = response.getContent();
//      log("CONTENT _" + content + "_");
        return content;
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
