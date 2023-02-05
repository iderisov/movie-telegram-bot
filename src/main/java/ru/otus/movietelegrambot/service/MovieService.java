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
import java.util.Optional;
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

        boolean chachedMovieExists = chatInfo.getMovies()
                .stream().anyMatch(m -> m.getGenre().equals(genre));


        if (chachedMovieExists) {
            return getMovie(chatInfo, genre);
        } else {
            return getMovieFromApi(genre, chatInfo);
        }

    }

    public void addChatInfo(long id) {
        chatInfoRepository.save(new ChatInfo(id, 0, new ArrayList<>()));
    }

    private Movie getMovie(ChatInfo chatInfo, String genre) {
        Movie result = chatInfo.getMovies().stream().filter(m -> m.getGenre().equals(genre)).findFirst().get();
        chatInfo.getMovies().remove(result);
        chatInfoRepository.replace(chatInfo);
        return result;
    }

    private Movie getMovieFromApi(String genre, ChatInfo chatInfo) {
        try {
            chatInfo.setPage(chatInfo.getPage() + 1);
            List<Movie> newMovies = kinopoiskHttpClient.getMovieByGenreFromApi(genre, chatInfo.getPage());
            chatInfo.getMovies().addAll(newMovies);
            chatInfoRepository.replace(chatInfo);
            return getMovie(chatInfo, genre);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error(ERROR_TEXT + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
