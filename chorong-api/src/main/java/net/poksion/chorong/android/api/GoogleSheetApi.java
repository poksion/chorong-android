package net.poksion.chorong.android.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface GoogleSheetApi {
    class Result extends ApiResult<List<String[]>> {
        public Result() {
            data = new ArrayList<>();
        }

        public boolean paging = false;
        public boolean lastPageHint = false;
    }

    Result getSheetByName(
            @NonNull String loginToken,
            @NonNull String docName,
            @Nullable String sheetName,
            int colCnt,
            int rowCnt,
            int pageIdx);

    Result getSheetById(
            @Nullable String loginToken,
            @NonNull String worksheetId,
            @Nullable String sheetName,
            int colCnt,
            int rowCnt,
            int pageIdx);
}
