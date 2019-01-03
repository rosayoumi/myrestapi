package com.channelg.myrestapi_test.events;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventReposirory extends JpaRepository<Event, Integer> {

}
