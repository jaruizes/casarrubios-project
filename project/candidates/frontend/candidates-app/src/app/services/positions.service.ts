import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Position} from "../model/position";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PositionsService {
  private readonly baseUrl = 'https://api.example.com/positions'; // URL base del servicio REST

  constructor(private http: HttpClient) {}

  getAllPositions(status?: number): Observable<Position[]> {
    let params = new HttpParams();
    if (status !== undefined) {
      params = params.set('status', status.toString());
    }
    return this.http.get<Position[]>(`${this.baseUrl}`, { params });
  }

  getPositionById(id: number): Observable<Position> {
    return this.http.get<Position>(`${this.baseUrl}/${id}`);
  }
}
