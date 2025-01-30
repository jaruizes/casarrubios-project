import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Position} from "../model/position";
import {Observable} from "rxjs";
import {Application} from "../model/application";

@Injectable({
  providedIn: 'root'
})
export class ApplicationsService {

  private readonly baseUrl = 'https://api.example.com/candidates'; // URL base del servicio REST

  constructor(private http: HttpClient) {}

  getCandidatesByPosition(position: number): Observable<Application[]> {
    let params = new HttpParams();
    params = params.set('position', position.toString());
    return this.http.get<Application[]>(`${this.baseUrl}`, { params });
  }
}
