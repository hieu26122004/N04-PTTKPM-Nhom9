import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamService } from '../service/exam.service';
import { Exam } from '../model/Exam';
import { Location } from '@angular/common';
import { Comment } from '../model/Comment';
import { CommentService } from '../service/comment.service';

@Component({
  selector: 'app-exam-detail',
  templateUrl: './exam-detail.component.html',
  styleUrls: ['./exam-detail.component.css'],
  standalone: false,
})
export class ExamDetailComponent implements OnInit {
  examId!: string;
  exam!: Exam;
  comments: Comment[] = [];
  showEditor: boolean = false;
  commentContent: string = '';
  isLoadingComments: boolean = false;

  editorModules = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      [{ script: 'sub' }, { script: 'super' }],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image', 'code-block'],
      [{ color: [] }, { background: [] }],
    ],
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private examService: ExamService,
    private location: Location,
    private router: Router,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    const examData = history.state.exam;
    if (examData) {
      this.exam = examData;
      this.examId = this.exam.id;
      this.loadCommentsForExam();
    } else {
      this.activatedRoute.paramMap.subscribe((params) => {
        this.examId = params.get('id')!;
        this.loadExamDetails();
        this.loadCommentsForExam();
      });
    }
  }

  loadExamDetails(): void {
    this.examService.getExamById(this.examId).subscribe({
      next: (examData: Exam) => {
        this.exam = examData;
      },
      error: (err) => {
        console.error('Error fetching exam details:', err);
      },
    });
  }

  loadCommentsForExam(): void {
    if (!this.examId) {
      console.warn('Exam ID is missing!');
      return;
    }

    this.commentService.getCommentByExamId(this.examId, null, null, null).subscribe({
      next: (comments: Comment[]) => {
        this.comments = comments;
      },
      error: (err) => {
        console.error('Error fetching comments:', err);
      },
    });
  }

  goBack(): void {
    this.location.back();
  }

  takeExam(): void {
    this.router.navigate(['/take', this.exam.id], { state: { exam: this.exam } });
  }

  toggleComments(): void {
    this.showEditor = !this.showEditor;
  }

  submitComment(): void {
    if (!this.commentContent.trim()) {
      console.warn('Reply content is empty!');
      return;
    }

    const newComment: Comment = {
      id: null,
      author: '', // Replace with user info if available
      examId: this.examId,
      content: this.commentContent,
      createdAt: new Date(),
      lastUpdated: new Date(),
      parentId: null, 
      replies: 0,
      deleted: false,
    };

    this.createComment(newComment);
    this.commentContent = '';
    this.showEditor = false;
  }

  createComment(comment: Comment): void {
    this.commentService.createComment(comment, this.examId).subscribe({
      next: (createdComment: Comment) => {
        this.comments.unshift(createdComment); // Add new comment to the top
      },
      error: (err) => {
        console.error('Error creating comment:', err);
      },
    });
  }

  loadMoreComments(): void {
    if (!this.comments.length) return;

    this.isLoadingComments = true;
    const lastComment = this.comments[this.comments.length - 1];
    this.commentService
      .getCommentByExamId(this.examId, null, lastComment.lastUpdated, lastComment.id)
      .subscribe({
        next: (moreComments: Comment[]) => {
          this.comments.push(...moreComments);
          this.isLoadingComments = false;
        },
        error: (err) => {
          console.error('Error fetching more comments:', err);
          this.isLoadingComments = false;
        },
      });
  }
}
