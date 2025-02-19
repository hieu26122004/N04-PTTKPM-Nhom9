import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeService {

  constructor() { }

  /**
   * Chuyển đổi mảng LocalDateTime từ backend thành chuỗi ISO-8601
   * @param dateArray Mảng số đại diện cho [year, month, day, hour, minute, second, nano]
   * @returns Chuỗi ISO-8601 hoặc null nếu input không hợp lệ
   */
  parseLocalDateTime(dateArray: any): string | null {
    // Kiểm tra xem dateArray có phải là một mảng
    if (!Array.isArray(dateArray)) {
      console.error('Invalid date array: Not an array', dateArray);
      return null;
    }
  
    // Kiểm tra tất cả các phần tử trong mảng có phải là số
    const isValidArray = dateArray.every((item) => typeof item === 'number');
  
    if (!isValidArray || dateArray.length < 6) {
      console.error('Invalid date array: Must be a number array with at least 6 elements', dateArray);
      return null;
    }
  
    // Chuyển đổi
    const [year, month, day, hour, minute, second, nano] = dateArray;
    const millisecond = nano ? Math.floor(nano / 1e6) : 0;
  
    try {
      const date = new Date(year, month - 1, day, hour, minute, second, millisecond);
      const isoString = date.toISOString(); // Trả về chuỗi ISO-8601
      return isoString.endsWith('Z') ? isoString.slice(0, -1) : isoString; // Xóa chữ Z nếu tồn tại
    } catch (error) {
      console.error('Error converting LocalDateTime:', error);
      return null;
    }
  }

  convertDateToLocalDateTimeString(date: Date | string): string | null {
    try {
      // Nếu input là chuỗi, chuyển sang đối tượng Date
      const dateObj = typeof date === 'string' ? new Date(date) : date;
  
      if (isNaN(dateObj.getTime())) {
        console.error('Invalid date input:', date);
        return null;
      }
  
      // Lấy các thành phần của ngày
      const year = dateObj.getFullYear();
      const month = String(dateObj.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
      const day = String(dateObj.getDate()).padStart(2, '0');
  
      const hours = String(dateObj.getHours()).padStart(2, '0');
      const minutes = String(dateObj.getMinutes()).padStart(2, '0');
      const seconds = String(dateObj.getSeconds()).padStart(2, '0');
  
      const milliseconds = String(dateObj.getMilliseconds()).padStart(3, '0');
  
      // Kết hợp thành chuỗi LocalDateTime
      return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}.${milliseconds}`;
    } catch (error) {
      console.error('Error converting date to LocalDateTime string:', error);
      return null;
    }
  }
  
}
