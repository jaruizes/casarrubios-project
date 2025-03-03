import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {PaginatedPositions, Position} from "../model/position";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PositionsService {
  private readonly baseUrl = environment.api.positions;

  constructor(private http: HttpClient) {}

  /**
   * Crear una nueva posición
   * @param position Datos de la posición a crear
   * @returns Observable de la posición creada
   */
  createPosition(position: Position): Observable<Position> {
    return this.http.post<Position>(`${this.baseUrl}`, position);
  }

  /**
   * Recuperar todas las posiciones con criterios opcionales
   * @param status (opcional) Estado de las posiciones a recuperar
   * @param page
   * @param pageSize
   * @returns Observable con la lista de posiciones
   */
  getAllPositions(page: number = 0, pageSize: number = 10): Observable<PaginatedPositions> {
    let params = new HttpParams();
    params = params.set('page', page.toString());
    params = params.set('limit', pageSize.toString());

    return this.http.get<PaginatedPositions>(`${this.baseUrl}`, { params });
  }

  /**
   * Recuperar los detalles de una posición por su ID
   * @param id ID de la posición
   * @returns Observable de la posición
   */
  getPositionById(id: number): Observable<Position> {
    return this.http.get<Position>(`${this.baseUrl}/${id}`);
  }

  /**
   * Actualizar los datos de una posición
   * @param id ID de la posición a actualizar
   * @param position Datos actualizados de la posición
   * @returns Observable de la posición actualizada
   */
  updatePosition(id: number, position: Position): Observable<Position> {
    return this.http.put<Position>(`${this.baseUrl}/${id}`, position);
  }
}
