import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ApplicationDetail, PaginatedApplications} from "../../model/application";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  private readonly baseUrl = environment.api.applications;

  constructor(private http: HttpClient) {}

  getApplicationsByPosition(position: number, page: number = 0, pageSize: number = 10): Observable<PaginatedApplications> {
    let params = new HttpParams();
    params = params.set('positionId', position.toString());
    params = params.set('page', page.toString());
    params = params.set('pageSize', pageSize.toString());
    return this.http.get<PaginatedApplications>(`${this.baseUrl}`, { params });
  }

  getApplicationDetail(id: string): Observable<ApplicationDetail> {
    return this.http.get<ApplicationDetail>(`${this.baseUrl}/${id}`);
  }

  getApplicationCV(applicationId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${applicationId}/cv`, {
      responseType: 'blob'
    });
  }
}
