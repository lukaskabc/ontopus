package cz.lukaskabc.ontology.ontopus.api.model;

import java.io.File;

/**
 * A file submitted in a form.
 *
 * <p>The file can either be cached on the server from a previous submission ({@link Type#SERVER}) or a newly uploaded
 * file ({@link Type#UPLOAD}) in both cases, the file is copied into the {@link ImportProcessContext#tempFolder} and the
 * {@link #file} points to the created copy.
 */
public class ReusableFile {
    /** The origin of the file */
    private final Type type;
    /** The name of the field in the form where the file was selected. */
    private final String formFieldName;
    /**
     * The original file name including the file type extension.
     *
     * <p>If the file was submitted as part of a folder submission, the name will also include the relative path.
     */
    private final String fileName;
    /** The file located in the temporary folder of the current import context. */
    private final File file;

    public ReusableFile(Type type, String formFieldName, String fileName, File file) {
        this.type = type;
        this.formFieldName = formFieldName;
        this.fileName = fileName;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public Type getType() {
        return type;
    }

    /** The type of the {@link ReusableFile} */
    public enum Type {
        /** The file is cached on the server from a previous submission. */
        SERVER,
        /** The file was uploaded with the form result. */
        UPLOAD
    }
}
