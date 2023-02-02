package ru.otus.movietelegrambot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.otus.movietelegrambot.config.BotConfig;
import ru.otus.movietelegrambot.model.Movie;
import ru.otus.movietelegrambot.service.MovieService;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KinopoiskMovieBot extends TelegramLongPollingBot {

    @Autowired
    private final BotConfig botConfig;

    private final List<BotCommand> commands = new ArrayList<>();

    private final MovieService movieService;

    static final String ERROR_TEXT = "Error occurred: ";
    static final String COMEDY_LINK = "/comedy";
    static final String HORROR_LINK = "/horror";
    static final String DRAMA_LINK = "/drama";
    static final String COMEDY_NAME = "комедия";
    static final String HORROR_NAME = "триллер";
    static final String DRAMA_NAME = "драма";
    static final String COMMAND_ERROR_TEXT = "Error setting bot's command list: ";
    static final String UNKNOWN_COMMAND = "Sorry, i dOn`T uNdErStAnD U...";


    public KinopoiskMovieBot(BotConfig botConfig, MovieService movieService) {
        this.botConfig = botConfig;
        this.movieService = movieService;

        commands.add(new BotCommand(COMEDY_LINK, COMEDY_NAME));
        commands.add(new BotCommand(DRAMA_LINK, DRAMA_NAME));
        commands.add(new BotCommand(HORROR_LINK, HORROR_NAME));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(COMMAND_ERROR_TEXT + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> startCommand(chatId, update.getMessage().getChat().getFirstName());
                case COMEDY_LINK -> getMovieByGenre(chatId, COMEDY_NAME);
                case HORROR_LINK -> getMovieByGenre(chatId, HORROR_NAME);
                case DRAMA_LINK -> getMovieByGenre(chatId, DRAMA_NAME);
                default -> sendMessage(chatId, UNKNOWN_COMMAND);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void startCommand(long chatId, String firstName) {
        String answer = String.format("Hi, %s", firstName);
        sendMessage(chatId, answer);
        movieService.addChatInfo(chatId);
        log.info("Replied to user " + firstName);
    }

    private void getMovieByGenre(long chatId, String genre) {

        Movie movie = movieService.getMovieByGenre(genre, chatId);

        String answer = movie.getName() + " (" + movie.getYear() + ") \n" +
                movie.getMovieLength() + "minutes \n \n" +
                movie.getRatingKp() + "KP \n" +
                movie.getRatingImdb() + "IMDB \n" +
                movie.getShortDescription() + "\n \n" +
                movie.getDescription() + "\n \n" +
                movie.getPosterUrl();

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        executeMessage(sendMessage);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

}
