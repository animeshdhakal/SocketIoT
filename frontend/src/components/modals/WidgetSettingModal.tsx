import React from "react";
import IWidgetSetting from "../../interfaces/IWidgetSetting";
import ButtonSettingModal from "./widgets/ButtonSettingModal";
import LabelSettingModal from "./widgets/LabelSettingModal";
import IUniversalWidget from "../../interfaces/IUniversalWidget";

const WidgetSettingModal: React.FC<IWidgetSetting<IUniversalWidget>> = ({
  widget,
  ...rest
}) => {
  if (widget.type == "BUTTON") {
    return <ButtonSettingModal widget={widget} {...rest} />;
  }
  if (widget.type == "LABEL") {
    return <LabelSettingModal widget={widget} {...rest} />;
  }
  return null;
};

export default WidgetSettingModal;
