package app.socketiot.server.api.model;

public class GAIIntent {
    static public class Input {
        static public class Payload {
            static public class Device {
                static public class Name {
                    public String name;
                    public String defaultNames[];
                    public String nicknames[];
                }

                static public class DeviceInfo {
                    public String manufacturer;
                    public String model;
                    public String hwVersion;
                    public String swVersion;
                }

                public String id;
                public String type;
                public String traits[];

                public Name name;
            }

            public Device[] devices;
        }

        public String intent;
        public Payload payload;
    }

    public String requestId;
    public Input[] inputs;

    public GAIIntent() {
    }

}
