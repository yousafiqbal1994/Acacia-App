package official.com.windowdetailssharingapp.export;

import android.graphics.pdf.PdfDocument;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import official.com.windowdetailssharingapp.core.db.LocalStorage;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.export.pdf.PdfCreator;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:11 AM
 */
public class ExportWizard {

    /**
     * Export a project by generating PDFs for all items in a project,
     * and compress them into a zip archive.
     *
     * @param project the project to export
     * @return project name, or null if export failed
     */
    @Nullable
    public static String exportProject(Project project) {
        // Delete existing exports
        File projectDir = new File(LocalStorage.getExportDir(), project.getName());
        if (projectDir.exists()) {
            String[] entries = projectDir.list();
            for (String s : entries) {
                File currentFile = new File(projectDir, s);
                currentFile.delete();
            }
            projectDir.delete();
        }
        new File(LocalStorage.getExportDir(), project.getName() + ".zip").delete();

        // Export all d/w items in the project
        List<String> paths = new ArrayList<>();
        for (Item item : project.getItemList()) {
            String path = exportItem(item);
            if (path != null) {
                paths.add(path);
            }
        }

        // Export project summary
        paths.add(exportSummary(project));

        // Cannot archive if no documents generated
        if (paths.isEmpty()) return null;

        // Create a zip archive
        return createZip(paths, project.getName()) ? project.getName() : null;
    }

    private static String exportSummary(Project project) {
        // Generate PDF document
        PdfDocument document = PdfCreator.getInstance().createFrom(project);

        // Save PDF to file
        return exportPdf(document, project.getName(), "Summary");
    }

    /**
     * Generate a pdf document from a d/w item and save it in
     * a file.
     *
     * @param item the item to which this document belongs
     * @return path of saved file, or null if file not saved
     */
    @Nullable
    public static String exportItem(@NonNull Item item) {
        // Generate PDF document
        PdfDocument document = PdfCreator.getInstance().createFrom(item);

        // Save PDF to file
        return exportPdf(document, item.getProject(), item.getId());
    }

    /**
     * Save a pdf document in a file.
     *
     * @param document    the document to save
     * @param projectName the project to which this item belongs
     * @param filename    name of the saved file
     * @return path of saved file, or null if file not saved
     */
    @Nullable
    private static String exportPdf(@NonNull PdfDocument document, String projectName, String filename) {
        try {
            File projectDir = new File(LocalStorage.getExportDir(), projectName);
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File pdfFile = new File(projectDir, filename + ".pdf");
            if (pdfFile.exists()) pdfFile.delete();

            OutputStream stream = new FileOutputStream(pdfFile);
            document.writeTo(stream);

            document.close();
            return pdfFile.getAbsolutePath();

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Compresses multiple files into a single zip file.
     *
     * @param files   list of files to archive
     * @param project name of the zip file
     * @return true of zipped successfully, else false
     */
    private static boolean createZip(List<String> files, String project) {
        try {
            File zipFile = new File(LocalStorage.getExportDir(), project + ".zip");
            if (zipFile.exists()) {
                if (!zipFile.delete()) throw new IOException();
            }

            if (!zipFile.createNewFile()) throw new IOException();

            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            int BUFFER = 2048;
            byte data[] = new byte[BUFFER];
            BufferedInputStream origin;
            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = new FileInputStream(files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}