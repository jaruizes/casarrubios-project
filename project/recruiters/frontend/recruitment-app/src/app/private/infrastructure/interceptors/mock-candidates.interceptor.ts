import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";
import {Candidate} from "../../model/candidate";

@Injectable()
export class MockCandidatesInterceptor implements HttpInterceptor {
  private mockData: Candidate[] = [
    {
      id: 1,
      name: 'Juan Palomo',
      tags: [{ name: 'Angular' }, { name: 'TypeScript' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-01-22T10:23:08.350Z',
    },
    {
      id: 2,
      name: 'María García',
      tags: [{ name: 'Node.js' }, { name: 'Express' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-01-24T10:23:08.350Z',
    },
    {
      id: 3,
      name: 'Pedro Pérez',
      tags: [{ name: 'Python' }, { name: 'Machine Learning' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-01-26T10:23:08.350Z',
    },
    {
      id: 4,
      name: 'Ana López',
      tags: [{ name: 'AWS' }, { name: 'Terraform' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-01-28T10:23:08.350Z',
    },
    {
      id: 5,
      name: 'Carlos Sánchez',
      tags: [{ name: 'Docker' }, { name: 'Kubernetes' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-01-30T10:23:08.350Z',
    },
    {
      id: 6,
      name: 'Sara Martínez',
      tags: [{ name: 'React' }, { name: 'Redux' }],
      cv: "/assets/cvs/cv.pdf",
      applicationDate: '2025-02-01T10:23:08.350Z',
    }
  ];

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('MockCandidatesInterceptor: ' + req.url);
    if (req.url.includes('https://api.example.com/candidates')) {
      const url = new URL(req.urlWithParams);

      if (req.method === 'GET' && url.searchParams.has('position')) {
        // const positionId = parseInt(url.searchParams.get('position') || '0', 10);
        return of(new HttpResponse({ status: 200, body: this.mockData }));
      }
    }
    return next.handle(req);
  }
}
