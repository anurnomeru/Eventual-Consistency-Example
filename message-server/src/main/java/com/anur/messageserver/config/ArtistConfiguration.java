package com.anur.messageserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@Component
public class ArtistConfiguration implements ApplicationListener<WebServerInitializedEvent> {

    @Autowired
    Environment environment;

    private int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

    public String getArtist() {
        return environment.getProperty("spring.application.name").toUpperCase() + " : " + serverPort;
    }

}
