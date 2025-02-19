export interface ExamRequest {
    id:string;
    name: string;
    subject: string;
    duration: number;
    provider: string;
    numberOfQuestion: number;
    difficultyLevel: string;
    due:string;
    maxAttempts:number;
    classId:string;
}