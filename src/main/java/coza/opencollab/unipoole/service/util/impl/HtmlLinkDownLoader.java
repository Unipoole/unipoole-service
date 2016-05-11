package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.ErrorCodes;
import coza.opencollab.unipoole.service.lms.LMSContentService;
import coza.opencollab.unipoole.service.util.LinkDownLoader;
import coza.opencollab.unipoole.service.util.LinkFileConsumer;
import coza.opencollab.unipoole.service.util.SessionManager;
import coza.opencollab.unipoole.service.util.TagFilter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * A LinkDownLoader for html img tags.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class HtmlLinkDownLoader implements LinkDownLoader {
    //<img src="https://my.unisa.ac.za/access/content/group/chs_sig/images/2779-7372-0-0_1547972.jpg" />
    private static final Logger LOG = Logger.getLogger(HtmlLinkDownLoader.class);
    /**
     * The session manager.
     */
    @Autowired
    private SessionManager sessionManager;
    /**
     * The content service.
     */
    private LMSContentService lmsContentService;
    /**
     * The html cleaner
     */
    private final HtmlCleaner htmlSetup;
    /**
     * The properties for the cleaner.
     */
    private final CleanerProperties htmlProperties;
    /**
     * The html out setdown, hahaha
     */
    private final SimpleHtmlSerializer htmlSetdown;
    /**
     * The xml declaration.
     */
    private static final String XML_ROOT = "<root>{0}</root>";
    /**
     * All the tag names (key) and attribute names (value) that need to be downloaded.
     */
    private Map<String, String> downloadTags;
    /**
     * All the filters for download tags. 
     */
    private Map<String, TagFilter> downloadTagFilters;
    /**
     * A prefix for the link set in the html.
     */
    private String linkPrefix = "";
    /**
     * The base url for lms content.
     */
    private String lmsContentBaseURL;
    
    /**
     * Default constructor setting the html cleaner up.
     */
    public HtmlLinkDownLoader(){
        htmlSetup = new HtmlCleaner();
        htmlProperties = htmlSetup.getProperties();
        htmlProperties.setAdvancedXmlEscape(false);
        htmlProperties.setUseEmptyElementTags(false);
        htmlProperties.setCharset(Defaults.UTF8.displayName());
        htmlProperties.setOmitDoctypeDeclaration(true);
        htmlProperties.setOmitHtmlEnvelope(true);
        htmlProperties.setOmitXmlDeclaration(true);
        htmlProperties.setRecognizeUnicodeChars(false);
        htmlSetdown = new SimpleHtmlSerializer(htmlProperties);
    }

    /**
     * The session manager.
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * The resource service.
     */
    public void setLMSContentService(LMSContentService lmsContentService) {
        this.lmsContentService = lmsContentService;
    }

    /**
     * All the tag names (key) and attribute names (value) that need to be downloaded.
     */
    public void setDownloadTags(Map<String, String> downloadTags) {
        this.downloadTags = downloadTags;
    }

    /**
     * All the filters for download tags. 
     */
    public void setDownloadTagFilters(Map<String, TagFilter> downloadTagFilters) {
        this.downloadTagFilters = downloadTagFilters;
    }

    /**
     * A prefix for the link set in the html.
     */
    public void setLinkPrefix(String linkPrefix) {
        this.linkPrefix = linkPrefix;
    }

    /**
     * The base url for lms content.
     */
    public void setLmsContentBaseURL(String lmsContentBaseURL) {
        this.lmsContentBaseURL = lmsContentBaseURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String update(String key, String content, LinkFileConsumer consumer) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        TagNode root = setupRoot(content);
        for(Map.Entry<String, String> entry: downloadTags.entrySet()){
            convert(key, consumer, root, entry.getKey(), entry.getValue());
        }
        return setdown(root);
    }
    
    /**
     * Convert the content into html.
     */
    private TagNode setupRoot(String content){
        content = MessageFormat.format(XML_ROOT, content);
        return htmlSetup.clean(content);
    }
    
    /**
     * Convert the html back to the content.
     */
    private String setdown(TagNode root){
        StringWriter writer = new StringWriter();
        try {
            htmlSetdown.write(root, writer, Defaults.UTF8.displayName(), true);
        } catch (IOException e) {
            throw new UnipooleException(ErrorCodes.XML_PARSING, "Could not write the xml to string.", e);
        }
        String content = writer.toString();
        return content.substring(6, content.length() - 7);
    }
    
    /**
     * Download and convert linked content.
     */
    private void convert(String key, LinkFileConsumer consumer, TagNode root, String tag, String attribute){
        List<TagNode> elements = root.getElementListByName(tag, true);
        for (TagNode tagNode : elements) {
            TagFilter filter = downloadTagFilters.get(tag);
            if(filter == null || filter.accept(tagNode)){
                String link = tagNode.getAttributeByName(attribute);
                tagNode.addAttribute(attribute, getLinkAndConvert(key, link, consumer));
            }
        }
    }

    /**
     * Download the linked file and add it to the consumer. Then convert the
     * link.
     *
     * @param link The link url.
     * @return Updated link url.
     */
    private String getLinkAndConvert(String key, String link, LinkFileConsumer consumer) {
        String fullname = null;
        byte[] bytes = null;
        try {
            if(link.startsWith(lmsContentBaseURL)){
                String id = link;
                id = id.substring(lmsContentBaseURL.length());
                id = URLDecoder.decode(id, Defaults.UTF8.displayName());
                bytes = lmsContentService.getResourceData(sessionManager.loginAsAdmin(), id);
            }else{
                URL url = new URL(link);
                bytes = FileCopyUtils.copyToByteArray(url.openStream());
            }
            String name =  FilenameUtils.getBaseName(link);
            String nameHash = DigestUtils.md5DigestAsHex(name.getBytes());
            String ext = FilenameUtils.getExtension(link);
            fullname = nameHash;
            if(ext != null && ext.trim().length() != 0){
                fullname = nameHash + "." + ext;
            }
            return linkPrefix + consumer.addFile(fullname, key, link, bytes);
        } catch (Exception e) {
            LOG.warn("Could not get the linked content.\nName: " + fullname + "\nKey: " + key + "\nLink: " + link + "\nBytes: " + (bytes==null? "NULL": "Not NULL") + ")", e);
            return link;
        }
    }
}
