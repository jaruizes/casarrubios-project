import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Position} from "../model/position";
import {Observable} from "rxjs";
import {Application, ApplicationDetail} from "../model/application";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  private readonly baseUrl = 'https://api.example.com/applications';

  constructor(private http: HttpClient) {}

  getApplicationsByPosition(position: number): Observable<Application[]> {
    let params = new HttpParams();
    params = params.set('position', position.toString());
    return this.http.get<Application[]>(`${this.baseUrl}`, { params });
  }

  getApplicationDetail(id: number): Observable<ApplicationDetail> {
    return this.http.get<ApplicationDetail>(`${this.baseUrl}/${id}`);
  }
}
