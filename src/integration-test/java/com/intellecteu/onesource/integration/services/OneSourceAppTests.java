package com.intellecteu.onesource.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class OneSourceAppTests extends AbstractTest {

    @Autowired
    private EventService eventService;

    @Test
    void contextLoads() {
        assertThat(eventService).isNotNull();
    }

}
