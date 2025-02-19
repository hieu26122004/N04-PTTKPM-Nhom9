package com.nhson.examservice.question.services;

import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.services.ExamService;
import com.nhson.examservice.exceptions.ExamNotFoundException;
import com.nhson.examservice.exceptions.PermissionDeniedException;
import com.nhson.examservice.exceptions.QuestionCountMismatchException;
import com.nhson.examservice.question.entities.Option;
import com.nhson.examservice.question.entities.Question;
import com.nhson.examservice.question.repositories.QuestionRepository;
import com.nhson.examservice.firebase.service.FirebaseService;
import com.nhson.examservice.user.UserServiceClient;
import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import org.springframework.mock.web.MockMultipartFile;
@Service
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamService examService;
    private final FirebaseService firebaseService;
    private final UserServiceClient userServiceClient;
    private final SnowflakeIdGenerator questionIdGenerator;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, ExamService examService, FirebaseService firebaseService, UserServiceClient userServiceClient, SnowflakeIdGenerator questionIdGenerator) {
        this.questionRepository = questionRepository;
        this.examService = examService;
        this.firebaseService = firebaseService;
        this.userServiceClient = userServiceClient;
        this.questionIdGenerator = questionIdGenerator;
    }

    @Transactional
    public List<Question> createNewQuestion(JwtAuthenticationToken authenticationToken, List<Question> questions){
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("The questions list cannot be empty");
        }
        String examId = questions.get(0).getExamId();

        Exam exam = examService.findExamById(examId);
        if (exam == null) {
            throw new ExamNotFoundException(String.format("Exam with id %s not found", examId));
        }

        // Kiểm tra số lượng câu hỏi
        if (!exam.getNumberOfQuestion().equals(questions.size())) {
            throw new QuestionCountMismatchException("Number of questions does not match the exam requirement");
        }
        String clazzId = exam.getClassId();

        if (clazzId != null && !clazzId.isEmpty()) {
            List<String> userPermissions = userServiceClient.getUserPermission(authenticationToken);
            if (!userPermissions.contains(clazzId + ".TEACHER")) {
                throw new PermissionDeniedException("You don't have permission to create questions for this class");
            }
        }

        questions.forEach(question -> {
            question.setQuestionId(String.valueOf(questionIdGenerator.next()));
            question.setContent(processContent(question.getContent()));
            question.getOptions().forEach(option -> {
                option.setContent(processContent(option.getContent()));
            });
        });


        questionRepository.saveAllQuestions(questions);

        return questions;
    }

    @Transactional
    public List<Question> getQuestionAndShuffleOptions(String examId) {
        List<Question> questions = questionRepository.findByExamIdOrderByQuestionOrderAsc(examId);
        Hibernate.initialize(questions);
        shuffleOptions(questions);
        return questions;
    }

    private String processContent(String questionContent) {
        Document doc = Jsoup.parse(questionContent);

        Elements images = doc.select("img");
        for (Element img : images) {
            String src = img.attr("src");
            if (src.startsWith("data:image")) {
                MultipartFile file = convertBase64ToMultipartFile(src);

                String imagePath = firebaseService.uploadImageToFirebase(file);

                img.attr("src", imagePath);
            }
        }
        String sanitizedHtml = Jsoup.clean(doc.html(),
                Safelist.relaxed()
                        .addTags("table", "tr", "td", "th", "iframe")
                        .addAttributes("iframe", "src", "width", "height", "frameborder", "allowfullscreen"));

        log.info("Original HTML: {}", questionContent);
        log.info("Processed image count: {}", images.size());
        log.info("Sanitized HTML: {}", sanitizedHtml);

        return sanitizedHtml;
    }


    public MultipartFile convertBase64ToMultipartFile(String base64Image) {
        try {
            String[] parts = base64Image.split(",");
            String imageData = parts[1];
            String mimeType = parts[0].split(":")[1].split(";")[0];

            byte[] decodedBytes = Base64.getDecoder().decode(imageData);

            return new MockMultipartFile("file", "image." + mimeType.split("/")[1], mimeType, decodedBytes) {
            };
        } catch (Exception e) {
            throw new RuntimeException("Error converting Base64 to MultipartFile", e);
        }
    }

    private void shuffleOptions(List<Question> questions) {
        for (Question question : questions) {
            List<Option> options = question.getOptions();
            if (options != null && !options.isEmpty()) {
                Collections.shuffle(options);
                char label = 'A';
                for (Option option : options) {
                    option.setLabel(String.valueOf(label));
                    label++;
                }
            }
        }
    }


}
