package com.chxip.campusinfo.entity;

import java.io.Serializable;

public class Comment implements Serializable{
	private static final long serialVersionUID = 6576408727621452039L;
	private int commentId;
	
	private String createTime;
	
	private String commentContent;
	
	private int messageId;
	
	private int commentUserId;
	
	private User commentUser;
	
	private int replyUserId;
	
	private User replyUser;

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(int commentUserId) {
		this.commentUserId = commentUserId;
	}

	public User getCommentUser() {
		return commentUser;
	}

	public void setCommentUser(User commentUser) {
		this.commentUser = commentUser;
	}

	public int getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(int replyUserId) {
		this.replyUserId = replyUserId;
	}

	public User getReplyUser() {
		return replyUser;
	}

	public void setReplyUser(User replyUser) {
		this.replyUser = replyUser;
	}
	
	
	

}
