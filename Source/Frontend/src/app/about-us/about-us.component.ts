import { Component } from '@angular/core';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrl: './about-us.component.css',
  standalone: false
})
export class AboutUsComponent {
  projectName = "Practice Makes Perfect";
  purpose = "To create an interactive and engaging platform that empowers individuals to enhance their skills across various domains, with a particular focus on programming, through hands-on practice, real-time collaboration, and continuous improvement.";
  background = "The project, Practice Makes Perfect, is a culmination of my final year’s work. It was born from my personal belief that the key to mastery in any field, especially programming, is consistent practice and learning by doing. The idea behind this platform is simple yet powerful: by giving learners the tools, the resources, and the community to practice, they can achieve significant improvements in their skills over time"
  + "This platform serves as a sandbox where users can experiment, solve challenges, receive real-time feedback, and connect with other learners to share insights and grow together. Through the use of modern web technologies like Angular, WebSockets, and real-time data processing, this project aims to demonstrate the power of web-based learning environments. It also serves as a tool to explore how technology can be leveraged to build scalable, user-friendly, and interactive platforms in the field of education.";
  
  objectives = [
    "At the heart of this project is a user interface designed to be intuitive, interactive, and visually appealing. The goal is to enhance the learning experience by providing an engaging and easy-to-navigate platform that encourages users to explore new challenges and opportunities for growth.",
    "This platform is designed to allow users to practice real-world problems and exercises that will challenge their skills. Whether you’re learning programming, mathematics, or any other skill, this platform will provide relevant and meaningful challenges to enhance your abilities.",
    "We believe in the power of community learning. The platform includes real-time collaboration tools where users can work together on challenges, share solutions, and learn from each other. This builds not just skills, but a sense of belonging and shared achievement",
    "To deliver the best experience, we’ve used modern web technologies such as Angular for a responsive and dynamic front-end, WebSockets for real-time data exchange, and advanced data processing techniques to ensure high performance. This enables users to seamlessly interact with the platform and collaborate in real time, no matter where they are.",
    "The platform is designed to be fully responsive, meaning it works seamlessly across devices of all types, from desktops to mobile phones. Whether you’re at home or on the go, you can continue practicing and learning without disruption.",
    "As the number of users and challenges grows, the platform must be able to handle increased demand without sacrificing performance. This project is designed with scalability in mind, ensuring that we can easily add more users and features without compromising on quality.",
    "One of the key features of Practice Makes Perfect is the ability for users to track their progress. Through detailed feedback and performance tracking, users can monitor their improvement over time. This helps learners stay motivated and provides a clear view of their growth and areas that need attention.",
    "While creating a great user experience is crucial, ensuring security and performance is just as important. This project follows the best practices in web development, making sure that users’ data is secure, and the platform runs efficiently and smoothly."
  ];
  

  teamMembers = [
    {
      name: "John Doe",
      role: "Lead Developer",
      bio: "I am responsible for the backend development of the platform, ensuring that the system architecture is robust and capable of handling a growing number of users. I work on the data structures, real-time data processing, and server-side logic that powers the platform, making sure it’s both scalable and responsive."
      + "John brings a wealth of experience in backend development and has been instrumental in building the foundation of this platform, ensuring everything runs smoothly and efficiently."
    },
    {
      name: "Jane Smith",
      role: "Frontend Developer",
      bio: "My focus is on creating an intuitive, engaging user interface that is both aesthetically pleasing and easy to use. I collaborate closely with the design team to create an interface that not only looks great but also enhances the user experience."
      + "Jane's design skills shine through in every interaction within the platform. Her ability to balance functionality with a sleek and modern design has helped shape the visual identity of Practice Makes Perfect."
    },
    {
      name: "Michael Brown",
      role: "Project Manager",
      bio: "I ensure that the project stays on track, managing timelines, setting goals, and aligning all team members with the project’s vision. My job is to ensure that everything is running smoothly and that we stay true to our mission while delivering a high-quality product."
      + "As the project manager, Michael is responsible for ensuring that the team remains focused and that the project delivers on its promises. His leadership helps keep the team motivated and ensures everything is executed as planned."
    }
  ];
}
