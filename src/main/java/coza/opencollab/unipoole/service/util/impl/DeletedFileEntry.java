package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.util.StorageEntry;
import java.io.File;
import java.io.FileFilter;

/**
 * This represents a file that was deleted
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DeletedFileEntry extends StorageEntry {
    private static final String DELETE_MARKER = "DEL_";

    /**
     * Return a file filter that filter for the marker files.
     */
    public static FileFilter getFileFilter() {
        return new FileFilter(){

            /**
             * Accept only files or directories that are markers for deleted content.
             */
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(DELETE_MARKER);
            }
        };
    }

    /**
     * Returns the absolute file of the file/directory that was deleted.
     */
    public static File getRealFile(File deletedFile) {
        return new File(deletedFile.getParentFile(), deletedFile.getName().substring(DELETE_MARKER.length()));
    }
    
    /**
     * Constructor setting the deleted file or directory name.
     */
    public DeletedFileEntry(String name, String directory, boolean isDirectory){
        super(DELETE_MARKER + name, directory, isDirectory);
    }
    
    /**
     * Always return null.
     * {@inheritDoc}
     */
    @Override
    public byte[] getContents(){
        return isDirectory()?null:"".getBytes(Defaults.UTF8);
    }
}
