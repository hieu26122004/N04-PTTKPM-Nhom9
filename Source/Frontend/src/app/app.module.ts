import {ExamsComponent} from './exams/exams.component';
import {LoginComponent} from './login/login.component';
import {RouterModule, Routes} from '@angular/router';
import {CreateExamComponent} from './create-exam/create-exam.component';
import {CreateQuestionComponent} from './create-question/create-question.component';
import {ExamDetailComponent} from './exam-detail/exam-detail.component';
import {TakeExamComponent} from './take-exam/take-exam.component';
import {AttemptDetailsComponent} from './attempt-details/attempt-details.component';
import {AttemptsComponent} from './attempts/attempts.component';
import {ClassDetailsComponent} from './class-details/class-details.component';
import {CommunitiesComponent} from './communities/communities.component';
import {CommunityDetailsComponent} from './community-details/community-details.component';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home/home.component';
import {NavComponent} from './nav/nav.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {ExamComponent} from './exam/exam.component';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {QuillModule} from 'ngx-quill';
import {AuthInterceptor} from './auth.interceptor';
import {AttemptComponent} from './attempt/attempt.component';
import { ExpandedCommunityComponent } from './expanded-community/expanded-community.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { MessagesComponent } from './messages/messages.component';
import { SettingsComponent } from './settings/settings.component';
import { ProfileComponent } from './profile/profile.component';
import { CategoryComponent } from './category/category.component';
import { CommentComponent } from './comment/comment.component';
import { ParticipantsComponent } from './participants/participants.component';
import { AnnouncementsComponent } from './announcements/announcements.component';
import { MaterialsComponent } from './materials/materials.component';
import { AssignmentsComponent } from './assignments/assignments.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';


const routes: Routes = [
  { path: '', component: ExamsComponent },
  { path: 'home', component: ExamsComponent},
  { path: 'login', component: LoginComponent },
  { path: 'exams', component: ExamsComponent },
  { path: 'create', component: CreateExamComponent },
  { path: 'create-question', component: CreateQuestionComponent },
  { path: 'exam/:id', component: ExamDetailComponent },
  { path: 'take/:id', component: TakeExamComponent },
  { path: 'attempt-details/:attemptId', component: AttemptDetailsComponent },
  { path: 'history', component: AttemptsComponent },
  { path: 'class/:id', component: ClassDetailsComponent },
  { path: 'communities', component: CommunitiesComponent },
  { path: 'community/:id', component: CommunityDetailsComponent },
  { path: 'community-expand/:id', component: ExpandedCommunityComponent },
  { path: 'about-us', component: AboutUsComponent },
  { path: 'messages', component: MessagesComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'categories', component: CategoryComponent }
  
];

@NgModule({
  declarations: [
    AppComponent , // Giữ AppComponent trong declarations
    LoginComponent,
    HomeComponent,
    NavComponent,
    SidebarComponent,
    ExamsComponent,
    SearchBarComponent,
    ExamComponent,
    CreateExamComponent,
    CreateQuestionComponent,
    ExamDetailComponent,
    TakeExamComponent,
    AttemptDetailsComponent,
    AttemptsComponent,
    AttemptComponent,
    ClassDetailsComponent,
    CommunitiesComponent,
    CommunityDetailsComponent,
    ExpandedCommunityComponent,
    AboutUsComponent,
    MessagesComponent,
    SettingsComponent,
    ProfileComponent,
    CategoryComponent,
    CommentComponent,
    ParticipantsComponent,
    AnnouncementsComponent,
    MaterialsComponent,
    AssignmentsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    ReactiveFormsModule,
    QuillModule.forRoot(),
    CommonModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatCardModule,
    MatTabsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
