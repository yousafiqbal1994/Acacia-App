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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import official.com.windowdetailssharingapp.core.db.LocalStorage;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.utils.Utils;


/**
 * This is a data-access class for the {@link Project} model class. It
 * provides an interface to create and retrieve projects from storage.
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 1:44 PM
 */
public class ProjectDao {

    private static ProjectDao ourInstance = new ProjectDao();
    private final File saveDir = LocalStorage.getSaveDir();

    private ProjectDao() {

    }

    public static ProjectDao getInstance() {
        return ourInstance;
    }

    /**
     * Build a Project object from metadata file.
     *
     * @param metaFile a json file containing project details
     * @return the project object, or null if creation fails
     */
    @Nullable
    private Project fromFile(@NonNull File metaFile) {
        if (metaFile.exists()) {
            try (JsonReader reader = new JsonReader(new FileReader(metaFile))) {
                return new Gson().fromJson(reader, Project.class);
            } catch (IOException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * Creates a new project. Any existing project with same
     * name is overwritten.
     *
     * @param project the project to create
     * @return new project, never null
     * @throws IOException if project cannot be created
     */
    @NonNull
    public Project add(Project project) throws IOException {
        this.save(project);
        return project;
    }

    /**
     * Creates a new project if it does not already exist.
     *
     * @param project the project to create
     * @return new or existing project, never null
     * @throws IOException if project cannot be created
     */
    @NonNull
    public Project addIfNotExists(Project project) throws IOException {
        Project existingProject = this.get(project.getName());
        if (existingProject == null) {
            project = add(project);
        }

        return project;
    }

    /**
     * Opens an existing project.
     *
     * @param name name of the project to open
     * @return specified project, or null if project does not exist
     */
    @Nullable
    public Project get(String name) {
        File projectDir = new File(saveDir, name.toUpperCase());
        File metaFile = new File(projectDir, "meta.json");
        return this.fromFile(metaFile);
    }

    /**
     * Returns a list of all existing projects, or an empty list
     * if no projects found.
     *
     * @return list of projects, never null
     */
    @NonNull
    public List<Project> getAll() {
        List<String> nameList = getNames();
        List<Project> projects = new ArrayList<>();
        for (String name : nameList) {
            Project project = this.get(name);
            if (project != null) {
                projects.add(project);
            }
        }
        return projects;
    }

    /**
     * Returns a list of names of existing projects, or an empty list
     * if no projects found.
     *
     * @return list of project names, never null
     */
    @NonNull
    public List<String> getNames() {
        String[] nameArray = saveDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return new File(file, name).isDirectory();
            }
        });

        List<String> nameList = new ArrayList<>();
        if (nameArray != null) {
            nameList.addAll(Arrays.asList(nameArray));
        }

        Collections.sort(nameList);
        return nameList;
    }

    /**
     * Writes project data to storage.
     *
     * @param project the project to be saved
     * @throws IOException if project fails to save
     */
    public void save(Project project) throws IOException {
        // Create project directory if it does not exist
        File projectDir = new File(saveDir, project.getName());
        if (!projectDir.exists()) {
            if (!projectDir.mkdirs()) {
                throw new IOException("Failed to create project directory.");
            }
        }

        // Write json to file
        File metaFile = new File(projectDir, "meta.json");
        String json = new Gson().toJson(project);
        Utils.writeToFile(metaFile, json);
    }

    public boolean remove(String project) {
        if (get(project) != null) {
            boolean deleted = false;

            File projectDir = new File(saveDir, project);
            if (projectDir.exists()) {
                String[] entries = projectDir.list();
                for (String s : entries) {
                    File currentFile = new File(projectDir, s);
                    currentFile.delete();
                }
                deleted = projectDir.delete();
            }

            File exportDir = new File(LocalStorage.getExportDir(), project);
            if (exportDir.exists()) {
                String[] entries = exportDir.list();
                for (String s : entries) {
                    File currentFile = new File(exportDir, s);
                    currentFile.delete();
                }
                deleted = exportDir.delete();
            }

            File exportedFile = new File(LocalStorage.getExportDir(), project + ".zip");
            if (exportedFile.exists()) {
                deleted = exportedFile.delete();
            }

            return deleted;
        }

        return false;
    }

}