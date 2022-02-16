import Widget from "./Widget";

export default interface Button extends Widget {
  type: "button";
  onLabel: string;
  offLabel: string;
  onValue: string;
  offValue: string;
}
