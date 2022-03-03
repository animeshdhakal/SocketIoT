export interface DeviceInterface {
  name: string;
  token: string;
  blueprint_id: string;
  online: boolean;
  lastIP: string;
}

export interface DeviceRes {
  devices: DeviceInterface[];
}
