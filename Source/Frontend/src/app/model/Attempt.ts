import { Answer } from "./Answer";
import { Question } from "./Question";
import { Result } from "./Result";

export interface Attempt {
    attemptId: string;
    userId: string;
    result: Result | null;
    questions:Question[];
    userAnswer:Answer[];
    examId:string;
    examName:string;
    totalTime:number;
    isNew: boolean;
    timestamp:Date;
}