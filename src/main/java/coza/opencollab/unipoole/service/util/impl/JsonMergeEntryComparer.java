package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.StorageEntryComparer;
import coza.opencollab.unipoole.util.JsonParser;

/**
 * A comparer that combines (merge) json files.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class JsonMergeEntryComparer extends AbstractEntryComparer implements StorageEntryComparer {

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageEntry diff(StorageEntry entryA, StorageEntry entryB) {
        String diff = JsonParser.diff(new String(entryA.getContents(), Defaults.UTF8), new String(entryB.getContents(), Defaults.UTF8));
        return new ByteArrayEntry(entryA.getName(), entryA.getRelativeDirectory(), diff.getBytes(Defaults.UTF8));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageEntry merge(StorageEntry entryA, StorageEntry entryB){
        String merged = JsonParser.merge(new String(entryA.getContents(), Defaults.UTF8), new String(entryB.getContents(), Defaults.UTF8));
        return new ByteArrayEntry(entryA.getName(), entryA.getRelativeDirectory(), merged.getBytes(Defaults.UTF8));
    }
}
