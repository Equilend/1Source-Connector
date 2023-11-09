package com.intellecteu.onesource.integration.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class OneSourceAppTests extends AbstractTest {

    @Autowired
    private EventService eventService;

    @Test
    void contextLoads() {
        assertThat(eventService).isNotNull();
    }

}
