package com.nhson.examservice.exam.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.entities.Status;
import com.nhson.examservice.exam.entities.Subject;
import com.nhson.examservice.exam.dto.ExamRequest;
import com.nhson.examservice.exam.event.ExamActiveEvent;
import com.nhson.examservice.exam.event.ExamDeletedEvent;
import com.nhson.examservice.exceptions.PermissionDeniedException;
import com.nhson.examservice.exam.repositories.ExamRepository;
import com.nhson.examservice.user.UserServiceClient;
import com.nhson.examservice.user.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ExamService {

    private final ExamRepository examRepository;
    private final UserServiceClient userServiceClient;
    private final PermissionService permissionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public ExamService(ExamRepository examRepository, UserServiceClient userServiceClient, PermissionService permissionService, ApplicationEventPublisher applicationEventPublisher) {
        this.examRepository = examRepository;
        this.userServiceClient = userServiceClient;
        this.permissionService = permissionService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<Exam> getNextExams(String subjectString, LocalDateTime lastExam, Integer limit, JwtAuthenticationToken authenticationToken) {
        System.out.println("Thực hiện get exams");
        Subject subject = null;
        if(subjectString!=null){
            try {
                subject = Subject.valueOf(subjectString.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid subject name: " + subjectString);
            }
        }
        limit = userServiceClient.getUserStatistics(authenticationToken).getLoadedExamsBatchSize();
        if (limit == null) {
            limit = 10;
        }
        List<Exam> nextExams = examRepository.getNext10ExamsBySubjectOrderByLastUpdateDate(subject, lastExam, limit);
        return nextExams;
    }

    @Cacheable(cacheNames = "exams", key = "#id")
    public Exam findExamById(String id) {
        return examRepository.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = "exams", allEntries = true)
    public Exam createNewExam(ExamRequest examRequest, JwtAuthenticationToken authenticationToken) {
        String clazzId = examRequest.getClassId();
        String username = authenticationToken.getToken().getClaims().get("username").toString();
        if (!permissionService.hasPermissionToCreateExam(clazzId, authenticationToken)) {
            throw new PermissionDeniedException("You don't have permission to create exams for class: " + clazzId);
        }
        examRequest.setProvider(username);
        Exam exam = new Exam(examRequest);
        return examRepository.save(exam);
    }

    @CacheEvict(cacheNames = "exams", key = "#examRequest.id")
    public Exam updateExam(ExamRequest examRequest){
        Exam exam = new Exam(examRequest);
        return examRepository.save(exam);
    }

    @CacheEvict(cacheNames = "exams", allEntries = true)
    public List<Exam> activeExam(Exam... exams) {
        for (Exam exam : exams) {
            if(exam.getStatus().equals(Status.ACCEPTED)){
                throw new IllegalArgumentException(String.format("Bài thi %s đã được kích hoạt", exam.getExamId()));
            }
            exam.activeExam();
            applicationEventPublisher.publishEvent(new ExamActiveEvent(this,exam));
        }
        return examRepository.saveAll(Arrays.asList(exams));
    }

    @Cacheable(cacheNames = "is_exist_exam", key = "#id")
    public boolean checkExamExists(String id){
        return examRepository.existsById(id);
    }

    public String uploadExamFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Tệp không được rỗng");
        }
        Bucket bucket = StorageClient.getInstance().bucket();;
        try {
            Blob blob = bucket.create(UUID.randomUUID().toString(),file.getInputStream(),"images/*");
            return blob.getMediaLink();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tải tệp lên: " + e.getMessage(), e);
        }
    }

    @CacheEvict(cacheNames = "exam", key = "#id")
    public String deleteExam(String id, JwtAuthenticationToken authenticationToken) {
        Exam exam = this.findExamById(id);
        String username = authenticationToken.getToken().getClaims().get("username").toString();
        boolean hasPermission = permissionService.hasPermissionToDelete(username,exam,authenticationToken);
        if(!hasPermission){
            throw new InvalidBearerTokenException("You don't have permission to delete this exam");
        }
        examRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new ExamDeletedEvent(this,exam));
        return id;
    }

    public List<Exam> notAcceptedExams(){
        return examRepository.findByStatus(Status.NOT_ACCEPTED);
    }

    public List<Exam> getExamByClass(String classId){
        return examRepository.findByClassId(classId);
    }

    public List<Exam> getExamByUser(LocalDateTime lastExam,JwtAuthenticationToken authenticationToken) {
        String username = authenticationToken.getToken().getClaimAsString("username");
        if (username == null || username.isEmpty()) {
            return new ArrayList<>();
        }
        lastExam = lastExam == null ? LocalDateTime.now() : lastExam;
        return examRepository.getByUserAndFilter(username,lastExam,10);
    }
}
