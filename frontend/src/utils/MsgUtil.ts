import { HEADER_SIZE } from "../config/Protocol";

export const create_message = (msg_type: number, ...args: any) => {
  let msg = args.map(String).join("\0");
  let msgBuff = new ArrayBuffer(msg.length + HEADER_SIZE);
  let msgDataView = new DataView(msgBuff);

  msgDataView.setInt16(0, msg.length);
  msgDataView.setInt16(2, msg_type);

  for (var i = 0; i < msg.length; i++) {
    msgDataView.setUint8(i + HEADER_SIZE, msg.charCodeAt(i));
  }
  return msgBuff;
};

export const parse_message = (msg: ArrayBuffer) => {
  const view = new DataView(msg);
  const len = view.getInt16(0);
  const type = view.getInt16(2);
  const rawBody = String.fromCharCode.apply(
    null,
    Array.from(new Uint8Array(msg, HEADER_SIZE, len))
  );
  const body = rawBody.split("\0");

  return {
    type,
    body,
  };
};
