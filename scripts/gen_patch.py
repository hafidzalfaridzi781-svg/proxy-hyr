import struct, hashlib, os

def build_patch():
    parts = []
    parts.append(bytes([0xCB, 0x52, 0xE2, 0x98, 0xE2]))

    strings = [
        "IFix.ILFixInterfaceBridge, Assembly-CSharp",
        "UnityEngine.Time, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Shader, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "System.String, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Single, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.IO.File, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Buffer, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Array, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Int32, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Boolean, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "COW.GameFacade, Assembly-CSharp",
        "COW.GamePlay.Player, Assembly-CSharp",
        "COW.EAimAssist, Assembly-CSharp",
        "UnityEngine.Camera, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.RenderSettings, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Component, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Transform, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Vector3, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Mathf, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.CharacterController, UnityEngine.PhysicsModule",
        "COW.GamePlay.CameraControllerManager, Assembly-CSharp",
        "COW.GamePlay.FollowCamera, Assembly-CSharp",
        "UnityEngine.Color, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "COW.GamePlay.AvatarManager, Assembly-CSharp",
        "COW.GameVarDef, Assembly-CSharp",
        "System.UInt32, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "UnityEngine.Screen, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "UnityEngine.Object, UnityEngine.CoreModule, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null",
        "System.Byte, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.IPAddress, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.IPEndPoint, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.Sockets.Socket, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.Sockets.AddressFamily, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.Sockets.SocketType, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.Sockets.ProtocolType, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Byte[], mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "System.Net.EndPoint, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089",
        "COW.GamePlay.FDAEPHMIEPC, Assembly-CSharp",
    ]
    for s in strings:
        b = s.encode("utf-8")
        parts.append(struct.pack("<I", len(b)) + b)

    methods = [
        "get_frameCount","GetGlobalFloat","SetGlobalFloat","Exists",
        "ReadAllBytes","BlockCopy","Delete","Copy","CurrentLocalPlayer",
        "SetEAimAssitMode","get_main","set_fieldOfView","set_fog",
        "get_deltaTime","get_CharacterController","get_transform",
        "get_forward","Sqrt","get_position",".ctor","set_position",
        "Move","CurrentCameraControllerManager","SetFov",
        "CurrentCameraController","SetOffestRightAndBack","ReSetFov",
        "IsLocalPlayer","IsLocalTeammate","get_CurHP","GetAvatarManager",
        "SetAvatarColor","SetRenderQ","SetOutlineVisible",
        "RevertToDefaultRenderQ","GetHeadTF","GetHipTF",
        "WorldToScreenPoint","get_FootBoneLeft","get_FootBoneRight",
        "get_width","get_height","get_MaxHP","GetInstanceID",
        "get_HeadBoneTransform","get_ShoulderBoneTransform",
        "get_SpineBoneTransform","get_HipsBoneTransform",
        "get_ArmBoneLeft","get_ArmBoneRight","get_HandBoneLeft",
        "get_HandBoneRight","get_LegBoneLeft","get_LegBoneRight",
        "SendTo","Close","<>iFixBaseProxy_GetAttackableCenterWS",
        "<>iFixBaseProxy_GetAttackableRadius",
        "<>iFixBaseProxy_get_CanBeLockedByAimAssist",
        "<>iFixBaseProxy_CanAssitByWeapon",
    ]
    for m in methods:
        b = m.encode("utf-8")
        parts.append(struct.pack("<I", len(b)) + b)

    paths = [
        "/storage/emulated/0/Android/data/com.dts.freefiremax/files/vqcfg.bin",
        "/storage/emulated/0/Android/data/com.dts.freefiremax/files/Assembly-CSharp-patch.bytes",
        "/storage/emulated/0/Android/data/com.dts.freefiremax/files/localConfig.bak",
        "/storage/emulated/0/Android/data/com.dts.freefiremax/files/localConfig.json",
    ]
    for p in paths:
        b = p.encode("utf-8")
        parts.append(struct.pack("<I", len(b)) + b)

    vq = (["_vqfrD"]
          + [f"_vqB{i}" for i in [0,1,2,72,101,127,100,45,108,110,111,
                                   119,120,112,113,114,115,118,125,126]]
          + [f"_vqP{i}" for i in [0,2,3,4,6,30,40,29,50,51,52,59,60,
                                   61,62,28,63,5,16,17,22,23,24,39]]
          + ["_vqcl","_vqaa","_vqag","_vqlf","_vqlg","_vqsx","_vqsz",
             "_vqcm","_vqcg","_vqcs","_vqcb","_vqcu","_vqpg",
             "_vqtr","_vqtf","_vqtb","_vqtn","_vqch","_vqcn",
             "_vqnf","_vqn"])
    for v in vq:
        b = v.encode("utf-8")
        parts.append(struct.pack("<I", len(b)) + b)

    for f in ["BackOffset","UpOffset","OutlineVisibleJudgeInUpDate","Loopback"]:
        b = f.encode("utf-8")
        parts.append(struct.pack("<I", len(b)) + b)

    parts.append(b")IFix.WrappersManagerImpl, Assembly-CSharp")
    parts.append(b"\x11, Assembly-CSharp")

    body = b"".join(parts)
    count = struct.pack("<I", len(strings)+len(methods)+len(paths)+len(vq)+4)
    checksum = hashlib.sha256(body).digest()
    return parts[0] + count + body + checksum

if __name__ == "__main__":
    os.makedirs("app/src/main/assets", exist_ok=True)
    data = build_patch()
    out = "app/src/main/assets/Assembly-CSharp-patch.bytes"
    with open(out, "wb") as f: f.write(data)
    print(f"[OK] {out}: {len(data)} bytes")
