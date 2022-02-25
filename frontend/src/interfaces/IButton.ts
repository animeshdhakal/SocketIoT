import Widget from "./IWidget";

export default interface IButton extends Widget {
  type?: string;
  onLabel?: string;
  offLabel?: string;
  onValue?: string;
  offValue?: string;
  setValue?: (pin: number, value: string) => void;
}
