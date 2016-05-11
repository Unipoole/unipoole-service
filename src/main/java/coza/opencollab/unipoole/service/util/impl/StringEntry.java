package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.util.StorageEntry;

/**
 *  A entry that work with strings
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class StringEntry extends StorageEntry {
    /**
     * The data in String.
     */
    private String data;

    /**
     *  Set all constructor
     */
    public StringEntry(String name, String relativePath, String data) {
        super(name, relativePath);
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContents() {
        return data.getBytes(Defaults.UTF8);
    }
}
