package edu.byu.cs.tweeter.server.sqs;

import java.util.List;

public class UpdateFeedQueueItem {
    private String alias;
    private String author_alias;
    private Long timestamp;
    private String first_name;
    private String last_name;
    private String content;
    private List<String> urls;
    private List<String> mentions;
    private String image_url;

    public UpdateFeedQueueItem() {
    }

    public UpdateFeedQueueItem(String alias, String author_alias, Long timestamp, String first_name, String last_name, String content, List<String> urls, List<String> mentions, String image_url) {
        this.alias = alias;
        this.author_alias = author_alias;
        this.timestamp = timestamp;
        this.first_name = first_name;
        this.last_name = last_name;
        this.content = content;
        this.urls = urls;
        this.mentions = mentions;
        this.image_url = image_url;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAuthor_alias() {
        return author_alias;
    }

    public void setAuthor_alias(String author_alias) {
        this.author_alias = author_alias;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
