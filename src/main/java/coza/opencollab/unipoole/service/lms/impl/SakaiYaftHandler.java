package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.YaftService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.createDocument;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.getAttributeAsDate;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.getElements;
import coza.opencollab.unipoole.shared.Attachment;
import coza.opencollab.unipoole.shared.Discussion;
import coza.opencollab.unipoole.shared.Forum;
import coza.opencollab.unipoole.shared.Message;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author OpenCollab
 */
public class SakaiYaftHandler implements ToolContentService {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SakaiYaftHandler.class);
    /**
     * The value of the the String which determines whether this entry is a
     * Unipoole generated entry e.g. UNIPOOLE_ Value is injected by Spring find
     * it in WEB-INF/config/lms-services.xml
     */
    private String userGeneratedPlaceHolder;

    public String getUserGeneratedPlaceHolder() {
        return this.userGeneratedPlaceHolder;
    }

    public void setUserGeneratedPlaceHolder(String userGeneratedPlaceHolder) {
        this.userGeneratedPlaceHolder = userGeneratedPlaceHolder;
    }
    /**
     * The value of the the String which determines what JSON key is used to
     * access the yaft data e.g. content Value is injected by Spring find it in
     * WEB-INF/config/lms-services.xml
     */
    private String jsonContentKey;

    public String getJsonContentKey() {
        return this.jsonContentKey;
    }

    public void setJsonContentKey(String jsonContentKey) {
        this.jsonContentKey = jsonContentKey;
    }
    /*
     * The Sakai Yaft  content for a  module
     */
    @Autowired
    private YaftService yaftService;
    /*
     * The Sakai resources for a module
     */
    @Autowired
    private SakaiResourceHandler resourcesHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        List<Forum> yaftForums = new ArrayList<Forum>();
        try {
            Document document = createDocument(yaftService.getForumsForSite(sessionId, moduleId, fromDate));
            return getYaftData(document, sessionId, moduleId, fromDate);
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve the content.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        return null;
    }

    /**
     * When given a String containing XML, create the XML document and parse it
     *
     * @return a list of Forum
     */
    public List<Forum> getYaftData(Document document, String sessionId, String moduleId, Date fromDate) {
        List<Forum> forums = new ArrayList<Forum>();
        //get forums for the site
        List<Element> forumsElements = getElements(document, "/list/forums/forum");
        for (Element ele : forumsElements) {
            Forum forum = new Forum();
            forum.setId(ele.attributeValue("id"));
            forum.setTitle(ele.attributeValue("title"));
            forum.setMessages(Integer.parseInt(ele.attributeValue("messages")));
            forum.setCreatorId(ele.attributeValue("creator_id"));
            forum.setCreatorName(ele.attributeValue("creator_name"));
            forum.setDescription(ele.attributeValue("description"));
            forum.setSiteId(ele.attributeValue("site_id"));
            forum.setStartDate(getAttributeAsDate(ele, "start"));
            forum.setEndDate(getAttributeAsDate(ele, "end"));
            forum.setStatus(ele.attributeValue("status"));
            forum.setUrl(ele.attributeValue("url"));
            forum.setLastMessageDate(getAttributeAsDate(ele, "last_message_date"));
            forum.setUnread(Integer.parseInt(ele.attributeValue("unread")));
            forum.setTopics(Integer.parseInt(ele.attributeValue("topics")));
            if (forum.getTopics() > 0) {
                List<Element> discussions = getElements(ele, "discussions/discussion");
                forum.setDiscussions(getDiscussions(discussions, sessionId, moduleId, fromDate));
            }
            // TODO : this must be removed as soon as we have the Yaft.utils.js
            // fix done
            if (!"DELETED".equals(forum.getStatus())) {
                forums.add(forum);
            }
        }
        return forums;
    }

    /**
     * When given a list of XML discussion elements, a session id, module id and
     * date to filter from, return a list of discussions
     *
     * @param discussionEle list of discussion elements
     * @param sessionId
     * @param moduleId
     * @param fromDate
     * @return
     */
    private List<Discussion> getDiscussions(List<Element> discussionEle, String sessionId, String moduleId, Date fromDate) {
        List<Discussion> discussions = new ArrayList<Discussion>();
        for (Element ele : discussionEle) {
            Discussion discus = new Discussion();
            discus.setId(ele.attributeValue("id"));
            discus.setForumId(ele.attributeValue("forum_id"));
            discus.setCreatorName(ele.attributeValue("creator_name"));
            discus.setCreatorId(ele.attributeValue("creator_id"));
            discus.setCreateDate(getAttributeAsDate(ele, "create_date"));
            discus.setPageId(ele.attributeValue("page_id"));
            discus.setSiteId(ele.attributeValue("site_id"));
            discus.setTopic(ele.attributeValue("topic"));
            discus.setUrl(ele.attributeValue("url"));
            discus.setContent(ele.attributeValue("content"));
            discus.setMessageCount(Integer.parseInt(ele.attributeValue("message_count")));
            discus.setLastMessageDate(getAttributeAsDate(ele, "last_message"));
            
            //if there are any attachments linked to this message, please retrieve and process them.
            if (discus.getAttachmentCount() > 0) {
                List<Element> attachmentsEle = getElements(ele, "attachments/attachment");
                discus.setAttachments(getAttachments(attachmentsEle, sessionId, moduleId, fromDate));
            }
            
            //If there are any messages belonging to this discussion go ahead and retrieve and process them.
            if (discus.getMessageCount() > 0) {
                List<Element> messagesEle = getElements(ele, "messages/message");
                discus.setMessages(getMessages(messagesEle, sessionId, moduleId, fromDate));
            }
            discussions.add(discus);
        }
        return discussions;
    }

    /**
     * Get process all messages when given a XML list of elements containing
     * Message information, session id, module id, date from which point to
     * filter from.
     *
     * @param messagesEle
     * @param sessionId
     * @param moduleId
     * @param fromDate
     * @return List<Message>
     */
    private List<Message> getMessages(List<Element> messagesEle, String sessionId, String moduleId, Date fromDate) {
        List<Message> messages = new ArrayList<Message>();
        for (Element ele : messagesEle) {
            Message message = new Message();
            message.setId(ele.attributeValue("id"));
            message.setTopic(ele.attributeValue("topic"));
            message.setContent(ele.attributeValue("content"));
            message.setCreateDate(getAttributeAsDate(ele, "create_date"));
            message.setCreatorName(ele.attributeValue("creator"));
            message.setCreatorId(ele.attributeValue("creator_id"));
            message.setDepth(Integer.parseInt(ele.attributeValue("depth")));
            message.setDiscussionId(ele.attributeValue("discussion_id"));
            message.setUrl(ele.attributeValue("url"));
            message.setGroupSize(Integer.parseInt(ele.attributeValue("group_size")));
            message.setParent(ele.attributeValue("parent"));
            message.setSiteId(ele.attributeValue("site_id"));
            message.setStatus(ele.attributeValue("status"));
            message.setAttachmentCount(Integer.parseInt(ele.attributeValue("attachment_count")));
            message.setReplyCount(Integer.parseInt(ele.attributeValue("reply_count")));
            //if there are any attachments linked to this message, please retrieve and process them.
            if (message.getAttachmentCount() > 0) {
                List<Element> attachmentsEle = getElements(ele, "attachments/attachment");
                message.setAttachments(getAttachments(attachmentsEle, sessionId, moduleId, fromDate));
            }
            // TODO : this must be removed as soon as we have the Yaft.utils.js
            // fix done
            if (!"DELETED".equals(message.getStatus())) {
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Get all attachments given a list of XML elements containing attachment
     * data, session id, module id and a date from whence to filter.
     *
     * @param attachemElements
     * @param sessionId
     * @param moduleId
     * @param fromDate
     * @return
     */
    private List<Attachment> getAttachments(List<Element> attachemElements, String sessionId, String moduleId, Date fromDate) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Element attEle : attachemElements) {
            Attachment att = new Attachment();
            try {
                att.setId(attEle.attributeValue("id"));
                att.setName(attEle.attributeValue("name"));
                att.setSize(Integer.parseInt(attEle.attributeValue("size")));
                att.setMimeType(attEle.attributeValue("type"));
                String url = attEle.attributeValue("url");
                int index = url.indexOf(moduleId);
                if(index == -1){
                    att.setPath(url);
                }else{
                    att.setPath(url.substring(index));
                }
                if (!sessionId.equals("test")) {
                    att.setContent(resourcesHandler.getResourceData(sessionId, attEle.attributeValue("id")));
                }
            } catch (NumberFormatException ex) {
                LOG.error(ex);
            }
            attachments.add(att);
        }
        return attachments;
    }
    //====================================    UPLOADS    =======================================

    /**
     * Update User content , in this instance Yaft content, when given :
     *
     * @param sessionId - Session retrieved from the web service
     * @param username - Username of the client making the request for auditing
     * @param moduleId - The module for which tool this update will be made
     * against.
     * @param content - Content which is to be processed and updated.
     * @return a map place holders and their replacement values after being
     * persisted.
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        Map<String, String> updatedEntries = new HashMap<String, String>();
        String forumId = "";
        ByteArrayInputStream bais = new ByteArrayInputStream(originalContent.getBytes());
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(bais);
            JsonNode contentNode = rootNode.get(jsonContentKey);
            //get forum ID
            Iterator<Map.Entry<String, JsonNode>> it3 = contentNode.getFields();
            while (it3.hasNext()) {
                Map.Entry pairs = (Map.Entry) it3.next();
                forumId = pairs.getKey().toString();
                Iterator it = contentNode.getElements();
                while (it.hasNext()) {
                    JsonNode discussions = (JsonNode) it.next();

                    // Get discussions and process them
                    JsonNode discusssionsNode = discussions.get("discussions");
                    updatedEntries = processDiscussions(discusssionsNode, updatedEntries, forumId, sessionId);
                    it.remove(); // avoids a ConcurrentModificationException
                }
                it3.remove();
            }
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return updatedEntries;
    }

    /**
     * Process the discussions, persist any new or modified discussions, delete
     * any which have been flagged.
     *
     * @param discussionNodes - JSON nodes containing discussion data
     * @param updatedEntries - a map of placeholder entries keys, which will be
     * linked to the persisted IDs
     * @param forumId - the forumId to which the discussion belongs
     * @param sessionId - sessionId retrieved from the web-service, with
     * administration privileges
     * @return an appended map of place holders and their replacement values
     * after being persisted.
     */
    private Map<String, String> processDiscussions(JsonNode discussionNodes, Map<String, String> updatedEntries, String forumId, String sessionId) {
        Iterator discussionsIterator = discussionNodes.getElements();
        Iterator idIterator = discussionNodes.getFieldNames();
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messages = new ArrayList<Message>();

        // The following block does the following : 
        // Determines whether this discussion needs to be created, updated or ignored and its contents updated/created.
        while (discussionsIterator.hasNext()) {
            JsonNode jn = (JsonNode) discussionsIterator.next();
            Map<String, String> discussionMap = new HashMap<String, String>();
            Iterator<Map.Entry<String, JsonNode>> discs = jn.getFields();
            String val = (String) idIterator.next();
            discussionMap.put("id", val);
            Discussion discussion = new Discussion();
            while (discs.hasNext()) {
                Map.Entry pairs = (Map.Entry) discs.next();
                JsonNode jsn = (JsonNode) pairs.getValue();
                // If it is a container node, it has enough data to populate the discussion pojo with.
                // This means that it will either be a new or an updated discussion.
                if (!jsn.isContainerNode()) {
                    discussionMap.put(pairs.getKey().toString(), pairs.getValue().toString().replace("\"", ""));
                } else {
                    // Get messages
                    messages = getMessages(jsn);
                }
                discs.remove();
            }
            try {
                discussion.setId(discussionMap.get("id"));
                if (discussionMap.get("topic") != null) {
                    discussion.setTopic(discussionMap.get("topic"));
                    discussion.setContent(discussionMap.get("content"));
                    discussion.setForumId(discussionMap.get("forum_id"));
                    discussion.setCreatorName(discussionMap.get("creator_name"));
                    discussion.setCreatorId(discussionMap.get("creator_id"));
                    discussion.setPageId(discussionMap.get("page_id"));
                    discussion.setSiteId(discussionMap.get("site_id"));
                }
                String xmlContent = "";
                // Determine whether to create or update
                if (isUserGenerated(discussion.getId())) {
                    //Create
                    xmlContent = yaftService.createDiscussion(sessionId, discussion.getSiteId(), discussion.getCreatorId(), discussion.getForumId(), discussion.getTopic(), discussion.getContent(), discussion.getCreateDate());
                    discussion.setId(xmlContent);
                    updatedEntries.put(discussionMap.get("id"), xmlContent);
                } else {
                    //update
                    if (null != discussionMap.get("topic")) {
                        //XXXm what is this?
                        xmlContent = yaftService.updateDisucssion(sessionId, discussion.getSiteId(), discussion.getCreatorId(), discussion.getForumId(), discussion.getId(), discussion.getTopic(), discussion.getContent(), discussion.getCreateDate(), "-1", "-1", "READY");
                    }
                }
                processNewOrUpdatedMessages(messages, discussion, updatedEntries, forumId, sessionId);
            } catch (Exception ex) {
                LOG.error("Process discussions : ", ex);
            }
            discussionsIterator.remove();
        }
        return updatedEntries;
    }

    /**
     * Create or update a message, when given:
     *
     * @param messages - a list of messages (Unipoole shared resources)
     * @param discussion - the discussion to which they belong
     * @param updatedEntries - a map of new object place holders and their
     * persisted value
     * @param forumId - the forum to which they belong
     * @param sessionId - session for a user with administrative privileges on
     * the web-service
     * @return an appended map of place holders and their replacement values
     * after being persisted.
     */
    private Map<String, String> processNewOrUpdatedMessages(List<Message> messages, Discussion discussion, Map<String, String> updatedEntries, String forumId, String sessionId) {
        try {
            //replace disucssion Ids in the messages
            int messageIndex = 0;
            for (Message message : messages) {
                messages = updateKeys(messages, updatedEntries);
                //parse Messages
                if (isUserGenerated(message.getId())) {
                    //create
                    String test = yaftService.createMessage(sessionId, message.getSiteId(), forumId, message.getCreatorId(), discussion.getId(), message.getTopic(), message.getContent(), message.getCreateDate(),message.getParent());
                    updatedEntries.put(message.getId(), test);
                    message.setId(test);
                } else {
                    //update
                    yaftService.updateMessage(sessionId, message.getSiteId(), forumId, message.getCreatorId(), message.getId(), message.getTopic(), message.getContent(), message.getCreateDate(),message.getParent());
                }
                messages.set(messageIndex, message);
                messages = updateKeys(messages, updatedEntries);
                messageIndex = messageIndex++;
            }
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return updatedEntries;
    }

    /**
     * Un-marshal a JsonNode containing messages into a list of Messages
     *
     * @param messages
     * @return list of Message
     */
    private static List<Message> getMessages(JsonNode messages) {
        ObjectMapper mapper = new ObjectMapper();
        List<Message> messageList = new ArrayList<Message>();
        Iterator messageIterator = messages.getElements();
        while (messageIterator.hasNext()) {
            JsonNode jsn = (JsonNode) messageIterator.next();
            try {
                Message message = mapper.readValue(jsn, Message.class);
                messageList.add(message);
            } catch (Exception ex) {
                LOG.error(ex);
            }
            messageIterator.remove();
        }
        return messageList;
    }

    //==============================   helpers  ================================================
    /**
     * Check if this node is a newly generated item. We do this by looking for
     * the USER_GENERATED value in the key. If it is found we know this is newly
     * generated else we will simply update it.
     *
     * @param id
     * @return True/False
     */
    private boolean isUserGenerated(String id) {
        if (null == id) {
            return false;
        }
        return ((id.indexOf(userGeneratedPlaceHolder) >= 0) ? true : false);
    }

    /**
     * We only know if a item needs to be updated/created if it has multiple
     * fields
     *
     * @param evalNode
     * @return
     */
    private static boolean toBeUpdated(JsonNode evalNode) {
        int count = 0;
        Iterator i = evalNode.getElements();
        while (i.hasNext()) {
            if (count > 1) {
                return true;
            }
            count++;
        }
        return false;
    }

    /**
     * Keeps track of all placeholder values for user generated content and maps
     * them to the newly created IDs once the items have been persisted.
     *
     * @param messages
     * @param keys
     * @return List of Messages
     */
    private List<Message> updateKeys(List<Message> messages, Map<String, String> keys) {
        int messageIndex = 0;
        for (Message message : messages) {
            if (null != keys.get(message.getDiscussionId())) {
                message.setDiscussionId(keys.get(message.getDiscussionId()));
            }
            if (null != keys.get(message.getParent())) {
                message.setParent(keys.get(message.getParent()));
            }
            messages.set(messageIndex, message);
            messageIndex = messageIndex++;
        }
        return messages;
    }
    

}
