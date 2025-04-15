import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {Position} from "../../model/position";

@Injectable()
export class MockPositionsInterceptor implements HttpInterceptor {
  private mockData: Position[] = [
    {
      id: 1,
      title: 'Frontend Developer',
      description: 'Develop user interfaces with Angular',
      tags: [{ name: 'Angular' }, { name: 'TypeScript' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '2 years', description: 'Frontend experience', mandatory: true },
        { key: 'Skill', value: 'Angular', description: 'Proficiency in Angular framework', mandatory: false },
      ],
      tasks: [{ description: 'Develop reusable components' }],
      benefits: [{ description: 'Flexible hours' }, { description: 'Remote work' }],
      applications: 10,
      creationDate: '2025-01-22T10:23:08.350Z',
    },
    {
      id: 2,
      title: 'Backend Developer',
      description: 'Build robust APIs with Node.js',
      tags: [{ name: 'Node.js' }, { name: 'Express' }],
      status: 2,
      requirements: [
        { key: 'Experience', value: '3 years', description: 'Backend experience', mandatory: true },
        { key: 'Skill', value: 'Node.js', description: 'Proficiency in Node.js', mandatory: true },
      ],
      tasks: [{ description: 'Design RESTful APIs' }],
      benefits: [{ description: 'Health insurance' }, { description: 'Stock options' }],
      applications: 5,
      creationDate: '2025-01-24T10:23:08.350Z',
    },
    {
      id: 3,
      title: 'Data Scientist',
      description: 'Analyze data and build predictive models',
      tags: [{ name: 'Python' }, { name: 'Machine Learning' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '2 years', description: 'Data analysis experience', mandatory: true },
        { key: 'Skill', value: 'Python', description: 'Proficiency in Python', mandatory: true },
      ],
      tasks: [{ description: 'Build predictive models' }],
      benefits: [{ description: 'Flexible hours' }, { description: 'Training budget' }],
      applications: 12,
      creationDate: '2025-01-26T10:23:08.350Z',
    },
    {
      id: 4,
      title: 'DevOps Engineer',
      description: 'Manage infrastructure and deployment pipelines',
      tags: [{ name: 'AWS' }, { name: 'Terraform' }],
      status: 1,
      requirements: [
        { key: 'Experience', value: '3 years', description: 'DevOps experience', mandatory: true },
        { key: 'Skill', value: 'Terraform', description: 'Experience with Terraform and AWS', mandatory: true },
      ],
      tasks: [{ description: 'Automate CI/CD pipelines' }],
      benefits: [{ description: 'Stock options' }, { description: 'Remote work' }],
      applications: 8,
      creationDate: '2025-01-27T10:23:08.350Z',
    },
    {
      id: 5,
      title: 'Product Manager',
      description: 'Lead product development and strategy',
      tags: [{ name: 'Leadership' }, { name: 'Product Management' }],
      status: 2,
      requirements: [
        { key: 'Experience', value: '5 years', description: 'Product management experience', mandatory: true },
        { key: 'Skill', value: 'Leadership', description: 'Strong leadership skills', mandatory: true },
      ],
      tasks: [{ description: 'Define product roadmap' }],
      benefits: [{ description: 'Company car' }, { description: 'Health insurance' }],
      applications: 15,
      creationDate: '2025-01-29T10:23:08.350Z',
    }
  ];

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes('https://api.example.com/positions')) {
      if (req.method === 'GET' && req.url.match(/\/\d+$/)) {
        const id = parseInt(req.url.split('/').pop() || '0', 10);
        const position = this.mockData.find((pos) => pos.id === id);
        if (position) {
          return of(new HttpResponse({ status: 200, body: position }));
        }
        return of(new HttpResponse({ status: 404 }));
      }

      if (req.method === 'GET') {
        if (req.url.includes('?status=')) {
          const status = parseInt(req.url.split('status=')[1], 10);
          const filteredData = this.mockData.filter((position) => position.status === status);
          return of(new HttpResponse({ status: 200, body: filteredData }));
        }
        return of(new HttpResponse({ status: 200, body: this.mockData }));
      }

      if (req.method === 'POST') {
        const newPosition = { ...req.body, id: this.mockData.length + 1 };
        newPosition.applications = 0;
        newPosition.creationDate = new Date().toISOString();

        this.mockData.push(newPosition);
        return of(new HttpResponse({ status: 201, body: newPosition }));
      }

      if (req.method === 'PUT') {
        const id = parseInt(req.url.split('/').pop() || '0', 10);
        const index = this.mockData.findIndex((position) => position.id === id);
        if (index > -1) {
          this.mockData[index] = { ...req.body, id };
          return of(new HttpResponse({ status: 200, body: this.mockData[index] }));
        }
        return of(new HttpResponse({ status: 404 }));
      }


    }
    return next.handle(req);
  }
}
