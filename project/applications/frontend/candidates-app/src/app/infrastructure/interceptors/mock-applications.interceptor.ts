import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";
import {Application} from "../../model/application";

@Injectable()
export class MockApplicationsInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('MockCandidatesInterceptor: ' + req.url);
    if (req.url.includes('https://api.example.com/candidates')) {
      const url = new URL(req.urlWithParams);

      if (req.method === 'GET' && url.searchParams.has('position')) {
        // const positionId = parseInt(url.searchParams.get('position') || '0', 10);
        return of(new HttpResponse({ status: 200, body: {} }));
      }

      if (req.method === 'POST') {
        console.log(req.body);
        return of(new HttpResponse({ status: 201 }));
      }
    }
    return next.handle(req);
  }
}
