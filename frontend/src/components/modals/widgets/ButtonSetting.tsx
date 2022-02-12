import React from "react";
import WidgetSettingModal from "./WigetSettingModal";

interface Props {
  show: boolean;
  onClose: () => void;
  onCreate: () => void;
  index: number;
  type: string;
  [key: string]: any;
}

const ButtonSetting: React.FC<Props> = ({ index, type, state, ...rest }) => {
  return null;
};

export default ButtonSetting;
