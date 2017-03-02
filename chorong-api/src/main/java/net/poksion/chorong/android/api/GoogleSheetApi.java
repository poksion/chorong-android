package net.poksion.chorong.android.api;

import java.util.ArrayList;
import java.util.List;

public interface GoogleSheetApi {
    class Result {
        public boolean validToken = true;
        public boolean paging = false;
        public boolean lastPageHint = false;
        public List< String[] > rows = new ArrayList<>();
    }

    Result getResultByName(String loginToken, String docName, String sheetName, int colCnt, int rowCnt, int pageIdx);
    Result getResultById(String loginToken, String worksheetId, String sheetName, int colCnt, int rowCnt, int pageIdx);
}
