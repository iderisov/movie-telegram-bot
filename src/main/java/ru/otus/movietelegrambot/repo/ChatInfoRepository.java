package ru.otus.movietelegrambot.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.movietelegrambot.model.ChatInfo;

@Repository
public class ChatInfoRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String HASH_KEY = "ChatInfo";

    public ChatInfo save(ChatInfo chatInfo) {
        redisTemplate.opsForHash().put(HASH_KEY, chatInfo.getId(), chatInfo);
        return chatInfo;
    }

    public ChatInfo findChatInfoById(long id) {
        return (ChatInfo) redisTemplate.opsForHash().get(HASH_KEY, id);
    }


    public String replace(ChatInfo chatInfo) {
        redisTemplate.opsForHash().put(HASH_KEY, chatInfo.getId(), chatInfo);
        return "chatInfo replaced";
    }

}
