import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Position} from "../model/position";
import {Observable} from "rxjs";
import {Candidate} from "../model/candidate";

@Injectable({
  providedIn: 'root'
})
export class CandidatesService {

  private readonly baseUrl = 'https://api.example.com/candidates'; // URL base del servicio REST

  constructor(private http: HttpClient) {}

  getCandidatesByPosition(position: number): Observable<Candidate[]> {
    let params = new HttpParams();
    params = params.set('position', position.toString());
    return this.http.get<Candidate[]>(`${this.baseUrl}`, { params });
  }
}
