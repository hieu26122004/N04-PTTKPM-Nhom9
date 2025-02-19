import { Answer } from "./Answer";

export class ExamSession {
    sessionId: string;
    userId: string;
    examId: string;
    examName:string;
    userAnswers: Answer[];
    startTime: Date;
    totalTime: number;
    remainTime: number;
    expirationTime: number;
    expired: boolean;
}