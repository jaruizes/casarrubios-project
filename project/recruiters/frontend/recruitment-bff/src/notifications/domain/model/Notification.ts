export interface Notification {
  id: string;
  applicationId: string
  positionId: number;
  type: string;
  data? : any;
}