package net.poksion.chorong.android.api;

import java.util.ArrayList;
import java.util.List;

public interface GoogleSheetApi {
    class Result extends ApiResult<List<String[]>> {
        Result() {
            data = new ArrayList<>();
        }

        public boolean paging = false;
        public boolean lastPageHint = false;
    }

    Result getResultByName(String loginToken, String docName, String sheetName, int colCnt, int rowCnt, int pageIdx);
    Result getResultById(String loginToken, String worksheetId, String sheetName, int colCnt, int rowCnt, int pageIdx);
}
