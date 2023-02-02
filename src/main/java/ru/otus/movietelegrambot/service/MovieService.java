package ru.otus.movietelegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.movietelegrambot.httpclient.KinopoiskHttpClient;
import ru.otus.movietelegrambot.model.ChatInfo;
import ru.otus.movietelegrambot.model.Movie;
import ru.otus.movietelegrambot.repo.ChatInfoRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final KinopoiskHttpClient kinopoiskHttpClient;
    private final ChatInfoRepository chatInfoRepository;

    static final String ERROR_TEXT = "Error occurred: ";


    public Movie getMovieByGenre(String genre, long chatId) {

        ChatInfo chatInfo = chatInfoRepository.findChatInfoById(chatId);

        List<Movie> cachedMoviesByGenre = chatInfoRepository.findChatInfoById(chatId).getMovies()
                .stream().filter(m -> m.getGenre().equals(genre)).toList();

        if (!cachedMoviesByGenre.isEmpty()) {
            return getMovie(chatInfo);
        } else {
            return getMovieFromApi(genre, chatInfo);
        }

    }

    public void addChatInfo(long id) {
        chatInfoRepository.save(new ChatInfo(id, 0, new ArrayList<>()));
    }

    private Movie getMovie(ChatInfo chatInfo) {
        Movie result = chatInfo.getMovies().remove(0);
        chatInfoRepository.replace(chatInfo);
        return result;
    }

    private Movie getMovieFromApi(String genre, ChatInfo chatInfo) {
        try {
            chatInfo.setPage(chatInfo.getPage() + 1);
            List<Movie> newMovies = kinopoiskHttpClient.getMovieByGenreFromApi(genre, chatInfo.getPage());
            chatInfo.setMovies(newMovies);
            return getMovie(chatInfo);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error(ERROR_TEXT + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
