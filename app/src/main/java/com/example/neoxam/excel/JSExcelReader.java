package com.example.neoxam.excel;

import android.util.Base64;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamedwassim.bezine on 13/03/2018.
 */

public class JSExcelReader {
    private ExcelReaderListener callback;

    public JSExcelReader(String filePath, final WebView webView, ExcelReaderListener callback) {
        this.callback = callback;

        File file = new File(filePath);

        try (InputStream is = new FileInputStream(file)) {
            // convert file to Base64
            if (file.length() > Integer.MAX_VALUE)
                Log.e("File too big", "file too big");
            byte[] bytes = new byte[(int) file.length()];

            int offset = 0;
            int numRead;
            while (offset < bytes.length &&
                    (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length)
                throw new Exception("Could not completely read file");

            final String b64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

            // feed the string into webview and get the result
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/AndroidParseExcel.html");
            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    webView.evaluateJavascript("convertFile('" + b64 + "');", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            parseJSON(value);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            Log.e("Convert Excel failure", ex.getMessage());
        }
    }

    private void parseJSON(String jsonString) {
        try {
            // return value is something like "{\n\"Sheet1\":\n[\"title\"...
            // you need to remove those escape character first
            JSONObject jsonRoot = new JSONObject(jsonString.substring(1, jsonString.length() - 1)
                    .replaceAll("\\\\n", "")
                    .replaceAll("\\\\\"", "\"")
                    .replaceAll("\\\\\\\\\"", "'"));
            JSONArray sheet1 = jsonRoot.optJSONArray("Sheet1");
            List<String> stringList = new ArrayList<>();

            JSONObject jsonObject;
            for (int i = 0; i < sheet1.length(); i++) {
                jsonObject = sheet1.getJSONObject(i);

                stringList.add(jsonObject.optString("title"));
            }

            callback.onReadExcelCompleted(stringList);
        } catch (Exception ex) {
            Log.e("Error in parse JSON", ex.getMessage());
        }
    }
}
