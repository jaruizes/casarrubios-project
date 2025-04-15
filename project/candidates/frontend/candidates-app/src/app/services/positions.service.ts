import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {PaginatedPositions, Position} from "../model/position";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PositionsService {
  private readonly baseUrl = environment.api.positions;

  constructor(private http: HttpClient) {}

  getAllPositions(page: number = 0, pageSize: number = 10): Observable<PaginatedPositions> {
    let params = new HttpParams();
    params = params.set('page', page.toString());
    params = params.set('limit', pageSize.toString());

    return this.http.get<PaginatedPositions>(`${this.baseUrl}`, { params });
  }

  getPositionById(id: number): Observable<Position> {
    return this.http.get<Position>(`${this.baseUrl}/${id}`);
  }
}
