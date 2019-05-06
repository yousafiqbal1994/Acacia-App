package official.com.windowdetailssharingapp.export.pdf;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import official.com.windowdetailssharingapp.core.db.LocalStorage;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.core.models.Property;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:11 AM
 */
public class PdfCreator {

    private static final PdfCreator ourInstance = new PdfCreator();

    // Page dimensions (A4)
    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;

    // Whitespace and margins
    private static final int PAGE_PADDING = 21;
    private static final int LINE_SPACING = 2;
    private static final int PARAGRAPH_SPACING = 24;

    // Font styles
    private static final int IMAGE_WIDTH = 192;
    private static final int TEXT_SIZE = 12;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int LINE_WIDTH = 105;

    // Print cursors
    private final TextPaint paint = new TextPaint();

    // Document and pages
    private int pageNo = 0;
    private PdfDocument document;
    private PdfDocument.Page currentPage;

    private int cursorX = PAGE_PADDING;
    private int cursorY = PAGE_PADDING + TEXT_SIZE;

    private PdfCreator() {
        paint.setColor(TEXT_COLOR);
        paint.setTextSize(TEXT_SIZE);
    }

    public static PdfCreator getInstance() {
        return ourInstance;
    }

    @NonNull
    public PdfDocument createFrom(@NonNull Project project) {
        startNewDocument();

        // Print header
        writeSimpleTextLine("PROJECT SUMMARY");
        writeEmphasizedTextLine(project.getName().toUpperCase());
        addSpacing(3);

        writeSimpleTextLine("JOB #:  " + project.getJobNumber());
        writeSimpleTextLine("PROJECT ADDRESS:  " + project.getProjectAddress());
        addSpacing(2);

        writeEmphasizedTextLine("Builder:");
        writeSimpleTextLine(project.getBuilder());
        addSpacing(1);

        writeEmphasizedTextLine("Site Contact:");
        writeSimpleTextLine(project.getSiteContact());
        addSpacing(1);

        writeEmphasizedTextLine("Comments:");
        writeSimpleTextLine(project.getComments());
        addSpacing(1);

        // Finish opened page
        document.finishPage(currentPage);
        currentPage = null;

        return document;
    }

    @NonNull
    public PdfDocument createFrom(@NonNull Item item) {
        startNewDocument();

        // Print header
        writeEmphasizedTextLine(item.getProject().toUpperCase());
        addSpacing(2);

        writeEmphasizedTextLine("Item ID:  " + item.getId().toUpperCase());
        writeEmphasizedTextLine("Type:  " + item.getType().toUpperCase());
        addSpacing(3);

        // Print d/w properties
        for (int index = 0; index < item.getProperties().size(); index++) {
            Property property = item.getProperties().get(index);
            Bitmap bitmap = LocalStorage.getBitmap(index, item.getId(), item.getProject());
            if (!property.getValue().isEmpty() || bitmap != null) {
                if (!property.getValue().isEmpty()) {
                    writeEmphasizedTextLine(property.getName());
                    writeSimpleTextLine(property.getValue());
                }
                if (bitmap != null) {
                    drawBitmap(bitmap);
                }
                addSpacing(1);
            }
        }

        // Finish opened page
        document.finishPage(currentPage);
        currentPage = null;

        return document;
    }

    private void startNewPage() {
        // Finish current page, if opened
        if (currentPage != null) document.finishPage(currentPage);

        // Start a new page
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, ++pageNo).create();
        currentPage = document.startPage(info);

        // Reset cursor position
        cursorX = PAGE_PADDING;
        cursorY = PAGE_PADDING + TEXT_SIZE;
    }

    private void startNewDocument() {
        document = new PdfDocument();
        pageNo = 0;

        startNewPage();
    }

    private void moveCursor(int cursorX, int cursorY) {
        // Is the current page finished?
        boolean finished = cursorY > PAGE_HEIGHT - PAGE_PADDING;
        if (finished) {
            startNewPage();
        } else {
            this.cursorX = cursorX;
            this.cursorY = cursorY;
        }
    }

    private void writeSimpleTextLine(String text) {
        if (text.length() > LINE_WIDTH) {
            String subtext = text.substring(0, LINE_WIDTH);
            int breakpoint = subtext.lastIndexOf(' ');

            writeSimpleTextLine(text.substring(0, breakpoint));
            writeSimpleTextLine(text.substring(breakpoint + 1));
        } else {
            paint.setFakeBoldText(false);
            currentPage.getCanvas().drawText(text, cursorX, cursorY, paint);
            moveCursor(cursorX, cursorY + TEXT_SIZE + LINE_SPACING);
        }
    }

    private void writeEmphasizedTextLine(String text) {
        if (text.length() > LINE_WIDTH) {
            String subtext = text.substring(0, LINE_WIDTH);
            int breakpoint = subtext.lastIndexOf(' ');

            writeEmphasizedTextLine(text.substring(0, breakpoint));
            writeEmphasizedTextLine(text.substring(breakpoint + 1));
        } else {
            paint.setFakeBoldText(true);
            currentPage.getCanvas().drawText(text, cursorX, cursorY, paint);
            moveCursor(cursorX, cursorY + TEXT_SIZE + LINE_SPACING);
        }
    }

    private void addSpacing(int i) {
        moveCursor(cursorX, cursorY + i * PARAGRAPH_SPACING);
    }

    private void drawBitmap(@NonNull Bitmap bitmap) {
        // Get original bitmap size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Scale down bitmap, preserving aspect ratio
        int finalWidth = IMAGE_WIDTH;
        int finalHeight = (int) (finalWidth * height / (float) width);

        // Create a new page if this bitmap won't fit on current page
        int remainingPage = (PAGE_HEIGHT - PAGE_PADDING) - cursorY;
        if (remainingPage < finalHeight) {
            startNewPage();
        }

        // Determine final bitmap location of page
        Rect box = new Rect(cursorX, cursorY, cursorX + finalWidth, cursorY + finalHeight);
        currentPage.getCanvas().drawBitmap(bitmap, null, box, paint);

        // Move cursor
        cursorY += finalHeight;
    }

}