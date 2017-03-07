package net.poksion.chorong.android.api;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import net.poksion.chorong.android.annotation.NonNull;
import net.poksion.chorong.android.annotation.Nullable;

public class GoogleSheetApiImpl extends ApiTemplate implements GoogleSheetApi {

    private final SpreadsheetService service;

    public GoogleSheetApiImpl() {
        this(null);
    }

    public GoogleSheetApiImpl(@Nullable String applicationName) {
        service = createService(SpreadsheetService.class, applicationName);
    }

    @Override
    public Result getSheetByName(
        @NonNull String loginToken,
        @NonNull String docName,
        @Nullable String sheetName,
        int colCnt,
        int rowCnt,
        int pageIdx) {

        return getSheetBy(loginToken, docName, null, sheetName, colCnt, rowCnt, pageIdx);
    }

    @Override
    public Result getSheetById(
        @Nullable String loginToken,
        @NonNull String worksheetId,
        @Nullable String sheetName,
        int colCnt,
        int rowCnt,
        int pageIdx) {

        return getSheetBy(loginToken, null, worksheetId, sheetName, colCnt, rowCnt, pageIdx);
    }

    private Result getSheetBy(
            final String loginToken,
            final String docName,
            final String docId,
            final String sheetName,
            final int colCnt,
            final int rowCnt,
            final int pageIdx) {

        Command<Result> command = new Command<Result>() {
            @Override
            public Result onTry() throws ServiceException, URISyntaxException, IOException {
                setBearerToken(service, loginToken);
                List<WorksheetEntry> doc = docName != null? getDocByName(docName) : getDocById(loginToken, docId);
                WorksheetEntry sheet = getSheetByName(doc, sheetName);
                return getValuesFromSheet(sheet, colCnt, rowCnt, pageIdx);
            }

            @Override
            public Result getEmptyResult() {
                return new Result();
            }
        };

        return invoke(command);
    }

    private List<WorksheetEntry> getDocByName(String docName) throws IOException, ServiceException {
        URL spreadsheetFeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        SpreadsheetFeed feed = service.getFeed(spreadsheetFeedUrl, SpreadsheetFeed.class);

        if (feed == null) {
            return null;
        }

        for(SpreadsheetEntry entry : feed.getEntries()) {
            if(entry.getTitle().getPlainText().equals(docName)) {
                return entry.getWorksheets();
            }
        }

        return null;
    }

    private List<WorksheetEntry> getDocById(String loginToken, String worksheetId) throws IOException, ServiceException {
        final String accessor = (loginToken != null && loginToken.length() > 0) ? "/private/full" : "/public/full";
        URL worksheetFeedUrl = new URL("https://spreadsheets.google.com/feeds/worksheets/" + worksheetId + accessor);
        WorksheetFeed feed = service.getFeed(worksheetFeedUrl, WorksheetFeed.class);

        if (feed == null) {
            return null;
        }

        return feed.getEntries();
    }

    private WorksheetEntry getSheetByName(List<WorksheetEntry> doc, String sheetName) {
        if (doc == null || doc.isEmpty()) {
            return null;
        }

        if (sheetName == null || sheetName.length() == 0) {
            return doc.get(0);
        }

        for (WorksheetEntry sheetEntry : doc) {
            if (sheetEntry.getTitle().getPlainText().equals(sheetName)) {
                return sheetEntry;
            }
        }

        return null;
    }

    private Result getValuesFromSheet(WorksheetEntry worksheet, int colCnt, int rowCnt, int pageIdx) throws URISyntaxException, IOException, ServiceException{
        Result result = new Result();

        if (worksheet == null) {
            return result;
        }

        String query = worksheet.getCellFeedUrl().toString() + "?min-col=1&max-col=" + colCnt;
        if (rowCnt > 0 && pageIdx >= 0 ) {
            int minRow = (pageIdx * rowCnt) + 1 + 1; // + header
            int maxRow = minRow + rowCnt - 1; // inclusive
            query += ("&min-row=" + minRow + "&max-row=" + maxRow);

            result.paging = true;
        }

        URL cellFeedUrl = new URL(query);
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        if (result.paging) {
            int currentRowCnt = cellFeed.getEntries().size() / colCnt;
            if (currentRowCnt < rowCnt) {
                result.lastPageHint = true;
            }
        }

        int colIdx = 1;
        String[] cols = null;
        boolean headPassed = false;
        for (CellEntry cell : cellFeed.getEntries()) {

            int col = colIdx % colCnt;

            if(col == 1 || cols == null) { // first column
                cols = new String[colCnt];
            }

            if (col == 0) { // last column
                cols[colCnt-1] = cell.getPlainTextContent();
                if (!headPassed) {
                    headPassed = true;
                    if (result.paging) {
                        result.data.add(cols);
                    }
                } else {
                    result.data.add(cols);
                }
                cols = null;
            } else {
                cols[col-1] = cell.getPlainTextContent();
            }

            colIdx++;
        }

        return result;
    }
}
