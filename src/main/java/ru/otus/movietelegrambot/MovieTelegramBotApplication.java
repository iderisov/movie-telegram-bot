package ru.otus.movietelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class MovieTelegramBotApplication {

	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

		SpringApplication.run(MovieTelegramBotApplication.class, args);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI("https://kinopoiskapiunofficial.tech/api/v2.2/films/301"))
				.version(HttpClient.Version.HTTP_2)
				.GET()
				.setHeader("X-API-KEY", "da51aae5-deea-453a-8588-9ef21f6d4fcc")
				.build();

		HttpResponse<String> response = HttpClient
				.newBuilder()
				.proxy(ProxySelector.getDefault())
				.build()
				.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println(response.body());

	}

}
