# N04-PTTKPM-Nhom9

Hệ thống luyện thi THPTQG

## Mô Tả Khái Quát Về Đề Tài

Dự án này nhằm xây dựng một hệ thống cung cấp và quản lý các bài thi THPTQG một cách hiệu quả, phục vụ nhu cầu ôn thi của học sinh. Hệ thống sẽ giúp học sinh tiếp cận với các tài liệu, bài thi phù hợp, qua đó nâng cao chất lượng học tập và chuẩn bị tốt hơn cho kỳ thi quan trọng.

## Yêu Cầu Tổ Chức Chương Trình Chính Về Công Nghệ, Chức Năng

### Công Nghệ

- **Frontend**: Hệ thống được phát triển sử dụng công nghệ web **Angular** cho giao diện người dùng.
- **Backend**: Sử dụng **Spring Framework** để xây dựng API và xử lý logic phía máy chủ.
- **Cơ sở dữ liệu**:
  - **MySQL**: Lưu trữ dữ liệu quan hệ như thông tin người dùng, bài thi, nhóm học tập.
  - **MongoDB**: Lưu trữ dữ liệu phi cấu trúc như lịch sử làm bài, báo cáo chi tiết.
  - **Redis** :
- **Message Broker**: RabbitMQ
- **Hỗ trợ nền tảng web**: Hệ thống hoạt động tốt trên trình duyệt web.

### Chức Năng Chính

1. **Cung Cấp Ngân Hàng Đề Thi**

   - Đề thi được phân loại theo môn học, năm thi, và mức độ khó.
   - Học sinh có thể làm bài thi trực tiếp trên hệ thống.

2. **Chấm Điểm Tự Động**

   - Chấm điểm tự động cho các bài thi trắc nghiệm với kết quả chi tiết và gợi ý cải thiện.
   - Hỗ trợ nhập điểm thủ công hoặc chấm tự luận (nếu tích hợp AI).

3. **Đóng Góp Bài Thi**

   - Người dùng có thể đóng góp bài thi vào hệ thống.
   - Các bài thi đóng góp sẽ được kiểm duyệt để đảm bảo chất lượng.

4. **Quản Lý Nhóm Học Tập**

   - Người dùng có thể tạo hoặc tham gia nhóm học tập.
   - Các nhóm hỗ trợ chia sẻ bài thi, tài liệu, và tổ chức thi thử.

5. **Thống Kê Và Phân Tích Kết Quả**

   - Lưu trữ lịch sử làm bài thi.
   - Cung cấp biểu đồ phân tích hiệu suất học tập và gợi ý cải thiện.

6. **Lịch Ôn Thi Cá Nhân Hóa**

   - Người dùng có thể tạo lịch ôn thi dựa trên thời gian còn lại và mục tiêu học tập.
   - Hệ thống gửi thông báo nhắc nhở ôn thi.

7. **Cập Nhật Đề Thi Thường Xuyên**

   - Đề thi được cập nhật từ các nguồn đáng tin cậy như Bộ GD&ĐT hoặc các trường THPT.

### Tính Sử Dụng, Áp Dụng, Các Thành Phần Người Dùng

#### Đối Tượng Người Dùng

1. **Học sinh**
   - Luyện thi, theo dõi tiến độ học tập, và tham gia nhóm học tập.
2. **Giáo viên**
   - Chia sẻ bài giảng, đề thi, và theo dõi tiến bộ của học sinh.
3. **Quản trị viên**
   - Quản lý người dùng, kiểm duyệt nội dung đóng góp, và duy trì hệ thống.

#### Mô Hình Áp Dụng

- Phục vụ học sinh ôn thi trên toàn quốc.
- Tích hợp vào các trung tâm luyện thi hoặc trường học để hỗ trợ giảng dạy.

### Kết Quả Dự Kiến/Mong Muốn

- **Đối với học sinh**: Cải thiện kỹ năng làm bài, hiểu rõ điểm mạnh/yếu, và chuẩn bị tốt hơn cho kỳ thi THPTQG.

- **Đối với giáo viên**: Hỗ trợ trong việc chia sẻ tài liệu và đánh giá hiệu quả giảng dạy.

- **Đối với cộng đồng**: Tạo ra một hệ thống ôn thi mở, khuyến khích sự hợp tác và chia sẻ tài nguyên giáo dục.

- **Kỳ vọng phát triển**:

  - Hệ thống đạt ít nhất 10.000 người dùng trong năm đầu triển khai.
  - Tích hợp thêm tính năng học tập thông minh (AI) để cá nhân hóa trải nghiệm người dùng.
  - Hợp tác với các trường học và trung tâm luyện thi để mở rộng quy mô.

