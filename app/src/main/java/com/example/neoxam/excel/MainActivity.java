package com.example.neoxam.excel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity implements ExcelReaderListener{



    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("message")
                .setTitle("title")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        new FileChooser(MainActivity.this, new String[]{"xls", "xlsx"})
                                .setFileListener(new FileChooser.FileSelectedListener() {
                                    @Override
                                    public void fileSelected(File file) {
                                        progressDialog = new ProgressDialog(MainActivity.this);
                                        progressDialog.setTitle("title");
                                        progressDialog.setMessage("message");
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setCanceledOnTouchOutside(false);

                                        Toast.makeText(MainActivity.this, file.getName(), Toast.LENGTH_SHORT).show();
                                        String filePath = file.getAbsolutePath();
                                        ExcelReaderListener excelReaderListener = (ExcelReaderListener) MainActivity.this;

                                        progressDialog.show();
                                        try {
                                            final WebView webView = new WebView(MainActivity.this);
                                            new JSExcelReader(filePath, webView, excelReaderListener);
                                        } catch (Exception ex) {
                                            Log.e("Import excel error", ex.getMessage());
                                        }
                                    }
                                })
                                .showDialog();
                    }
                })
                .show();
    }
    @Override
    public void onReadExcelCompleted(List<String> stringList) {
        Toast.makeText(MainActivity.this, "Parse Completed", Toast.LENGTH_SHORT).show();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Write into DB

    }

}
