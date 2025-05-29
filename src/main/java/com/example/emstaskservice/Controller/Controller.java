package com.example.emstaskservice.controller;

import com.example.emstaskservice.OpenFeign.Validate;
import com.example.emstaskservice.dto.*;
import com.example.emstaskservice.enums.Priority;
import com.example.emstaskservice.enums.TaskStatus;
import com.example.emstaskservice.exception.CustomException;
import com.example.emstaskservice.model.TaskModel;
import com.example.emstaskservice.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
public class Controller {
    private final TaskService taskService;
    @Autowired
    public Controller(TaskService taskService){
        this.taskService = taskService;
    }
    @Autowired
    private Validate validate;
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> add (@Valid @RequestBody RequestInsertTaskDto requestInsertTaskDto)
    {
       return taskService.addTask(requestInsertTaskDto);

    }


    @PostMapping("/getAllbyId")
    public ResponseEntity<List<TaskModel>> getAllById(@Valid @RequestBody RequestListUUidsDto requestListUUidsDto){
        return ResponseEntity.ok(taskService.getAllByTaskId(requestListUUidsDto));
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<TaskModel> getAll(@CookieValue("jwt_token") String token) {


        String cookieHeader = "jwt_token=" + token;

        try {
            String userIdString = validate.validate(cookieHeader);



            if (userIdString.isEmpty()) {
                throw new CustomException("Invalid token or server down");
            }

            UUID userId = UUID.fromString(userIdString);
            return taskService.getTasksByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Invalid UUID or failed to fetch tasks");
        }
    }


}
