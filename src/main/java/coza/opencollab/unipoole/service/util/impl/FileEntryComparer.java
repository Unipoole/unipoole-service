package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.StorageEntryComparer;
import java.util.Arrays;

/**
 * A comparer for files. This class just compare whether files are new
 * of the content was updated but then handle the files as a whole.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class FileEntryComparer extends AbstractEntryComparer implements StorageEntryComparer{

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageEntry diff(StorageEntry entryA, StorageEntry entryB) {
        if(isContentDifferent(entryA, entryB)){
            return entryB;
        }else{
            return null;
        }
    }

    /**
     * Compare the actual contents of the entries.
     * 
     * @return true if it is different, otherwise false.
     */
    private boolean isContentDifferent(StorageEntry entryA, StorageEntry entryB) {
        return !Arrays.equals(entryA.getContents(), entryB.getContents());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageEntry merge(StorageEntry entryA, StorageEntry entryB){
        return entryB;
    }
}
