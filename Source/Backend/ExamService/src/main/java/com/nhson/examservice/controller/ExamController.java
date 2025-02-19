package com.nhson.examservice.controller;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.vote.entities.VoteType;
import com.nhson.examservice.exam.dto.ExamRequest;
import com.nhson.examservice.vote.event.ExamVoteEvent;
import com.nhson.examservice.exam.services.ExamService;
import com.nhson.examservice.vote.services.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@RestController
@RequestMapping("/exams")
public class ExamController {
    private final ExamService examService;
    private final VoteService voteService;
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public ExamController(ExamService examService, VoteService voteService) {
        this.examService = examService;
        this.voteService = voteService;
    }


    @PostMapping("/{id}/up")
    public ResponseEntity<String> upvote(@PathVariable("id") String examId, JwtAuthenticationToken authenticationToken) {
        try {
            voteService.upvote(examId, authenticationToken);
            sendVoteEvent(examId, VoteType.UPVOTE);
            return ResponseEntity.ok("Upvoted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing upvote");
        }
    }

    @PostMapping("/{id}/down")
    public ResponseEntity<String> downvote(@PathVariable("id") String examId, JwtAuthenticationToken authenticationToken) {
        try {
            voteService.downvote(examId, authenticationToken);
            sendVoteEvent(examId, VoteType.DOWNVOTE);
            return ResponseEntity.ok("Downvoted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing downvote");
        }
    }
    @GetMapping("/votes/stream")
    public SseEmitter streamVotes() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }
    private void sendVoteEvent(String examId, VoteType vote) throws IOException {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .data(new ExamVoteEvent(examId, vote))
                        .name("voteEvent"));
            } catch (IOException e) {
                System.out.println(e);
                emitters.remove(emitter);
            }
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String fileUrl = examService.uploadExamFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
    }

    /**
     * Lấy danh sách tất cả bài thi là Status.ACCEPTED và Status.PUBLIC tiếp theo tuỳ theo các tham số
     *
     * @param lastExam   thời gian tạo của bài thi cuối cùng nhận được (Optional)
     * @param limit     số lượng bài thi được trả về (Optional)
     * @param subject   môn học
     * @return          Danh sách 10 bài thi tiếp theo
     */
    @GetMapping()
    public List<Exam> getAllExams(@RequestParam(name = "lastExam" , required = false)LocalDateTime lastExam,
                               @RequestParam(name = "limit", required = false) int limit,
                               @RequestParam(name = "subject", required = false) String subject,
                                  JwtAuthenticationToken authenticationToken){
        List<Exam> nextExams = examService.getNextExams(subject, lastExam, limit, authenticationToken);
        return nextExams;
    }
    /**
     * Kích hoạt 1 hoặc nhiều bài thi
     *
     * @param exams danh sách bài thi chưa kích hoạt
     * @return danh sách bài thi được kích hoạt hoặc thông báo lỗi nếu có bài thi đã được kích hoạt
     */
    @PutMapping("/activate")
    public ResponseEntity<?> activateExams(@RequestBody Exam... exams) {
        List<Exam> activatedExams = examService.activeExam(exams);
        return ResponseEntity.ok(activatedExams);
    }

    @GetMapping("/not-accept")
    public ResponseEntity<List<Exam>> notAcceptExams(){
        List<Exam> notAcceptedExams = examService.notAcceptedExams();
        return ResponseEntity.ok(notAcceptedExams);
    }

    /**
     * Tạo mới một bài thi
     *
     * @param examRequest thông tin bài thi
     * @return            bài thi mới (trong trường hợp thành công)
     */
    @PostMapping("/create")
    public ResponseEntity<Exam> createExam(@RequestParam(name = "class-id", required = false) String classId, @RequestBody ExamRequest examRequest, JwtAuthenticationToken authenticationToken) {
        Exam newExam = examService.createNewExam(examRequest, authenticationToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExam);
    }
    /**
     * Xóa một bài thi dựa trên ID
     *
     * @param id ID của bài thi cần xóa
     * @return Thông báo kết quả xóa bài thi
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable String id,JwtAuthenticationToken authenticationToken) {
        String result = examService.deleteExam(id,authenticationToken);
        return ResponseEntity.ok(result);
    }
    /**
     * Lấy thông tin chi tiết của bài thi
     * @param id
     * @return chi tiết bài thi
     * */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamDetails(@PathVariable(name = "id" ) String id) {
        Exam exam = examService.findExamById(id);
        return ResponseEntity.ok(exam);
    }
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Exam>> getExamByClass(@PathVariable(name = "classId") String classId){
        List<Exam> exams = examService.getExamByClass(classId);
        return ResponseEntity.ok(exams);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Exam>> getExamByUser(@RequestParam(name = "lastExam", required = false)LocalDateTime lastExam,JwtAuthenticationToken authenticationToken) {
        List<Exam> exams = examService.getExamByUser(lastExam,authenticationToken);
        return ResponseEntity.ok(exams);
    }

}