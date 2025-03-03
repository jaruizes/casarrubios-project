import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Application, ApplicationDetail, PaginatedApplications, PositionApplied} from "../../model/application";
import {environment} from "../../../../environments/environment";
import {Tag} from "../../model/position";

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

  getApplicationDetail(id: number): Observable<ApplicationDetail> {
    return this.http.get<ApplicationDetail>(`${this.baseUrl}/${id}`);
  }
}
