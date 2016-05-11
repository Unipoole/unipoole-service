package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.util.StorageEntry;
import java.util.Arrays;

/**
 * A entry that work with bytes
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class ByteArrayEntry extends StorageEntry{
    /**
     * The data in bytes.
     */
    private final byte[] bytes;
    
    /**
     * Set all constructor
     */
    public ByteArrayEntry(String name, String relativeDirectory, byte[] bytes){
        super(name, relativeDirectory);
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContents() {
        return bytes;
    }
}
