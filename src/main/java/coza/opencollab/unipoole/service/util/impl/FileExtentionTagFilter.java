package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.util.TagFilter;
import java.util.List;
import org.htmlcleaner.TagNode;

/**
 * A tag filter that looks at the links in attributes and filter on a list of
 * extention names.
 *
 * Links with no extention will not be downloaded.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class FileExtentionTagFilter implements TagFilter {

    /**
     * The attribute to look at.
     */
    private String attribute;
    /**
     * The valid extentions.
     */
    private List<String> extentions;

    /**
     * The attribute to look at.
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * The valid extentions.
     */
    public void setExtentions(List<String> extentions) {
        this.extentions = extentions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(TagNode node) {
        String extention = null;
        String link = node.getAttributeByName(attribute);
        int index = link.lastIndexOf(".");
        if (index > 0) {
            extention = link.substring(0, index+1);
        }
        return extentions.contains(extention);
    }

}
