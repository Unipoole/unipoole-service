package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.service.util.LinkDownLoader;
import coza.opencollab.unipoole.service.util.LinkFileConsumer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class inspects html and download referenced files.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class MultiLinkDownLoader implements LinkDownLoader{
    /**
     * A list of LinkDownloaders to use.
     */
    private List<LinkDownLoader> linkDownloaders = new ArrayList<LinkDownLoader>();

    /**
     * A list of LinkDownloaders to use.
     */
    public void setLinkDownloaders(List<LinkDownLoader> linkDownloaders) {
        this.linkDownloaders = linkDownloaders;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String update(String key, String content, LinkFileConsumer consumer) {
        for(LinkDownLoader linkDownloader: linkDownloaders){
            content = linkDownloader.update(key, content, consumer);
        }
        return content;
    }
}
