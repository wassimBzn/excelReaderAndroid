package com.example.neoxam.excel;

import java.util.List;

/**
 * Created by mohamedwassim.bezine on 13/03/2018.
 */

public interface ExcelReaderListener {
    void onReadExcelCompleted(List<String> stringList);

}
