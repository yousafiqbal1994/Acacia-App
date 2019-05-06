package official.com.windowdetailssharingapp.core.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import official.com.windowdetailssharingapp.core.db.LocalStorage;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.core.models.Recipient;
import official.com.windowdetailssharingapp.utils.Utils;


/**
 * This is a data-access class for the {@link Project} model class. It
 * provides an interface to create and retrieve projects from storage.
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 1:44 PM
 */
public class RecipientsDao {

    private static RecipientsDao ourInstance = new RecipientsDao();
    private final File saveDir = LocalStorage.getSaveDir();

    private final String PREFIX = "recipient_";
    private final String FILETYPE = ".json";

    private RecipientsDao() {

    }

    public static RecipientsDao getInstance() {
        return ourInstance;
    }

    /**
     * Build a Recipient object from metadata file.
     *
     * @param metaFile a json file containing recipient details
     * @return the recipient object, or null if creation fails
     */
    @Nullable
    private Recipient fromFile(@NonNull File metaFile) {
        if (metaFile.exists()) {
            try (JsonReader reader = new JsonReader(new FileReader(metaFile))) {
                return new Gson().fromJson(reader, Recipient.class);
            } catch (IOException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * Creates a new recipient. Any existing recipient with same
     * email is overwritten.
     *
     * @param recipient the recipient to create
     * @return new recipient, never null
     * @throws IOException if recipient cannot be created
     */
    @NonNull
    public Recipient add(Recipient recipient) throws IOException {
        this.save(recipient);
        return recipient;
    }

    /**
     * Creates a new recipient if it does not already exist.
     *
     * @param recipient the recipient to create
     * @return new or existing recipient, never null
     * @throws IOException if recipient cannot be created
     */
    @NonNull
    public Recipient addIfNotExists(Recipient recipient) throws IOException {
        Recipient existingRecipient = this.get(recipient.getEmail());
        if (existingRecipient == null) {
            recipient = add(recipient);
        }

        return recipient;
    }

    /**
     * Opens an existing recipient.
     *
     * @param email email of the recipient to open
     * @return specified recipient, or null if recipient does not exist
     */
    @Nullable
    public Recipient get(String email) {
        File projectDir = new File(saveDir, PREFIX + email.toLowerCase() + FILETYPE);
        return this.fromFile(projectDir);
    }

    /**
     * Returns a list of all existing recipients, or an empty list
     * if no recipients found.
     *
     * @return list of recipients, never null
     */
    @NonNull
    public List<Recipient> getAll() {
        List<String> emailList = getEmails();
        List<Recipient> recipients = new ArrayList<>();
        for (String email : emailList) {
            Recipient recipient = this.get(email);
            if (recipient != null) {
                recipients.add(recipient);
            }
        }
        return recipients;
    }

    /**
     * Returns a list of emails of existing recipients, or an empty list
     * if no emails found.
     *
     * @return list of project names, never null
     */
    @NonNull
    public List<String> getEmails() {
        String[] filesList = saveDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.startsWith(PREFIX);
            }
        });

        List<String> emailList = new ArrayList<>();
        if (filesList != null) {
            for (String filename : filesList) {
                emailList.add(filename.split(PREFIX)[1].split(FILETYPE)[0]);
            }
        }

        Collections.sort(emailList);
        return emailList;
    }

    /**
     * Writes recipient data to storage.
     *
     * @param recipient the recipient to be saved
     * @throws IOException if recipient fails to save
     */
    public void save(Recipient recipient) throws IOException {
        // Create save directory if it does not exist
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new IOException("Failed to create project directory.");
            }
        }

        // Write json to file
        File recipientFile = new File(saveDir, PREFIX + recipient.getEmail().toLowerCase() + FILETYPE);
        String json = new Gson().toJson(recipient);
        Utils.writeToFile(recipientFile, json);
    }

    public boolean remove(Recipient recipient) {
        File recipientFile = new File(saveDir, PREFIX + recipient.getEmail().toLowerCase() + FILETYPE);
        if (recipientFile.exists()) {
            return recipientFile.delete();
        }
        return false;
    }

}