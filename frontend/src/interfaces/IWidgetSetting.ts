import IUniversalWidget from "./IUniversalWidget";
import React from "react";

export interface IWidgetSettingWidget {
  type: string;
  index: number;
}

export default interface IWidgetSetting<T> {
  widget: IWidgetSettingWidget;
  widgets: T[];
  setWidgets: React.Dispatch<React.SetStateAction<IUniversalWidget[]>>;
  setWidget: React.Dispatch<React.SetStateAction<IWidgetSettingWidget>>;
}
