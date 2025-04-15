export interface Notification {
  id: string;
  applicationId: string
  positionId: number;
  type: string;
  seen: boolean;
  data? : any;
}
