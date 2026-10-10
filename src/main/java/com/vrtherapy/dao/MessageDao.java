package com.vrtherapy.dao;

import com.vrtherapy.model.Message;

import java.util.List;

public interface MessageDao extends GenericDao<Message, Long> {

    List<Message> findConversation(Long userOneId, Long userTwoId);

    long countUnreadMessages(Long recipientId);

    void markConversationAsRead(Long senderId, Long recipientId);
}
