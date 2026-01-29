package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.OntoPuSApplication;
import java.io.*;
import java.lang.ref.Cleaner;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamSource;

/** Source providing input stream to a file that will be automatically deleted when this object is garbage collected */
public class ConsumableInputStreamSource implements InputStreamSource {
    private static final Logger log = LogManager.getLogger(ConsumableInputStreamSource.class);

    /** Watched reference, when this reference is lost, the {@link #cleanFileAction} will be executed by the cleaner. */
    private final CleanableFile cleanableFile;
    /** The cleanable action registered in the cleaner, executed when the input stream is closed. */
    private final Cleaner.Cleanable cleanable;
    /** The thread safe action that deletes the file (only once) when executed. */
    private final CleanFileAction cleanFileAction;

    private final AtomicBoolean consumed = new AtomicBoolean(false);

    public ConsumableInputStreamSource(File file) {
        this.cleanableFile = new CleanableFile(file);
        this.cleanFileAction = new CleanFileAction(file);
        this.cleanable = OntoPuSApplication.CLEANER.register(cleanableFile, cleanFileAction);
    }

    private record CleanableFile(File file) {}

    @Override
    public @NonNull InputStream getInputStream() throws IOException {
        if (cleanFileAction.wasCleaned()
                || // throws when the file was already cleaned
                !consumed.compareAndSet(false, true)) { // or when this thread was not able to consume
            throw new FileNotFoundException();
        }
        return new ConsumableInputStream(cleanableFile, cleanable);
    }

    /** Thread safe action that deletes the provided file when executed. */
    private static class CleanFileAction implements Runnable {
        private final File file;
        private final AtomicBoolean cleaned = new AtomicBoolean(false);

        private CleanFileAction(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            if (cleaned.compareAndSet(false, true)) {
                try {
                    file.delete();
                } catch (Exception e) {
                    log.warn("Could not clean up file {}", file.getAbsolutePath(), e);
                }
            }
        }

        public boolean wasCleaned() {
            return cleaned.get();
        }
    }

    /** {@link FileInputStream} that automatically deletes the supplied file once closed or disposed. */
    private static class ConsumableInputStream extends FileInputStream {
        /** Strong reference to the cleanable file */
        @SuppressWarnings({"unused", "FieldCanBeLocal"})
        private final CleanableFile cleanableFile;
        /** Registered cleanable task executed once the stream is closed. */
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
