with open("../server/core/src/main/java/app/socketiot/server/core/model/enums/MsgType.java") as f:
    s = f.read()
    f.close()


classSplit = s.split(" ")

classname = classSplit[classSplit.index("class")+1]

variables = s.replace("\n", "").split("{")[1].split("}")[0].split(";")

ts = "enum " + classname + " {\n"

for variable in variables:
    stripped = variable.strip()
    if stripped:
        statement = stripped.split("=")[0].strip().split(" ")[-1]
        value = stripped.split("=")[1].strip()  
        ts += "    " + statement + " = " + value + ",\n"

ts += "}\n\n"

ts += "export default " + classname + ";"


with open("src/protocol/MsgType.ts", "w") as f:
    f.write(ts)
    f.close()
