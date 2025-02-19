import { Option } from "./Option";

export interface Question{
    examId: string;
    questionId: string;
    content: any;
    options: Option[];
    questionOrder: number;
    isNew: boolean;
}