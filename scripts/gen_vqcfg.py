import struct, os

def build():
    b = bytearray()
    b += struct.pack("<d", 1.0)
    b += b"\x00" * 88
    for _ in range(5): b += struct.pack("<f", 1.0)
    b += b"\x00" * 200
    b += struct.pack("<f", 1.0)
    b += b"\x00" * 200
    values = [1.0,70.0,200.0,6.0,4.0,2.5,0.7,0.85,45.0,200.0,
              12.0,220.0,8.0,45.0,190.0,180.0,12.0,3.5,1.0,12.0,
              6.0,2.0,1.0,100.0,0.0,0.0,100.0,0.0,0.0,1.0,
              600.0,0.0,0.0,0.0,200.0,110.0,105.0,1.0,220.0,
              180.0,90.0,6.0,6.0,10.0,8.0,3.0,0.6145]
    for v in values: b += struct.pack("<f", v)
    while len(b) % 8: b += b"\x00"
    return bytes(b)

if __name__ == "__main__":
    os.makedirs("app/src/main/assets", exist_ok=True)
    data = build()
    out = "app/src/main/assets/vqcfg.bin"
    with open(out, "wb") as f: f.write(data)
    print(f"[OK] {out}: {len(data)} bytes")
