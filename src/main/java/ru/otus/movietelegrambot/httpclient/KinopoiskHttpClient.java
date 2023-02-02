package ru.otus.movietelegrambot.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.otus.movietelegrambot.model.Movie;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class KinopoiskHttpClient {


    @Value("${api.kinopoisk.token}")
    private String token;

    public List<Movie> getMovieByGenreFromApi(String genre, int page) throws IOException, InterruptedException, URISyntaxException {


        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("https://api.kinopoisk.dev/movie?search[]=movie" +
                        "&search[]=%s&search[]=1980-2021&search[]=7-10&search[]=!null&search[]=!null&search[]=!null" +
                        "&field[]=type&field[]=genres.name&field[]=year&field[]=rating.kp&field[]=name&field[]=votes.kp&field[]=poster.url" +
                        "&limit=5&page=%d&token=%s", genre, page, token)))
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient
                .newBuilder()
                .proxy(ProxySelector.getDefault())
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());


        String json = response.body();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json).get("docs");

        ObjectReader reader = mapper.readerFor(new TypeReference<List<Movie>>() {
        });
        List<Movie> list = reader.readValue(jsonNode);
        list.forEach(m -> m.setGenre(genre));

        return list;

    }

}
