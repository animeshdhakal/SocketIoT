export enum PinMode {
  input,
  output,
}

export default interface IWidget {
  x?: number;
  y?: number;
  pinMode?: PinMode;
  pin?: number;
  type?: string;
  value?: string;
  name?: string;
}
