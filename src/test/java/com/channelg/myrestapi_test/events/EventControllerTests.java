package com.channelg.myrestapi_test.events;

import com.channelg.myrestapi_test.common.BaseControllerTest;
import com.channelg.myrestapi_test.common.RestDocConfiguration;
import com.channelg.myrestapi_test.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventReposirory eventReposirory;

    //@MockBean
    //EventReposirory eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Rest API TEST ....")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,20))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,25))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,25))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
            .andExpect(jsonPath("id").value(Matchers.not(100)))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DREFT.name()))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.query-events").exists())
            .andExpect(jsonPath("_links.update-event").exists())
            .andDo(document("create-event",
                    links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("link to query an existing events"),
                            linkWithRel("update-event").description("link to update an existing event"),
                            linkWithRel("profile").description("link to an existing event profile")
                    ),
                    requestHeaders(
                            headerWithName(HttpHeaders.ACCEPT).description("access header"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                    ),
                    requestFields(
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                            fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                            fieldWithPath("endEventDateTime").description("date time of end of new event"),
                            fieldWithPath("location").description("location of new event"),
                            fieldWithPath("basePrice").description("base price of new event"),
                            fieldWithPath("maxPrice").description("max price of new event"),
                            fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                    ),
                    responseHeaders(
                            headerWithName(HttpHeaders.LOCATION).description("location header"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("Contnt type")
                    ),
                    responseFields(
                            fieldWithPath("id").description("ID of new event"),
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new event"),
                            fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                            fieldWithPath("endEventDateTime").description("date time of end of new event"),
                            fieldWithPath("location").description("location of new event"),
                            fieldWithPath("basePrice").description("base price of new event"),
                            fieldWithPath("maxPrice").description("max price of new event"),
                            fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                            fieldWithPath("free").description("It tells if this events is free or not"),
                            fieldWithPath("offline").description("It tells if this event is offline event or not"),
                            fieldWithPath("eventStatus").description("event status"),
                            fieldWithPath("_links.self.href").description("link to self"),
                            fieldWithPath("_links.query-events.href").description("link to query an existing events"),
                            fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                            fieldWithPath("_links.profile.href").description("link to an existing event profile")
                    )
            ))
        ;
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 오류를 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Rest API TEST ....")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,20))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,25))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,25))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 오류가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못되어 있는 경우에 오류가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest API TEST ....")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,20))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,25))
                .endEventDateTime(LocalDateTime.of(2018,11,24,14,25))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타트업 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                /*
                .andExpect(jsonPath("$[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                */
                //.andExpect(jsonPath("$[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);
/*
        IntStream.range(0, 30).forEach(i -> {
           this.generateEvent(i);
        });
*/
        // When & Then
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    private void generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .build();
                this.eventReposirory.save(event);
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvents(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // When & Then
        this.mockMvc.perform(get("api/events/1883"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvents(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;
    }

    @Test
    @TestDescription(("입력값이 비어있는 경우에 이벤트 수정 실패"))
    public void updateEvent400_Empty() throws Exception {
        // Given
        Event event = this.generateEvents(200);
        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription(("입력값이 잘못된 경우에 이벤트 수정 실패"))
    public void updateEvent400_Wrong() throws Exception {
        // Given
        Event event = this.generateEvents(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription(("존재하지 않는 이벤트 수정 실패"))
    public void updateEvent404() throws Exception {
        // Given
        Event event = this.generateEvents(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/122321")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvents(int index) {
        Event event = Event.builder()
                    .name("event" + index)
                    .description("test event")
                    .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,20))
                    .beginEventDateTime(LocalDateTime.of(2018,11,25,14,25))
                    .endEventDateTime(LocalDateTime.of(2018,11,26,14,25))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 D2 스타트업 팩토리")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DREFT)
                    .build();

        return this.eventReposirory.save(event);
    }

}
