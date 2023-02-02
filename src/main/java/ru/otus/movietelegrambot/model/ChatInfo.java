package ru.otus.movietelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash("ChatInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo implements Serializable {

    @Id
    private long id;

    private int page;

    private List<Movie> movies;
}
