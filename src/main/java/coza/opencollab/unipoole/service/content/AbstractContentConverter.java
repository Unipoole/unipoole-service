package coza.opencollab.unipoole.service.content;

import coza.opencollab.unipoole.service.util.StorageEntry;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Just a abstract implementation of the content converter
 * so that we can have default implementations.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public abstract class AbstractContentConverter<T, U> implements ContentConverter<T, U>{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convertUserData(String moduleId, U data) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasExtraResources(String moduleId) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, File> getExtraResources(String moduleId) {
        return null;
    }
}
