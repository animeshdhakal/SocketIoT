import React from "react";
import IWidgetSetting from "../../interfaces/IWidgetSetting";
import ButtonSettingModal from "./widgets/ButtonSettingModal";

const WidgetSettingModal: React.FC<IWidgetSetting> = ({ widget, ...rest }) => {
  if (widget.type == "BUTTON") {
    return <ButtonSettingModal widget={widget} {...rest} />;
  }
  return null;
};

export default WidgetSettingModal;
