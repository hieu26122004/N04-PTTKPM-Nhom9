package com.nhson.examservice.controller;

import com.nhson.examservice.question.entities.Question;
import com.nhson.examservice.question.services.QuestionService;
import com.nhson.examservice.validator.ValidCreateQuestionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }


    /**
     * Tạo 1 loạt câu hỏi cùng trong cùng 1 bài thi
     *
     * @param questions các câu hỏi trong cùng 1 bài thi
     * @return danh sách các câu hỏi của 1 bài thi
     * */
    @PostMapping
    public ResponseEntity<List<Question>> save(JwtAuthenticationToken authenticationToken, @ValidCreateQuestionRequest @RequestBody List<Question> questions){
        List<Question> newQuestion = questionService.createNewQuestion(authenticationToken,questions);
        return ResponseEntity.status(HttpStatus.CREATED).body(newQuestion);
    }

    /**
     * Cập nhật các câu hỏi trong cùng 1 bài thi
     *
     * @param questions các câu hỏi trong cùng 1 bài thi
     * @return danh sách các câu hỏi trong cùng 1 bài thi
     * */
    @PutMapping
    public ResponseEntity<List<Question>> update(JwtAuthenticationToken authenticationToken,@ValidCreateQuestionRequest @RequestBody List<Question> questions){
        List<Question> newQuestion = questionService.createNewQuestion(authenticationToken,questions);
        return ResponseEntity.status(HttpStatus.OK).body(newQuestion);
    }

    /**
     * Lấy danh sách các câu hỏi theo examId và xáo trộn chúng
     * @param examId
     * @return danh sách các câu hỏi trong cùng 1 bài thi đã được xáo trộn
     * */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<Question>> getQuestionsByExamId(@PathVariable String examId, JwtAuthenticationToken authenticationToken) {
        List<Question> questions = questionService.getQuestionAndShuffleOptions(examId);
        return ResponseEntity.ok(questions);
    }

}
