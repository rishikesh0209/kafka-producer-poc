package com.javapoc.kafkapoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javapoc.kafkapoc.domain.LibraryEvent;
import com.javapoc.kafkapoc.domain.LibraryEventType;
import com.javapoc.kafkapoc.producer.LibraryEventProducer;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
// import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        //invoke kafka producer
        // this is an asynchronous function
//        libraryEventProducer.sendLibraryEvent(libraryEvent);
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);  //to explicitly to add a new entry
        libraryEventProducer.sendLibraryEvent(libraryEvent);
        //to send record headers
//         libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/v1/sync_libraryevent")
    public ResponseEntity<LibraryEvent> PostEventSynchronous(@RequestBody LibraryEvent libraryEvent) throws TimeoutException, ExecutionException, InterruptedException, Exception {

        //invoke kafka producer
        // this is an synchronous function
        // libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //PUT
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        log.info("LibraryEvent : {} ",libraryEvent );
        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}