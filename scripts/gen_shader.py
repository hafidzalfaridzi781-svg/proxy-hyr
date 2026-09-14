import struct, os

def sfield(s):
    b = s.encode("utf-8")
    return struct.pack("<I", len(b)) + b

def build():
    out = b""
    out += struct.pack("<I", 4)
    out += struct.pack("<I", 0)
    out += struct.pack("<I", 1)
    for _ in range(3):
        out += sfield("FOG_LINEAR")
        out += sfield("LINEAR_IN_GAMMA")
        out += sfield("BRNIGHT_ON")
        out += struct.pack("<I", 2)
        out += struct.pack("<I", 2)
    out += sfield("FOG_LINEAR")
    out += sfield("BRNIGHT_ON")
    out += struct.pack("<I", 1)
    out += sfield("LINEAR_IN_GAMMA")
    out += struct.pack("<I", 0)
    return out

if __name__ == "__main__":
    os.makedirs("app/src/main/assets", exist_ok=True)
    data = build()
    out = "app/src/main/assets/ShaderStripSettings"
    with open(out, "wb") as f: f.write(data)
    print(f"[OK] {out}: {len(data)} bytes")
