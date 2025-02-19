import { Component } from '@angular/core';

@Component({
  selector: 'app-communities',
  templateUrl: './communities.component.html',
  styleUrls: ['./communities.component.css'],
  standalone: false
})
export class CommunitiesComponent {
  communities = [
    { name: 'Math', image: 'assets/math.webp', quote: 'Mathematics is the most beautiful and most powerful creation of the human spirit.' },
    { name: 'Physics', image: 'assets/physics.png', quote: 'Physics is the most fundamental of all sciences.' },
    { name: 'Chemistry', image: 'assets/chemistry.png', quote: 'Chemistry is the central science.' },
    { name: 'Biology', image: 'assets/biology.png', quote: 'Biology is the study of life.' },
    { name: 'History', image: 'assets/history.png', quote: 'History is written by the victors.' },
    { name: 'Geography', image: 'assets/geography.png', quote: 'Geography is the study of places and the relationships between people and their environments.' },
    { name: 'Civics', image: 'assets/civics.png', quote: 'Civics teaches the skills needed to be an informed and active citizen.' }
  ];
}
