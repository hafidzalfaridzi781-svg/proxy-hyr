import os, subprocess, sys

os.makedirs("app/src/main/assets", exist_ok=True)
subprocess.run([sys.executable, "scripts/gen_patch.py"], check=True)
subprocess.run([sys.executable, "scripts/gen_shader.py"], check=True)
subprocess.run([sys.executable, "scripts/gen_vqcfg.py"], check=True)
print("[OK] All binary assets generated")
