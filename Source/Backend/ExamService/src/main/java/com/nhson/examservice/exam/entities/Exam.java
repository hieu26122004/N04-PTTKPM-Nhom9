package com.nhson.examservice.exam.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nhson.examservice.exam.dto.ExamRequest;
import com.nhson.examservice.vote.entities.Vote;
import com.nhson.examservice.vote.entities.VoteType;
import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "exams")
@NoArgsConstructor
public class Exam implements Persistable<String> {

    @Id
    @GenericGenerator(name = "custom-exam-id", strategy = "com.nhson.examservice.exam.id.CustomExamIdGenerator")
    @GeneratedValue(generator = "custom-exam-id")
    private String examId;
    @Column(name = "exam_name")
    private String name;
    @Column(name = "class_id")
    private String classId;
    @Enumerated(EnumType.STRING)
    private Subject subject;
    private Integer duration;
    private String provider;
    @Column(name = "number_of_question")
    private Integer numberOfQuestion;
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private LocalDateTime due;
    private Integer maxAttempts;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Type type;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Vote> votes;
    @Transient
    private int upvoteCount;
    @Transient
    private int downvoteCount;
    private int commentCount;
    @Transient
    private boolean isNew = false;

    public Exam(String examId, String name, Subject subject, Integer duration, String provider,
                Integer numberOfQuestion, DifficultyLevel difficultyLevel, LocalDateTime createdDate,
                LocalDateTime lastUpdatedDate, LocalDateTime due, Integer maxAttempts,String classId,Type type, Status status,List<Vote> votes) {
        this.examId = examId;
        this.name = name;
        this.subject = subject;
        this.duration = duration;
        this.provider = provider;
        this.numberOfQuestion = numberOfQuestion;
        this.difficultyLevel = difficultyLevel;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.due = due;
        this.maxAttempts = maxAttempts;
        this.status = status;
        this.isNew = (examId == null || examId.isEmpty());
        this.classId = classId;
        this.type = type;
        this.votes = votes;
        this.upvoteCount = 0;
        this.downvoteCount = 0;
        for (Vote vote : votes) {
            if(vote.getVoteType().equals(VoteType.UPVOTE)){
                this.upvoteCount++;
            }else{
                this.downvoteCount++;
            }
        }
    }
    public Exam(ExamRequest examRequest) {
        if(examRequest.getId() == null || examRequest.getId().isEmpty()){
            isNew = true;
//            this.examId = UUID.randomUUID().toString();
        }else {
            isNew = false;
            this.examId = examRequest.getId();
        }
        this.name = examRequest.getName();
        this.subject = examRequest.getSubject();
        this.duration = examRequest.getDuration();
        this.provider = examRequest.getProvider();
        this.numberOfQuestion = examRequest.getNumberOfQuestion();
        this.difficultyLevel = examRequest.getDifficultyLevel();
        this.status = Status.NOT_ACCEPTED;
        this.due = examRequest.getDue();
        if(examRequest.getClassId() == null || examRequest.getClassId().isEmpty()){
            this.type = Type.PUBLIC;
        }else {
            this.type = Type.PRIVATE;
            this.classId = examRequest.getClassId();
            this.maxAttempts = examRequest.getMaxAttempts();
        }
    }

    public Exam(String examId) {
        this.examId = examId;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "examId='" + examId + '\'' +
                ", name='" + name + '\'' +
                ", classId='" + classId + '\'' +
                ", subject=" + subject +
                ", duration=" + duration +
                ", provider='" + provider + '\'' +
                ", numberOfQuestion=" + numberOfQuestion +
                ", difficultyLevel=" + difficultyLevel +
                ", createdDate=" + createdDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", due=" + due +
                ", maxAttempts=" + maxAttempts +
                ", status=" + status +
                ", type=" + type +
                ", upvoteCount=" + upvoteCount +
                ", downvoteCount=" + downvoteCount +
                ", commentCount=" + commentCount +
                ", isNew=" + isNew +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDate = LocalDateTime.now();
    }

    @PostLoad
    protected void postLoad(){
        this.upvoteCount = 0;
        this.downvoteCount = 0;
        for (Vote vote : votes) {
            if(vote.getVoteType().equals(VoteType.UPVOTE)){
                this.upvoteCount++;
            }else{
                this.downvoteCount++;
            }
        }
    }

    @Override
    public String getId() {
        return this.examId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void activeExam(){
        this.status = Status.ACCEPTED;
    }


}
