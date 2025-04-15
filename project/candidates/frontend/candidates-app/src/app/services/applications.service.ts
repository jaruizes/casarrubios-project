import { Injectable } from '@angular/core';
import {HttpClient } from "@angular/common/http";
import {Observable} from "rxjs";
import {Application} from "../model/application";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {
  private readonly baseUrl = environment.api.applications;

  constructor(private http: HttpClient) {}

  applyToPosition(application: Application, file: File): Observable<Application> {
    const formData = new FormData();
    formData.append('positionId', application.positionId.toString());
    formData.append('candidate', JSON.stringify(application.candidate));
    formData.append('cv', file, file.name);


    return this.http.post<Application>(`${this.baseUrl}`, formData);
  }
}
