package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.OntoPuSApplication;
import java.io.*;
import java.lang.ref.Cleaner;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamSource;

/** Source providing input stream to a file that will be automatically deleted */
public class ConsumableInputStreamSource implements InputStreamSource {

    private final CleanableFile cleanableFile;
    private final Cleaner.Cleanable cleanable;
    private boolean consumed = false;

    public ConsumableInputStreamSource(File file) {
        this.cleanableFile = new CleanableFile(file);
        this.cleanable = OntoPuSApplication.CLEANER.register(cleanableFile, cleanableFile);
    }

    @Override
    public synchronized @NonNull InputStream getInputStream() throws IOException {
        if (cleanableFile.isEmpty() || consumed) {
            throw new FileNotFoundException();
        }
        consumed = true;
        return new ConsumableInputStream(cleanableFile, cleanable);
    }

    private static class CleanableFile implements Runnable {
        private final File file;
        private boolean cleaned = false;

        private CleanableFile(File file) {
            Objects.requireNonNull(file);
            this.file = file;
        }

        public boolean isEmpty() {
            return cleaned;
        }

        /** Deletes the file */
        @Override
        public synchronized void run() {
            if (cleaned) {
                return;
            }
            cleaned = true;
            file.delete();
        }
    }

    /** {@link FileInputStream} that automatically deletes the supplied file once closed or disposed. */
    private static class ConsumableInputStream extends FileInputStream {
        /// keeping the strong reference to the file
        @SuppressWarnings({"unused", "FieldCanBeLocal"})
        private final CleanableFile cleanableFile;

        private final Cleaner.Cleanable cleanable;

        public ConsumableInputStream(CleanableFile cleanableFile, Cleaner.Cleanable cleanable)
                throws FileNotFoundException {
            super(cleanableFile.file);
            this.cleanableFile = cleanableFile;
            this.cleanable = cleanable;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.cleanable.clean();
        }
    }
}
