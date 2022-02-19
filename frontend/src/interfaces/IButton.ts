import Widget from "./IWidget";

export default interface IButton extends Widget {
  type?: "button";
  onLabel?: string;
  offLabel?: string;
  onValue?: string;
  offValue?: string;
}
