package com.anur.messageapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@Component
public class ArtistConfiguration {

    @Autowired
    Environment environment;

    public String getArtist() {
        return Objects.requireNonNull(environment.getProperty("spring.application.name")).toUpperCase();
    }
}
