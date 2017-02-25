package net.poksion.chorong.android.api;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class GoogleSheetApiImpl implements GoogleSheetApi {

    private final String applicationName;

    public GoogleSheetApiImpl(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public Result getValues(String loginToken, String docName, String sheetName, int colCnt, int rowCnt, int pageIdx) {
        try {
            SpreadsheetService service = new SpreadsheetService(applicationName);
            service.setHeader("Authorization", "Bearer " + loginToken);

            URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            WorksheetEntry sheet = null;

            for(SpreadsheetEntry entry : feed.getEntries()){
                if(entry.getTitle().getPlainText().equals(docName)){
                    if (sheetName == null) {
                        sheet = entry.getWorksheets().get(0);
                    } else {
                        for (WorksheetEntry sheetEntry : entry.getWorksheets()) {
                            if (sheetEntry.getTitle().getPlainText().equals(sheetName)) {
                                sheet = sheetEntry;
                                break;
                            }
                        }
                    }
                    break;
                }
            }

            return getValuesFromSheet(service, sheet, colCnt, rowCnt, pageIdx);

        } catch (ServiceException e) {
            e.printStackTrace();
            Result result = new Result();
            result.validToken = false;
            return result;

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Result getValuesFromSheet(SpreadsheetService service, WorksheetEntry worksheet, int colCnt, int rowCnt, int pageIdx) throws URISyntaxException, IOException, ServiceException{
        Result result = new Result();

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
            int totalRowCnt = cellFeed.getEntries().size() / colCnt;
            result.lastPage = ((pageIdx * rowCnt) + rowCnt >= totalRowCnt);
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
                        result.rows.add(cols);
                    }
                } else {
                    result.rows.add(cols);
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
