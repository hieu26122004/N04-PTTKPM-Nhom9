package com.nhson.examservice.question.repositories;

import com.nhson.examservice.question.entities.Option;
import com.nhson.examservice.question.entities.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class QuestionRepositoryImpl implements CustomQuestionRepository{

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // SQL Insert cho bảng questions
    private static final String INSERT_QUESTION_SQL = "INSERT INTO questions (question_id, exam_id, content, question_order) " +
            "VALUES (:questionId, :examId, :content, :questionOrder)";

    // SQL Insert cho bảng question_options
    private static final String INSERT_OPTION_SQL = "INSERT INTO question_options (question_id, option_label, option_content, is_correct) " +
            "VALUES (:questionId, :label, :content, :isCorrect)";

    public QuestionRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Lưu một câu hỏi cùng các tùy chọn của nó vào cơ sở dữ liệu.
     *
     * @param question Thực thể Question cần lưu
     */
    @Transactional
    public void saveQuestion(Question question) {

        // Tạo đối tượng MapSqlParameterSource cho câu hỏi
        MapSqlParameterSource questionParams = new MapSqlParameterSource();
        questionParams.addValue("questionId", question.getQuestionId());
        questionParams.addValue("examId", question.getExamId());
        questionParams.addValue("content", question.getContent());
        questionParams.addValue("questionOrder", question.getQuestionOrder());

        // Thực hiện lưu câu hỏi
        jdbcTemplate.update(INSERT_QUESTION_SQL, questionParams);

        // Lưu các tùy chọn liên quan
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            for (Option option : question.getOptions()) {
                MapSqlParameterSource optionParams = new MapSqlParameterSource();
                optionParams.addValue("questionId", question.getQuestionId());
                optionParams.addValue("label", option.getLabel());
                optionParams.addValue("content", option.getContent());
                optionParams.addValue("isCorrect", option.getIsCorrect());

                jdbcTemplate.update(INSERT_OPTION_SQL, optionParams);
            }
        }
    }

    /**
     * Lưu nhiều câu hỏi cùng các tùy chọn của chúng vào cơ sở dữ liệu.
     *
     * @param questions Danh sách các thực thể Question cần lưu
     */
    @Transactional
    public void saveAllQuestions(List<Question> questions) {
        for (Question question : questions) {
            saveQuestion(question);
        }
    }
}
