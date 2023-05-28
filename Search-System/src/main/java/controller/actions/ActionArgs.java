package controller.actions;

import java.net.http.HttpClient;

public record ActionArgs(HttpClient httpClient, String params) {
}
