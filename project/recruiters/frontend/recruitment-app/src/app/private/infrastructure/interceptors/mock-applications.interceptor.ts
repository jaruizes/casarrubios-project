import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";
import {Application, ApplicationDetail} from "../../model/application";

@Injectable()
export class MockApplicationsInterceptor implements HttpInterceptor {
  private mockData: Application[] = [
    {
      id: 1,
      candidate: 'Juan Palomo',
      tags: [{ name: 'Angular' }, { name: 'TypeScript' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-01-22T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 80 },
        { id: 3, matchingPercentage: 60 },
      ]
    },
    {
      id: 2,
      candidate: 'María García',
      tags: [{ name: 'Node.js' }, { name: 'Express' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-01-24T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 90 },
        { id: 3, matchingPercentage: 70 },
      ]
    },
    {
      id: 3,
      candidate: 'Pedro Pérez',
      tags: [{ name: 'Python' }, { name: 'Machine Learning' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-01-26T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 80 },
        { id: 3, matchingPercentage: 60 },
      ]
    },
    {
      id: 4,
      candidate: 'Ana López',
      tags: [{ name: 'AWS' }, { name: 'Terraform' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-01-28T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 90 },
        { id: 3, matchingPercentage: 70 },
      ]
    },
    {
      id: 5,
      candidate: 'Carlos Sánchez',
      tags: [{ name: 'Docker' }, { name: 'Kubernetes' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-01-30T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 80 },
        { id: 3, matchingPercentage: 60 }
      ]
    },
    {
      id: 6,
      candidate: 'Sara Martínez',
      tags: [{ name: 'React' }, { name: 'Redux' }],
      cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf",
      applicationDate: '2025-02-01T10:23:08.350Z',
      positionsApplied: [
        { id: 1, matchingPercentage: 100 },
        { id: 2, matchingPercentage: 90 },
        { id: 3, matchingPercentage: 70 }
      ]
    }
  ];

  private mockApplicationDetailData: ApplicationDetail[] = [
    {
      id: 1,
      position: { id: 1, title: 'Frontend Developer', createdAt: '2025-01-22T10:23:08.350Z' },
      applicationDate: '2025-01-22T10:23:08.350Z',
      candidate: {
        name: 'Juan',
        lastName: 'Palomo',
        email: 'prueba@email.com',
        phone: '123456789',
        tags: [{ name: 'Angular' }, { name: 'TypeScript' }],
        totalExperience: 3,
        currentRole: 'Frontend Developer',
        summary: 'Frontend developer with 3 years of experience',
        cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf"
      },
      matchingPercentage: 100,
      questions: ['What is Angular?', 'What is TypeScript?'],
      analysis: 'Juan has a strong background in frontend development'
    },
    {
      id: 2,
      position: { id: 2, title: 'Backend Developer', createdAt: '2025-01-24T10:23:08.350Z' },
      applicationDate: '2025-01-24T10:23:08.350Z',
      candidate: {
        name: 'María',
        lastName: 'García',
        email: 'prueba@email.com',
        phone: '123456789',
        tags: [{ name: 'Node.js' }, { name: 'Express' }],
        totalExperience: 4,
        currentRole: 'Backend Developer',
        summary: 'Backend developer with 4 years of experience',
        cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf"
      },
      matchingPercentage: 90,
      questions: ['What is Node.js?', 'What is Express?'],
      analysis: 'María has a solid background in backend development'
    },
    {
      id: 3,
      position: {id: 3, title: 'Data Scientist', createdAt: '2025-01-26T10:23:08.350Z'},
      applicationDate: '2025-01-26T10:23:08.350Z',
      candidate: {
        name: 'Pedro',
        lastName: 'Pérez',
        email: 'prueba@email.com',
        phone: '123456789',
        tags: [{name: 'Python'}, {name: 'Machine Learning'}],
        totalExperience: 2,
        currentRole: 'Data Scientist',
        summary: 'Data scientist with 2 years of experience',
        cv: "/assets/cvs/Fictional_AI_Expert_CV_Spanish.pdf"
      },
      matchingPercentage: 90,
      questions: ['What is Node.js?', 'What is Express?'],
      analysis: 'María has a solid background in backend development'
    }
  ]

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('MockCandidatesInterceptor: ' + req.url);

    if (req.url.includes('https://api.example.com/applications')) {
      const url = new URL(req.urlWithParams);

      if (req.method === 'GET' && req.url.match(/\/\d+$/)) {
        const applicationId = parseInt(req.url.split('/').pop() || '0', 10);
        const application = this.mockApplicationDetailData.find((application) => application.id === applicationId);
        if (application) {
          return of(new HttpResponse({ status: 200, body: application }));
        }
        return of(new HttpResponse({ status: 404 }));
      }

      if (req.method === 'GET' && url.searchParams.has('position')) {
        // const positionId = parseInt(url.searchParams.get('position') || '0', 10);
        return of(new HttpResponse({ status: 200, body: this.mockData }));
      }
    }
    return next.handle(req);
  }

}
