package ru.otus.movietelegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@RedisHash("Movie")
public class Movie implements Serializable {

    @Id
    private String id;

    private String name;
    private String movieLength;
    private String shortDescription;
    private String description;
    private String year;

    private String posterUrl;
    private String ratingKp;
    private String ratingImdb;

    private String genre;

    @SuppressWarnings("unchecked")
    @JsonProperty("poster")
    private void unpackNestedPostUrl(Map<String, String> poster) {
        this.posterUrl = poster.get("url");
    }


    @SuppressWarnings("unchecked")
    @JsonProperty("rating")
    private void unpackNestedRatings(Map<String, String> rating) {
        this.ratingKp = rating.get("kp");
        this.ratingImdb = rating.get("imdb");
    }


}
