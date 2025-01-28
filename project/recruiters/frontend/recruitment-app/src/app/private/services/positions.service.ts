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
   * @returns Observable con la lista de posiciones
   */
  getAllPositions(status?: number): Observable<Position[]> {
    let params = new HttpParams();
    if (status !== undefined) {
      params = params.set('status', status.toString());
    }
    return this.http.get<Position[]>(`${this.baseUrl}`, { params });
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
